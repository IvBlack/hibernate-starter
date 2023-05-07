package com.IVdev;

import com.IVdev.converter.BirthdayConverter;
import com.IVdev.entity.Birthday;
import com.IVdev.entity.Role;
import com.IVdev.entity.User;
import com.IVdev.type.JsonType;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.sql.SQLException;
import java.time.LocalDate;

public class HibernateRunner {
    public static void main(String[] args) throws SQLException {

        //**********подключение к БД через Driver Manager************

//        создание пула подключений
//        BlockingDeque<Connection> pool = null;
//        Connection connection = pool.take();

//        коннект к БД через параметры
//        Connection connection = DriverManager
//                .getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "pass2023");


        //*********обертки-аналоги для connection/pool через hibernate, на основе cfg.xml********
        Configuration configuration = new Configuration();
//        configuration.setPhysicalNamingStrategy(new CamelCaseToUnderscoresNamingStrategy());
//        configuration.addAnnotatedClass(User.class);

        //автоустановка кастомного конвертера
        //hibernate автоматически применит конвертер ко всем полям Birthday в сущностях
        configuration.addAttributeConverter(BirthdayConverter.class, true);
        configuration.registerTypeOverride(new JsonBinaryType());
        configuration.configure();

        //возвращает фабрику на основании всех полей класса Configuration + cfg.xml
        //один экземпляр на все приложение, как и connection pool
        try(SessionFactory sf = configuration.buildSessionFactory();

            //обертка для connection на уровне hibernate для управления сущностями
            Session ss = sf.openSession()) {
            ss.beginTransaction();

            //создадим сущность и сохраним в БД
            User user = User.builder()
                    .username("ivan@ya.ru")
                    .birthDate(new Birthday(LocalDate.of(2020, 1, 15)))
                    .firstname("IV")
                    .lastname("B.")
                    .role(Role.ADMIN)
                    .info("""
                            {
                                "name": "Ivan",
                                "id": 25
                            }
                            """)
                    .build();
            ss.persist(user);

            ss.getTransaction().commit();
        }
    }
}
