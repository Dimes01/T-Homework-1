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
        ListNode<T> newNode = new ListNode<>(t, last, null);
        if (last != null) {
            last.setNextNode(newNode);
        }
        last = newNode;
        if (first == null) {
            first = newNode;
            first.setNextNode(last);
            last.setPreviousNode(first);
        }
        ++size;
        return true;
    }

    public T get(long index) {
        return getNode(index).getValue();
    }

    public T remove(int index) {
        var rem = getNode(index);
        removeNode(rem);
        return rem.getValue();
    }

    public boolean remove(Object o) {
        var node = getNode(o);
        if (node != null) {
            removeNode(node);
        }
        return node != null;
    }

    public boolean contains(Object o) {
        return getNode(o) != null;
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
                temp = temp.getNextNode();
            }
        }
        return temp;
    }

    private ListNode<T> getNode(long index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException();
        ListNode<T> temp;
        if (index < size / 2) {
            temp = first;
            for (; index >= 0; --index) {
                temp = temp.getNextNode();
            }
        } else {
            temp = last;
            for (; index >= 0; --index) {
                temp = temp.getPreviousNode();
            }
        }
        return temp;
    }

    private void removeNode(ListNode<T> node) {
        if (node.getPreviousNode() != null) {
            node.getPreviousNode().setNextNode(node.getNextNode());
        } else {
            first = node.getNextNode();
        }
        if (node.getNextNode() != null) {
            node.getNextNode().setPreviousNode(node.getPreviousNode());
        } else {
            last = node.getPreviousNode();
        }
        size--;
    }
}
