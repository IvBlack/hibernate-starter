package com.IVdev.util;

import com.IVdev.converter.BirthdayConverter;
import lombok.experimental.UtilityClass;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

//вынесем функционал для сессий из HibernateRunner в отдельный класс
//для удобства тестирования + чистоты кода
@UtilityClass
public class HibernateUtil {
    public static SessionFactory buildSessionFactory() {

        Configuration configuration = new Configuration();
//        configuration.setPhysicalNamingStrategy(new CamelCaseToUnderscoresNamingStrategy());
//        configuration.addAnnotatedClass(User.class);

        //автоустановка кастомного конвертера
        //hibernate автоматически применит конвертер ко всем полям Birthday в сущностях
        configuration.addAttributeConverter(BirthdayConverter.class, true);

//        configuration.registerTypeOverride(new JsonBinaryType());
        configuration.configure();
        return configuration.buildSessionFactory();
    }
}
