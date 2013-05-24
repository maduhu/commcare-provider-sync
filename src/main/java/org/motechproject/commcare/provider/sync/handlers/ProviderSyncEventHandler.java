package org.motechproject.commcare.provider.sync.handlers;

import org.motechproject.commcare.provider.sync.constants.EventSubjects;
import org.motechproject.commcare.provider.sync.service.CommCareHttpClientService;
import org.motechproject.event.listener.annotations.MotechListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProviderSyncEventHandler {

    CommCareHttpClientService commCareHttpClientService;

    @Autowired
    public ProviderSyncEventHandler(CommCareHttpClientService commCareHttpClientService) {
        this.commCareHttpClientService = commCareHttpClientService;
    }

    @MotechListener(subjects = {EventSubjects.FETCH_PROVIDER})
    public void handleProviderSync() {
        commCareHttpClientService.getProviderDetails();
    }
}
