package com.IVdev;

import com.IVdev.entity.Birthday;
import com.IVdev.entity.Role;
import com.IVdev.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.joining;

//скрытая работа hibernate, используя reflectionAPI по запросу SQL.
//код вынесен в тест.

class HibernateRunnerTest {

    @Test
    void checkGetReflectionApi() throws SQLException, NoSuchMethodException, InvocationTargetException,
            InstantiationException, IllegalAccessException,
            NoSuchFieldException {

        //get columns from DB
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.getString("username");
        resultSet.getString("firstname");
        resultSet.getString("lastname");

        //entity init
        Class<User> clazz = User.class;

        //object init
        Constructor<User> constructor = clazz.getConstructor();
        User user = constructor.newInstance();
        //push values through empty constructor
        Field usernameField = clazz.getDeclaredField("username");
        usernameField.setAccessible(true);
        usernameField.set(user, resultSet.getString("username"));
    }

    @Test
    void checkReflectionAPI() throws SQLException, IllegalAccessException {
        User user = User.builder()
                .username("ivan@ya.ru")
                .birthDate(new Birthday(LocalDate.of(2020, 1, 15)))
                .firstname("IV")
                .lastname("B.")
                .role(Role.ADMIN)
                .build();

        //возьмем всю метаинфо через reflectionAPI об этом классе
        //получим вручную и смапим имя таблицы, поля для вставки в запрос
        String sql = """
                insert
                into
                %s
                (%s)
                values
                (%s)
                """;
        String tableName = ofNullable(user.getClass().getAnnotation(Table.class))
                .map(tableAnnotation -> tableAnnotation.schema()+"."+tableAnnotation.name())
                .orElse(user.getClass().getName());

        Field[] declaredFields = user.getClass().getDeclaredFields();

        String columnNames = Arrays.stream(declaredFields)
                .map(field -> ofNullable(field.getAnnotation(Column.class))
                        .map(Column::name)
                        .orElse(field.getName()))
                .collect(joining(", "));

        //значение каждого поля смапим на скрытое поле preparedStatement "?" JDBC
        String columnValues = Arrays.stream(declaredFields)
                .map(field -> "?")
                .collect(joining(", "));

        //форматирование полученного sql
        System.out.println(sql.formatted(tableName, columnNames, columnValues));

        Connection connection = null;
        PreparedStatement prStatement = connection.prepareStatement(sql.formatted(tableName, columnNames, columnValues));

        //для каждого атрибута preparedStatement установим значение
        for (Field declaredField : declaredFields) {
            declaredField.setAccessible(true);
            //приведение типа поля на каждой итерации - автоматически, под капотом.
            prStatement.setObject(1, declaredField.get(user));
        }
    }
}