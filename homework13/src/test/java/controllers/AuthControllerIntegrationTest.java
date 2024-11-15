package controllers;

import org.example.Homework13Application;
import org.example.dto.PasswordResetConfirmRequest;
import org.example.dto.PasswordResetRequest;
import org.example.entities.User;
import org.example.dto.LoginRequest;
import org.example.dto.SignUpRequest;
import org.example.repositories.UserRepository;
import org.example.services.PasswordResetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
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
    @Autowired private PasswordResetService passwordResetService;

    private final ObjectMapper utilMapper = new ObjectMapper();
    private final SignUpRequest signUpRequest = new SignUpRequest("user1", "user1@mail.ru", "1111", "Ivan", "ROLE_USER");

    public AuthControllerIntegrationTest() throws JsonProcessingException { }

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
    }


    public static Stream<Arguments> validationSignUpParameters() {
        String username = "test";
        String email = "test@mail.ru";
        String password = "0";
        String name = "Test";
        String authority = "ROLE_USER";
        return Stream.of(
            Arguments.of(new SignUpRequest(username, email, password, name, authority), status().isOk()),
            Arguments.of(new SignUpRequest(username, email, password, name, ""), status().isBadRequest()),
            Arguments.of(new SignUpRequest(username, email, password, "", authority), status().isBadRequest()),
            Arguments.of(new SignUpRequest(username, email, password, "", ""), status().isBadRequest()),
            Arguments.of(new SignUpRequest(username, email, "", name, authority), status().isBadRequest()),
            Arguments.of(new SignUpRequest(username, email, "", name, ""), status().isBadRequest()),
            Arguments.of(new SignUpRequest(username, email, "", "", authority), status().isBadRequest()),
            Arguments.of(new SignUpRequest(username, email, "", "", ""), status().isBadRequest()),
            Arguments.of(new SignUpRequest(username, "", password, name, authority), status().isBadRequest()),
            Arguments.of(new SignUpRequest(username, "", password, name, ""), status().isBadRequest()),
            Arguments.of(new SignUpRequest(username, "", password, "", authority), status().isBadRequest()),
            Arguments.of(new SignUpRequest(username, "", password, "", ""), status().isBadRequest()),
            Arguments.of(new SignUpRequest(username, "", "", name, authority), status().isBadRequest()),
            Arguments.of(new SignUpRequest(username, "", "", name, ""), status().isBadRequest()),
            Arguments.of(new SignUpRequest(username, "", "", "", authority), status().isBadRequest()),
            Arguments.of(new SignUpRequest(username, "", "", "", ""), status().isBadRequest()),
            Arguments.of(new SignUpRequest("", email, password, name, authority), status().isBadRequest()),
            Arguments.of(new SignUpRequest("", email, password, name, ""), status().isBadRequest()),
            Arguments.of(new SignUpRequest("", email, password, "", authority), status().isBadRequest()),
            Arguments.of(new SignUpRequest("", email, password, "", ""), status().isBadRequest()),
            Arguments.of(new SignUpRequest("", email, "", name, authority), status().isBadRequest()),
            Arguments.of(new SignUpRequest("", email, "", name, ""), status().isBadRequest()),
            Arguments.of(new SignUpRequest("", email, "", "", authority), status().isBadRequest()),
            Arguments.of(new SignUpRequest("", email, "", "", ""), status().isBadRequest()),
            Arguments.of(new SignUpRequest("", "", password, name, authority), status().isBadRequest()),
            Arguments.of(new SignUpRequest("", "", password, name, ""), status().isBadRequest()),
            Arguments.of(new SignUpRequest("", "", password, "", authority), status().isBadRequest()),
            Arguments.of(new SignUpRequest("", "", password, "", ""), status().isBadRequest()),
            Arguments.of(new SignUpRequest("", "", "", name, authority), status().isBadRequest()),
            Arguments.of(new SignUpRequest("", "", "", name, ""), status().isBadRequest()),
            Arguments.of(new SignUpRequest("", "", "", "", authority), status().isBadRequest()),
            Arguments.of(new SignUpRequest("", "", "", "", ""), status().isBadRequest())
        );
    }

    @ParameterizedTest
    @MethodSource("validationSignUpParameters")
    public void register_validateParams_allSituations(SignUpRequest testSignUpRequest, ResultMatcher resultMatcher) throws Exception {
        // Arrange
        String requestString = utilMapper.writeValueAsString(testSignUpRequest);

        // Act & Assert
        mockMvc.perform(post("/register")
                .content(requestString)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(resultMatcher);
    }


    @Test
    public void register_notExistedUser_ok() throws Exception {
        // Arrange
        String requestString = utilMapper.writeValueAsString(signUpRequest);

        // Act
        String responseString = mockMvc.perform(post("/register")
                .content(requestString)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();

        User user = utilMapper.readValue(responseString, User.class);
        User expectedUser = userRepository.findByUsername(signUpRequest.getUsername());

        // Assert
        assertEquals(expectedUser, user);
    }

    @Test
    public void register_existedUser_badRequest() throws Exception {
        // Arrange
        String requestString = utilMapper.writeValueAsString(signUpRequest);
        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setEmail(signUpRequest.getEmail());
        user.setAuthority("ROLE_USER");
        user.setPassword(signUpRequest.getPassword());
        user.setName(signUpRequest.getName());
        userRepository.save(user);

        // Act
        mockMvc.perform(post("/register")
                .content(requestString)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());

        // Assert
        assertNotNull(userRepository.findByUsername(signUpRequest.getUsername()));
    }

    @Test
    public void login_existedUser_ok() throws Exception {
        // Arrange
        User user = new User("user1", "user1@mail.ru", passwordEncoder.encode("1111"), "ROLE_USER", "user1");
        userRepository.save(user);
        LoginRequest request = new LoginRequest("user1", "1111");
        String requestString = utilMapper.writeValueAsString(request);

        MockHttpSession session = new MockHttpSession();

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                .content(requestString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
            .andExpect(status().isOk());
    }

    @Test
    public void login_notExistedUser_unauthorized() throws Exception {
        // Arrange
        LoginRequest request = new LoginRequest("user-1", "pass-1");
        String requestString = utilMapper.writeValueAsString(request);

        MockHttpSession session = new MockHttpSession();

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                .content(requestString)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void logout_ok() throws Exception {
        // Arrange
        User user = new User(signUpRequest.getUsername(), signUpRequest.getEmail(), passwordEncoder.encode(signUpRequest.getPassword()), "ROLE_USER", signUpRequest.getName());
        userRepository.save(user);

        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(post("/login")
                .content(utilMapper.writeValueAsString(new LoginRequest(signUpRequest.getUsername(), signUpRequest.getPassword())))
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
            .andExpect(status().isOk());

        // Act
        mockMvc.perform(post("/logout")
                .session(session))
            .andExpect(status().isOk());

        // Assert
        assertTrue(session.isInvalid());
    }

    @Test
    public void passwordReset_userExists_confirmationCodeSent() throws Exception {
        // Arrange
        User user = new User(signUpRequest.getUsername(), signUpRequest.getEmail(), passwordEncoder.encode(signUpRequest.getPassword()), "ROLE_USER", signUpRequest.getName());
        userRepository.save(user);
        PasswordResetRequest resetRequest = new PasswordResetRequest(user.getUsername());

        // Act & Assert
        mockMvc.perform(post("/password-reset")
                .content(utilMapper.writeValueAsString(resetRequest))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    public void passwordReset_userNotExists_userNotFound() throws Exception {
        // Arrange
        PasswordResetRequest resetRequest = new PasswordResetRequest("nonexistentUser");

        // Act & Assert
        mockMvc.perform(post("/password-reset")
                .content(utilMapper.writeValueAsString(resetRequest))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    public void passwordResetConfirm_invalidCode_unauthorized() throws Exception {
        // Arrange
        User user = new User(signUpRequest.getUsername(), signUpRequest.getEmail(), passwordEncoder.encode(signUpRequest.getPassword()), "ROLE_USER", signUpRequest.getName());
        userRepository.save(user);
        String newPassword = "newPassword";
        PasswordResetConfirmRequest resetConfirmRequest = new PasswordResetConfirmRequest(user.getUsername(), "wrongCode", newPassword);

        // Act & Assert
        mockMvc.perform(post("/password-reset/confirm")
                .content(utilMapper.writeValueAsString(resetConfirmRequest))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());

    }

    @Test
    public void passwordResetConfirm_validCode_passwordResetSuccessful() throws Exception {
        // Arrange
        User user = new User(signUpRequest.getUsername(), signUpRequest.getEmail(), passwordEncoder.encode(signUpRequest.getPassword()), "ROLE_USER", signUpRequest.getName());
        userRepository.save(user);

        String confirmationCode = "0000";
        passwordResetService.sendConfirmationCode(user);
        String newPassword = "newPassword";
        String hashNewPassword = passwordEncoder.encode(newPassword);
        PasswordResetConfirmRequest resetConfirmRequest = new PasswordResetConfirmRequest(user.getUsername(), confirmationCode, newPassword);

        // Act
        mockMvc.perform(post("/password-reset/confirm")
                .content(utilMapper.writeValueAsString(resetConfirmRequest))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        // Assert
        User updatedUser = userRepository.findByUsername(user.getUsername());
        assertNotNull(updatedUser);
        assertTrue(passwordEncoder.matches(newPassword, updatedUser.getPassword()));
    }
}
