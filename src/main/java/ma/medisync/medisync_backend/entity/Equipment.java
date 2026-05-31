package com.medisync.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Equipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String serialNumber;
    @ManyToOne
    private Room assignedRoom;
}