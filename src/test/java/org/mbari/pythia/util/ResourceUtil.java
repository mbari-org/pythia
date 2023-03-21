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

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ResourceUtil {

    public static Path locateResource(String resource) {
        try {
            var url = ResourceUtil.class.getResource(resource);
            assertNotNull(url, "Could not find " + resource);
            var path = Paths.get(url.toURI());
            assertTrue(Files.exists(path));
            return path;
        } catch (Exception e) {
            fail("Unable to find resource `" + resource + "`");
            return null;
        }
    }
}
