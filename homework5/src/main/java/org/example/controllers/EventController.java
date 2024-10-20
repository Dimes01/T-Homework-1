package org.example.controllers;

import jakarta.validation.Valid;
import org.example.exceptions.EntityNotFoundException;
import org.example.exceptions.InvalidEntityException;
import org.example.models.homework10.Event;
import org.example.services.PlaceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/events")
public class EventController {
    private final PlaceService placeService;
    private final Logger logger = LoggerFactory.getLogger(EventController.class);

    @Autowired
    public EventController(PlaceService placeService) {
        this.placeService = placeService;
    }

    @GetMapping
    public ResponseEntity<List<Event>> getAllEvents() {
        logger.info("Method 'getAllEvents' is started");
        List<Event> events = placeService.getAllEvents();
        logger.info("Method 'getAllEvents' is finished");
        return ResponseEntity.ok(events);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable Long id) {
        logger.info("Method 'getEventById' is started");
        Event event = placeService.getEventById(id);
        if (event == null) {
            logger.warn("Method 'getEventById': not found element");
            throw new EntityNotFoundException("Event not found with ID: " + id);
        }
        logger.info("Method 'getEventById' is finished");
        return ResponseEntity.ok(event);
    }

    @PostMapping
    public ResponseEntity<Event> postCreateEvent(@Valid @RequestBody Event event) {
        logger.info("Method 'postCreateEvent' is started");
        if (placeService.getPlaceByIdWithEvents(event.getPlace().getId()) == null) {
            logger.warn("Method 'postCreateEvent': place not found");
            throw new InvalidEntityException("Place not found with ID: " + event.getPlace().getId());
        }
        Event createdEvent = placeService.createEvent(event);
        logger.info("Method 'postCreateEvent' is finished");
        return ResponseEntity.ok(createdEvent);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Event> putUpdateEvent(@PathVariable Long id, @Valid @RequestBody Event event) {
        logger.info("Method 'putUpdateEvent' is started");
        Event existingEvent = placeService.getEventById(id);
        if (existingEvent == null) {
            logger.warn("Method 'putUpdateEvent': not found element");
            throw new EntityNotFoundException("Event not found with ID: " + id);
        }
        if (placeService.getPlaceByIdWithEvents(event.getPlace().getId()) == null) {
            logger.warn("Method 'putUpdateEvent': place not found");
            throw new InvalidEntityException("Place not found with ID: " + event.getPlace().getId());
        }
        event.setId(id); // Set the ID to the event for updating
        Event updatedEvent = placeService.updateEvent(id, event);
        logger.info("Method 'putUpdateEvent' is finished");
        return ResponseEntity.ok(updatedEvent);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        logger.info("Method 'deleteEvent' is started");
        Event event = placeService.getEventById(id);
        if (event == null) {
            logger.warn("Method 'deleteEvent': not found element");
            throw new EntityNotFoundException("Event not found with ID: " + id);
        }
        placeService.deleteEvent(id);
        logger.info("Method 'deleteEvent' is finished");
        return ResponseEntity.noContent().build();
    }
}
