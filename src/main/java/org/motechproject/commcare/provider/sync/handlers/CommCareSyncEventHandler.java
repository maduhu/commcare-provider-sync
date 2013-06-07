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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CommCareSyncEventHandler {
    private static final Logger logger = LoggerFactory.getLogger("commcare-provider-sync");

    CommCareSyncService commCareSyncService;
    EventRelay eventRelay;

    @Autowired
    public CommCareSyncEventHandler(CommCareSyncService commCareSyncService, EventRelay eventRelay) {
        this.commCareSyncService = commCareSyncService;
        this.eventRelay = eventRelay;
    }

    @MotechListener(subjects = {EventConstants.COMMCARE_PROVIDER_SYNC_EVENT})
    @SuppressWarnings("unused - motechEvent expected as parameter by cron invoker")
    public void handleProviderSync(MotechEvent motechEvent) {
        logger.info("Handling provider sync event");
        commCareSyncService.fetchAndPublishProviderDetails(new EventPublishAction() {
            @Override
            public void publish(BaseResponse baseResponse) {
                logger.info("Publishing provider details");
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
    @SuppressWarnings("unused - motechEvent expected as parameter by cron invoker")
    public void handleGroupSync(MotechEvent motechEvent) {
        logger.info("Handling group sync event");
        commCareSyncService.fetchAndPublishGroupDetails(new EventPublishAction() {
            @Override
            public void publish(BaseResponse baseResponse) {
                logger.info("Publishing group details");
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
