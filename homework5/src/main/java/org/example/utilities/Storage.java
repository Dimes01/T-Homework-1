package org.example.utilities;

import org.example.interfaces.DataObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class Storage<T> implements DataObserver<T> {
    private final ConcurrentHashMap<Long, T> storage = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, List<Snapshot<T>>> snapshots = new ConcurrentHashMap<>();
    private final Logger logger = LoggerFactory.getLogger(Storage.class);

    private void createSnapshot(Long id, T entity) {
        logger.debug("Method 'createSnapshot': start");
        Snapshot<T> snapshot = new Snapshot<>(id, entity);
        snapshots.computeIfAbsent(id, k -> new ArrayList<>()).add(snapshot);
        logger.debug("Method 'createSnapshot': snapshot for entity with id({}) created", id);
    }

    public List<Snapshot<T>> getHistory(Long id) {
        logger.info("Method 'getHistory': started");
        List<Snapshot<T>> history = snapshots.getOrDefault(id, Collections.emptyList());
        logger.info("Method 'getHistory': finished");
        return history;
    }

    public List<T> getAll() {
        logger.info("Method 'getAll': started");
        logger.info("Method 'getAll': finished");
        return storage.values().stream().toList();
    }

    public T getById(Long id) {
        logger.info("Method 'getById': started");
        T elem = storage.get(id);
        if (elem == null)
            logger.warn("Method 'getById': required element is not found");
        logger.info("Method 'getById': finished");
        return elem;
    }

    public T save(Long id, T entity) {
        logger.info("Method 'save': started");
        createSnapshot(id, entity);
        storage.put(id, entity);
        logger.debug("Method 'save': entity with id({}) saved", id);
        logger.info("Method 'save': finished");
        return entity;
    }

    public boolean update(Long id, T entity) {
        logger.info("Method 'update': started");
        boolean result = storage.containsKey(id);
        if (result) {
            createSnapshot(id, entity);
            storage.put(id, entity);
            logger.debug("Method 'update': result is entity with id({})", id);
        } else {
            logger.debug("Method 'update': not found element");
        }
        logger.info("Method 'update': finished");
        return result;
    }

    public boolean delete(Long id) {
        logger.info("Method 'delete': started");
        boolean result = storage.remove(id) != null;
        if (result) {
            createSnapshot(id, null);
            logger.debug("Method 'delete': deleted entity with id({})", id);
        }
        logger.info("Method 'delete': finished");
        return result;
    }

    @Override
    public void updateData(Long id, T data) {
        logger.debug("Method 'update' from observer: started");
        save(id, data);
        logger.debug("Method 'update' from observer: finished");
    }
}
