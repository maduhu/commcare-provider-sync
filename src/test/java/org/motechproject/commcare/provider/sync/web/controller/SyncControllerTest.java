package org.motechproject.commcare.provider.sync.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.commcare.provider.sync.response.BatchJobType;
import org.motechproject.commcare.provider.sync.service.CommCareSyncService;
import org.springframework.core.task.TaskExecutor;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class SyncControllerTest {

    @Mock
    private TaskExecutor taskExecutor;
    @Mock
    private CommCareSyncService commCareSyncService;
    private SyncController controller;


    @Before
    public void setUp() {
        initMocks(this);
        controller = new SyncController(commCareSyncService, taskExecutor);
    }

    @Test
    public void shouldStartProviderSync() {
        controller.syncProvider();

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(taskExecutor).execute(runnableCaptor.capture());

        Runnable actualRunnable = runnableCaptor.getValue();

        actualRunnable.run();

        verify(commCareSyncService).startSync(BatchJobType.PROVIDER);
    }

    @Test
    public void shouldStartGroupSync() {
        controller.syncGroup();

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(taskExecutor).execute(runnableCaptor.capture());

        Runnable actualRunnable = runnableCaptor.getValue();

        actualRunnable.run();

        verify(commCareSyncService).startSync(BatchJobType.GROUP);
    }
}
