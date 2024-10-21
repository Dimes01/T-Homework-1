package controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.Homework5Application;
import org.example.models.homework10.Event;
import org.example.models.homework10.Place;
import org.example.repositories.EventRepository;
import org.example.repositories.PlaceRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = Homework5Application.class)
@AutoConfigureMockMvc
@Testcontainers
public class EventControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private EventRepository eventRepository;
    @Autowired private PlaceRepository placeRepository;

    private final ObjectMapper utilObjectMapper = new ObjectMapper();
    private Place testPlace;
    private String testPlaceString;

    @Container
    private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"));

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
    }

    @BeforeAll
    static void startContainer() { postgresContainer.start(); }

    @AfterAll
    static void stopContainer() { postgresContainer.stop(); }


    @BeforeEach
    void setUp() throws JsonProcessingException {
        testPlace = new Place();
        testPlace.setName("Test Place");
        testPlace.setSlug("test-place");
        testPlace.setTimezone("UTC");
        testPlace.setLanguage("en");
        testPlace.setCurrency("USD");
        placeRepository.save(testPlace);
        testPlaceString = utilObjectMapper.writeValueAsString(testPlace);
    }

    @Test
    void testCreateEvent() throws Exception {
        // Arrange
        Event event = new Event();
        event.setName("Test Event");
        event.setDate(LocalDate.now());
        event.setPlaceId(testPlace);

        // Act
        mockMvc.perform(post("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(testPlaceString))
                .andExpect(status().isOk());

        // Assert
        assertThat(eventRepository.findAll()).hasSize(1);
        assertThat(eventRepository.findAll().getFirst().getName()).isEqualTo("Test Event");
    }

    @Test
    void testGetEventById() throws Exception {
        // Arrange
        Event savedEvent = new Event();
        savedEvent.setName("Test Event");
        savedEvent.setDate(LocalDate.now());
        savedEvent.setPlaceId(testPlace);
        eventRepository.save(savedEvent);

        // Act & Assert
        mockMvc.perform(get("/api/v1/events/" + savedEvent.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(utilObjectMapper.writeValueAsString(savedEvent)));
    }
}