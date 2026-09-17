package com.dsf.librar.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "supplier")
public class Supplier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "cellphone")
    private String cellphone;
    @Column(name = "phone")
    private String phone;
    @Column(name = "email")
    private String email;

    @Column(name = "note")
    private String note;

    @Column(nullable = false)
    private Boolean active = true;
}