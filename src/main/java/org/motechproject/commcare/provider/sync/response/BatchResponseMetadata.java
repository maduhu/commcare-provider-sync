package org.motechproject.commcare.provider.sync.response;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.codehaus.jackson.annotate.JsonProperty;

public class BatchResponseMetadata {
    @JsonProperty
    private Integer limit;
    @JsonProperty
    private String next;
    @JsonProperty
    private Integer offset;
    @JsonProperty
    private String previous;
    @JsonProperty("total_count")
    private Integer totalCount;

    public BatchResponseMetadata() {
    }

    public BatchRequestQuery getNextBatchQuery(int batchSize) {
        if(!hasNext()) {
            return null;
        }
        return new BatchRequestQuery(offset + batchSize);
    }

    public void setNext(String next) {
        this.next = next;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public boolean hasNext() {
        return StringUtils.isNotBlank(this.next);
    }

    @Override
    public boolean equals(Object other) {
        return EqualsBuilder.reflectionEquals(this, other);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
