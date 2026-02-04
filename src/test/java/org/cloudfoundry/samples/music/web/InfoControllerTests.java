package org.cloudfoundry.samples.music.web;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@WebMvcTest(InfoController.class)
public class InfoControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private Environment environment;

    @Test
    public void requestReturnsRequestDetails() throws Exception {
        mockMvc.perform(get("/request"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.session-id").exists())
                .andExpect(jsonPath("$.protocol").exists())
                .andExpect(jsonPath("$.method").value("GET"))
                .andExpect(jsonPath("$.scheme").exists())
                .andExpect(jsonPath("$.remote-addr").exists());
    }

    @Test
    public void appInfoReturnsProfilesAndServices() throws Exception {
        when(environment.getActiveProfiles()).thenReturn(new String[] { "test" });

        mockMvc.perform(get("/appinfo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.profiles[0]").value("test"))
                .andExpect(jsonPath("$.services").isArray());
    }

    @Test
    public void serviceReturnsServiceList() throws Exception {
        mockMvc.perform(get("/service"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
