package org.example;

import lombok.NonNull;
import models.lesson3.CustomLinkedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        LOGGER.info("Method 'main' started");

        var customList = new CustomLinkedList<Integer>();
        LOGGER.debug("An instance 'customList' of CustomLinkedList is created");

        customList.add(1);
        LOGGER.debug("A value 1 added to 'customList'");

        LOGGER.debug(String.format("A value %d is getting from 'customList'", customList.get(0)));

        customList.remove(0);
        LOGGER.debug("An element with index 0 removed from 'customList'");

        if (customList.contains(1)) {
            LOGGER.debug("An element 1 contains in 'customList'");
        } else {
            LOGGER.debug("An element 1 does not contain in 'customList'");
        }

        var tempList = new LinkedList<Integer>();
        for (int i = 0; i < 5; ++i) {
            tempList.add(i);
            LOGGER.debug(String.format("Value %d added to 'tempList'", i));
        }
        customList.addAll(tempList);
        LOGGER.debug("A collection added to 'customList'");

        LOGGER.info("Method 'main' finished");
    }
}