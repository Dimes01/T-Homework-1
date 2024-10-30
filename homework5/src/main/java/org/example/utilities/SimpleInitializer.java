package org.example.utilities;

import lombok.RequiredArgsConstructor;
import org.example.interfaces.DataObserver;
import org.example.interfaces.DataSubject;
import org.example.interfaces.Initializer;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@RequiredArgsConstructor
public class SimpleInitializer extends Initializer implements DataSubject<Object> {
    private final List<DataObserver<Object>> observers = new CopyOnWriteArrayList<>();

    @Override
    public void addObserver(DataObserver observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(DataObserver<Object> observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(Long id, Object data) {
        observers.forEach(observer -> observer.updateData(id, data));
    }
}
