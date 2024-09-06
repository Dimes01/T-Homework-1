package models.lesson3;

public class ListNode<T> {
    private T previousNode;
    private T nextNode;

    public T getNext() {
        return nextNode;
    }

    public T getPrevious() {
        return previousNode;
    }

    public void setNext(T node) {
        nextNode = node;
    }

    public void setPrevious(T node) {
        previousNode = node;
    }
}
