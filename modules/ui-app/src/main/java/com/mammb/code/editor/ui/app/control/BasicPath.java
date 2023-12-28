package com.mammb.code.editor.ui.app.control;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

public class BasicPath implements PathItem {

    private final Path raw;


    public BasicPath(Path raw) {
        this.raw = raw;
    }

    @Override
    public String name() {
        return raw.getFileName().toString();
    }

    @Override
    public Path raw() {
        return raw;
    }

    @Override
    public FileSystem getFileSystem() {
        return raw.getFileSystem();
    }

    @Override
    public boolean isAbsolute() {
        return raw.isAbsolute();
    }

    @Override
    public Path getRoot() {
        return raw.getRoot();
    }

    @Override
    public Path getFileName() {
        return raw.getFileName();
    }

    @Override
    public Path getParent() {
        return raw.getParent();
    }

    @Override
    public int getNameCount() {
        return raw.getNameCount();
    }

    @Override
    public Path getName(int index) {
        return raw.getName(index);
    }

    @Override
    public Path subpath(int beginIndex, int endIndex) {
        return raw.subpath(beginIndex, endIndex);
    }

    @Override
    public boolean startsWith(Path other) {
        return raw.startsWith(other);
    }

    @Override
    public boolean endsWith(Path other) {
        return raw.endsWith(other);
    }

    @Override
    public Path normalize() {
        return raw.normalize();
    }

    @Override
    public Path resolve(Path other) {
        return raw.resolve(other);
    }

    @Override
    public Path relativize(Path other) {
        return raw.relativize(other);
    }

    @Override
    public URI toUri() {
        return raw.toUri();
    }

    @Override
    public Path toAbsolutePath() {
        return raw.toAbsolutePath();
    }

    @Override
    public Path toRealPath(LinkOption... options) throws IOException {
        return raw.toRealPath(options);
    }

    @Override
    public WatchKey register(WatchService watcher, WatchEvent.Kind<?>[] events, WatchEvent.Modifier... modifiers) throws IOException {
        return raw.register(watcher, events, modifiers);
    }

    @Override
    public int compareTo(Path other) {
        return raw.compareTo(other);
    }

    @Override
    public boolean equals(Object other) {
        return raw.equals(other);
    }

    @Override
    public int hashCode() {
        return raw.hashCode();
    }

    @Override
    public String toString() {
        return raw.toString();
    }

}
