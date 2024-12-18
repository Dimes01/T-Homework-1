package org.example;

import org.example.models.lesson3.CustomLinkedList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class CustomLinkedListTest {
    private CustomLinkedList<Integer> list;

    @BeforeEach
    public void setUp() {
        list = new CustomLinkedList<>();
    }

    // ---------- Тесты, связанные с инициализацией -----------------------------------------------------------------
    @Test
    public void testConstructorWithCollection() {
        var integers = Arrays.asList(1, 2, 3);
        list = new CustomLinkedList<>(integers);
        assertEquals(3, list.size());
        for (int i = 0; i < integers.size(); i++) {
            assertEquals(integers.get(i), list.get(i));
        }
    }

    @Test
    public void testSizeInitially() {
        assertEquals(0, list.size());
    }

    // ---------- Методы добавления элементов ----------------------------------------------------------------------
    @Test
    public void testAdd() {
        list.add(1);
        assertEquals(1, list.get(0));
        assertEquals(1, list.size());
    }

    @Test
    public void testAddMultiple() {
        list.add(1); list.add(2); list.add(3);
        assertEquals(3, list.size());
        assertEquals(1, list.get(0));
        assertEquals(2, list.get(1));
        assertEquals(3, list.get(2));
    }

    @Test
    public void testAddAll() {
        var elements = Arrays.asList(1, 2, 3, 4, 5);
        list.addAll(elements);
        assertEquals(5, list.size());
        for (int i = 0; i < elements.size(); i++) {
            assertEquals(elements.get(i), list.get(i));
        }
    }

    // ---------- Методы удаления элементов ----------------------------------------------------------------------
    @Test
    public void testRemoveByIndex() {
        list.add(1); list.add(2); list.add(3);
        assertEquals(2, list.remove(1));
        assertEquals(2, list.size());
        assertEquals(1, list.get(0));
        assertEquals(3, list.get(1));
    }

    @Test
    public void testRemoveByObject() {
        list.add(1);
        list.add(2);
        list.add(3);
        assertTrue(list.remove(Integer.valueOf(2)));
        assertEquals(2, list.size());
        assertEquals(1, list.get(0));
        assertEquals(3, list.get(1));
        assertFalse(list.contains(2));
    }

    @Test
    public void testRemoveFromEmptyList() {
        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(0));
        assertFalse(list.remove(Integer.valueOf(1)));
    }

    @Test
    public void testRemoveNonExistentObject() {
        list.add(1);
        list.add(2);
        assertFalse(list.remove(Integer.valueOf(3)));
        assertEquals(2, list.size());
    }

    // ---------- Методы получения элементов ----------------------------------------------------------------------
    @Test
    public void testGet() {
        list.add(1);
        list.add(2);
        list.add(3);
        assertEquals(1, list.get(0));
        assertEquals(2, list.get(1));
        assertEquals(3, list.get(2));
    }

    @Test
    public void testGetFromEmptyList() {
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(0));
    }

    @Test
    public void testIndexOutOfBounds() {
        CustomLinkedList<Integer> list = new CustomLinkedList<>(Arrays.asList(1, 2, 3));
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(3));
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(3));
        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(-1));
    }

    @Test
    public void testContains() {
        list.add(1);
        list.add(2);
        assertTrue(list.contains(1));
        assertTrue(list.contains(2));
        assertFalse(list.contains(3));
    }

    // ---------- Итератор ----------------------------------------------------------------------

    private void initForIteratorTests() {
        list.addAll(Arrays.asList(1, 2, 3));
    }

    @Test
    void hasNext_allSituations() {
        // Arrange
        initForIteratorTests();
        var iterator = list.iterator();

        // Act & Assert
        // Good situations
        for (int i = 0; i < list.size(); ++i) {
            assertTrue(iterator.hasNext());
            iterator.next();
        }
        // Bad situation
        assertFalse(iterator.hasNext());
    }

    @Test
    void next_allSituations() {
        // Arrange & Act
        initForIteratorTests();
        var iterator = list.iterator();

        // Assert
        // Good situations
        for (int i = 0; i < list.size(); ++i) {
            assertEquals(list.get(i), iterator.next());
        }
        // Bad situations
        assertThrows(NoSuchElementException.class, iterator::next);
    }

    @Test
    void forEachRemaining_notEmptyIterator() {
        // Arrange
        initForIteratorTests();
        var iterator = list.iterator();

        // Act
        // Пропуск первого элемента
        iterator.next();

        // Сохранение оставшихся элементов с помощью forEachRemaining
        List<Integer> remainingElements = new ArrayList<>();
        Consumer<Integer> consumer = remainingElements::add;
        iterator.forEachRemaining(consumer);

        // Assert
        assertEquals(List.of(2, 3), remainingElements);
    }

    @Test
    void forEachRemaining_emptyIterator() {
        // Arrange
        initForIteratorTests();
        var emptyList = new CustomLinkedList<Integer>();
        var iterator = emptyList.iterator();
        List<Integer> elements = new ArrayList<>();

        // Act
        iterator.forEachRemaining(elements::add);

        // Assert
        assertTrue(elements.isEmpty());
    }
}
