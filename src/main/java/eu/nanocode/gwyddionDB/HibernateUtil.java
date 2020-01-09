package eu.nanocode.gwyddionDB;

import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

public class HibernateUtil {
    private static SessionFactory sessionFactory;
    private static String pass;
    
    public static void setPass(String password) {
    	pass = password;
    }
    
    private static SessionFactory buildSessionFactory() 
    {
        try {
        	Configuration cfg = new Configuration();
        	cfg.configure("\\hibernate.properties\\hibernate.cfg.xml");
        	if(pass!=null) {
        		cfg.getProperties().setProperty("hibernate.connection.password", pass);
        	}
        	/*
        	ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()//
                    .configure("\\hibernate.properties\\hibernate.cfg.xml").build();
        	Metadata metadata = new MetadataSources(serviceRegistry).getMetadataBuilder().build();
        	return metadata.getSessionFactoryBuilder().build();*/
        	return cfg.buildSessionFactory();
 
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }
 
    public static SessionFactory getSessionFactory() {
    	sessionFactory = buildSessionFactory();
        return sessionFactory;
    }
 
    public static void shutdown() {
        getSessionFactory().close();
    }

}
