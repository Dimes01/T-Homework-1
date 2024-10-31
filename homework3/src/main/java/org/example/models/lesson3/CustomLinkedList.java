package org.example.models.lesson3;

import org.example.interfaces.CustomIterator;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;

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

    public CustomIterator<T> iterator() {
        return new CustomIteratorImpl();
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

    public boolean remove(T removeNode) {
        var node = getNode(removeNode);
        if (node != null) {
            removeNode(node);
        }
        return node != null;
    }

    public boolean contains(T node) {
        return getNode(node) != null;
    }

    public boolean addAll(Collection<? extends T> c) {
        for (var elem : c) {
            add(elem);
        }
        return true;
    }

    private ListNode<T> getNode(T node) {
        var temp = first;
        while (temp != null) {
            if (Objects.equals(temp.getValue(), node)) {
                return temp;
            }
            temp = temp.getNextNode();
        }
        return null;
    }

    private ListNode<T> getNode(long index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException();
        if (index < size / 2) {
            var temp = first;
            for (long i = 0; i < index; ++i) {
                temp = temp.getNextNode();
            }
            return temp;
        } else {
            var temp = last;
            for (long i = size - 1; i > index; --i) {
                temp = temp.getPreviousNode();
            }
            return temp;
        }
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

    private class CustomIteratorImpl implements CustomIterator<T> {
        private ListNode<T> current = first;

        @Override
        public boolean hasNext() {
            return current != null;
        }

        @Override
        public T next() {
            if (current == null) {
                throw new NoSuchElementException();
            }
            T value = current.getValue();
            current = current.getNextNode();
            return value;
        }

        @Override
        public void forEachRemaining(Consumer<? super T> action) {
            while (current != null) {
                action.accept(current.getValue());
                current = current.getNextNode();
            }
        }
    }
}
