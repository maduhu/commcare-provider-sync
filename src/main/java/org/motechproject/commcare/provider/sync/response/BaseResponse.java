package org.motechproject.commcare.provider.sync.response;

import org.codehaus.jackson.annotate.JsonProperty;

public class BaseResponse {
    @JsonProperty
    private Meta meta;

    public BaseResponse() {
    }

    public BaseResponse(Meta meta) {
        this.meta = meta;
    }

    public Meta getMeta() {
        return meta;
    }

    public boolean hasMoreRecordsToFetch() {
        return meta != null && meta.hasNext();
    }
}
