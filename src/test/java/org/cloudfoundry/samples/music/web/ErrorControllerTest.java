package org.cloudfoundry.samples.music.web;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(ErrorController.class)
public class ErrorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testThrowException() throws Exception {
        try {
            mockMvc.perform(get("/errors/throw"))
                    .andExpect(status().is5xxServerError());
        } catch (Exception e) {
            // The controller throws a NullPointerException which may not be caught by MockMvc
            // Verify that the root cause is the expected exception
            Throwable cause = e;
            while (cause.getCause() != null) {
                cause = cause.getCause();
            }
            assert cause instanceof NullPointerException;
            assert cause.getMessage().contains("Forcing an exception to be thrown");
        }
    }

    // Note: We cannot test /errors/kill endpoint as it would terminate the test process
    // Note: We cannot test /errors/fill-heap endpoint as it would cause OutOfMemoryError
}
