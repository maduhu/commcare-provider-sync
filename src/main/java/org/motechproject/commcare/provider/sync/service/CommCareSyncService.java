package org.motechproject.commcare.provider.sync.service;

import org.motechproject.commcare.provider.sync.constants.PropertyConstants;
import org.motechproject.commcare.provider.sync.response.BaseResponse;
import org.motechproject.commcare.provider.sync.response.GroupDetailsResponse;
import org.motechproject.commcare.provider.sync.response.Meta;
import org.motechproject.commcare.provider.sync.response.ProviderDetailsResponse;
import org.motechproject.server.config.SettingsFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class CommCareSyncService {
    private CommCareHttpClientService commCareHttpClientService;
    private SettingsFacade providerSyncSettings;

    @Autowired
    public CommCareSyncService(CommCareHttpClientService commCareHttpClientService, @Qualifier("providerSyncSettings") SettingsFacade providerSyncSettings) {
        this.commCareHttpClientService = commCareHttpClientService;
        this.providerSyncSettings = providerSyncSettings;
    }

    public void fetchAndPublishProviderDetails(EventPublishAction eventPublishAction) {
        String url = getUrl(providerSyncSettings.getProperty(PropertyConstants.COMMCARE_BASE_URL), providerSyncSettings.getProperty(PropertyConstants.COMMCARE_GET_PROVIDER_LIST_API_URL));
        fetchAndPublish(url, providerSyncSettings.getProperty(PropertyConstants.PROVIDER_BATCH_SIZE), ProviderDetailsResponse.class, eventPublishAction);
    }

    public void fetchAndPublishGroupDetails(EventPublishAction eventPublishAction) {
        String url = getUrl(providerSyncSettings.getProperty(PropertyConstants.COMMCARE_BASE_URL), providerSyncSettings.getProperty(PropertyConstants.COMMCARE_GET_GROUP_LIST_API_URL));
        fetchAndPublish(url, providerSyncSettings.getProperty(PropertyConstants.GROUP_BATCH_SIZE), GroupDetailsResponse.class, eventPublishAction);
    }

    private <T> void fetchAndPublish(String url, String batchSize, Class<T> responseType, EventPublishAction eventPublishAction) {
        BaseResponse baseResponse = new BaseResponse(new Meta(String.format(PropertyConstants.URL_PARAMS, batchSize)));
        while (baseResponse.hasMoreRecordsToFetch()) {
            String requestUrlWithParams = url + baseResponse.getMeta().getNextQuery();
            T response = commCareHttpClientService.getResponse(requestUrlWithParams, responseType);
            if (response == null)
                break;
            baseResponse = (BaseResponse) response;
            eventPublishAction.publish(baseResponse);
        }
    }

    private String getUrl(String baseUrl, String apiUrl) {
        return String.format("%s%s", baseUrl, apiUrl);
    }
}
