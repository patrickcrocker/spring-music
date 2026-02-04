package org.cloudfoundry.samples.music.web;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
public class InfoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private Environment springEnvironment;

    @InjectMocks
    private InfoController infoController;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(infoController).build();
    }

    @Test
    public void testRequestInfo() throws Exception {
        mockMvc.perform(get("/request"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.session-id").exists())
                .andExpect(jsonPath("$.protocol").exists())
                .andExpect(jsonPath("$.method").value("GET"))
                .andExpect(jsonPath("$.scheme").exists())
                .andExpect(jsonPath("$.remote-addr").exists());
    }

    @Test
    public void testAppInfo() throws Exception {
        String[] profiles = {"test", "local"};
        when(springEnvironment.getActiveProfiles()).thenReturn(profiles);

        mockMvc.perform(get("/appinfo"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.profiles").isArray())
                .andExpect(jsonPath("$.services").isArray());
    }

    @Test
    public void testServiceInfo() throws Exception {
        mockMvc.perform(get("/service"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }
}
