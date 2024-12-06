package com.mammb.code.editor.fx;

import com.mammb.code.editor.core.Context;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AppContext implements Context {

    private static final System.Logger log = System.getLogger(AppContext.class.getName());

    private AppConfig appConfig = new AppConfig();

    @Override
    public AppConfig config() {
        return appConfig;
    }

    public static class AppConfig implements Context.Config {

        private static final Map<String, Object> map = new ConcurrentHashMap<>();
        private final Path configPath = configDir().resolve(Path.of("config.properties"));

        public AppConfig() {
            try {
                Files.createDirectories(configPath.getParent());
                if (!Files.exists(configPath)) Files.createFile(configPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            load();
            Runtime.getRuntime().addShutdownHook(new Thread(this::save));
        }

        public double windowWidth() {
            return Double.parseDouble(map.getOrDefault("windowWidth", 640).toString());
        }
        public void windowWidth(double val) {
            map.put("windowWidth", val);
        }

        public double windowHeight() {
            return Double.parseDouble(map.getOrDefault("windowHeight", 480.0).toString());
        }
        public void windowHeight(double val) {
            map.put("windowHeight", val);
        }

        public double windowPositionX() {
            return Double.parseDouble(map.getOrDefault("windowPositionX", 320).toString());
        }
        public void windowPositionX(double val) {
            map.put("windowPositionX", val);
        }

        public double windowPositionY() {
            return Double.parseDouble(map.getOrDefault("windowPositionY", 240).toString());
        }
        public void windowPositionY(double val) {
            map.put("windowPositionY", val);
        }


        private void load() {
            try {
                for (String line : Files.readAllLines(configPath)) {
                    var str = line.trim();
                    if (str.startsWith("#") || str.startsWith("!") || !str.contains("=")) continue;
                    var kv = str.split("=", 2);
                    map.put(kv[0], kv[1]);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        private void save() {
            try {
                List<String> merged = new ArrayList<>();
                for (String line : Files.readAllLines(configPath)) {
                    var str = line.trim();
                    if (str.startsWith("#") || str.startsWith("!") || !str.contains("=")) {
                        merged.add(line);
                        continue;
                    }
                    var kv = str.split("=", 2);
                    if (map.containsKey(kv[0])) {
                        var v = map.remove(kv[0]);
                        merged.add(kv[0] + "=" + v);
                    }
                }
                for (var entry : map.entrySet()) {
                    merged.add(entry.getKey() + "=" + entry.getValue());
                }
                Files.write(configPath, merged);
            } catch (IOException ignore) {
                log.log(System.Logger.Level.ERROR, ignore);
            }
        }
    }

    private static Path configDir() {
        Path home = Path.of(System.getProperty("user.home"));
        String osName = System.getProperty("os.name").toLowerCase();
        return osName.contains("windows")
            ? home.resolve("AppData", "Roaming", "min-editor", Version.val)
            : osName.contains("linux")
            ? home.resolve(".config", "min-editor", Version.val)
            : osName.contains("mac")
            ? home.resolve("Library", "Application Support", "min-editor", Version.val)
            : home.resolve("min-editor", Version.val);
    }

}
