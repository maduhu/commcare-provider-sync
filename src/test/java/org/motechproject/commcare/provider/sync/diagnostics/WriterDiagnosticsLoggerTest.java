package org.motechproject.commcare.provider.sync.diagnostics;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.StringWriter;
import java.io.Writer;

import static org.junit.Assert.assertEquals;

public class WriterDiagnosticsLoggerTest {

    private Writer writer;

    private WriterDiagnosticsLogger logger;


    @Before
    public void setUp() {
        writer = new StringWriter();
        logger = new WriterDiagnosticsLogger(writer);
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

        assertEquals(String.format("some message\nnull\nsome other message\nEXCEPTION: %s\nhello\nEXCEPTION: %s\n", ExceptionUtils.getFullStackTrace(exception), ExceptionUtils.getFullStackTrace(someOtherException)), writer.toString());
    }

    @Test
    public void shouldReturnEmptyStringIfNoMessageLogged() {
        assertEquals("", writer.toString());
    }
}
