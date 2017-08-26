package com.rebate.common.property;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class SpringProfileActiveListener implements ServletContextListener {
    private static final String PROS_FILENAME = "profile.properties";


    @Override
    public void contextInitialized(ServletContextEvent sce) {

        try {
            InputStream resourceAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(PROS_FILENAME);
            Properties properties=new Properties();
            properties.load(resourceAsStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
