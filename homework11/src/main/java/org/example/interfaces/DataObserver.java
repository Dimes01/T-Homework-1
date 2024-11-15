package org.example.interfaces;

public interface DataObserver<T> {
    void updateData(Long id, T data);
}
