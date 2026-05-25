import java.io.*;
import java.net.URI;
import java.nio.channels.Channels;
import java.nio.file.*;

final String macos_amd64   = "https://github.com/graalvm/graalvm-ce-builds/releases/download/jdk-25.0.1/graalvm-community-jdk-25.0.1_macos-x64_bin.tar.gz";
final String macos_aarch64 = "https://github.com/graalvm/graalvm-ce-builds/releases/download/jdk-25.0.2/graalvm-community-jdk-25.0.2_macos-aarch64_bin.tar.gz";
final String linux_amd64   = "https://github.com/graalvm/graalvm-ce-builds/releases/download/jdk-25.0.2/graalvm-community-jdk-25.0.2_linux-x64_bin.tar.gz";
final String linux_aarch64 = "https://github.com/graalvm/graalvm-ce-builds/releases/download/jdk-25.0.2/graalvm-community-jdk-25.0.2_linux-aarch64_bin.tar.gz";
final String windows_amd64 = "https://github.com/graalvm/graalvm-ce-builds/releases/download/jdk-25.0.2/graalvm-community-jdk-25.0.2_windows-x64_bin.zip";
final Path basePath = Path.of("graal");

final String os = System.getProperty("os.name").toLowerCase();
final String arch = System.getProperty("os.arch").toLowerCase().startsWith("aarch64") ? "aarch64" : "x64";


void main(String... args) throws Exception {
    Path path = downloadGraal();
    Path home = extract(path).orElseThrow();
}

Path downloadGraal() {

    if (!Files.exists(basePath)) {
        try {
            Files.createDirectories(basePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    String repo;
    if (os.startsWith("mac")) {
        repo = arch.startsWith("aarch64") ? macos_aarch64 : macos_amd64;
    } else if (os.startsWith("linux")) {
        repo = arch.startsWith("aarch64") ? linux_aarch64 : linux_amd64;
    } else if (os.startsWith("win")) {
        repo = windows_amd64;
    } else {
        throw new RuntimeException("unsupported");
    }

    Path path = basePath.resolve(repo.substring(repo.lastIndexOf("/") + 1));
    if (Files.exists(path)) {
        return path;
    }

    IO.println("download from " + repo);
    try (var ch = Channels.newChannel(new URI(repo).toURL().openStream());
         var fc = new FileOutputStream(path.toFile()).getChannel()) {
        fc.transferFrom(ch, 0, Long.MAX_VALUE);
    } catch (IOException | URISyntaxException e) {
        throw new RuntimeException(e);
    }
    return path;
}

Optional<Path> extract(Path path) {
    try {

        Path dir = path.getParent();
        List<Path> before = new ArrayList<>();
        try (Stream<Path> stream = Files.list(dir)) {
            stream.forEach(before::add);
        }

        ProcessBuilder pb = os.startsWith("win")
            ? new ProcessBuilder("cmd.exe", "/c", "tar", "-xzf", path.toString(), "-C", dir.toString())
            : new ProcessBuilder("tar", "-xzf", path.toString(), "-C", dir.toString());
        pb.inheritIO();
        Process process = pb.start();
        int exitCode = process.waitFor();
        process.destroyForcibly();
        if (process.waitFor() != 0) {
            throw new RuntimeException("extractor error");
        }

        try (Stream<Path> stream = Files.list(dir)) {
            return stream.filter(p -> !before.contains(p)).findFirst();
        }

    } catch (IOException | InterruptedException e) {
        throw new RuntimeException(e);
    }
}
