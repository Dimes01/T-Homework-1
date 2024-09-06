import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import models.City;
import org.example.Main;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MainTest {
    @Mock
    private Logger LOGGER = mock(Logger.class);

    @Mock
    private ObjectMapper objectMapper = mock(ObjectMapper.class);

    @Mock
    private XmlMapper xmlMapper = mock(XmlMapper.class);


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        reset(LOGGER);
        reset(objectMapper);
        reset(xmlMapper);
    }

    @Test
    public void test_JSON_FileDoesNotExist() {
        File file = mock(File.class);
        when (file.exists()).thenReturn(false);

        City city = Main.fromJSON(file);

        assertNull(city);
    }

    @Test
    public void test_JSON_FileCannotRead() {
        File file = mock(File.class);
        when (file.exists()).thenReturn(true);
        when (file.canRead()).thenReturn(false);

        City city = Main.fromJSON(file);

        assertNull(city);
    }

//    @Test
//    public void test_JSON_ReadIOException() throws IOException {
//        File file = mock(File.class);
//        when (file.exists()).thenReturn(true);
//        when (file.canRead()).thenReturn(true);
//        var exception = new IOException();
//        when (objectMapper.readValue(file, City.class)).thenThrow(exception);
//
//        City city = main.fromJSON(file);
//
//        assertNull(city);
//    }
//
//    @Test
//    public void test_JSON_ReadSuccess() throws IOException {
//        File file = mock(File.class);
//        when (file.exists()).thenReturn(true);
//        when (file.canRead()).thenReturn(true);
//
//        var expectedCity = new City();
//        when (objectMapper.readValue(file, City.class)).thenReturn(expectedCity);
//
//        City city = main.fromJSON(file);
//
//        assertNotNull(city);
//        assertEquals(expectedCity, city);
//    }

//    @Test
//    public void test_XML_FileDoesNotExist() {
//
//    }
//
//    @Test
//    public void test_XML_FileCannotWrite() {
//
//    }
//
//    @Test
//    public void test_XML_WriteIOException() {
//
//    }
//
//    @Test
//    public void test_XML_WriteSuccess() {
//
//    }
}
