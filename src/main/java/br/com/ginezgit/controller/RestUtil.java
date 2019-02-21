package br.com.ginezgit.controller;

import jersey.repackaged.com.google.common.collect.Maps;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;

public class RestUtil {

    public static Response badRequest(Object entity) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(Maps.immutableEntry("details", entity))
                .type( MediaType.APPLICATION_JSON)
                .build();
    }

    public static Response unauthorized(Object entity) {
        return Response.status(Response.Status.UNAUTHORIZED)
                .entity(Maps.immutableEntry("details", entity))
                .type( MediaType.APPLICATION_JSON)
                .build();
    }

    public static Response error() {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Maps.immutableEntry("details", "An error has ocurred on our application, please try again later or contact our support team"))
                .type( MediaType.APPLICATION_JSON)
                .build();
    }

}
