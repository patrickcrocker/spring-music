package org.cloudfoundry.samples.music.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InfoController.class)
class InfoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void requestInfoReturnsRequestMetadata() throws Exception {
        mockMvc.perform(get("/request")
                        .with(request -> {
                            request.setRemoteAddr("10.0.0.5");
                            return request;
                        }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.session-id").isNotEmpty())
                .andExpect(jsonPath("$.protocol").value("HTTP/1.1"))
                .andExpect(jsonPath("$.method").value("GET"))
                .andExpect(jsonPath("$.scheme").value("http"))
                .andExpect(jsonPath("$.remote-addr").value("10.0.0.5"));
    }

    @Test
    void appinfoReturnsProfilesAndServicesArrays() throws Exception {
        mockMvc.perform(get("/appinfo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.profiles").isArray())
                .andExpect(jsonPath("$.services").isArray());
    }

    @Test
    void serviceReturnsServiceList() throws Exception {
        mockMvc.perform(get("/service"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
