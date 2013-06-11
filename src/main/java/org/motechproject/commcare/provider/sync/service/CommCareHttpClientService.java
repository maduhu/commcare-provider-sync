package org.motechproject.commcare.provider.sync.service;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.motechproject.commcare.provider.sync.constants.PropertyConstants;
import org.motechproject.commcare.provider.sync.response.BatchRequestQuery;
import org.motechproject.server.config.SettingsFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class CommCareHttpClientService {
    private static final Logger logger = LoggerFactory.getLogger("commcare-provider-sync");
    private RestTemplate restTemplate;
    private SettingsFacade providerSyncSettings;

    @Autowired
    public CommCareHttpClientService(@Qualifier("commCareRestTemplate") RestTemplate restTemplate,
                                     @Qualifier("providerSyncSettings") SettingsFacade providerSyncSettings) {
        this.providerSyncSettings = providerSyncSettings;
        this.restTemplate = restTemplate;
        setUpAuthentication();
    }

    private void setUpAuthentication() {
        logger.info("Setting up authentication for CommCareHQ http client");
        Credentials credentials = new UsernamePasswordCredentials(providerSyncSettings.getProperty(PropertyConstants.USERNAME), providerSyncSettings.getProperty(PropertyConstants.PASSWORD));
        BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, credentials);

        HttpComponentsClientHttpRequestFactory requestFactory = (HttpComponentsClientHttpRequestFactory) restTemplate.getRequestFactory();
        DefaultHttpClient httpClient = (DefaultHttpClient) requestFactory.getHttpClient();
        httpClient.setCredentialsProvider(credentialsProvider);
    }

    private <T> T getResponse(String requestUrl, Class<T> responseType) {
        String completeUrl = completeUrl(requestUrl);
        logger.info(String.format("Sending http GET request, request url: %s", completeUrl));
        return restTemplate.getForEntity(completeUrl, responseType).getBody();
    }

    public <T> T fetchBatch(String listUrl, BatchRequestQuery batchRequestQuery, Class<T> responseType) {
        String requestUrlWithParams = listUrl + batchRequestQuery.toQuery(PropertyConstants.URL_PARAMS);
        return getResponse(requestUrlWithParams, responseType);
    }

    private String completeUrl(String relativeUrl) {
        return String.format("%s/%s", providerSyncSettings.getProperty(PropertyConstants.COMMCARE_BASE_URL), relativeUrl);
    }
}
