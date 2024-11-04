package org.example.interfaces;

public interface DataSubject<T> {
    void addObserver(DataObserver<T> observer);
    void removeObserver(DataObserver<T> observer);
    void notifyObservers(Long id, T data);
}
