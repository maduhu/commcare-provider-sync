package org.motechproject.commcare.provider.sync.handlers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.commcare.provider.sync.constants.EventConstants;
import org.motechproject.commcare.provider.sync.response.Provider;
import org.motechproject.commcare.provider.sync.response.ProviderDetailsResponse;
import org.motechproject.commcare.provider.sync.service.CommCareHttpClientService;
import org.motechproject.commcare.provider.sync.service.EventPublishAction;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;

import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ProviderSyncEventHandlerTest {
    @Mock
    CommCareHttpClientService commCareHttpClientService;
    @Mock
    EventRelay eventRelay;

    @Test
    public void shouldHandleProviderSyncEventAndPublishProviderDetails() {
        ProviderSyncEventHandler providerSyncEventHandler = new ProviderSyncEventHandler(commCareHttpClientService, eventRelay);

        providerSyncEventHandler.handleProviderSync();

        ArgumentCaptor<EventPublishAction> eventPublishActionCaptor = ArgumentCaptor.forClass(EventPublishAction.class);
        verify(commCareHttpClientService).fetchAndPublishProviderDetails(eventPublishActionCaptor.capture());

        EventPublishAction actualEventPublishAction = eventPublishActionCaptor.getValue();
        ProviderDetailsResponse mockedProviderDetailsResponse = mock(ProviderDetailsResponse.class);
        when(mockedProviderDetailsResponse.hasNoProviders()).thenReturn(false);
        ArrayList<Provider> providers = new ArrayList<>();
        when(mockedProviderDetailsResponse.getProviders()).thenReturn(providers);

        actualEventPublishAction.publish(mockedProviderDetailsResponse);

        ArgumentCaptor<MotechEvent> motechEventCaptor = ArgumentCaptor.forClass(MotechEvent.class);
        verify(eventRelay).sendEventMessage(motechEventCaptor.capture());
        MotechEvent actualMotechEvent = motechEventCaptor.getValue();
        assertEquals(EventConstants.PROVIDER_DETAILS_EVENT, actualMotechEvent.getSubject());
        assertEquals(1, actualMotechEvent.getParameters().size());
        assertEquals(providers, actualMotechEvent.getParameters().get(EventConstants.PROVIDER_DETAILS));
    }
}
