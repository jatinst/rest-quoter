package com.jatinst.webapp.quoter;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import com.jatinst.webapp.quoter.dao.Dao;
import com.jatinst.webapp.quoter.dao.StuffDao;

public class Starter extends GuiceServletContextListener {

    // added the static because the HK2 - Guice Bridge needs this injector and most examples seem to use it this way
    // This unfortunately seems to have some fun side effects...like the Listener has to be initialized before the
    // actual app
    // both in the web.xml as well as the unit tests. Really silly behavior to be honest!
    // TODO - figure out a better way to handle this!
    public static Injector injector;

    @Override
    protected Injector getInjector() {
        System.out.println("Getting injector");

        injector = Guice.createInjector(new ServletModule() {
            @Override
            protected void configureServlets() {

                bind(new TypeLiteral<Dao<String>>() {
                }).to(StuffDao.class);

                /*
                 * //Below only works with Jersey 1.x and guice, need to use the HK2 bridge - so not using this anymore
                 * for Jersey 2.X ResourceConfig rc = new PackagesResourceConfig("com.jatinst.webapp.quoter"); for
                 * (Class<?> resource : rc.getClasses()) { bind(resource); }
                 * 
                 * serve("/services/*").with(GuiceContainer.class);
                 */
            }
        });

        return injector;
    }
}
