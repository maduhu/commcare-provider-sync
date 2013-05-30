package org.motechproject.commcare.provider.sync.service;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.motechproject.commcare.provider.sync.constants.PropertyConstants;
import org.motechproject.server.config.SettingsFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

public class CommCareHttpClientService {
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
        Credentials credentials = new UsernamePasswordCredentials(providerSyncSettings.getProperty(PropertyConstants.USERNAME), providerSyncSettings.getProperty(PropertyConstants.PASSWORD));
        BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, credentials);

        HttpComponentsClientHttpRequestFactory requestFactory = (HttpComponentsClientHttpRequestFactory) restTemplate.getRequestFactory();
        DefaultHttpClient httpClient = (DefaultHttpClient) requestFactory.getHttpClient();
        httpClient.setCredentialsProvider(credentialsProvider);
    }

    public <T> T getResponse(String requestUrl, Class<T> responseType) {
        return restTemplate.getForEntity(requestUrl, responseType).getBody();
    }
}
