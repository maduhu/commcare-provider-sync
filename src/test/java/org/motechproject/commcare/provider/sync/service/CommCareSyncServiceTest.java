package org.motechproject.commcare.provider.sync.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.commcare.provider.sync.TestUtils;
import org.motechproject.commcare.provider.sync.constants.PropertyConstants;
import org.motechproject.commcare.provider.sync.response.BaseResponse;
import org.motechproject.commcare.provider.sync.response.GroupDetailsResponse;
import org.motechproject.commcare.provider.sync.response.ProviderDetailsResponse;
import org.motechproject.server.config.SettingsFacade;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CommCareSyncServiceTest {
    @Mock
    private SettingsFacade providerSyncSettings;
    @Mock
    private CommCareHttpClientService commCareHttpClientService;

    private CommCareSyncService commcareSyncService;

    @Before
    public void setUp() {
        commcareSyncService = new CommCareSyncService(commCareHttpClientService, providerSyncSettings);
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
        String urlForSecondRequest = String.format("%s%s?offset=100&limit=%s&format=json", baseUrl, apiUrl, batchSize);
        setUpResponse(urlForFirstRequest, urlForSecondRequest, ProviderDetailsResponse.class, getProviderResponseString());

        commcareSyncService.fetchAndPublishProviderDetails(testEventPublishAction);

        verifyHttpClientInteraction(testEventPublishAction, urlForFirstRequest, urlForSecondRequest, ProviderDetailsResponse.class);
    }

    @Test
    public void shouldGetGroupDetailsBySendingMultipleRequestsBasedOnResponseAndPublishIt() {
        String baseUrl = "baseurl/";
        String apiUrl = "apiurl";
        String batchSize = "100";
        TestEventPublishAction testEventPublishAction = new TestEventPublishAction();
        when(providerSyncSettings.getProperty(PropertyConstants.COMMCARE_BASE_URL)).thenReturn(baseUrl);
        when(providerSyncSettings.getProperty(PropertyConstants.COMMCARE_GET_GROUP_LIST_API_URL)).thenReturn(apiUrl);
        when(providerSyncSettings.getProperty(PropertyConstants.GROUP_BATCH_SIZE)).thenReturn(batchSize);

        String urlForFirstRequest = String.format("%s%s?offset=0&limit=%s&format=json", baseUrl, apiUrl, batchSize);
        String urlForSecondRequest = String.format("%s%s?offset=100&limit=%s&format=json", baseUrl, apiUrl, batchSize);
        setUpResponse(urlForFirstRequest, urlForSecondRequest, GroupDetailsResponse.class, getGroupResponseString());

        commcareSyncService.fetchAndPublishGroupDetails(testEventPublishAction);

        verifyHttpClientInteraction(testEventPublishAction, urlForFirstRequest, urlForSecondRequest, GroupDetailsResponse.class);
    }

    private <T> void setUpResponse(String urlForFirstRequest, String urlForSecondRequest, Class<T> responseType, String responseString) {
        T responseForFirstRequest = TestUtils.fromJson(responseString, responseType);
        when(commCareHttpClientService.getResponse(urlForFirstRequest, responseType)).thenReturn(responseForFirstRequest);

        T responseForSecondRequest = TestUtils.fromJson(responseString.replace("\"next\": \"?offset=100&limit=100&format=json\"", "\"next\": null"), responseType);
        when(commCareHttpClientService.getResponse(urlForSecondRequest, responseType)).thenReturn(responseForSecondRequest);
    }

    private <T> void verifyHttpClientInteraction(TestEventPublishAction testEventPublishAction, String urlForFirstRequest, String urlForSecondRequest, Class<T> responseType) {
        verify(commCareHttpClientService).getResponse(urlForFirstRequest, responseType);
        verify(commCareHttpClientService).getResponse(urlForSecondRequest, responseType);
        verifyNoMoreInteractions(commCareHttpClientService);

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
        when(commCareHttpClientService.getResponse(url, ProviderDetailsResponse.class)).thenReturn(null);

        commcareSyncService.fetchAndPublishProviderDetails(testEventPublishAction);

        verify(commCareHttpClientService).getResponse(url, ProviderDetailsResponse.class);
        verifyNoMoreInteractions(commCareHttpClientService);

        assertEquals(0, testEventPublishAction.getPublishCount());
    }

    private String getProviderResponseString() {
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

    private String getGroupResponseString() {
        return "{\n" +
                "    \"meta\": {\n" +
                "        \"limit\": 1,\n" +
                "        \"next\": \"?offset=100&limit=100&format=json\",\n" +
                "        \"offset\": 0,\n" +
                "        \"previous\": null,\n" +
                "        \"total_count\": 410\n" +
                "    },\n" +
                "    \"objects\": [\n" +
                "        {\n" +
                "            \"case_sharing\": true,\n" +
                "            \"domain\": \"care-bihar\",\n" +
                "            \"id\": \"3c5a80e4db53049dfc110c368a0d05d4\",\n" +
                "            \"metadata\": {\n" +
                "                \"awc-code\": \"\"\n" +
                "            },\n" +
                "            \"name\": \"danny team 1\",\n" +
                "            \"path\": [],\n" +
                "            \"reporting\": true,\n" +
                "            \"resource_uri\": \"\",\n" +
                "            \"users\": []\n" +
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
