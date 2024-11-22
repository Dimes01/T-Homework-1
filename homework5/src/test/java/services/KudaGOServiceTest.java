package services;

import org.example.Homework5Application;
import org.example.models.Category;
import org.example.models.Location;
import org.example.services.KudaGOService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.RequestHeadersUriSpec;
import org.springframework.web.client.RestClientException;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = Homework5Application.class)
@Disabled
public class KudaGOServiceTest {

    @Mock
    private RestClient restClient;

    @Mock
    private RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private RestClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    private final KudaGOService kudaGOService = new KudaGOService(restClient);

    private static final Category[] expectedCategories = new Category[] {
            new Category(1L, "cat1", "category1"),
            new Category(2L, "cat2", "category2")
    };

    private static final Location[] expectedLocations = new Location[] {
            new Location("slug1", "name1", "timezone1", "language1", "currency1"),
            new Location("slug2", "name2", "timezone2", "language2", "currency2")
    };

    private static Stream<Arguments> categories_allSituations() {
        return Stream.of(
                Arguments.of((Object) expectedCategories),
                Arguments.of((Object) null)
        );
    }

    @ParameterizedTest
    @MethodSource("categories_allSituations")
    public void getCategories(Category[] inputArray) {
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(Mockito.anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(Category[].class)).thenReturn(inputArray);

        // Act
        var categories = kudaGOService.getCategories();
        var expectedSize = inputArray != null ? inputArray.length : 0;

        // Assert
        assertEquals(expectedSize, categories.size());
        for (int i = 0; i < expectedSize; ++i) {
            assertEquals(inputArray[i], categories.get(i));
        }
    }


    private static Stream<Arguments> locations_allSituations() {
        return Stream.of(
                Arguments.of((Object) expectedLocations),
                Arguments.of((Object) null)
        );
    }

    @ParameterizedTest
    @MethodSource("locations_allSituations")
    public void getLocations(Location[] inputArray) {
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(Mockito.anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(Location[].class)).thenReturn(inputArray);

        // Act
        var categories = kudaGOService.getLocations();
        var expectedSize = inputArray != null ? inputArray.length : 0;

        // Assert
        assertEquals(expectedSize, categories.size());
        for (int i = 0; i < expectedSize; ++i) {
            assertEquals(inputArray[i], categories.get(i));
        }
    }


    @Test
    public void getCategories_throw_RestClientException() {
        // Arrange
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(Mockito.anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(Category[].class)).thenThrow(RestClientException.class);

        // Act
        // Assert
        assertThrows(RestClientException.class, () -> kudaGOService.getCategories());
    }

    @Test
    public void getLocations_throw_RestClientException() {
        // Arrange
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(Mockito.anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(Location[].class)).thenThrow(RestClientException.class);

        // Act
        // Assert
        assertThrows(RestClientException.class, () -> kudaGOService.getLocations());
    }
}


