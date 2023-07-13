package com.IVdev;

import com.IVdev.entity.User;
import com.IVdev.util.HibernateUtil;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

@Slf4j
public class HibernateRunner {
    //use Logger only from slf4j
    //not necessary if annotation @Slf4j created
    //private static final Logger log = LoggerFactory.getLogger(HibernateRunner.class);
    public static void main(String[] args) throws SQLException {

        //entity is transient
        User user = User.builder()
                .username("ivan@gmail.com")
                .lastname("Ivanov")
                .firstname("Ivan")
                .build();

        //returns a factory based on all fields of the Configuration class + cfg.xml
        //one instance for the entire application, just like the connection pool
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
