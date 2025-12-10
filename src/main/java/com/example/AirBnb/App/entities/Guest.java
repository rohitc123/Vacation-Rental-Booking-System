package com.example.AirBnb.App.entities;

import com.example.AirBnb.App.entities.enums.Gender;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Guest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(nullable = false)
    private String name;

    private Integer age;
}
