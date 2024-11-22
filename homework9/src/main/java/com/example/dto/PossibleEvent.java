package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.example.models.EventDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PossibleEvent {
    private String eventName;
    private EventDate[] dates;
    private int minPrice;
    private int maxPrice;
}
