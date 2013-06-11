package org.motechproject.commcare.provider.sync.response;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BatchResponseTest {
    @Test
    public void shouldTellIfResponseHasRecords(){
        BatchResponse<Provider> batchResponse = new BatchResponse<>();
        assertFalse(batchResponse.hasRecords());
        assertTrue(batchResponse.getRecords().isEmpty());

        List<Provider> records = Arrays.asList(new Provider(), new Provider());
        batchResponse.setRecords(records);
        assertTrue(batchResponse.hasRecords());
        assertEquals(records, batchResponse.getRecords());
    }
}
