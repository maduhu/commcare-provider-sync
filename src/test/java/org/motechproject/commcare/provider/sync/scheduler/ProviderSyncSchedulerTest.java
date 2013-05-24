package org.motechproject.commcare.provider.sync.scheduler;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.commcare.provider.sync.constants.EventSubjects;
import org.motechproject.event.MotechEvent;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.domain.CronSchedulableJob;
import org.motechproject.server.config.SettingsFacade;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProviderSyncSchedulerTest {

    @Mock
    private SettingsFacade providerSyncSettings;
    @Mock
    private MotechSchedulerService motechSchedulerService;

    @Test
    public void shouldScheduleProviderSyncCronJob() {
        String cronExpression = "0 6 * * 0";
        String cronExpressionKey = "provider.sync.cron.expression";
        when(providerSyncSettings.getProperty(cronExpressionKey)).thenReturn(cronExpression);

        new ProviderSyncScheduler(providerSyncSettings, motechSchedulerService);

        verify(providerSyncSettings).getProperty(cronExpressionKey);
        ArgumentCaptor<CronSchedulableJob> cronJobCaptor = ArgumentCaptor.forClass(CronSchedulableJob.class);
        verify(motechSchedulerService).scheduleJob(cronJobCaptor.capture());
        CronSchedulableJob actualScheduledCronJob = cronJobCaptor.getValue();
        assertEquals(cronExpression, actualScheduledCronJob.getCronExpression());
        assertEquals(new MotechEvent(EventSubjects.FETCH_PROVIDER), actualScheduledCronJob.getMotechEvent());
    }
}
