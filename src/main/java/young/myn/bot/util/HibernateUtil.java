package young.myn.bot.util;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
    private static final SessionFactory mySessionFactory;

    static{
        try {
            Configuration configuration = new Configuration();
            configuration.configure();

            mySessionFactory = configuration.buildSessionFactory();
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }
    public static Session getSession() throws HibernateException{
        return mySessionFactory.openSession();
    }
}
