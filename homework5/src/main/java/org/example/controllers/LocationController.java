package org.example.controllers;
import org.example.models.Location;
import org.example.utilities.Storage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/v1/locations")
public class LocationController {
    private final Storage<Location> storage;
    private final Logger logger = LoggerFactory.getLogger(LocationController.class);

    @Autowired
    public LocationController(Storage<Location> storage) {
        this.storage = storage;
    }

    @GetMapping
    public ResponseEntity<List<Location>> getAllLocations() {
        logger.info("Method 'getAllLocations' is started");
        List<Location> locations = storage.getAll();
        logger.info("Method 'getAllLocations' is finished");
        return ResponseEntity.ok(locations);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Location> getLocationById(@PathVariable Long id) {
        logger.info("Method 'getLocationById' is started");
        Location location = storage.getById(id);
        if (location == null) {
            logger.warn("Method 'getLocationById': not found element");
            return ResponseEntity.badRequest().build();
        }
        logger.info("Method 'getLocationById' is finished");
        return ResponseEntity.ok(location);
    }

    @PostMapping("/{id}")
    public ResponseEntity<Location> postCreateLocation(@PathVariable Long id, @RequestBody Location location) {
        logger.info("Method 'postCreateLocation' is started");
        Location elem = storage.save(id, location);
        logger.info("Method 'postCreateLocation' is finished");
        return ResponseEntity.ok(elem);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Location> putUpdateLocation(@PathVariable Long id, @RequestBody Location location) {
        logger.info("Method 'putUpdateLocation' is started");
        boolean result = storage.update(id, location);
        if (!result) {
            logger.warn("Method 'putUpdateLocation': could not update element");
            return ResponseEntity.badRequest().build();
        }
        logger.info("Method 'putUpdateLocation' is finished");
        return ResponseEntity.ok(location);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Location> deleteLocation(@PathVariable Long id) {
        logger.info("Method 'deleteLocation' is started");
        Location deletingElem = storage.getById(id);
        boolean isDelete = storage.delete(id);
        if (!isDelete) {
            logger.warn("Method 'deleteLocation': could not delete element");
            return ResponseEntity.badRequest().build();
        }
        logger.info("Method 'deleteLocation' is finished");
        return ResponseEntity.ok(deletingElem);
    }
}
