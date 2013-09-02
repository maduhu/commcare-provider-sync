package org.motechproject.commcare.provider.sync.handlers;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.commcare.provider.sync.constants.EventConstants;
import org.motechproject.commcare.provider.sync.response.BatchJobType;
import org.motechproject.commcare.provider.sync.response.BatchRequestQuery;
import org.motechproject.commcare.provider.sync.service.CommCareSyncService;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CommCareSyncEventHandlerTest {
    @Mock
    CommCareSyncService commCareSyncService;
    @Mock
    EventRelay eventRelay;

    private CommCareSyncEventHandler commCareSyncEventHandler;

    @Before
    public void setUp() {
        commCareSyncEventHandler = new CommCareSyncEventHandler(commCareSyncService, eventRelay);
    }

    @Test
    public void shouldHandleProviderSyncEvent() {
        commCareSyncEventHandler.handleProviderSync(null);

        verify(commCareSyncService).startSync(BatchJobType.PROVIDER);
    }

    @Test
    public void shouldHandleGroupSyncEvent() {
        commCareSyncEventHandler.handleGroupSync(null);

        verify(commCareSyncService).startSync(BatchJobType.GROUP);
    }

    @Test
    public void shouldFetchProviderDetailsInBatch() {
        BatchRequestQuery batchRequestQuery = new BatchRequestQuery(12);
        MotechEvent event = new MotechEvent();
        event.getParameters().put(EventConstants.BATCH_QUERY, batchRequestQuery);

        commCareSyncEventHandler.fetchProviderDetailsInBatch(event);

        verify(commCareSyncService).fetchDetailsInBatch(batchRequestQuery, BatchJobType.PROVIDER);
    }

    @Test
    public void shouldFetchGroupDetailsInBatch() {
        BatchRequestQuery batchRequestQuery = new BatchRequestQuery(12);
        MotechEvent event = new MotechEvent();
        event.getParameters().put(EventConstants.BATCH_QUERY, batchRequestQuery);

        commCareSyncEventHandler.fetchGroupDetailsInBatch(event);

        verify(commCareSyncService).fetchDetailsInBatch(batchRequestQuery, BatchJobType.GROUP);
    }
}
