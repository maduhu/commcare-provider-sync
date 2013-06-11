package org.motechproject.commcare.provider.sync.service;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.commcare.provider.sync.constants.PropertyConstants;
import org.motechproject.commcare.provider.sync.response.BatchRequestQuery;
import org.motechproject.commcare.provider.sync.response.ProviderDetailsResponse;
import org.motechproject.server.config.SettingsFacade;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CommCareHttpClientServiceTest {
    @Mock
    private SettingsFacade providerSyncSettings;
    @Mock
    private RestTemplate restTemplate;

    private CommCareHttpClientService commCareHttpClientService;
    private String username = "username";
    private String password = "password";
    private String baseUrl = "baseurl";

    @Before
    public void setUp() {
        when(providerSyncSettings.getProperty(PropertyConstants.USERNAME)).thenReturn(username);
        when(providerSyncSettings.getProperty(PropertyConstants.PASSWORD)).thenReturn(password);
        when(providerSyncSettings.getProperty(PropertyConstants.COMMCARE_BASE_URL)).thenReturn(baseUrl);
        when(restTemplate.getRequestFactory()).thenReturn(new HttpComponentsClientHttpRequestFactory());
        commCareHttpClientService = new CommCareHttpClientService(restTemplate, providerSyncSettings);
    }

    @Test
    public void shouldSetUpAuthenticationOnConstruction() {
        BasicCredentialsProvider expectedCredentialsProvider = new BasicCredentialsProvider();
        expectedCredentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
        HttpComponentsClientHttpRequestFactory requestFactory = (HttpComponentsClientHttpRequestFactory) restTemplate.getRequestFactory();
        DefaultHttpClient httpClient = (DefaultHttpClient) requestFactory.getHttpClient();

        assertEquals(expectedCredentialsProvider.getCredentials(AuthScope.ANY), httpClient.getCredentialsProvider().getCredentials(AuthScope.ANY));
    }

    @Test
    public void shouldSendAGetRequestForBatchAndReturnTheResponse() {
        String listUrl = "listUrl";
        int offset = 12;
        int batchSize = 15;

        Class<ProviderDetailsResponse> responseType = ProviderDetailsResponse.class;
        ProviderDetailsResponse expectedResponse = new ProviderDetailsResponse();
        ResponseEntity<ProviderDetailsResponse> mockedResponseEntity = mock(ResponseEntity.class);
        when(restTemplate.getForEntity("baseurl/listUrl?offset=12&limit=15&format=json", responseType)).thenReturn(mockedResponseEntity);

        when(mockedResponseEntity.getBody()).thenReturn(expectedResponse);

        BatchRequestQuery batchRequestQuery = new BatchRequestQuery(offset);
        batchRequestQuery.setBatchSize(batchSize);

        ProviderDetailsResponse actualResponse = commCareHttpClientService.fetchBatch(listUrl, batchRequestQuery, responseType);

        assertEquals(expectedResponse, actualResponse);
    }
}
