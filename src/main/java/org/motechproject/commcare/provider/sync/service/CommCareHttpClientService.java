package org.motechproject.commcare.provider.sync.service;

import org.apache.commons.lang.StringUtils;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.motechproject.commcare.provider.sync.response.ProviderDetailsResponse;
import org.motechproject.server.config.SettingsFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

@Component
public class CommCareHttpClientService {

    private RestTemplate restTemplate;
    private SettingsFacade providerSyncSettings;

    private static final String USERNAME = "commcare.authentication.username";
    private static final String PASSWORD = "commcare.authentication.password";
    private static final String COMMCARE_BASE_URL = "commcare.base.url";
    private static final String COMMCARE_GET_PROVIDER_LIST_API_URL = "commcare.get.provider.list.api.url";

    @Autowired
    public CommCareHttpClientService(@Qualifier("commcareRestTemplate") RestTemplate restTemplate, @Qualifier("providerSyncSettings") SettingsFacade providerSyncSettings) {
        this.providerSyncSettings = providerSyncSettings;
        this.restTemplate = restTemplate;
        setUpAuthentication();
    }

    private void setUpAuthentication() {
        Credentials credentials = new UsernamePasswordCredentials(providerSyncSettings.getProperty(USERNAME), providerSyncSettings.getProperty(PASSWORD));
        BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, credentials);

        HttpComponentsClientHttpRequestFactory requestFactory = (HttpComponentsClientHttpRequestFactory) restTemplate.getRequestFactory();
        DefaultHttpClient httpClient = (DefaultHttpClient) requestFactory.getHttpClient();
        httpClient.setCredentialsProvider(credentialsProvider);
    }

    public ProviderDetailsResponse getProviderDetails() {
        String url = getUrl(providerSyncSettings.getProperty(COMMCARE_BASE_URL), providerSyncSettings.getProperty(COMMCARE_GET_PROVIDER_LIST_API_URL));
        return getRequest(url, ProviderDetailsResponse.class);
    }

    private String getUrl(String baseUrl, String apiUrl) {
        return String.format("%s/%s", StringUtils.removeEnd(baseUrl, "/"), StringUtils.removeStart(apiUrl, "/"));
    }

    private <T> T getRequest(String requestUrl, Class<T> responseType) {
        return restTemplate.getForEntity(requestUrl, responseType, getUrlVariables()).getBody();
    }

    private HashMap<String, String> getUrlVariables() {
        return new HashMap<String, String>() {{
            put("format", "json");
            put("limit", providerSyncSettings.getProperty("commcare.api.response.limit"));
            put("offset", providerSyncSettings.getProperty("commcare.api.response.offset"));
        }};
    }
}
