package com.IVdev.entity;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

//кастомный класс для кастомного типа в сущности User
public record Birthday(LocalDate birthDate) {

    //автоматический определитель возраста
    public long getAge() {
        return ChronoUnit.YEARS.between(birthDate, LocalDate.now());
    }
}
