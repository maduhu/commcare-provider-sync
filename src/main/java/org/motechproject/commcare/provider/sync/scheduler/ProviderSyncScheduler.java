package org.motechproject.commcare.provider.sync.scheduler;

import org.motechproject.commcare.provider.sync.constants.EventConstants;
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
public class ProviderSyncScheduler {

    private static final String CRON_EXPRESSION = "provider.sync.cron.expression";

    @Autowired
    public ProviderSyncScheduler(@Qualifier("providerSyncSettings") SettingsFacade providerSyncSettings, MotechSchedulerService motechSchedulerService) {
        String cronExpression = providerSyncSettings.getProperty(CRON_EXPRESSION);
        CronSchedulableJob cronJob = new CronSchedulableJob(new MotechEvent(EventConstants.COMMCARE_PROVIDER_SYNC_EVENT), cronExpression);
        motechSchedulerService.scheduleJob(cronJob);
    }
}
