package br.com.ginezgit;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

public class JerseyApp extends ResourceConfig {
    public JerseyApp() {
        packages(true, "br.com.ginezgit.controller");

        register(JacksonFeature.class);

    }
}
