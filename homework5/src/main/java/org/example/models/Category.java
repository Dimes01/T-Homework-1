package org.example.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Category {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("slug")
    private String slug;

    @JsonProperty("name")
    private String name;
}

