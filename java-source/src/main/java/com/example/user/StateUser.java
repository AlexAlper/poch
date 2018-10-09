package com.example.user;

import com.example.DTO.UserDTO;
import net.corda.core.contracts.LinearState;
import net.corda.core.schemas.QueryableState;
import net.corda.core.serialization.CordaSerializable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import net.corda.core.schemas.MappedSchema;
import net.corda.core.schemas.PersistentState;
import org.jetbrains.annotations.NotNull;
import com.example.schema.DocSchemaV2;

import java.util.List;
import java.util.Set;

@CordaSerializable

public class StateUser implements LinearState, QueryableState{
    private final UniqueIdentifier linearId;
    private final String cor_id;
    private final String bill;

    private final Set<Party> parties;



    public StateUser(String cor_id, String bill,  Set<Party> parties, UniqueIdentifier linearId){
        this.linearId = linearId;
        this.cor_id = cor_id;
        this.bill = bill;
        this.parties = parties;
    }


    public String getCor_id(){
        return  cor_id;
    }

    public String getBill(){
        return bill;
    }


    @NotNull
    @Override
    public UniqueIdentifier getLinearId() {
        return linearId;
    }

    @NotNull
    @Override
    public Iterable<MappedSchema> supportedSchemas() {
        return ImmutableList.of(new DocSchemaV2());
    }

    @NotNull
    @Override
    public PersistentState generateMappedObject(MappedSchema schema) {
        if (schema instanceof DocSchemaV2) {
            return new DocSchemaV2.PersistentDoc(
                    this.linearId.getId(),
                    this.cor_id,
                    this.bill

            );
        } else {
            //throw new IllegalAccessException("Неопознанная схема $schema");
            return null;
        }
    }


    @NotNull
    @Override
    public List<AbstractParty> getParticipants() {
        return Lists.newArrayList(this.parties);
    }

    public UserDTO createDTO() {
        UserDTO CorStateDTO = new UserDTO();
        CorStateDTO.setLinearId(this.linearId);
        CorStateDTO.setCor_id(this.cor_id);
        CorStateDTO.setBill(this.bill);
        return CorStateDTO;
    }



    public Set<Party> getParties() {
        return parties;
    }
}
