/*
 * Copyright 2023 Monterey Bay Aquarium Research Institute
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }

    public static List<String> load(URL url) {
        try (var inputStream = url.openStream()) {
            return load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<String> load(InputStream inputStream) {
        var reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        return reader.lines().map(String::trim).toList();
    }
}
