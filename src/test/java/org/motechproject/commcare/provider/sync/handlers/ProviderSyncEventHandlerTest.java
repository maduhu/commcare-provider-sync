package org.motechproject.commcare.provider.sync.handlers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.commcare.provider.sync.service.CommCareHttpClientService;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ProviderSyncEventHandlerTest {
    @Mock
    CommCareHttpClientService commCareHttpClientService;

    @Test
    public void shouldHandleProviderSyncEvent() {
        ProviderSyncEventHandler providerSyncEventHandler = new ProviderSyncEventHandler(commCareHttpClientService);

        providerSyncEventHandler.handleProviderSync();

        verify(commCareHttpClientService).getProviderDetails();
    }
}
