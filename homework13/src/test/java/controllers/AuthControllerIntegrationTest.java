package controllers;

import org.example.Homework13Application;
import org.example.entities.User;
import org.example.dto.LoginRequest;
import org.example.dto.RegistrationRequest;
import org.example.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the {@link org.example.controllers.AuthController}
 */
@SpringBootTest(classes = Homework13Application.class)
@AutoConfigureMockMvc
public class AuthControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    private final ObjectMapper utilMapper = new ObjectMapper();
    private final RegistrationRequest registrationRequest = new RegistrationRequest("user1", "password");

    public AuthControllerIntegrationTest() throws JsonProcessingException { }

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
    }

    @Test
    public void register() throws Exception {
        // Arrange
        String registrationFormJson = utilMapper.writeValueAsString(registrationRequest);

        // Act
        String responseString = mockMvc.perform(post("/register")
                .content(registrationFormJson)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
        User user = utilMapper.readValue(responseString, User.class);
        User expectedUser = userRepository.findByUsername(registrationRequest.getUsername());

        // Assert
        assertEquals(expectedUser.getId(), user.getId());
    }

    @Test
    public void login_existedUser_ok() throws Exception {
        // Arrange
        User user = registrationRequest.createUser(passwordEncoder, "ROLE_USER");
        userRepository.save(user);
        LoginRequest request = new LoginRequest(registrationRequest.getUsername(), registrationRequest.getPassword());
        String requestString = utilMapper.writeValueAsString(request);

        // Act & Assert
        mockMvc.perform(post("/login")
                .content(requestString)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    public void login_notExistedUser_unauthorized() throws Exception {
        // Arrange
        LoginRequest request = new LoginRequest("user-1", "pass-1");
        String requestString = utilMapper.writeValueAsString(request);

        // Act & Assert
        mockMvc.perform(post("/login")
                .content(requestString)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }
}
