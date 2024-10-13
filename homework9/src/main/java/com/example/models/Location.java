package com.example.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Location {
    @JsonProperty("slug")
    private String slug;

    @JsonProperty("name")
    private String name;

    @JsonProperty("timezone")
    private String timezone;

//    @JsonProperty("coords")
//    private Coordinates coordinates;

    @JsonProperty("language")
    private String language;

    @JsonProperty("currency")
    private String currency;
}
