package org.example.services;

import org.example.models.homework10.Event;
import org.example.models.homework10.Place;
import org.example.repositories.EventRepository;
import org.example.repositories.PlaceRepository;
import org.example.utilities.EventSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class PlaceService {

    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private EventRepository eventRepository;

    public Place getPlaceByIdWithEvents(Long id) {
        return placeRepository.findByIdWithEvents(id);
    }

    public List<Event> searchEvents(String name, Long placeId, LocalDate fromDate, LocalDate toDate) {
        Specification<Event> spec = Specification.where(EventSpecification.byName(name))
                .and(EventSpecification.byPlace(placeId))
                .and(EventSpecification.byDateRange(fromDate, toDate));
        return eventRepository.findAll(spec);
    }

    public void deletePlace(Long id) {
        placeRepository.deleteById(id);
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Event getEventById(Long id) {
        return eventRepository.findById(id).orElse(null);
    }

    public Event createEvent(Event event) {
        return eventRepository.save(event);
    }

    public Event updateEvent(Long id, Event event) {
        if (!eventRepository.existsById(id)) {
            return null;
        }
        event.setId(id);
        return eventRepository.save(event);
    }

    public boolean deleteEvent(Long id) {
        if (!eventRepository.existsById(id)) {
            return false;
        }
        eventRepository.deleteById(id);
        return true;
    }
}