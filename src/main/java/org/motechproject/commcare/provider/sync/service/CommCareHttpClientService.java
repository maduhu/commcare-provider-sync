package org.motechproject.commcare.provider.sync.service;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.motechproject.commcare.provider.sync.constants.PropertyConstants;
import org.motechproject.commcare.provider.sync.response.BaseResponse;
import org.motechproject.commcare.provider.sync.response.Meta;
import org.motechproject.commcare.provider.sync.response.ProviderDetailsResponse;
import org.motechproject.server.config.SettingsFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class CommCareHttpClientService {

    private RestTemplate restTemplate;
    private SettingsFacade providerSyncSettings;

    @Autowired
    public CommCareHttpClientService(@Qualifier("commcareRestTemplate") RestTemplate restTemplate,
                                     @Qualifier("providerSyncSettings") SettingsFacade providerSyncSettings) {
        this.providerSyncSettings = providerSyncSettings;
        this.restTemplate = restTemplate;
        setUpAuthentication();
    }

    private void setUpAuthentication() {
        Credentials credentials = new UsernamePasswordCredentials(providerSyncSettings.getProperty(PropertyConstants.USERNAME), providerSyncSettings.getProperty(PropertyConstants.PASSWORD));
        BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, credentials);

        HttpComponentsClientHttpRequestFactory requestFactory = (HttpComponentsClientHttpRequestFactory) restTemplate.getRequestFactory();
        DefaultHttpClient httpClient = (DefaultHttpClient) requestFactory.getHttpClient();
        httpClient.setCredentialsProvider(credentialsProvider);
    }

    public void fetchAndPublishProviderDetails(EventPublishAction eventPublishAction) {
        String url = providerSyncSettings.getProperty(PropertyConstants.COMMCARE_BASE_URL) + providerSyncSettings.getProperty(PropertyConstants.COMMCARE_GET_PROVIDER_LIST_API_URL);
        fetchAndPublish(url, providerSyncSettings.getProperty(PropertyConstants.PROVIDER_BATCH_SIZE), ProviderDetailsResponse.class, eventPublishAction);
    }

    private <T> void fetchAndPublish(String url, String batchSize, Class<T> responseType, EventPublishAction eventPublishAction) {
        BaseResponse baseResponse = new BaseResponse(new Meta(String.format(PropertyConstants.URL_PARAMS, batchSize)));
        while (baseResponse.hasMoreRecordsToFetch()) {
            String requestUrlWithParams = url + baseResponse.getMeta().getNextQuery();
            T response = getResponse(requestUrlWithParams, responseType);
            if (response == null)
                break;
            baseResponse = (BaseResponse) response;
            eventPublishAction.publish(baseResponse);
        }
    }

    private <T> T getResponse(String requestUrl, Class<T> responseType) {
        return restTemplate.getForEntity(requestUrl, responseType).getBody();
    }
}
