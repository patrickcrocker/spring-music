package org.cloudfoundry.samples.music.domain;

import org.junit.Test;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

public class RandomIdGeneratorTest {

    @Test
    public void generatesUniqueNonNullIds() {
        RandomIdGenerator generator = new RandomIdGenerator();

        String first = generator.generateId();
        String second = generator.generateId();

        assertNotNull(first);
        assertNotNull(second);
        assertNotEquals(first, second);
    }
}
