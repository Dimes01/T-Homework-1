package com.example.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Event {
    private int id;
    private String name;
    private int budget;
    private String currency;
    private Date dateFrom;
    private Date dateTo;
}
