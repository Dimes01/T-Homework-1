package controllers;

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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class EventControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private PlaceRepository placeRepository;

    private Place testPlace;

    @BeforeEach
    void setUp() {
        // Создаем тестовое место
        testPlace = new Place();
        testPlace.setName("Test Place");
        testPlace.setSlug("test-place");
        testPlace.setTimezone("UTC");
        testPlace.setLanguage("en");
        testPlace.setCurrency("USD");
        placeRepository.save(testPlace);
    }

    @Test
    void testCreateEvent() throws Exception {
        Event event = new Event();
        event.setName("Test Event");
        event.setDate(LocalDate.now());
        event.setPlace(testPlace);

        mockMvc.perform(post("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Test Event\", \"date\": \"2024-10-20\", \"place\": {\"id\": " + testPlace.getId() + "}}"))
                .andExpect(status().isOk());

        // Проверяем, что событие было сохранено в базе данных
        assertThat(eventRepository.findAll()).hasSize(1);
        assertThat(eventRepository.findAll().get(0).getName()).isEqualTo("Test Event");
    }

    @Test
    void testGetEventById() throws Exception {
        // Сначала создаем событие
        Event savedEvent = new Event();
        savedEvent.setName("Test Event");
        savedEvent.setDate(LocalDate.now());
        savedEvent.setPlace(testPlace);
        eventRepository.save(savedEvent);

        // Проверяем получение события по ID
        mockMvc.perform(get("/api/v1/events/" + savedEvent.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Event"));
    }
}