package org.motechproject.commcare.provider.sync.response;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class BatchRequestQueryTest {
    @Test
    public void shouldSetOffsetAndBatchSizeOnGivenQueryTemplate() {
        int offset = 12;
        int batchSize = 10;
        BatchRequestQuery batchRequestQuery = new BatchRequestQuery(offset);
        batchRequestQuery.setBatchSize(batchSize);

        String url = batchRequestQuery.toQuery("uri?offset=%s&limit=%s");

        assertEquals("uri?offset=12&limit=10", url);
    }
}
