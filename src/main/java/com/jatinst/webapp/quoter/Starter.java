package com.jatinst.webapp.quoter;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import com.jatinst.webapp.quoter.dao.Dao;
import com.jatinst.webapp.quoter.dao.StuffDao;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

public class Starter extends GuiceServletContextListener {

    @Override
    protected Injector getInjector() {
        return Guice.createInjector(new ServletModule() {
            @Override
            protected void configureServlets() {

                bind(new TypeLiteral<Dao<String>>() {
                }).to(StuffDao.class);

                ResourceConfig rc = new PackagesResourceConfig("com.jatinst.webapp.quoter");
                for (Class<?> resource : rc.getClasses()) {
                    bind(resource);
                }

                serve("/services/*").with(GuiceContainer.class);
            }
        });
    }
}
