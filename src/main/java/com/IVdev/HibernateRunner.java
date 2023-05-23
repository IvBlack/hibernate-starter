package com.IVdev;

import com.IVdev.entity.User;
import com.IVdev.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.sql.SQLException;

public class HibernateRunner {
    public static void main(String[] args) throws SQLException {

        //entyty is transient
        User user = User.builder()
                .username("ivan@gmail.com")
                .lastname("Ivanov")
                .firstname("Ivan")
                .build();

        //возвращает фабрику на основании всех полей класса Configuration + cfg.xml
        //один экземпляр на все приложение, как и connection pool
        try(SessionFactory sf = HibernateUtil.buildSessionFactory()) {
            try(Session session1 = sf.openSession()) {
                session1.beginTransaction();

                //entity is in persistent state
                session1.persist(user);

                session1.getTransaction().commit();
            }
            try(Session session2 = sf.openSession()) {
                session2.beginTransaction();

                //works like: transient -> persistent(through select-method) -> removed
//                session2.remove(user);


                /*
                refresh getting actual info from DB and mapping all fields onto entity
                Literally next:

                User freshUser = get(User.class, user.getUsername());
                user.setLastname(freshUser.getLastname);
                user.setFirstname(freshUser.getFirstname);

                user.setFirstname("Oleg");
                session2.refresh(user);
                */


                /*
                merge working contrary: request to DB -> create new user -> set fields from old user
                Literally next:

                User freshUser = get(User.class, user.getUsername());
                freshUser.setLastname(user.getLastname);
                freshUser.setFirstname(user.getFirstname);

                Object mergedUser = session2.merge(user);
                */

                session2.getTransaction().commit();
            }
        }
    }
}
