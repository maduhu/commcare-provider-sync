package org.motechproject.commcare.provider.sync.response;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

public class ProviderDetailsResponse extends BaseResponse {
    @JsonProperty("objects")
    private List<Provider> providers;

    public ProviderDetailsResponse() {
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
