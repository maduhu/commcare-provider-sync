package org.motechproject.commcare.provider.sync.scheduler;

import org.motechproject.event.MotechEvent;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.domain.CronSchedulableJob;
import org.motechproject.server.config.SettingsFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.HashMap;

import static org.motechproject.commcare.provider.sync.constants.EventConstants.*;
import static org.motechproject.commcare.provider.sync.constants.PropertyConstants.GROUP_SYNC_CRON_EXPRESSION;
import static org.motechproject.commcare.provider.sync.constants.PropertyConstants.PROVIDER_SYNC_CRON_EXPRESSION;

@Component
@Lazy(false)
public class CommCareSyncScheduler {

    private static final Logger logger = LoggerFactory.getLogger("commcare-provider-sync");

    @Autowired
    public CommCareSyncScheduler(@Qualifier("providerSyncSettings") SettingsFacade providerSyncSettings, MotechSchedulerService motechSchedulerService) {
        String providerSyncCronExpression = providerSyncSettings.getProperty(PROVIDER_SYNC_CRON_EXPRESSION);
        logger.info(String.format("Setting up cron job for provider sync with cron expression %s", providerSyncCronExpression));
        scheduleCronJob(motechSchedulerService, providerSyncCronExpression, PROVIDER_SYNC_EVENT, PROVIDER_SYNC_JOB_ID_KEY);

        String groupSyncCronExpression = providerSyncSettings.getProperty(GROUP_SYNC_CRON_EXPRESSION);
        logger.info(String.format("Setting up cron job for group sync with cron expression %s", groupSyncCronExpression));
        scheduleCronJob(motechSchedulerService, groupSyncCronExpression, GROUP_SYNC_EVENT, GROUP_SYNC_JOB_ID_KEY);
    }

    private void scheduleCronJob(MotechSchedulerService motechSchedulerService, String cronExpression, String eventSubject, String jobIdKey) {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(MotechSchedulerService.JOB_ID_KEY, jobIdKey);
        CronSchedulableJob providerSyncCronJob = new CronSchedulableJob(new MotechEvent(eventSubject, parameters), cronExpression);
        motechSchedulerService.scheduleJob(providerSyncCronJob);
    }
}
