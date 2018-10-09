package com.example.DTO;

import net.corda.core.contracts.UniqueIdentifier;

public class UserDTO {
    private UniqueIdentifier linearId;
    private String cor_id;
    private  String bill;

    public UserDTO() {
    }

    public UserDTO(String cor_id, String bill){
        this.linearId = new UniqueIdentifier();
        this.cor_id = cor_id;
        this.bill = bill;

    }

    public UserDTO(String cor_id,  String bill, UniqueIdentifier linearId){
        this.linearId = linearId;
        this.cor_id = cor_id;
        this.bill = bill;


    }

    public UniqueIdentifier getLinearId() {
        return linearId;
    }

    public void setLinearId(UniqueIdentifier linearId) {
        this.linearId = linearId;
    }

    public String getCor_id() {
        return cor_id;
    }

    public void setCor_id(String cor_id) {
        this.cor_id = cor_id;
    }

    public String getBill() { return bill; }

    public void setBill(String bill) {
        this.bill = bill;
    }



}
