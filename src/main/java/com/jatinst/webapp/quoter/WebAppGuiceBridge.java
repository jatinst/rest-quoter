package com.jatinst.webapp.quoter;

import javax.inject.Inject;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.server.ResourceConfig;
import org.jvnet.hk2.guice.bridge.api.GuiceBridge;
import org.jvnet.hk2.guice.bridge.api.GuiceIntoHK2Bridge;

public class WebAppGuiceBridge extends ResourceConfig {

    @Inject
    public WebAppGuiceBridge(ServiceLocator serviceLocator) {
        // Set package to look for resources in
        packages(Starter.class.getPackage().toString());

        System.out.println("Registering injectables...");

        GuiceBridge.getGuiceBridge().initializeGuiceBridge(serviceLocator);

        GuiceIntoHK2Bridge guiceBridge = serviceLocator.getService(GuiceIntoHK2Bridge.class);
        guiceBridge.bridgeGuiceInjector(Starter.injector);

    }
}
