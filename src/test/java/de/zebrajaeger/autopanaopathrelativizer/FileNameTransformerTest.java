package de.zebrajaeger.autopanaopathrelativizer;

import org.apache.commons.lang3.SystemUtils;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileNameTransformerTest {
    @Test
    public void test1() {
        final String r = new FileNameTransformer("%e.%n-%d")
                .transform(new File("/foo/bar/narf.xxx"));

        if (SystemUtils.OS_NAME.toLowerCase().startsWith("windows")) {
            assertEquals(r, "xxx.narf-\\foo\\bar");
        }else{
            assertEquals(r, "xxx.narf-/foo/bar");
        }
    }
}
