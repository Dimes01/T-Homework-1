package controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.example.Homework13Application;
import org.example.models.Event;
import org.example.models.Place;
import org.example.repositories.EventRepository;
import org.example.repositories.PlaceRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = Homework13Application.class)
@AutoConfigureMockMvc
@Testcontainers
public class EventControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private EventRepository eventRepository;
    @Autowired private PlaceRepository placeRepository;

    private final ObjectMapper utilObjectMapper = new ObjectMapper();

    private static final List<String> datesString = List.of("2024-10-23", "2024-10-24");
    private static final List<LocalDate> dates = List.of(LocalDate.parse(datesString.get(0)), LocalDate.parse(datesString.get(1)));
    private static final List<Place> places = List.of(
            Place.builder().name("Place1").slug("place1").timezone("UTC").language("en").currency("USD").build(),
            Place.builder().name("Place2").slug("place2").timezone("UTC").language("en").currency("USD").build());
    private static final List<Event> events = List.of(
            Event.builder().name("Event11").date(dates.get(0)).placeId(places.get(0)).build(),
            Event.builder().name("Event12").date(dates.get(0)).placeId(places.get(1)).build(),
            Event.builder().name("Event21").date(dates.get(1)).placeId(places.get(0)).build(),
            Event.builder().name("Event22").date(dates.get(1)).placeId(places.get(1)).build()
    );

    @PostConstruct
    void init() {
        placeRepository.saveAll(places);
        eventRepository.saveAll(events);
    }


    @Test
    void getEventById_existedId_returnEvent() throws Exception {
        // Arrange
        var testEvent = events.getFirst();

        // Act & Assert
        mockMvc.perform(get("/api/v1/events/" + testEvent.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(utilObjectMapper.writeValueAsString(testEvent)));
    }

    @Test
    void getEventById_notExistedId_returnNotFound() throws Exception {
        // Arrange & Act & Assert
        mockMvc.perform(get("/api/v1/events/-1"))
                .andExpect(status().isNotFound());
    }

    private static Stream<Arguments> events_getByFilter() {
        return Stream.of(
                Arguments.of(events.get(0).getName(), null, null, null, List.of(events.get(0))),
                Arguments.of(events.get(1).getName(), null, null, null, List.of(events.get(1))),
                Arguments.of(events.get(2).getName(), null, null, null, List.of(events.get(2))),
                Arguments.of(events.get(3).getName(), null, null, null, List.of(events.get(3))),

                Arguments.of(null, places.get(0).getName(), null, null, List.of(events.get(0), events.get(2))),
                Arguments.of(null, places.get(1).getName(), null, null, List.of(events.get(1), events.get(3))),

                Arguments.of(events.get(0).getName(), places.get(0).getName(), null, null, List.of(events.get(0))),
                Arguments.of(events.get(0).getName(), places.get(1).getName(), null, null, Collections.emptyList()),
                Arguments.of(events.get(1).getName(), places.get(0).getName(), null, null, Collections.emptyList()),
                Arguments.of(events.get(1).getName(), places.get(1).getName(), null, null, List.of(events.get(1))),
                Arguments.of(events.get(2).getName(), places.get(0).getName(), null, null, List.of(events.get(2))),
                Arguments.of(events.get(2).getName(), places.get(1).getName(), null, null, Collections.emptyList()),
                Arguments.of(events.get(3).getName(), places.get(0).getName(), null, null, Collections.emptyList()),
                Arguments.of(events.get(3).getName(), places.get(1).getName(), null, null, List.of(events.get(3))),

                Arguments.of(null, null, datesString.get(0), null, List.of(events.get(0), events.get(1), events.get(2), events.get(3))),
                Arguments.of(null, null, datesString.get(1), null, List.of(events.get(2), events.get(3))),
                Arguments.of(null, null, null, datesString.get(0), List.of(events.get(0), events.get(1))),
                Arguments.of(null, null, null, datesString.get(1), List.of(events.get(0), events.get(1), events.get(2), events.get(3))),
                Arguments.of(null, null, datesString.get(1), datesString.get(0), Collections.emptyList()),
                Arguments.of(null, null, datesString.get(0), datesString.get(1), List.of(events.get(0), events.get(1), events.get(2), events.get(3)))
        );
    }

    @ParameterizedTest
    @MethodSource("events_getByFilter")
    void getEventsByFilter(String name, String placeId, String fromDate, String toDate, List<Event> expectedEvents) throws Exception {
        // Arrange & Act & Assert
        String listString = mockMvc.perform(get("/api/v1/events/filter")
                        .queryParam("name", name)
                        .queryParam("placeId", placeId)
                        .queryParam("fromDate", fromDate)
                        .queryParam("toDate", toDate))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<Event> returnedEvents = Arrays.stream(utilObjectMapper.readValue(listString, Event[].class)).toList();
        Assertions.assertEquals(expectedEvents.size(), returnedEvents.size());
        for (int i = 0; i < expectedEvents.size(); ++i) {
            Assertions.assertEquals(expectedEvents.get(i), returnedEvents.get(i));
        }
    }


    private static Stream<Arguments> events_create_goodSituations() {
        var testPlace = places.getFirst();
        var testEvent = events.getFirst();
        var newEvent1 = Event.builder().name("New Event 1").date(LocalDate.now()).placeId(testPlace).build();

        return Stream.of(
                Arguments.of(newEvent1),
                Arguments.of(testEvent)
        );
    }

    @ParameterizedTest
    @MethodSource("events_create_goodSituations")
    void postCreateEvent(Event event) throws Exception {
        // Arrange
        var newEventString = utilObjectMapper.writeValueAsString(event);

        // Act & Assert
        String result = mockMvc.perform(post("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newEventString))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        Event returnedEvent = utilObjectMapper.readValue(result, Event.class);
        Event savedEvent = eventRepository.findById(returnedEvent.getId()).orElseThrow();
        Assertions.assertEquals(returnedEvent.getId(), savedEvent.getId());
    }

    @Test
    void postCreateEvent_existedEventAndNotExistedPlace_returnBadRequest() throws Exception {
        // Arrange
        var notExistedPlace = Place.builder().id(-1L).name("Not Existed Place").slug("not-existed-place").timezone("UTC").language("en").currency("USD").build();
        var newEvent = Event.builder().name("New Event Test").date(LocalDate.now()).placeId(notExistedPlace).build();
        var eventString = utilObjectMapper.writeValueAsString(newEvent);

        // Act & Assert
        mockMvc.perform(post("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(eventString))
                .andExpect(status().isBadRequest());
    }

    private static Stream<Arguments> events_update_goodSituations() {
        var newEvent = events.getFirst();
        newEvent = Event.builder().name("New Event").date(newEvent.getDate()).placeId(newEvent.getPlaceId()).build();
        return Stream.of(
                Arguments.of(newEvent),
                Arguments.of(events.getFirst())
        );
    }

    @ParameterizedTest
    @MethodSource("events_update_goodSituations")
    void putUpdateEvent_goodSituations(Event newEvent) throws Exception {
        var eventString = utilObjectMapper.writeValueAsString(newEvent);

        var result = mockMvc.perform(put("/api/v1/events/" + newEvent.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(eventString))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        Event returnedEvent = utilObjectMapper.readValue(result, Event.class);
        Event savedEvent = eventRepository.findById(returnedEvent.getId()).orElseThrow();
        Assertions.assertEquals(returnedEvent.getId(), savedEvent.getId());
    }

    private static Stream<Arguments> events_update_badSituations() {
        var newEvent1 = events.getFirst();
        newEvent1 = Event.builder().id(-1L).name(newEvent1.getName()).date(newEvent1.getDate()).placeId(newEvent1.getPlaceId()).build();
        var notExistedPlace = Place.builder().id(-1L).name("Not Existed Place").slug("not-existed-place").timezone("UTC").language("en").currency("USD").build();
        var newEvent2 = Event.builder().name(newEvent1.getName()).date(newEvent1.getDate()).placeId(notExistedPlace).build();
        return Stream.of(
                Arguments.of(newEvent1, status().isNotFound()),
                Arguments.of(newEvent2, status().isBadRequest())
        );
    }

    @ParameterizedTest
    @MethodSource("events_update_badSituations")
    void putUpdateEvent_badSituations(Event newEvent, ResultMatcher matcher) throws Exception {
        var eventString = utilObjectMapper.writeValueAsString(newEvent);

        mockMvc.perform(put("/api/v1/events/" + newEvent.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(eventString))
                .andExpect(matcher);
    }

    @Test
    void deleteEvent_goodSituations() throws Exception {
        // Arrange
        var event = events.getFirst();

        // Act & Assert
        mockMvc.perform(delete("/api/v1/events/" + event.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void deleteEvent_badSituations() throws Exception {
        // Assert & Act & Assert
        mockMvc.perform(delete("/api/v1/events/-1"))
                .andExpect(status().isNotFound());
    }
}