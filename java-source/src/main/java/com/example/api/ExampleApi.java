package com.example.api;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.node.NodeInfo;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.*;
import net.corda.core.transactions.SignedTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.example.core.helpers.*;
import com.example.flow.*;
import com.example.DTO.*;
import net.corda.core.messaging.FlowProgressHandle;
import java.util.ArrayList;
import com.example.user.*;


import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static java.util.stream.Collectors.toList;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.OK;

// This API is accessible from /api/example. All paths specified below are relative to it.
@Path("example")
public class ExampleApi {
    private final CordaRPCOps rpcOps;
    private final CordaX500Name myLegalName;

    private final List<String> serviceNames = ImmutableList.of("Notary");

    static private final Logger logger = LoggerFactory.getLogger(ExampleApi.class);

    public ExampleApi(CordaRPCOps rpcOps) {
        this.rpcOps = rpcOps;
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
     * Returns all parties registered with the [NetworkMapService]. These names can be used to look up identities
     * using the [IdentityService].
     */
    @GET
    @Path("peers")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, List<CordaX500Name>> getPeers() {
        List<NodeInfo> nodeInfoSnapshot = rpcOps.networkMapSnapshot();
        return ImmutableMap.of("peers", nodeInfoSnapshot
                .stream()
                .map(node -> node.getLegalIdentities().get(0).getName())
                .filter(name -> !name.equals(myLegalName) && !serviceNames.contains(name.getOrganisation()))
                .collect(toList()));
    }

    /**
     * Displays all IOU states that exist in the node's vault.
     */


    @GET
    @Path("get-user")
    @Produces(MediaType.APPLICATION_JSON)
    public List<UserDTO> getCorrespondents() {
        List<StateAndRef<StateUser>> allCorrespondents = rpcOps.vaultQuery(StateUser.class).getStates();
        List<UserDTO> resultList = new ArrayList<>();

        try {
            if (allCorrespondents != null) {
                for (StateAndRef<StateUser> sr : allCorrespondents) {
                    resultList.add(sr.getState().component1().createDTO());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultList;
    }

    @PUT
    @Path("create-user")
    public Response createCor(@QueryParam("cor_id") String cor_id,
                              @QueryParam("bill") String bill

    )
    {
        if (StringHelper.isEmptyOrWhitespace(cor_id) ||
                StringHelper.isEmptyOrWhitespace(bill)){
            return Response.status(BAD_REQUEST).entity("Полня должны быть заданы").build();
        }

        try {
            FlowProgressHandle<SignedTransaction> flowHandle = rpcOps
                    .startTrackedFlowDynamic(FlowUser.Initiator.class, cor_id, bill);
            flowHandle.getProgress().subscribe(evt -> System.out.printf(">> %s\n", evt));

            // The line below blocks and waits for the flow to return.
            final SignedTransaction result = flowHandle
                    .getReturnValue()
                    .get();

            final String msg = String.format("Transaction id %s committed to ledger.\n", result.getId());
            return Response.status(CREATED).entity(msg).build();

        } catch (Throwable ex) {
            final String msg = ex.getMessage();
            logger.error(ex.getMessage(), ex);
            return Response.status(BAD_REQUEST).entity(msg).build();
        }
    }

    @PUT
    @Path("change-user")
    public Response changeCor(UserDTO CorStateDTO) {
        if (CorStateDTO == null) return Response.status(BAD_REQUEST)
                .entity("Error: UserDTO = null. Передана пустая структура").build();

        try {
            //  Date CorCreateDate = DateHelper.createFromString(UserDTO.getCocCreate(), DateHelper.SIMPLE_DATE_FORMAT_RU);
            //CorCreateDate = docCreateDate != null ? docCreateDate : new Date();
            //Date planDate = DateHelper.createFromString(docStateDTO.getExecutePlanDate(), DateHelper.SIMPLE_DATE_FORMAT_RU);
            //planDate = planDate != null ? planDate : new Date();
            //Date factDate = DateHelper.createFromString(docStateDTO.getExecuteFactDate(), DateHelper.SIMPLE_DATE_FORMAT_RU);
            //factDate = factDate != null ? factDate : new Date();
//            Party employee = rpcOps.wellKnownPartyFromX500Name(docStateDTO.getEmployee());
//            if (employee == null) {
//                throw new NullPointerException("Employee not found: " +
//                        docStateDTO.getEmployee() != null ? docStateDTO.getEmployee().toString() : "null");
//            }

            FlowProgressHandle<SignedTransaction> flowHandle = rpcOps
                    .startTrackedFlowDynamic(EditFlowUser.Initiator.class,
                            StringHelper.getValueOrDefault(CorStateDTO.getCor_id()),
                            StringHelper.getValueOrDefault(CorStateDTO.getBill()),
                            CorStateDTO.getLinearId()

                    );
            flowHandle.getProgress().subscribe(evt -> System.out.printf(">> %s\n", evt));
            final SignedTransaction result = flowHandle
                    .getReturnValue()
                    .get();

            final String msg = String.format("Transaction id %s committed to ledger.\n", result.getId());
            return Response.status(CREATED).entity(msg).build();
        } catch (Throwable ex) {
            final String message = ex.getMessage();
            logger.error(ex.getMessage(), ex);
            return Response.status(BAD_REQUEST).entity("Message: " + message + " Details: " + ex.toString()).build();
        }
    }




}
