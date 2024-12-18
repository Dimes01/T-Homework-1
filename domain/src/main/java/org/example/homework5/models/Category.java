package org.example.homework5.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
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

