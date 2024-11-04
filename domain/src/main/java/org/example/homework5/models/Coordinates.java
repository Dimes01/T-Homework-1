package org.example.homework5.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Coordinates {
    @JsonProperty("lat")
    private Double lat;

    @JsonProperty("lon")
    private Double lon;
}
