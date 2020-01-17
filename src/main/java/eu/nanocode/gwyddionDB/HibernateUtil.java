package eu.nanocode.gwyddionDB;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
    private static SessionFactory sessionFactory;
    private static SQLConn connSettings;
    
    public static void setconnSettings(SQLConn conn) {
    	connSettings = conn;
    }
    
    private static SessionFactory buildSessionFactory() 
    {
        try {
        	Configuration cfg = new Configuration();
        	cfg.configure("\\hibernate.properties\\hibernate.cfg.xml");
        	if(connSettings!=null) {
        		cfg.getProperties().setProperty("hibernate.connection.username", connSettings.user);
        		cfg.getProperties().setProperty("hibernate.connection.password", connSettings.password);
        		
        		if(connSettings.conifg!=SQLConn.SQLConfig.Other) {
            		cfg.getProperties().setProperty("hibernate.connection.driver_class", connSettings.conifg.driver_class);
            		cfg.getProperties().setProperty("hibernate.dialect", connSettings.conifg.dialect);
            		cfg.getProperties().setProperty("hibernate.connection.url", connSettings.conifg.urlPrefix + connSettings.url + (connSettings.useSSL ? "" : "?useSSL=false") );
        		}
        		else {
            		cfg.getProperties().setProperty("hibernate.connection.driver_class", connSettings.other_driver_class);
            		cfg.getProperties().setProperty("hibernate.dialect", connSettings.other_dialect);
            		cfg.getProperties().setProperty("hibernate.connection.url", connSettings.url + (connSettings.useSSL ? "" : "?useSSL=false") );
        		}	
        										
        	}
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
