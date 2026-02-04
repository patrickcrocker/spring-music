package org.cloudfoundry.samples.music.web;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class ErrorControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private ErrorController errorController;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(errorController).build();
    }

    @Test(expected = Exception.class)
    public void testThrowException() throws Exception {
        mockMvc.perform(get("/errors/throw"));
    }
}
