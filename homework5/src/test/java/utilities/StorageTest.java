package utilities;

import org.example.models.Category;
import org.example.utilities.Storage;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class StorageTest {
    private Storage<Category> storage;
    private final Category[] initElements = new Category[]{
            new Category(1L, "slug1", "name1"),
            new Category(2L, "slug2", "name2"),
            new Category(3L, "slug3", "name3"),
    };

    private void set_up(Category[] elements) {
        storage = new Storage<>();
        for (int id = 1; id <= elements.length; ++id)
            storage.save((long) id, elements[id - 1]);
    }


    private Stream<Arguments> getAll_goodAndBadSituations() {
        return Stream.of(
                Arguments.of((Object) new Category[] {}),
                Arguments.of((Object) initElements)
        );
    }

    @ParameterizedTest
    @MethodSource("getAll_goodAndBadSituations")
    public void getAll(Category[] elements) {
        set_up(elements);
        assertEquals(Arrays.stream(elements).toList(), storage.getAll());
    }


    private Stream<Arguments> getById_goodAndBadSituations() {
        return Stream.of(
                Arguments.of(new Category[] {}, 1, null),
                Arguments.of(initElements, 0, null),
                Arguments.of(initElements, 1, initElements[0])
        );
    }

    @ParameterizedTest
    @MethodSource("getById_goodAndBadSituations")
    public void getById(Category[] elements, long id, Category expected) {
        set_up(elements);
        assertEquals(expected, storage.getById(id));
    }


    private Stream<Arguments> update_goodAndBadSituations() {
        var newElem = new Category(1L, "newSlug1", "newName1");
        return Stream.of(
                Arguments.of(new Category[] {}, 1, newElem, false),
                Arguments.of(initElements, 0, newElem, false),
                Arguments.of(initElements, 1, newElem, true)
        );
    }

    @ParameterizedTest
    @MethodSource("update_goodAndBadSituations")
    public void update(Category[] elements, long id, Category newElem, boolean expected) {
        set_up(elements);
        assertEquals(expected, storage.update(id, newElem));
    }


    private Stream<Arguments> delete_goodAndBadSituations() {
        return Stream.of(
                Arguments.of(new Category[] {}, 1, false),
                Arguments.of(initElements, 0, false),
                Arguments.of(initElements, 1, true)
        );
    }

    @ParameterizedTest
    @MethodSource("delete_goodAndBadSituations")
    public void delete(Category[] elements, long id, boolean expected) {
        // Arrange
        set_up(elements);
        var expectedSizeAfter = expected ? elements.length - 1 : elements.length;

        // Act
        // Assert
        assertEquals(expected, storage.delete(id));
        assertEquals(expectedSizeAfter, storage.getAll().size());
    }
}



