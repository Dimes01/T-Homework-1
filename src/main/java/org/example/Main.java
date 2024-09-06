package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import models.City;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final XmlMapper xmlMapper = new XmlMapper();
    private static final String[] fileNames = new String[]{ "city.json", "city-error.json" };

    public static void main(String[] args) {
        LOGGER.info("Method 'main' started");

        for (var fileName : fileNames) {
            var fileJson = new File(String.format("src/main/resources/%s", fileName));
            var city = fromJSON(fileJson);
            if (city != null) {
                var nameFileXml = fileName.substring(0, fileName.length() - 5) + ".xml";
                var fileXml = new File(String.format("src/main/resources/%s", nameFileXml));
                try {
                    if (fileXml.createNewFile()) {
                        LOGGER.info("The file with extension .xml is created");
                    } else {
                        LOGGER.info("The file with extension .xml already exists");
                    }
                } catch (IOException e) {
                    LOGGER.error("The file with extension .xml could not be created");
                    LOGGER.error(e.getMessage());
                }
                toXML(fileXml, city);
            } else {
                LOGGER.warn("The nullable object is not going to write");
            }
        }

        LOGGER.info("Method 'main' finished");
    }

    public static City fromJSON(File file) {
        LOGGER.info("Method 'fromJSON' started");
        LOGGER.debug(String.format("File object: %s", file));
        City city = null;
        if (!file.exists()) {
            LOGGER.warn(String.format("File '%s' does not exist", file.getName()));
        } else if (!file.canRead()) {
            LOGGER.warn(String.format("File '%s' can not read", file.getName()));
        } else {
            try {
                city = objectMapper.readValue(file, City.class);
                LOGGER.info(String.format("The object has read from file '%s'", file.getName()));
            } catch (IOException e) {
                LOGGER.error(String.format("The file '%s' could not be read", file.getName()));
                LOGGER.error(e.getMessage());
            }
        }
        LOGGER.debug(String.format("The returned value is %s", city));
        LOGGER.info("Method 'fromJSON' finished");
        return city;
    }

    public static void toXML(File file, City city) {
        LOGGER.info("Method 'toXML' started");
        LOGGER.debug(String.format("File object: %s", file));
        LOGGER.debug(String.format("City object: %s", city));
        if (!file.exists()) {
            LOGGER.warn(String.format("File '%s' does not exist", file.getName()));
        } else if (!file.canWrite()) {
            LOGGER.warn(String.format("File '%s' can not write", file.getName()));
        } else {
            try {
                xmlMapper.writeValue(file, city);
                LOGGER.info(String.format("The object has written in file '%s'", file.getName()));
            } catch (IOException e) {
                LOGGER.error(String.format("The file '%s' could not be write", file.getName()));
                LOGGER.error(e.getMessage());
            }
        }

        LOGGER.info("Method 'toXML' finished");
    }
}