package org.motechproject.commcare.provider.sync.web.controller;

import org.motechproject.commcare.provider.sync.response.BatchJobType;
import org.motechproject.commcare.provider.sync.service.CommCareSyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/web-api/commcare-provider-sync/sync")
public class SyncController {
    private static final Logger logger = LoggerFactory.getLogger("commcare-provider-sync");

    private TaskExecutor taskExecutor;
    private CommCareSyncService commCareSyncService;

    @Autowired
    public SyncController(CommCareSyncService commCareSyncService, @Qualifier("syncTaskExecutor") TaskExecutor taskExecutor) {
        this.commCareSyncService = commCareSyncService;
        this.taskExecutor = taskExecutor;
    }

    @RequestMapping(value = "/provider", method = RequestMethod.GET)
    @ResponseBody
    public String syncProvider() {
        taskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                logger.info("Starting provider sync");
                commCareSyncService.startSync(BatchJobType.PROVIDER);
            }
        });
        return "provider sync started.";
    }

    @RequestMapping(value = "/group", method = RequestMethod.GET)
    @ResponseBody
    public String syncGroup() {
        taskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                logger.info("Starting group sync");
                commCareSyncService.startSync(BatchJobType.GROUP);
            }
        });
        return "group sync started.";
    }

}
