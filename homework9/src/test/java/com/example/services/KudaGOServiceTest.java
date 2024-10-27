package com.example.services;

import com.example.models.Event;
import org.example.homework9.models.EventDate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.net.URI;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class KudaGOServiceTest {
    private static final Event event1 = new Event(1, "Event 1", 200, 700, new EventDate[] {
            new EventDate(getCalendar(2024, Calendar.OCTOBER, 12).getTime(), getCalendar(2024, Calendar.OCTOBER, 13).getTime()),
            new EventDate(getCalendar(2024, Calendar.OCTOBER, 19).getTime(), getCalendar(2024, Calendar.OCTOBER, 20).getTime())
    }, 5);

    private static final Event event2 = new Event(2, "Event 2", 400, 1000, new EventDate[] {
            new EventDate(getCalendar(2024, Calendar.OCTOBER, 12).getTime(), getCalendar(2024, Calendar.OCTOBER, 16).getTime()),
    }, 7);

    private static final Event event3 = new Event(3, "Event 3", 1200, 2000, new EventDate[] {
            new EventDate(getCalendar(2024, Calendar.OCTOBER, 12).getTime(), getCalendar(2024, Calendar.OCTOBER, 13).getTime()),
            new EventDate(getCalendar(2024, Calendar.OCTOBER, 19).getTime(), getCalendar(2024, Calendar.OCTOBER, 20).getTime())
    }, 20);

    private static final Event[] events = new Event[] { event1, event2, event3 };

    @Mock private RestClient restClient;
    @Mock private Semaphore semaphore;
    @Mock private RestClient.RequestHeadersUriSpec requestHeadersUriSpec;
    @Mock private RestClient.RequestHeadersSpec requestHeadersSpec;
    @Mock private RestClient.ResponseSpec responseSpec;

    @InjectMocks
    private KudaGOService kudaGOService;

    @BeforeEach
    public void setUp() {
        when(semaphore.tryAcquire()).thenReturn(true);

        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(URI.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(Event[].class)).thenReturn(events);
    }

    private static Stream<Arguments> getDates() {
        var newEvent1 = new Event(event1.getId(), event1.getName(), event1.getMinCost(), event1.getMaxCost(),
                new EventDate[] { event1.getDates()[0] }, event1.getFavoritesCount());

        var newEvent3 = new Event(event3.getId(), event3.getName(), event3.getMinCost(), event3.getMaxCost(),
                new EventDate[] { event3.getDates()[0] }, event3.getFavoritesCount());

        return Stream.of(
                Arguments.of(
                        getCalendar(2024, Calendar.OCTOBER, 12).getTime(),
                        getCalendar(2024, Calendar.OCTOBER, 20).getTime(),
                        events
                ),
                Arguments.of(
                        getCalendar(2024, Calendar.OCTOBER, 12).getTime(),
                        getCalendar(2024, Calendar.OCTOBER, 13).getTime(),
                        new Event[] { newEvent1, newEvent3 }
                ),
                Arguments.of(
                        getCalendar(2024, Calendar.OCTOBER, 21).getTime(),
                        getCalendar(2024, Calendar.OCTOBER, 22).getTime(),
                        new Event[] {}
                ),
                Arguments.of(
                        getCalendar(2024, Calendar.OCTOBER, 13).getTime(),
                        getCalendar(2024, Calendar.OCTOBER, 12).getTime(),
                        new Event[] {}
                )
        );
    }

    private static Calendar getCalendar(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    @ParameterizedTest
    @MethodSource("getDates")
    void getPossibleEvents(Date startDate, Date endDate, Event[] expectedEvents) {
        // Arrange
        when(responseSpec.body(Event[].class)).thenReturn(expectedEvents);

        // Act
        var actualEvents = kudaGOService.getPossibleEvents(startDate, endDate);

        // Assert
        assertEquals(Arrays.stream(expectedEvents).toList(), actualEvents);
    }

    @ParameterizedTest
    @MethodSource("getDates")
    void getPossibleEventsFlux(Date startDate, Date endDate, Event[] expectedEvents) {
        // Arrange
        Flux<Event> expectedEventsFlux = expectedEvents.length == 0 ? Flux.empty() : Flux.fromArray(expectedEvents);
        when(responseSpec.body(Flux.class)).thenReturn(expectedEventsFlux);

        // Act
        var actualEvents = kudaGOService.getPossibleEventsFlux(startDate, endDate);

        // Assert
        StepVerifier.create(actualEvents)
                .expectNext(expectedEvents)
                .verifyComplete();
    }

    private static Stream<Arguments> eventsAndBudgets() {
        return Stream.of(
                Arguments.of(500, events, new Event[] { event1, event2 }),
                Arguments.of(0, events, new Event[] {}),
                Arguments.of(2000, events, events),
                Arguments.of(-100, events, new Event[] {})
        );
    }

    @ParameterizedTest
    @MethodSource("eventsAndBudgets")
    void filterEventsByBudget(double budget, Event[] events, Event[] expectedEvents) {
        // Arrange & Act
        var filteredEvents = kudaGOService.filterEventsByBudget(List.of(events), budget);

        // Assert
        assertEquals(Arrays.stream(expectedEvents).toList(), filteredEvents);
    }

    @ParameterizedTest
    @MethodSource("eventsAndBudgets")
    void filterEventsByBudgetFlux(double budget, Event[] events, Event[] expectedEvents) {
        // Arrange & Act
        Flux<Event> eventsFlux = Flux.fromArray(events);
        var filteredEvents = kudaGOService.filterEventsByBudgetFlux(eventsFlux, budget);

        // Assert
        StepVerifier.create(filteredEvents)
                .expectNext(expectedEvents)
                .verifyComplete();
    }

    @Test
    public void testSemaphoreUsageInGetEventsPage() throws InterruptedException {
        Date from = getCalendar(2024, Calendar.OCTOBER, 12).getTime();
        Date to = getCalendar(2024, Calendar.OCTOBER, 13).getTime();

        when(responseSpec.body(Event[].class)).thenReturn(events);

        List<Event> events = kudaGOService.getPossibleEvents(from, to);

        assertNotNull(events);
        verify(semaphore, times(1)).acquire();
        verify(semaphore, times(1)).release();
    }
}
