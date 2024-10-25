package org.example.homework9.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.homework9.models.EventDate;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PossibleEvent {
    private String eventName;
    private EventDate[] dates;
    private int minPrice;
    private int maxPrice;
}
