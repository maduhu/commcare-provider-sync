package org.motechproject.commcare.provider.sync.service;

import org.motechproject.commcare.provider.sync.response.BaseResponse;

public interface EventPublishAction {
    void publish(BaseResponse baseResponse);
}
