package org.motechproject.commcare.provider.sync.response;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class BatchResponseMetadataTest {
    @Test
    public void shouldReturnNextBatchQuery() {
        int offset = 12;
        int batchSize = 15;
        BatchResponseMetadata batchResponseMetadata = new BatchResponseMetadata();
        batchResponseMetadata.setNext("somenext");
        batchResponseMetadata.setOffset(offset);
        BatchRequestQuery nextBatchQuery = batchResponseMetadata.getNextBatchQuery(batchSize);

        assertEquals(27, nextBatchQuery.getOffset());
    }

    @Test
    public void shouldReturnNullNextBatchQueryIfNextIsNull() {
        int offset = 12;
        int batchSize = 15;
        BatchResponseMetadata batchResponseMetadata = new BatchResponseMetadata();
        batchResponseMetadata.setOffset(offset);
        BatchRequestQuery nextBatchQuery = batchResponseMetadata.getNextBatchQuery(batchSize);

        assertNull(nextBatchQuery);
    }

    @Test
    public void shouldCheckIfMetadataHasNextQuery() {
        BatchResponseMetadata batchResponseMetadata = new BatchResponseMetadata();
        assertFalse(batchResponseMetadata.hasNext());

        batchResponseMetadata.setNext("");
        assertFalse(batchResponseMetadata.hasNext());

        batchResponseMetadata.setNext("    ");
        assertFalse(batchResponseMetadata.hasNext());

        batchResponseMetadata.setNext("next");
        assertTrue(batchResponseMetadata.hasNext());
    }
}
