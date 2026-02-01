/*
 * Copyright 2023-2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mammb.code.editor.core.tools;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * A utility class for searching for a specific pattern in files within a directory.
 * This class provides functionality to scan readable, regular files in a directory
 * tree, looking for occurrences of a pattern, and reporting matches alongside metadata
 * such as the file path, character encoding, line number, and contextual snippet.
 * <p>
 * This class handles large files by processing them in chunks with overlapping regions
 * to ensure continuity for patterns spanning chunk boundaries. It can also detect character
 * encoding, skip binary files, and generate contextual snippets around matches.
 * @author Naotsugu Kobayashi
 */
public class FindInFiles {

    private static final long CHUNK_SIZE = 128 * 1024 * 1024;
    private static final int OVERLAP_BYTES = 8192;
    private static final int SNIPPET_CONTEXT = 60;

    private static final Set<String> BIN_EXTENSIONS = Set.of(
        "class", "jar", "war", "ear",
        "zip", ".7z", ".rar", "gz", "tar", "tgz", "lzh", "sit",
        "exe", "app", "elf", "ico", "cur",
        "png", "jpg", "jpeg", "gif", "bmp", "iso",
        "aac", "mp4", "mp3", "wav", "avi", "mpg",
        "doc", "docx", "xls", "xlsx", "ppt", "pptx", "pdf"
    );

    private static final List<Charset> CHARSET_CANDIDATES = charsetCandidates();

    public record Found(Path path, Charset charset, long line, String snippet) { }

