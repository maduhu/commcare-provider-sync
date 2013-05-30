package org.motechproject.commcare.provider.sync.handlers;

import org.motechproject.commcare.provider.sync.constants.EventConstants;
import org.motechproject.commcare.provider.sync.response.BaseResponse;
import org.motechproject.commcare.provider.sync.response.GroupDetailsResponse;
import org.motechproject.commcare.provider.sync.response.ProviderDetailsResponse;
import org.motechproject.commcare.provider.sync.service.CommCareSyncService;
import org.motechproject.commcare.provider.sync.service.EventPublishAction;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.event.listener.annotations.MotechListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CommCareSyncEventHandler {

    CommCareSyncService commCareSyncService;
    EventRelay eventRelay;

    @Autowired
    public CommCareSyncEventHandler(CommCareSyncService commCareSyncService, EventRelay eventRelay) {
        this.commCareSyncService = commCareSyncService;
        this.eventRelay = eventRelay;
    }

    @MotechListener(subjects = {EventConstants.COMMCARE_PROVIDER_SYNC_EVENT})
    public void handleProviderSync() {
        commCareSyncService.fetchAndPublishProviderDetails(new EventPublishAction() {
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

    @MotechListener(subjects = {EventConstants.COMMCARE_GROUP_SYNC_EVENT})
    public void handleGroupSync() {
        commCareSyncService.fetchAndPublishGroupDetails(new EventPublishAction() {
            @Override
            public void publish(BaseResponse baseResponse) {
                GroupDetailsResponse groupDetailsResponse = (GroupDetailsResponse) baseResponse;
                if (groupDetailsResponse.hasNoGroups())
                    return;
                Map<String, Object> parameters = new HashMap<>();
                parameters.put(EventConstants.GROUP_DETAILS, groupDetailsResponse.getGroups());
                eventRelay.sendEventMessage(new MotechEvent(EventConstants.GROUP_DETAILS_EVENT, parameters));
            }
        });
    }
}
