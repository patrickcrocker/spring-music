package org.cloudfoundry.samples.music.web;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(ErrorController.class)
public class ErrorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test(expected = Exception.class)
    public void testThrowException() throws Exception {
        mockMvc.perform(get("/errors/throw"));
    }

    // Note: We cannot test /errors/kill endpoint as it would terminate the test process
    // Note: We cannot test /errors/fill-heap endpoint as it would cause OutOfMemoryError
}