    /**
     * Executes a search operation on readable, regular files within the specified directory, using a given
     * regular expression pattern, and processes the matched results through a provided consumer.
     * <p>
     * This method traverses the directory tree starting at the given path, and for each readable and regular
     * file, it searches for text matching the provided regex pattern. Matching results are passed as a list
     * to the provided consumer. The method uses a virtual thread per task executor for parallel processing.
     *
     * @param dir        the root directory to recursively search files in
     * @param patternStr the regular expression pattern as a string for matching file contents
     * @param consumer   a consumer that processes a list of {@code Found} objects representing the matches
     *
     * @throws RuntimeException if an I/O error occurs while traversing the directory or processing files
     */
    public static void run(Path dir, String patternStr, Consumer<List<Found>> consumer) {

        final var pattern = Pattern.compile(patternStr);

        try (final var executor = Executors.newVirtualThreadPerTaskExecutor();
             final Stream<Path> stream = Files.walk(dir)) {

            stream.filter(Files::isReadable)
                .filter(Files::isRegularFile)
                .forEach(path -> executor.submit(() -> consumer.accept(processFile(path, pattern))));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private static List<Found> processFile(Path path, Pattern pattern) {

        if (isBinaryLike(path)) return List.of();

        try (var fc = FileChannel.open(path, StandardOpenOption.READ)) {

            long fileSize = fc.size();
            if (fileSize == 0) return List.of();

            // detect charset using the beginning of the file
            var cs = detectCharset(fc);

            long filePosition = 0;
            long currentLine = 1;
            int overlapSkipChars = 0;
            List<Found> list = null;

            while (filePosition < fileSize) {
                long remaining = fileSize - filePosition;
                long mapSize = Math.min(CHUNK_SIZE, remaining);

                MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, filePosition, mapSize);

                int limit = (int) mapSize;
                boolean isLastChunk = (filePosition + mapSize == fileSize);
                long nextPosDelta = limit; // default: advance by full chunk size
                int nextOverlapSkipChars = 0;

                if (!isLastChunk) {
                    // try to align to the last newline in this chunk to avoid splitting lines
                    int lastNewlinePos = findLastLineBreak(bb, limit);

                    if (lastNewlinePos > 0) {
                        // newline found: cut cleanly at the newline
                        limit = lastNewlinePos;
                        nextPosDelta = limit;
                    } else {
                        // a newline isn't found (very long line):
                        // force overlap. rewind the next start position by OVERLAP_BYTES.
                        // we assume no newlines exist in the overlapped region (since we scanned for it).
                        nextPosDelta = limit - OVERLAP_BYTES;

                        // calculate how many chars correspond to the overlap bytes for the next chunk
                        float maxBytesPerChar = cs.newEncoder().maxBytesPerChar();
                        nextOverlapSkipChars = (int) (OVERLAP_BYTES / maxBytesPerChar);
                    }
                }

                // decode to char buffer
                bb.limit(limit);
                CharBuffer cb = cs.decode(bb);

                // searchInChunk
                Matcher matcher = pattern.matcher(cb);
                int lastScanPos = 0;
                while (matcher.find()) {
                    int start = matcher.start();
                    if (start < overlapSkipChars) {
                        continue; // skip duplicate range
                    }
                    currentLine += countlines(cb, lastScanPos, start);
                    lastScanPos = start;
                    if (list == null) {
                        list = new ArrayList<>();
                    }
                    list.add(new Found(path, cs, currentLine, snippet(cb, matcher)));
                }
                // count remaining lines
                currentLine += countlines(cb, lastScanPos, cb.length());

                filePosition += nextPosDelta;
                overlapSkipChars = nextOverlapSkipChars;
                bb = null; // unmap hint
            }

            return (list == null) ? List.of() : list;

        } catch (IOException ignore) { }

        return List.of();
    }

    private static int findLastLineBreak(ByteBuffer bb, int limit) {
        // optimization: Only scan the end of the buffer (twice the overlap size)
        int scanStart = Math.max(0, limit - (OVERLAP_BYTES * 2));
        for (int i = limit - 1; i >= scanStart; i--) {
            byte b = bb.get(i);
            if (b == '\n') return i + 1;
        }
        return 0; // not found
    }

    private static int countlines(CharBuffer cb, int start, int end) {
        int count = 0;
        for (int i = start; i < end; i++) {
            if (cb.charAt(i) == '\n') {
                count++;
            }
        }
        return count;
    }

    private static String snippet(CharBuffer cb, Matcher matcher) {

        int start = matcher.start();
        int end = matcher.end();

        // extract context before a match
        int contextStart = start;
        int contextCount = 0;
        while (contextStart > 0 && contextCount < SNIPPET_CONTEXT) {
            char c = cb.charAt(contextStart - 1);
            if (c == '\n') break;
            contextStart--;
            contextCount++;
        }

        // extract context after a match
        int contextEnd = end;
        contextCount = 0;
        while (contextEnd < cb.length() && contextCount < SNIPPET_CONTEXT) {
            char c = cb.charAt(contextEnd);
            if (c == '\n') break;
            contextEnd++;
            contextCount++;
        }
        var prefix = (contextStart > 0 && cb.charAt(contextStart - 1) != '\n') ? "..." : "";
        var suffix = (contextEnd < cb.length() && cb.charAt(contextEnd) != '\n') ? "..." : "";

        return prefix + cb.subSequence(contextStart, contextEnd).toString() + suffix;
    }


    private static Charset detectCharset(FileChannel fc) throws IOException {
        long size = Math.min(fc.size(), 4096);
        MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, size);
        for (Charset cs : CHARSET_CANDIDATES) {
            if (tryDecode(bb.duplicate(), cs)) return cs;
        }
        return StandardCharsets.UTF_8;
    }

    private static boolean tryDecode(ByteBuffer buffer, Charset cs) {
        try {
            CharsetDecoder decoder = cs.newDecoder()
                .onMalformedInput(CodingErrorAction.REPORT)
                .onUnmappableCharacter(CodingErrorAction.REPORT);
            int capacity = (int) (buffer.remaining() * decoder.maxCharsPerByte());
            CharBuffer cb = CharBuffer.allocate(capacity);
            return !decoder.decode(buffer, cb, true).isError();
        } catch (Exception ignore) { }
        return false;
    }

    private static boolean isBinaryLike(Path path) {
        var string = path.toString().toLowerCase();
        int dot = string.lastIndexOf('.');
        if (dot != -1) {
            return BIN_EXTENSIONS.contains(string.substring(dot + 1));
        }
        return false;
    }

    private static List<Charset> charsetCandidates() {
        LinkedHashSet<Charset> set = new LinkedHashSet<>();
        set.add(StandardCharsets.UTF_8);
        set.add(Charset.forName("Windows-31J"));
        set.add(Charset.forName("EUC-JP"));
        set.addAll(Charset.availableCharsets().values());
        return List.copyOf(set);
    }

}
