package models;

import lombok.Data;

@Data
public class City {
    private String slug;
    private Coordinates coords;
}
