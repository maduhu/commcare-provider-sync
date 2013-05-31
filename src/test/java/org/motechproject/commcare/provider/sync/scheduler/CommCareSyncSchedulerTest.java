package org.motechproject.commcare.provider.sync.scheduler;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.commcare.provider.sync.constants.EventConstants;
import org.motechproject.commcare.provider.sync.constants.PropertyConstants;
import org.motechproject.event.MotechEvent;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.domain.CronSchedulableJob;
import org.motechproject.server.config.SettingsFacade;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CommCareSyncSchedulerTest {

    @Mock
    private SettingsFacade providerSyncSettings;
    @Mock
    private MotechSchedulerService motechSchedulerService;

    @Test
    public void shouldScheduleProviderSyncCronJob() {
        String providerSyncCronExpression = "0 6 * * 0";
        String ownerSyncCronExpression = "0 6 * * 0";
        when(providerSyncSettings.getProperty(PropertyConstants.PROVIDER_SYNC_CRON_EXPRESSION)).thenReturn(providerSyncCronExpression);
        when(providerSyncSettings.getProperty(PropertyConstants.GROUP_SYNC_CRON_EXPRESSION)).thenReturn(ownerSyncCronExpression);

        new CommCareSyncScheduler(providerSyncSettings, motechSchedulerService);

        ArgumentCaptor<CronSchedulableJob> cronJobCaptor = ArgumentCaptor.forClass(CronSchedulableJob.class);
        verify(motechSchedulerService, times(2)).scheduleJob(cronJobCaptor.capture());
        List<CronSchedulableJob> actualScheduledCronJob = cronJobCaptor.getAllValues();
        assertEquals(2, actualScheduledCronJob.size());
        assertEquals(providerSyncCronExpression, actualScheduledCronJob.get(0).getCronExpression());
        assertEquals(new MotechEvent(EventConstants.COMMCARE_PROVIDER_SYNC_EVENT), actualScheduledCronJob.get(0).getMotechEvent());
        assertEquals(ownerSyncCronExpression, actualScheduledCronJob.get(1).getCronExpression());
        assertEquals(new MotechEvent(EventConstants.COMMCARE_GROUP_SYNC_EVENT), actualScheduledCronJob.get(1).getMotechEvent());
    }
}
