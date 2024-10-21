package org.example.models.homework10;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "places")
public class Place {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String slug;
    private String name;
    private String timezone;
    private String language;
    private String currency;

    @OneToMany(mappedBy = "placeId", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Event> events;
}
