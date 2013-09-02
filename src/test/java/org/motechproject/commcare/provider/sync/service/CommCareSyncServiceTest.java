package org.motechproject.commcare.provider.sync.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.commcare.provider.sync.constants.EventConstants;
import org.motechproject.commcare.provider.sync.response.BatchJobType;
import org.motechproject.commcare.provider.sync.response.BatchRequestQuery;
import org.motechproject.commcare.provider.sync.response.BatchResponseMetadata;
import org.motechproject.commcare.provider.sync.response.Group;
import org.motechproject.commcare.provider.sync.response.GroupDetailsResponse;
import org.motechproject.commcare.provider.sync.response.Provider;
import org.motechproject.commcare.provider.sync.response.ProviderDetailsResponse;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.server.config.SettingsFacade;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CommCareSyncServiceTest {
    @Mock
    private SettingsFacade providerSyncSettings;
    @Mock
    private CommCareHttpClientService commCareHttpClientService;
    @Mock
    private EventRelay eventRelay;

    private CommCareSyncService commcareSyncService;

    @Before
    public void setUp() {
        commcareSyncService = new CommCareSyncService(eventRelay, commCareHttpClientService, providerSyncSettings);
    }

    @Test
    public void shouldStartSync() {
        int batchSize = 45;
        String providerUrl = "providerListUrl";
        ProviderDetailsResponse providerDetailsResponse = mock(ProviderDetailsResponse.class);
        BatchResponseMetadata meta = mock(BatchResponseMetadata.class);
        final BatchRequestQuery nextBatchRequestQuery = new BatchRequestQuery(12);

        when(providerSyncSettings.getProperty("commcare.batch.size.provider")).thenReturn(Integer.toString(batchSize));
        when(providerSyncSettings.getProperty("commcare.list.url.provider")).thenReturn(providerUrl);

        when(commCareHttpClientService.fetchBatch(eq(providerUrl), any(BatchRequestQuery.class), eq(ProviderDetailsResponse.class))).thenReturn(providerDetailsResponse);

        final List<Provider> providers = Arrays.asList(new Provider(), new Provider());
        when(providerDetailsResponse.getRecords()).thenReturn(providers);
        when(providerDetailsResponse.getMeta()).thenReturn(meta);
        when(providerDetailsResponse.hasRecords()).thenReturn(true);
        when(meta.getNextBatchQuery(batchSize)).thenReturn(nextBatchRequestQuery);
        when(meta.hasNext()).thenReturn(true);

        commcareSyncService.startSync(BatchJobType.PROVIDER);

        ArgumentCaptor<BatchRequestQuery> batchRequestQueryArgumentCaptor = ArgumentCaptor.forClass(BatchRequestQuery.class);
        verify(commCareHttpClientService).fetchBatch(eq(providerUrl), batchRequestQueryArgumentCaptor.capture(), eq(ProviderDetailsResponse.class));
        BatchRequestQuery actualBatchRequestQuery = batchRequestQueryArgumentCaptor.getValue();
        assertEquals(0, actualBatchRequestQuery.getOffset());
        assertEquals(batchSize, actualBatchRequestQuery.getBatchSize());

        assertEventToBePublished(new MotechEvent(EventConstants.PROVIDER_DETAILS_EVENT, new HashMap<String, Object>() {{
            put(EventConstants.DETAILS_LIST, providers);
        }}), new MotechEvent(EventConstants.PROVIDER_FETCH_DETAILS_IN_BATCH_EVENT, new HashMap<String, Object>() {{
            put(EventConstants.BATCH_QUERY, nextBatchRequestQuery);
        }}));
    }

    @Test
    public void shouldFetchDetailsInBatch() {
        int batchSize = 45;
        String providerUrl = "providerListUrl";
        BatchRequestQuery batchRequestQuery = mock(BatchRequestQuery.class);
        ProviderDetailsResponse providerDetailsResponse = mock(ProviderDetailsResponse.class);
        BatchResponseMetadata meta = mock(BatchResponseMetadata.class);
        final BatchRequestQuery nextBatchRequestQuery = new BatchRequestQuery(12);

        when(providerSyncSettings.getProperty("commcare.batch.size.provider")).thenReturn(Integer.toString(batchSize));
        when(providerSyncSettings.getProperty("commcare.list.url.provider")).thenReturn(providerUrl);
        when(commCareHttpClientService.fetchBatch(providerUrl, batchRequestQuery, ProviderDetailsResponse.class)).thenReturn(providerDetailsResponse);

        final List<Provider> providers = Arrays.asList(new Provider(), new Provider());
        when(providerDetailsResponse.getRecords()).thenReturn(providers);
        when(providerDetailsResponse.getMeta()).thenReturn(meta);
        when(providerDetailsResponse.hasRecords()).thenReturn(true);
        when(meta.getNextBatchQuery(batchSize)).thenReturn(nextBatchRequestQuery);
        when(meta.hasNext()).thenReturn(true);

        commcareSyncService.fetchDetailsInBatch(batchRequestQuery, BatchJobType.PROVIDER);

        verify(batchRequestQuery).setBatchSize(batchSize);

        assertEventToBePublished(new MotechEvent(EventConstants.PROVIDER_DETAILS_EVENT, new HashMap<String, Object>() {{
            put(EventConstants.DETAILS_LIST, providers);
        }}), new MotechEvent(EventConstants.PROVIDER_FETCH_DETAILS_IN_BATCH_EVENT, new HashMap<String, Object>() {{
            put(EventConstants.BATCH_QUERY, nextBatchRequestQuery);
        }}));
    }

    @Test
    public void shouldNotRaiseDetailsEventIfResponseHasNoRecords() {
        int batchSize = 45;
        String groupUrl = "providerListUrl";
        BatchRequestQuery batchRequestQuery = mock(BatchRequestQuery.class);
        GroupDetailsResponse groupDetailsResponse = mock(GroupDetailsResponse.class);
        BatchResponseMetadata meta = mock(BatchResponseMetadata.class);
        final BatchRequestQuery nextBatchRequestQuery = new BatchRequestQuery(12);

        when(providerSyncSettings.getProperty("commcare.batch.size.group")).thenReturn(Integer.toString(batchSize));
        when(providerSyncSettings.getProperty("commcare.list.url.group")).thenReturn(groupUrl);
        when(commCareHttpClientService.fetchBatch(groupUrl, batchRequestQuery, GroupDetailsResponse.class)).thenReturn(groupDetailsResponse);

        when(groupDetailsResponse.getRecords()).thenReturn(new ArrayList<Group>());
        when(groupDetailsResponse.hasRecords()).thenReturn(false);
        when(groupDetailsResponse.getMeta()).thenReturn(meta);
        when(meta.getNextBatchQuery(batchSize)).thenReturn(nextBatchRequestQuery);
        when(meta.hasNext()).thenReturn(true);

        commcareSyncService.fetchDetailsInBatch(batchRequestQuery, BatchJobType.GROUP);

        verify(batchRequestQuery).setBatchSize(batchSize);

        assertEventToBePublished(new MotechEvent(EventConstants.GROUP_FETCH_DETAILS_IN_BATCH_EVENT, new HashMap<String, Object>() {{
            put(EventConstants.BATCH_QUERY, nextBatchRequestQuery);
        }}));
        verifyNoMoreInteractions(eventRelay);
    }

    @Test
    public void shouldNotRaiseNextBatchEventIfNextQueryParamInResponseIsNull() {
        int batchSize = 45;
        String providerUrl = "providerListUrl";
        BatchRequestQuery batchRequestQuery = mock(BatchRequestQuery.class);
        ProviderDetailsResponse providerDetailsResponse = mock(ProviderDetailsResponse.class);
        BatchResponseMetadata meta = mock(BatchResponseMetadata.class);
        final BatchRequestQuery nextBatchRequestQuery = new BatchRequestQuery(12);

        when(providerSyncSettings.getProperty("commcare.batch.size.provider")).thenReturn(Integer.toString(batchSize));
        when(providerSyncSettings.getProperty("commcare.list.url.provider")).thenReturn(providerUrl);
        when(commCareHttpClientService.fetchBatch(providerUrl, batchRequestQuery, ProviderDetailsResponse.class)).thenReturn(providerDetailsResponse);

        final List<Provider> providers = Arrays.asList(new Provider(), new Provider());
        when(providerDetailsResponse.getRecords()).thenReturn(providers);
        when(providerDetailsResponse.getMeta()).thenReturn(meta);
        when(providerDetailsResponse.hasRecords()).thenReturn(true);
        when(meta.getNextBatchQuery(batchSize)).thenReturn(nextBatchRequestQuery);
        when(meta.hasNext()).thenReturn(false);

        commcareSyncService.fetchDetailsInBatch(batchRequestQuery, BatchJobType.PROVIDER);

        verify(batchRequestQuery).setBatchSize(batchSize);

        assertEventToBePublished(new MotechEvent(EventConstants.PROVIDER_DETAILS_EVENT, new HashMap<String, Object>() {{
            put(EventConstants.DETAILS_LIST, providers);
        }}));
        verifyNoMoreInteractions(eventRelay);
    }

    private void assertEventToBePublished(MotechEvent... expectedEvents) {
        ArgumentCaptor<MotechEvent> motechEventCaptor = ArgumentCaptor.forClass(MotechEvent.class);
        verify(eventRelay, times(expectedEvents.length)).sendEventMessage(motechEventCaptor.capture());
        List<MotechEvent> actualEvents = motechEventCaptor.getAllValues();
        int counter = 0;
        for (MotechEvent actualEvent : actualEvents) {
            assertEquals(actualEvent, expectedEvents[counter]);
            counter++;
        }
    }
}
