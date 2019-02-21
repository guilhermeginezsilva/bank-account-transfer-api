package br.com.ginezgit;

import br.com.ginezgit.controller.CORSResponseFilter;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

public class JerseyApp extends ResourceConfig {
    public JerseyApp() {
        packages(true, "br.com.ginezgit.controller");

        register(JacksonFeature.class);
        register(CORSResponseFilter.class);

    }
}
