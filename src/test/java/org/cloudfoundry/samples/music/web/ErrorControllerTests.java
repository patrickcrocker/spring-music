package org.cloudfoundry.samples.music.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@RunWith(SpringRunner.class)
@WebMvcTest(ErrorController.class)
public class ErrorControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    public void throwEndpointReturnsServerError() throws Exception {
        mockMvc.perform(get("/errors/throw"))
                .andExpect(status().is5xxServerError());
    }

    @Test
    public void errorMappingsExist() {
        RequestMappingHandlerMapping mapping = applicationContext.getBean(RequestMappingHandlerMapping.class);
        Map<RequestMappingInfo, ?> handlerMethods = mapping.getHandlerMethods();

        assertThat(handlerMethods.keySet())
                .anyMatch(info -> info.getPatternValues().contains("/errors/kill"))
                .anyMatch(info -> info.getPatternValues().contains("/errors/fill-heap"));
    }
}
