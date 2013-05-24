package org.motechproject.commcare.provider.sync.response;

import org.codehaus.jackson.annotate.JsonProperty;

public class BaseResponse {
    @JsonProperty
    private Meta meta;

    public BaseResponse() {
    }
}
