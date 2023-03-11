package org.mbari.pythia.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

public class NamesUtil {

    public static List<String> load(Path path) {
        try (var inputStream = Files.newInputStream(path)) {
            return load(inputStream);
        }
        catch (IOException e) {
            return Collections.emptyList();
        }
    }

    public static List<String> load(URL url) {
        try(var inputStream = url.openStream()) {
            return load(inputStream);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<String> load(InputStream inputStream) {
        var reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        return reader.lines().map(String::trim).toList();
    }
}
