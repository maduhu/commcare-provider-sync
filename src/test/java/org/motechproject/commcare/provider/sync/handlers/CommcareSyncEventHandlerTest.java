package org.motechproject.commcare.provider.sync.handlers;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.commcare.provider.sync.constants.EventConstants;
import org.motechproject.commcare.provider.sync.response.Group;
import org.motechproject.commcare.provider.sync.response.GroupDetailsResponse;
import org.motechproject.commcare.provider.sync.response.Provider;
import org.motechproject.commcare.provider.sync.response.ProviderDetailsResponse;
import org.motechproject.commcare.provider.sync.service.CommCareSyncService;
import org.motechproject.commcare.provider.sync.service.EventPublishAction;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;

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
    public void shouldHandleProviderSyncEventAndPublishProviderDetails() {
        commCareSyncEventHandler.handleProviderSync();

        ArgumentCaptor<EventPublishAction> eventPublishActionCaptor = ArgumentCaptor.forClass(EventPublishAction.class);
        verify(commCareSyncService).fetchAndPublishProviderDetails(eventPublishActionCaptor.capture());

        EventPublishAction actualEventPublishAction = eventPublishActionCaptor.getValue();
        ProviderDetailsResponse mockedProviderDetailsResponse = mock(ProviderDetailsResponse.class);
        when(mockedProviderDetailsResponse.hasNoProviders()).thenReturn(false);
        ArrayList<Provider> providers = new ArrayList<>();
        when(mockedProviderDetailsResponse.getProviders()).thenReturn(providers);

        actualEventPublishAction.publish(mockedProviderDetailsResponse);

        assertEventToBePublished(providers, EventConstants.PROVIDER_DETAILS_EVENT, EventConstants.PROVIDER_DETAILS);
    }

    @Test
    public void shouldHandleGroupSyncEventAndPublishGroupDetails() {
        commCareSyncEventHandler.handleGroupSync();

        ArgumentCaptor<EventPublishAction> eventPublishActionCaptor = ArgumentCaptor.forClass(EventPublishAction.class);
        verify(commCareSyncService).fetchAndPublishGroupDetails(eventPublishActionCaptor.capture());

        EventPublishAction actualEventPublishAction = eventPublishActionCaptor.getValue();
        GroupDetailsResponse mockedGroupDetailResponse = mock(GroupDetailsResponse.class);
        when(mockedGroupDetailResponse.hasNoGroups()).thenReturn(false);
        ArrayList<Group> groups = new ArrayList<>();
        when(mockedGroupDetailResponse.getGroups()).thenReturn(groups);

        actualEventPublishAction.publish(mockedGroupDetailResponse);

        assertEventToBePublished(groups, EventConstants.GROUP_DETAILS_EVENT, EventConstants.GROUP_DETAILS);
    }

    private void assertEventToBePublished(List details, String eventSubject, String eventParameterKey) {
        ArgumentCaptor<MotechEvent> motechEventCaptor = ArgumentCaptor.forClass(MotechEvent.class);
        verify(eventRelay).sendEventMessage(motechEventCaptor.capture());
        MotechEvent actualMotechEvent = motechEventCaptor.getValue();
        assertEquals(eventSubject, actualMotechEvent.getSubject());
        assertEquals(1, actualMotechEvent.getParameters().size());
        assertEquals(details, actualMotechEvent.getParameters().get(eventParameterKey));
    }
}
