package com.example.Assignment_0.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDate;

@Entity
@Data
public class Leave {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    @NonNull
    private Employee employee;

    @Column(name = "leave_date")
    @NonNull
    private LocalDate leaveDate;
}
