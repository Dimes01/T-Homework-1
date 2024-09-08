package models.lesson3;

import java.util.Collection;

public class CustomLinkedList<T> {
    private long size = 0;
    private ListNode<T> first = null;
    private ListNode<T> last = null;

    public CustomLinkedList() {
    }

    public CustomLinkedList(Collection<? extends T> coll) {
        this();
        addAll(coll);
    }

    public boolean add(T t) {
        last = new ListNode<>(t, last, null);
        last.getPrevious().setNext(last);
        ++size;
        return true;
    }

    public T get(long index) {
        return getNode(index).getValue();
    }

    public T remove(int index) {
        var rem = getNode(index);
        if (index == 0) {

        }
    }

//    public boolean remove(Object o) {
//
//    }

    public boolean contains(Object o) {

    }

    public boolean addAll(Collection<? extends T> c) {

    }

    private ListNode<T> getNode(long index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException();
        ListNode<T> temp;
        if (index < size / 2) {
            temp = first;
            while (index >= 0) {
                --index;
                temp = temp.getNext();
            }
        } else {
            temp = last;
            while (index >= 0) {
                --index;
                temp = temp.getPrevious();
            }
        }
        return temp;
    }
}
