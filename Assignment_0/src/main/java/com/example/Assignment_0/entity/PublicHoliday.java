package com.example.Assignment_0.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class PublicHoliday {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "holiday_date")
    private LocalDate holidayDate;

    @Column(name = "description")
    private String description;
}
