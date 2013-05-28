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
import org.motechproject.commcare.provider.sync.TestUtils;
import org.motechproject.commcare.provider.sync.constants.PropertyConstants;
import org.motechproject.commcare.provider.sync.response.BaseResponse;
import org.motechproject.commcare.provider.sync.response.ProviderDetailsResponse;
import org.motechproject.server.config.SettingsFacade;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CommCareHttpClientServiceTest {
    @Mock
    private SettingsFacade providerSyncSettings;
    @Mock
    private RestTemplate restTemplate;

    private CommCareHttpClientService commcareHttpClientService;
    private String username = "username";
    private String password = "password";

    @Before
    public void setUp() {
        when(providerSyncSettings.getProperty(PropertyConstants.USERNAME)).thenReturn(username);
        when(providerSyncSettings.getProperty(PropertyConstants.PASSWORD)).thenReturn(password);
        when(restTemplate.getRequestFactory()).thenReturn(new HttpComponentsClientHttpRequestFactory());
        commcareHttpClientService = new CommCareHttpClientService(restTemplate, providerSyncSettings);
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
    public void shouldGetProviderDetailsBySendingMultipleRequestsBasedOnResponseAndPublishIt() {
        String baseUrl = "baseurl/";
        String apiUrl = "apiurl";
        String batchSize = "100";
        TestEventPublishAction testEventPublishAction = new TestEventPublishAction();
        when(providerSyncSettings.getProperty(PropertyConstants.COMMCARE_BASE_URL)).thenReturn(baseUrl);
        when(providerSyncSettings.getProperty(PropertyConstants.COMMCARE_GET_PROVIDER_LIST_API_URL)).thenReturn(apiUrl);
        when(providerSyncSettings.getProperty(PropertyConstants.PROVIDER_BATCH_SIZE)).thenReturn(batchSize);

        String urlForFirstRequest = String.format("%s%s?offset=0&limit=%s&format=json", baseUrl, apiUrl, batchSize);
        ResponseEntity<ProviderDetailsResponse> mockedResponseEntity1 = mock(ResponseEntity.class);
        when(restTemplate.getForEntity(urlForFirstRequest, ProviderDetailsResponse.class)).thenReturn(mockedResponseEntity1);
        ProviderDetailsResponse responseForFirstRequest = TestUtils.fromJson(getResponseString(), ProviderDetailsResponse.class);
        when(mockedResponseEntity1.getBody()).thenReturn(responseForFirstRequest);

        String urlForSecondRequest = String.format("%s%s?offset=100&limit=%s&format=json", baseUrl, apiUrl, batchSize);
        ResponseEntity<ProviderDetailsResponse> mockedResponseEntity2 = mock(ResponseEntity.class);
        when(restTemplate.getForEntity(urlForSecondRequest, ProviderDetailsResponse.class)).thenReturn(mockedResponseEntity2);
        ProviderDetailsResponse responseForSecondRequest = TestUtils.fromJson(getResponseString().replace("\"next\": \"?offset=100&limit=100&format=json\"", "\"next\": null"), ProviderDetailsResponse.class);
        when(mockedResponseEntity2.getBody()).thenReturn(responseForSecondRequest);

        commcareHttpClientService.fetchAndPublishProviderDetails(testEventPublishAction);

        verify(restTemplate).getForEntity(urlForFirstRequest, ProviderDetailsResponse.class);
        verify(restTemplate).getForEntity(urlForSecondRequest, ProviderDetailsResponse.class);
        verify(restTemplate, times(2)).getForEntity(anyString(), any(Class.class));

        assertEquals(2, testEventPublishAction.getPublishCount());
    }

    @Test
    public void shouldStopGettingProviderDetailsIfResponseIsNull() {
        String baseUrl = "baseurl";
        String apiUrl = "/apiurl";
        String batchSize = "100";
        TestEventPublishAction testEventPublishAction = new TestEventPublishAction();
        when(providerSyncSettings.getProperty(PropertyConstants.COMMCARE_BASE_URL)).thenReturn(baseUrl);
        when(providerSyncSettings.getProperty(PropertyConstants.COMMCARE_GET_PROVIDER_LIST_API_URL)).thenReturn(apiUrl);
        when(providerSyncSettings.getProperty(PropertyConstants.PROVIDER_BATCH_SIZE)).thenReturn(batchSize);

        String url = String.format("%s%s?offset=0&limit=%s&format=json", baseUrl, apiUrl, batchSize);
        ResponseEntity<ProviderDetailsResponse> mockedResponseEntity = mock(ResponseEntity.class);
        when(restTemplate.getForEntity(url, ProviderDetailsResponse.class)).thenReturn(mockedResponseEntity);
        when(mockedResponseEntity.getBody()).thenReturn(null);

        commcareHttpClientService.fetchAndPublishProviderDetails(testEventPublishAction);

        verify(restTemplate).getForEntity(url, ProviderDetailsResponse.class);
        verify(restTemplate, times(1)).getForEntity(anyString(), any(Class.class));

        assertEquals(0, testEventPublishAction.getPublishCount());
    }

    private String getResponseString() {
        return "{\n" +
                "    \"meta\": {\n" +
                "        \"limit\": 1,\n" +
                "        \"next\": \"?offset=100&limit=100&format=json\",\n" +
                "        \"offset\": 0,\n" +
                "        \"previous\": null,\n" +
                "        \"total_count\": 611\n" +
                "    },\n" +
                "    \"objects\": [\n" +
                "        {\n" +
                "            \"default_phone_number\": \"8294168471\",\n" +
                "            \"email\": \"\",\n" +
                "            \"first_name\": \"Dr.Pramod\",\n" +
                "            \"groups\": [\n" +
                "                \"89fda0284e008d2e0c980fb13fc18199\"\n" +
                "            ],\n" +
                "            \"id\": \"b0645df855266f29849eb2515b5ed57c\",\n" +
                "            \"last_name\": \"Kumar Gautam\",\n" +
                "            \"phone_numbers\": [\n" +
                "                \"8294168471\"\n" +
                "            ],\n" +
                "            \"resource_uri\": \"\",\n" +
                "            \"user_data\": {\n" +
                "                \"asset-id\": \"MP818\",\n" +
                "                \"block\": \"Sonbarsa\"\n" +
                "            },\n" +
                "            \"username\": \"8294168471@care-bihar.commcarehq.org\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";
    }

    private class TestEventPublishAction implements EventPublishAction {
        private int publishCount;

        @Override
        public void publish(BaseResponse baseResponse) {
            publishCount++;
        }

        public int getPublishCount() {
            return publishCount;
        }
    }
}
