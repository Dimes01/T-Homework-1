package org.example;

import lombok.NonNull;
import models.lesson3.CustomLinkedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        LOGGER.info("Method 'main' started");

        var customList = new CustomLinkedList<Integer>();
        LOGGER.debug("An instance 'customList' of CustomLinkedList is created");

        // add
        customList.add(1);
        LOGGER.debug("A value 1 added to 'customList'");

        // get
        LOGGER.debug(String.format("A value %d is getting from 'customList'", customList.get(0)));

        // remove
        customList.remove(0);
        LOGGER.debug("An element with index 0 removed from 'customList'");

        // contains
        if (customList.contains(1)) {
            LOGGER.debug("An element 1 contains in 'customList'");
        } else {
            LOGGER.debug("An element 1 does not contain in 'customList'");
        }

        // addAll
        var tempList = new LinkedList<Integer>();
        for (int i = 0; i < 5; ++i) {
            tempList.add(i);
            LOGGER.debug(String.format("Value %d added to 'tempList'", i));
        }
        customList.addAll(tempList);
        LOGGER.debug("A collection added to 'customList'");

        // stream
        var stream = Stream.of(1, 2, 3, 4, 5);
        LOGGER.debug("Stream is maked");
        CustomLinkedList<Integer> list = stream.reduce(
                new CustomLinkedList<>(),
                (acc, elem) -> {
                    acc.add(elem);
                    return acc;
                },
                (acc1, acc2) -> {
                    for (int i = 0; i < acc2.size(); ++i) {
                        acc1.add(acc2.get(i));
                    }

                    return acc1;
                }
        );
        LOGGER.debug(String.format("Stream converted to CustomLinkedList with size: %d", list.size()));
        for (int i = 0; i < list.size(); ++i) {
            LOGGER.debug(String.format("%d value is %d", i, list.get(i)));
        }

        LOGGER.info("Method 'main' finished");
    }
}