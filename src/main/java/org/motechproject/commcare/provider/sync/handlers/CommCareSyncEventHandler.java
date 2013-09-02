package org.motechproject.commcare.provider.sync.handlers;

import org.motechproject.commcare.provider.sync.constants.EventConstants;
import org.motechproject.commcare.provider.sync.response.BatchJobType;
import org.motechproject.commcare.provider.sync.response.BatchRequestQuery;
import org.motechproject.commcare.provider.sync.service.CommCareSyncService;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.event.listener.annotations.MotechListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CommCareSyncEventHandler {
    private static final Logger logger = LoggerFactory.getLogger("commcare-provider-sync");

    private CommCareSyncService commCareSyncService;
    private EventRelay eventRelay;

    @Autowired
    public CommCareSyncEventHandler(CommCareSyncService commCareSyncService, EventRelay eventRelay) {
        this.commCareSyncService = commCareSyncService;
        this.eventRelay = eventRelay;
    }

    @MotechListener(subjects = {EventConstants.PROVIDER_SYNC_EVENT})
    @SuppressWarnings("unused - motechEvent expected as parameter by cron invoker")
    public synchronized void handleProviderSync(MotechEvent motechEvent) {
        logger.info("Handling provider sync event");
        commCareSyncService.startSync(BatchJobType.PROVIDER);
    }

    @MotechListener(subjects = {EventConstants.PROVIDER_FETCH_DETAILS_IN_BATCH_EVENT})
    @SuppressWarnings("unused - motechEvent expected as parameter by cron invoker")
    public void fetchProviderDetailsInBatch(MotechEvent motechEvent) {
        logger.info("Handling provider batch sync event");
        BatchRequestQuery batchQuery = (BatchRequestQuery) motechEvent.getParameters().get(EventConstants.BATCH_QUERY);
        commCareSyncService.fetchDetailsInBatch(batchQuery, BatchJobType.PROVIDER);
    }


    @MotechListener(subjects = {EventConstants.GROUP_SYNC_EVENT})
    @SuppressWarnings("unused - motechEvent expected as parameter by cron invoker")
    public synchronized void handleGroupSync(MotechEvent motechEvent) {
        logger.info("Handling group sync event");
        commCareSyncService.startSync(BatchJobType.GROUP);
    }

    @MotechListener(subjects = {EventConstants.GROUP_FETCH_DETAILS_IN_BATCH_EVENT})
    @SuppressWarnings("unused - motechEvent expected as parameter by cron invoker")
    public void fetchGroupDetailsInBatch(MotechEvent motechEvent) {
        logger.info("Handling group batch sync event");
        BatchRequestQuery batchQuery = (BatchRequestQuery) motechEvent.getParameters().get(EventConstants.BATCH_QUERY);
        commCareSyncService.fetchDetailsInBatch(batchQuery, BatchJobType.GROUP);
    }
}
