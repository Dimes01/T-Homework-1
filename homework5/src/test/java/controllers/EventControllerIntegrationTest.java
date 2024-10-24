package controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.Homework5Application;
import org.example.models.homework10.Event;
import org.example.models.homework10.Place;
import org.example.repositories.EventRepository;
import org.example.repositories.PlaceRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.*;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = Homework5Application.class)
@AutoConfigureMockMvc
@Testcontainers
public class EventControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private static EventRepository eventRepository;
    @Autowired private static PlaceRepository placeRepository;

    @Value("{spring.datasource.url}")
    private static String databaseName;

    @Value("{spring.datasource.username}")
    private static String username;

    @Value("{spring.datasource.password}")
    private static String password;

    private final ObjectMapper utilObjectMapper = new ObjectMapper();

    private static final List<LocalDate> dates = List.of(LocalDate.parse("2024-10-23"), LocalDate.parse("2024-10-24"));
    private static final List<Place> places = List.of(
            Place.builder().name("Place1").slug("place1").timezone("UTC").language("en").currency("USD").build(),
            Place.builder().name("Place2").slug("place2").timezone("UTC").language("en").currency("USD").build());
    private static final List<Event> events = new LinkedList<>();

    @Container
    private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"))
            .withDatabaseName(databaseName)
            .withUsername(username)
            .withPassword(password);

    @DynamicPropertySource
    private static void properties(final DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
    }

    @BeforeAll
    public static void startContainer() {
        postgresContainer.start();

        places.forEach(placeRepository::save);
        for (int i = 0; i < dates.size(); ++i) {
            for (int j = 0; j < places.size(); ++j) {
                var event = Event.builder().name("Event " + (i + 1) + (j + 1)).date(dates.get(i)).placeId(places.get(j)).build();
                events.add(event);
                eventRepository.save(event);
            }
        }
    }

    @AfterAll
    public static void stopContainer() {
        postgresContainer.stop();
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

    private static Stream<Arguments> events_getByFilter() {
        List<Arguments> arguments = new LinkedList<>();
        arguments.add(Arguments.of(null, null, null, null, Collections.emptyList()));

        // Проверка на поиск по названию события
        for (Event event : events) {
            arguments.add(Arguments.of(event.getName(), null, null, null, List.of(event)));
        }

        // Проверка на поиск по локации события
        for (Place place : places) {
            var currentPlaceId = place.getId();
            List<Event> expectedEvent = events.stream().filter(e -> {
                var eventPlaceId = e.getPlaceId().getId();
                return Objects.equals(eventPlaceId, currentPlaceId);
            }).toList();
            arguments.add(Arguments.of(null, place.getName(), null, null, expectedEvent));
        }

        // Проверка на поиск по дате начала события
        for (LocalDate currentDate : dates) {
            List<Event> expectedEvent = events.stream().filter(event -> {
                var eventDate = event.getDate();
                return eventDate.isAfter(currentDate) || eventDate.isEqual(currentDate);
            }).toList();
            arguments.add(Arguments.of(null, null, currentDate, null, expectedEvent));
        }

        // Проверка на поиск по дате окончания события
        for (LocalDate currentDate : dates) {
            List<Event> expectedEvent = events.stream().filter(event -> {
                var eventDate = event.getDate();
                return eventDate.isBefore(currentDate) || eventDate.isEqual(currentDate);
            }).toList();
            arguments.add(Arguments.of(null, null, null, currentDate, expectedEvent));
        }

//        return Stream.of(
//                Arguments.of(event11.getName(), place1.getName(), null, null, List.of(event11)),
//                Arguments.of(event11.getName(), place2.getName(), null, null, Collections.emptyList()),
//                Arguments.of(event12.getName(), place1.getName(), null, null, Collections.emptyList()),
//                Arguments.of(event12.getName(), place2.getName(), null, null, List.of(event12)),
//                Arguments.of(event21.getName(), place1.getName(), null, null, List.of(event21)),
//                Arguments.of(event21.getName(), place2.getName(), null, null, Collections.emptyList()),
//                Arguments.of(event22.getName(), place1.getName(), null, null, Collections.emptyList()),
//                Arguments.of(event22.getName(), place2.getName(), null, null, List.of(event22)),
//
//                Arguments.of(null, null, date2.toString(), date1.toString(), Collections.emptyList()),
//                Arguments.of(null, null, date1.toString(), date2.toString(), List.of(event11, event12, event21, event22))
//        );
        return arguments.stream();
//        return Stream.of(
//                Arguments.of(event11.getName(), null, null, null, List.of(event11)),
//                Arguments.of(event12.getName(), null, null, null, List.of(event12)),
//                Arguments.of(event21.getName(), null, null, null, List.of(event21)),
//                Arguments.of(event22.getName(), null, null, null, List.of(event22)),
//
//                Arguments.of(null, place1.getName(), null, null, List.of(event11, event21)),
//                Arguments.of(null, place2.getName(), null, null, List.of(event12, event22)),
//
//                Arguments.of(event11.getName(), place1.getName(), null, null, List.of(event11)),
//                Arguments.of(event11.getName(), place2.getName(), null, null, Collections.emptyList()),
//                Arguments.of(event12.getName(), place1.getName(), null, null, Collections.emptyList()),
//                Arguments.of(event12.getName(), place2.getName(), null, null, List.of(event12)),
//                Arguments.of(event21.getName(), place1.getName(), null, null, List.of(event21)),
//                Arguments.of(event21.getName(), place2.getName(), null, null, Collections.emptyList()),
//                Arguments.of(event22.getName(), place1.getName(), null, null, Collections.emptyList()),
//                Arguments.of(event22.getName(), place2.getName(), null, null, List.of(event22)),
//
//                Arguments.of(null, null, date1.toString(), null, List.of(event11, event12, event21, event22)),
//                Arguments.of(null, null, date2.toString(), null, List.of(event21, event22)),
//                Arguments.of(null, null, null, date1.toString(), List.of(event11, event12)),
//                Arguments.of(null, null, null, date2.toString(), List.of(event11, event12, event21, event22)),
//                Arguments.of(null, null, date2.toString(), date1.toString(), Collections.emptyList()),
//                Arguments.of(null, null, date1.toString(), date2.toString(), List.of(event11, event12, event21, event22))
//        );
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
        Assertions.assertEquals(expectedEvents, returnedEvents);
    }
}