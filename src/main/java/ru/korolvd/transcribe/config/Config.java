package ru.korolvd.transcribe.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Config {
    public static final String STORAGE_BUCKET = "yandex.cloud.storage.bucket";
    public static final String STORAGE_KEY_ID = "yandex.cloud.storage.key.id";
    public static final String STORAGE_KEY_SECRET = "yandex.cloud.storage.key.secret";
    public static final String ACCOUNT_KEY_ID = "yandex.cloud.service-account.key.id";
    public static final String ACCOUNT_ACCOUNT_SECRET = "yandex.cloud.service-account.key.secret";
    public static final String VIDEO_FILE = "video_file";
    public static final String AUDIO_FILE = "audio_file";

    private final Map<String, String> values = new HashMap<>();

    public Config(String path, String[] args) {
        loadProperties(path);
        loadArgs(args);
    }

    private void loadArgs(String[] args) {
        if (args.length != 1) {
            throw new IllegalArgumentException("One argument required - video file name");
        }
        String fileName = args[0];
        File file = new File(fileName);
        if (!file.exists()) {
            throw new IllegalArgumentException(String.format("Not exist %s", file.getAbsoluteFile()));
        }
        values.put(VIDEO_FILE, fileName);
        values.put(AUDIO_FILE, file.getName().split("\\.")[0] + ".mp3");
    }

    private void loadProperties(String path) {
        try (BufferedReader read = new BufferedReader(new FileReader(path))) {
            read.lines()
                    .filter(l -> l.contains("=") && !l.contains("#"))
                    .map(s -> s.split("="))
                    .forEach(p -> {
                        if (p.length == 2 && (!p[0].isEmpty()) && !p[1].isEmpty()) {
                            values.put(p[0], p[1]);
                        } else {
                            throw new IllegalArgumentException("Incorrect parameters. Using KEY=VALUE");
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getValue(String key) {
        String value = values.get(key);
        if (Objects.isNull(value)) {
            throw new IllegalArgumentException("Property " + key + " not found. Check cfg/transcribe.properties or application args");
        }
        return value;
    }
}
