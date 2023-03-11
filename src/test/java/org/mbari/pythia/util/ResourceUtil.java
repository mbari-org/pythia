package org.mbari.pythia.util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class ResourceUtil {

    public static Path locateResource(String resource) {
        try {
            var url = ResourceUtil.class.getResource(resource);
            assertNotNull(url, "Could not find " + resource);
            var path = Paths.get(url.toURI());
            assertTrue(Files.exists(path));
            return path;
        }
        catch (Exception e) {
            fail("Unable to find resource `" + resource + "`");
            return null;
        }
    }
}
