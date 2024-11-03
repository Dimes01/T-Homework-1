package controllers;

import org.example.Homework13Application;
import org.example.entities.User;
import org.example.models.RegistrationForm;
import org.example.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the {@link org.example.controllers.AuthController}
 */
@SpringBootTest(classes = Homework13Application.class)
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;

    private final ObjectMapper utilMapper = new ObjectMapper();
    private final RegistrationForm registrationForm = new RegistrationForm("user1", "password");
    private final String registrationFormJson = utilMapper.writeValueAsString(registrationForm);

    public AuthControllerTest() throws JsonProcessingException { }

    @Test
    public void register() throws Exception {
        // Arrange & Act
        userRepository.deleteAll();
        String responseString = mockMvc.perform(post("/register")
                .content(registrationFormJson)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
        User user = utilMapper.readValue(responseString, User.class);
        User expectedUser = userRepository.findByUsername(registrationForm.getUsername());

        // Assert
        assertEquals(expectedUser.getId(), user.getId());
        userRepository.delete(expectedUser);
    }
}
