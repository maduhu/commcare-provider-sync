package org.motechproject.commcare.provider.sync.response;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class BatchJobTypeTest {
    @Test
    public void shouldQualifyTheGivenPropertyWithBatchJobType() {
        assertEquals("property:provider", BatchJobType.PROVIDER.qualify("property:%s"));

        assertEquals("property:group", BatchJobType.GROUP.qualify("property:%s"));
    }
}
