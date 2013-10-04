package org.motechproject.commcare.provider.sync.diagnostics;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.MockitoAnnotations.initMocks;

public class StringDiagnosticsLoggerTest {

    private StringDiagnosticsLogger logger;

    @Before
    public void setUp() {
        initMocks(this);
        logger = new StringDiagnosticsLogger();
    }

    @Test
    public void shouldAppendAllMessages() {
        String nullString = null;
        RuntimeException exception = new RuntimeException("some exception");
        RuntimeException someOtherException = new RuntimeException("some other exception");

        logger.log("some message");
        logger.log(nullString);
        logger.log("some other message");
        logger.log(exception);
        logger.log("hello");
        logger.log(someOtherException);

        assertEquals(String.format("some message\nnull\nsome other message\nEXCEPTION: %s\nhello\nEXCEPTION: %s\n", ExceptionUtils.getFullStackTrace(exception), ExceptionUtils.getFullStackTrace(someOtherException)), logger.toString());
    }

    @Test
    public void shouldReturnEmptyStringIfNoMessageLogged() {
        assertEquals("", logger.toString());
    }

}
