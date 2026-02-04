package org.cloudfoundry.samples.music.web;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@RunWith(SpringRunner.class)
@WebMvcTest(ErrorController.class)
public class ErrorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testThrowException() throws Exception {
        try {
            mockMvc.perform(get("/errors/throw"));
            // If no exception is thrown, fail the test
            fail("Expected NullPointerException to be thrown");
        } catch (Exception e) {
            // The controller throws a NullPointerException which may be wrapped in ServletException
            // Verify that the root cause is the expected exception
            Throwable cause = e;
            while (cause.getCause() != null) {
                cause = cause.getCause();
            }
            assertTrue("Expected NullPointerException but got " + cause.getClass().getName(),
                    cause instanceof NullPointerException);
            assertTrue("Expected message to contain 'Forcing an exception to be thrown'",
                    cause.getMessage().contains("Forcing an exception to be thrown"));
        }
    }

    // Note: We cannot test /errors/kill endpoint as it would terminate the test process
    // Note: We cannot test /errors/fill-heap endpoint as it would cause OutOfMemoryError
}
