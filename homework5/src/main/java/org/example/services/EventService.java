package org.example.services;

import org.example.models.homework10.Event;
import org.example.models.homework10.Place;
import org.example.repositories.EventRepository;
import org.example.utilities.EventSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class EventService {
    @Autowired
    private EventRepository eventRepository;

    public List<Event> findEvents(String name, Place place, LocalDate fromDate, LocalDate toDate) {
        Specification<Event> spec = Specification
                .where(EventSpecification.byName(name))
                .and(EventSpecification.byPlace(place.getId()))
                .and(EventSpecification.byDateRange(fromDate, toDate));
        return eventRepository.findAll(spec);
    }
}
