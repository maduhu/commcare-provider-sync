package org.motechproject.commcare.provider.sync.response;

import java.io.Serializable;

public class BatchRequestQuery implements Serializable {
    private int batchSize;
    private int offset;

    public BatchRequestQuery(int offset) {
        this.offset = offset;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public String toQuery(String queryTemplate) {
        return String.format(queryTemplate, offset, batchSize);
    }

    @Override
    public String toString() {
        return String.format("offset: %s, Batch Size: %s", offset, batchSize);
    }

    public int getOffset() {
        return offset;
    }

}
