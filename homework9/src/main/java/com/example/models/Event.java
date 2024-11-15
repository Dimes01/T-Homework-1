package com.example.models;

import com.example.utilities.EventDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonDeserialize(using = EventDeserializer.class)
public class Event {
    private int id;
    private String name;
    private int minCost;
    private int maxCost;
    private EventDate[] dates;
    private int favoritesCount;
}
