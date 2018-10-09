package com.example.com.template;

import com.google.common.collect.ImmutableMap;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.messaging.CordaRPCOps;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;

// This API is accessible from /api/template. The endpoint paths specified below are relative to it.
@Path("template")
public class TemplateApi {
    private final CordaRPCOps rpcOps;
    private final CordaX500Name myLegalName;

    public TemplateApi(CordaRPCOps services) {
        this.rpcOps = services;
        this.myLegalName = rpcOps.nodeInfo().getLegalIdentities().get(0).getName();
    }

    /**
     * Returns the node's name.
     */
    @GET
    @Path("me")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, CordaX500Name> whoami() {
        return ImmutableMap.of("me", myLegalName);
    }

    /**
     * Accessible at /api/template/templateGetEndpoint.
     */
    @GET
    @Path("templateGetEndpoint")
    @Produces(MediaType.APPLICATION_JSON)
    public Response templateGetEndpoint() {
        return Response.ok("Template GET endpoint.").build();
    }
}
