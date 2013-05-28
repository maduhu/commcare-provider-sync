package org.motechproject.commcare.provider.sync.response;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.codehaus.jackson.annotate.JsonProperty;

public class Meta {
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

    public Meta() {
    }

    public Meta(String next) {
        this.next = next;
    }

    public String getNextQuery() {
        return next;
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
