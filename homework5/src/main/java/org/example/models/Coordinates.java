package org.example.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Coordinates {
    @JsonProperty("lat")
    private Double lat;

    @JsonProperty("lon")
    private Double lon;
}
