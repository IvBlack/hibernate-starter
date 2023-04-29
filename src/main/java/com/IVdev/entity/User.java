package com.IVdev.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

//POJO-class с условиями есть сущность: неImmutable, private-поля, пустой конструктор, @Entity, @Id

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "users", schema = "public")
public class User {
    @Id
    private String username;
    private Integer age;
    @Column(name = "birth_date")
    private LocalDate date;
    private String firstname;
    private String lastname;
    private Role role;
}
