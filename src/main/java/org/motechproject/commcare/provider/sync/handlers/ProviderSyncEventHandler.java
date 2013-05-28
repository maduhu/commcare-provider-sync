package org.motechproject.commcare.provider.sync.handlers;

import org.motechproject.commcare.provider.sync.constants.EventConstants;
import org.motechproject.commcare.provider.sync.response.BaseResponse;
import org.motechproject.commcare.provider.sync.response.ProviderDetailsResponse;
import org.motechproject.commcare.provider.sync.service.CommCareHttpClientService;
import org.motechproject.commcare.provider.sync.service.EventPublishAction;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.event.listener.annotations.MotechListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ProviderSyncEventHandler {

    CommCareHttpClientService commCareHttpClientService;
    EventRelay eventRelay;

    @Autowired
    public ProviderSyncEventHandler(CommCareHttpClientService commCareHttpClientService, EventRelay eventRelay) {
        this.commCareHttpClientService = commCareHttpClientService;
        this.eventRelay = eventRelay;
    }

    @MotechListener(subjects = {EventConstants.COMMCARE_PROVIDER_SYNC_EVENT})
    public void handleProviderSync() {
        commCareHttpClientService.fetchAndPublishProviderDetails(new EventPublishAction() {
            @Override
            public void publish(BaseResponse baseResponse) {
                ProviderDetailsResponse providerDetailsResponse = (ProviderDetailsResponse) baseResponse;
                if (providerDetailsResponse.hasNoProviders())
                    return;
                Map<String, Object> parameters = new HashMap<>();
                parameters.put(EventConstants.PROVIDER_DETAILS, providerDetailsResponse.getProviders());
                eventRelay.sendEventMessage(new MotechEvent(EventConstants.PROVIDER_DETAILS_EVENT, parameters));
            }
        });
    }
}
