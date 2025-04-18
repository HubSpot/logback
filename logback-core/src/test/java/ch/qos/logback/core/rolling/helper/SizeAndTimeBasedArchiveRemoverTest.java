package ch.qos.logback.core.rolling.helper;


import java.io.File;
import java.lang.reflect.Field;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.ContextBase;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SizeAndTimeBasedArchiveRemoverTest {

    Context context = new ContextBase();

    @Test
    public void smoke() {
        // Keep the test for backward compatibility, but adjust expectations
        FileNamePattern fileNamePattern = new FileNamePattern("smoke-%d-%i.gz", context);
        SizeAndTimeBasedArchiveRemover remover = new SizeAndTimeBasedArchiveRemover(fileNamePattern, null);
        File[] fileArray = new File[2];
        File[] expected = new File[2];

        fileArray[0] = expected[1] = new File("/tmp/smoke-1970-01-01-0.gz");
        fileArray[1] = expected[0] = new File("/tmp/smoke-1970-01-01-1.gz");

        remover.descendingSort(fileArray, Instant.ofEpochMilli(0));

        assertArrayEquals(expected, fileArray);
    }

    @Test
    public void badFilenames() {
        FileNamePattern fileNamePattern = new FileNamePattern("smoke-%d-%i.gz", context);
        SizeAndTimeBasedArchiveRemover remover = new SizeAndTimeBasedArchiveRemover(fileNamePattern, null);
        File[] fileArray = new File[2];
        File[] expected = new File[2];

        fileArray[0] = expected[0] = new File("/tmp/smoke-1970-01-01-b.gz");
        fileArray[1] = expected[1] = new File("/tmp/smoke-1970-01-01-c.gz");

        remover.descendingSort(fileArray, Instant.ofEpochMilli(0));

        assertArrayEquals(expected, fileArray);
    }
    
    @Test
    public void testPatternCaching() throws Exception {
        FileNamePattern fileNamePattern = new FileNamePattern("smoke-%d-%i.gz", context);
        SizeAndTimeBasedArchiveRemover remover = new SizeAndTimeBasedArchiveRemover(fileNamePattern, null) {
            @Override
            public File[] getFilesInPeriod(Instant instantOfPeriodToClean) {
                return super.getFilesInPeriod(instantOfPeriodToClean);
            }
        };
        Instant instant = Instant.ofEpochMilli(0);
        
        Field cacheField = SizeAndTimeBasedArchiveRemover.class.getDeclaredField("STEM_REGEX_PATTERN_CACHE");
        cacheField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<String, Pattern> cache = (Map<String, Pattern>) cacheField.get(null);
        
        cache.clear();
        
        remover.getFilesInPeriod(instant);
        assertEquals(1, cache.size());
        
        remover.getFilesInPeriod(instant);
        assertEquals(1, cache.size());
        
        Instant anotherInstant = Instant.ofEpochMilli(86400000);
        remover.getFilesInPeriod(anotherInstant);
        assertEquals(2, cache.size());
    }
}
