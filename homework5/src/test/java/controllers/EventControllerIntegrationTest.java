package controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.Homework5Application;
import org.example.models.homework10.Event;
import org.example.models.homework10.Place;
import org.example.repositories.EventRepository;
import org.example.repositories.PlaceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

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
    private Event testEvent;


    @BeforeEach
    void setUp() throws JsonProcessingException {
        testPlace = Place.builder().name("Test Place").slug("test-place").timezone("UTC").language("en").currency("USD").build();
        placeRepository.save(testPlace);
        testEvent = Event.builder().name("Test Event").date(LocalDate.now()).placeId(testPlace).build();
        eventRepository.save(testEvent);
    }

    @Test
    void testCreateEvent() throws Exception {
        // Arrange
        var testPlaceString = utilObjectMapper.writeValueAsString(testPlace);

        // Act
        mockMvc.perform(post("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(testPlaceString))
                .andExpect(status().isOk());

        // Assert
        assertThat(eventRepository.findAll().getFirst().getName()).isEqualTo("Test Event");
    }

    @Test
    void getEventById_existedId_returnEvent() throws Exception {
        // Assert & Arrange & Act
        mockMvc.perform(get("/api/v1/events/" + testEvent.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(utilObjectMapper.writeValueAsString(testEvent)));
    }

    @Test
    void getEventById_notExistedId_returnNotFound() throws Exception {
        // Arrange & Act & Assert
        mockMvc.perform(get("/api/v1/events/" + testEvent.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void postCreateEvent_notExistedEvent_returnOk() throws Exception {
        // Arrange
        var newEvent = Event.builder().name("New Event").date(LocalDate.now()).placeId(testPlace).build();
        var newEventString = utilObjectMapper.writeValueAsString(newEvent);

        // Act & Assert
        mockMvc.perform(post("/api/v1/events").content(newEventString))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(newEvent.getName()));
    }
}