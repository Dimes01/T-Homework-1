package models.lesson3;

public class ListNode<T> {
    private T value;
    private ListNode<T> previousNode;
    private ListNode<T> nextNode;

    public ListNode(T value, ListNode<T> previousNode, ListNode<T> nextNode) {
        this.value = value;
        this.previousNode = previousNode;
        this.nextNode = nextNode;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T newValue) {
        value = newValue;
    }

    public ListNode<T> getNext() {
        return nextNode;
    }

    public ListNode<T> getPrevious() {
        return previousNode;
    }

    public void setNext(ListNode<T> node) {
        nextNode = node;
    }

    public void setPrevious(ListNode<T> node) {
        previousNode = node;
    }
}
