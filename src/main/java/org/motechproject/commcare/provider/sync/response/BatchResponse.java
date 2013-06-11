package org.motechproject.commcare.provider.sync.response;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class BatchResponse<T> {

    @JsonProperty("objects")
    private List<T> records = new ArrayList<>();

    @JsonProperty
    private BatchResponseMetadata meta;

    public BatchResponse() {
    }

    public BatchResponse(BatchResponseMetadata meta) {
        this.meta = meta;
    }

    public BatchResponseMetadata getMeta() {
        return meta;
    }

    @Override
    public boolean equals(Object other) {
        return EqualsBuilder.reflectionEquals(this, other);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    public List<T> getRecords() {
        return records;
    }

    public boolean hasRecords() {
        return records.size() > 0;
    }

    public void setRecords(List<T> records) {
        this.records = records;
    }
}
