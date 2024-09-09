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

    public long size() {
        return size;
    }

    public boolean add(T t) {
        last = new ListNode<>(t, last, null);
        if (size() == 0) {
            last.setPrevious(last);
            last.setNext(last);
            first = last;
        } else {
            last.getPrevious().setNext(last);
        }

        ++size;
        return true;
    }

    public T get(long index) {
        return getNode(index).getValue();
    }

    public T remove(int index) {
        var rem = getNode(index);
        if (index == 0) {
            removeFirst();
        } else if (index == size - 1) {
            removeLast();
        } else {
            var prev = rem.getPrevious();
            var next = rem.getNext();
            prev.setNext(next);
            next.setPrevious(prev);
        }
        return rem.getValue();
    }

    public boolean remove(Object o) {
        var node = getNode(o);
        if (node != null) {
            if (node.getPrevious() == null)
                removeFirst();
            else if (node.getNext() == null)
                removeLast();
            else {
                var prev = node.getPrevious();
                var next = node.getNext();
                prev.setNext(next);
                next.setPrevious(prev);
            }
        }
        return node != null;
    }

    public boolean contains(Object o) {
        var temp = first;
        var flag = false;
        while (temp != null && !flag) {
            if (temp.getValue().equals(o)) {
                flag = true;
            }
            temp = temp.getNext();
        }
        return flag;
    }

    public boolean addAll(Collection<? extends T> c) {
        for (var elem : c) {
            add(elem);
        }
        return true;
    }

    private ListNode<T> getNode(Object o) {
        var temp = first;
        var flag = false;
        while (temp != null && !flag) {
            if (temp.getValue().equals(o)) {
                flag = true;
            } else {
                temp = temp.getNext();
            }
        }
        return temp;
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

    private T removeFirst() {
        var temp = first;
        first = first.getNext();
        first.setPrevious(null);
        return temp.getValue();
    }

    private T removeLast() {
        var temp = last;
        last = last.getPrevious();
        last.setNext(null);
        return temp.getValue();
    }
}
