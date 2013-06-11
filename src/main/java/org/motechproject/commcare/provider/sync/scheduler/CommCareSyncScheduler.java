package org.motechproject.commcare.provider.sync.scheduler;

import org.motechproject.commcare.provider.sync.constants.EventConstants;
import org.motechproject.commcare.provider.sync.constants.PropertyConstants;
import org.motechproject.event.MotechEvent;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.domain.CronSchedulableJob;
import org.motechproject.server.config.SettingsFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@Lazy(false)
public class CommCareSyncScheduler {

    @Autowired
    public CommCareSyncScheduler(@Qualifier("providerSyncSettings") SettingsFacade providerSyncSettings, MotechSchedulerService motechSchedulerService) {
        scheduleCronJob(motechSchedulerService, providerSyncSettings.getProperty(PropertyConstants.PROVIDER_SYNC_CRON_EXPRESSION), EventConstants.PROVIDER_SYNC_EVENT);
        scheduleCronJob(motechSchedulerService, providerSyncSettings.getProperty(PropertyConstants.GROUP_SYNC_CRON_EXPRESSION), EventConstants.GROUP_SYNC_EVENT);
    }

    private void scheduleCronJob(MotechSchedulerService motechSchedulerService, String cronExpression, String eventSubject) {
        CronSchedulableJob providerSyncCronJob = new CronSchedulableJob(new MotechEvent(eventSubject), cronExpression);
        motechSchedulerService.scheduleJob(providerSyncCronJob);
    }
}
