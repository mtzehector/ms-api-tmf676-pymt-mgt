package mx.att.digital.api.handlers;

import mx.att.digital.api.controllers.ThrowingController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ThrowingController.class)
@AutoConfigureMockMvc(addFilters = false)
class ControllerAdviceWebTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void illegalArgument_mapsTo400() throws Exception {
        mockMvc.perform(get("/__test__/throw/illegal").accept(MediaType.APPLICATION_JSON))
              .andExpect(status().isBadRequest())
              .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    void nullPointer_mapsTo500() throws Exception {
        mockMvc.perform(get("/__test__/throw/null").accept(MediaType.APPLICATION_JSON))
              .andExpect(status().is5xxServerError())
              .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }
}
