package utilities;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.common.Json;
import org.example.homework5.models.Category;
import org.example.homework5.models.Location;
import org.example.services.KudaGOService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.RestClient;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;
import org.wiremock.integrations.testcontainers.WireMockContainer;

import java.util.Arrays;
import java.util.concurrent.Semaphore;

class InitializerTest {
    private static final Category[] responseBodyCategories = new Category[] {
            new Category(1L, "cat1", "category1"),
            new Category(2L, "cat2", "category2"),
    };
    private static final Location[] responseBodyLocations = new Location[] {
            new Location("cat1", "name1", "timezone1", "language1", "currency1"),
            new Location("cat2", "name2", "timezone2", "language2", "currency2"),
    };
    private static final String responseBodyCategoriesString = Json.write(responseBodyCategories);
    private static final String responseBodyLocationsString = Json.write(responseBodyLocations);

    @Autowired
    private static Semaphore semaphore;
    private static RestClient restClient;
    private static KudaGOService kudaGOService;

    @Container
    private static final WireMockContainer wireMockServer = new WireMockContainer(DockerImageName.parse("wiremock/wiremock:3.9.1"))
            .withExposedPorts(8080);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("kudago.baseUrl", () -> "http://" + wireMockServer.getHost() + ":" + wireMockServer.getFirstMappedPort());
    }

    @BeforeAll
    public static void setUp() {
        wireMockServer.start();
        var baseUrl = "http://" + wireMockServer.getHost() + ":" + wireMockServer.getFirstMappedPort();
        restClient = RestClient.builder().baseUrl(baseUrl).build();
        kudaGOService = new KudaGOService(restClient, semaphore);

        WireMock.configureFor(wireMockServer.getHost(), wireMockServer.getFirstMappedPort());

        WireMock.stubFor(WireMock.get(WireMock.urlPathEqualTo("/public-api/v1.4/place-categories/"))
                .withQueryParam("lang", WireMock.equalTo("ru"))
                .willReturn(WireMock.aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseBodyCategoriesString)
                )
        );

        WireMock.stubFor(WireMock.get(WireMock.urlPathEqualTo("/public-api/v1.4/locations/"))
                .withQueryParam("lang", WireMock.equalTo("ru"))
                .withQueryParam("fields", WireMock.equalTo("slug,name,timezone,coords,language,currency"))
                .willReturn(WireMock.aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseBodyLocationsString)
                )
        );
    }

    @AfterAll
    public static void tearDown() {
        wireMockServer.stop();
    }

    @Test
    public void run() {
        // Arrange
        // Act
        var categories = kudaGOService.getCategories();
        var locations = kudaGOService.getLocations();

        // Assert
        assertEquals(Arrays.stream(responseBodyCategories).toList(), categories);
        assertEquals(Arrays.stream(responseBodyLocations).toList(), locations);
    }
}
