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
import org.motechproject.commcare.provider.sync.response.ProviderDetailsResponse;
import org.motechproject.server.config.SettingsFacade;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

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
        when(providerSyncSettings.getProperty("commcare.authentication.username")).thenReturn(username);
        when(providerSyncSettings.getProperty("commcare.authentication.password")).thenReturn(password);
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
    public void shouldGetProviderDetails() {
        String expectedURL = "baseurl/apiurl";
        final String limit = "0";
        final String offset = "0";
        HashMap<String, String> expectedURLVariables = new HashMap<String, String>() {{
            put("format", "json");
            put("limit", limit);
            put("offset", offset);
        }};
        when(providerSyncSettings.getProperty("commcare.base.url")).thenReturn("baseurl/");
        when(providerSyncSettings.getProperty("commcare.get.provider.list.api.url")).thenReturn("apiurl");
        when(providerSyncSettings.getProperty("commcare.api.response.limit")).thenReturn(limit);
        when(providerSyncSettings.getProperty("commcare.api.response.offset")).thenReturn(offset);

        ResponseEntity<ProviderDetailsResponse> mockedResponseEntity = mock(ResponseEntity.class);
        when(restTemplate.getForEntity(expectedURL, ProviderDetailsResponse.class, expectedURLVariables)).thenReturn(mockedResponseEntity);
        when(mockedResponseEntity.getBody()).thenReturn(TestUtils.fromJson(getResponseString(), ProviderDetailsResponse.class));

        ProviderDetailsResponse actualProviderDetails = commcareHttpClientService.getProviderDetails();

        verify(restTemplate).getForEntity(expectedURL, ProviderDetailsResponse.class, expectedURLVariables);
        assertEquals(actualProviderDetails, TestUtils.fromJson(getResponseString(), ProviderDetailsResponse.class));
    }

    @Test
    public void shouldGetCorrectURL() {
        when(providerSyncSettings.getProperty("commcare.base.url")).thenReturn("baseurl/");
        when(providerSyncSettings.getProperty("commcare.get.provider.list.api.url")).thenReturn("/apiurl");
        when(providerSyncSettings.getProperty("commcare.api.response.limit")).thenReturn("0");
        when(providerSyncSettings.getProperty("commcare.api.response.offset")).thenReturn("0");
        String expectedUrlToBeCalled = "baseurl/apiurl";
        ResponseEntity responseEntity = mock(ResponseEntity.class);
        when(restTemplate.getForEntity(eq(expectedUrlToBeCalled), any(Class.class), anyMap())).thenReturn(responseEntity);
        when(responseEntity.getBody()).thenReturn(new ProviderDetailsResponse());

        commcareHttpClientService.getProviderDetails();

        verify(restTemplate).getForEntity(eq(expectedUrlToBeCalled), any(Class.class), anyMap());

    }

    private String getResponseString() {
        return "{\n" +
                "    \"meta\": {\n" +
                "        \"limit\": 1,\n" +
                "        \"next\": \"?offset=1&limit=1&format=json\",\n" +
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
}
