package com.example.schema;
import com.google.common.collect.ImmutableList;
import net.corda.core.schemas.MappedSchema;
import net.corda.core.schemas.PersistentState;
import com.example.schema.IOUSchema;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;
import java.util.UUID;

public class DocSchemaV2 extends MappedSchema {

    public DocSchemaV2() {
        super(IOUSchema.class, 1, ImmutableList.of(com.example.schema.DocSchemaV2.PersistentDoc.class));
    }

    @Entity
    @Table(name = "doc_states")
    public static class PersistentDoc extends PersistentState {
        @Column(name = "linear_id")
        private final UUID linearId;
        @Column(name = "cor_id")
        private final String cor_id;
        @Column(name = "bill")
        private final String bill;




        public PersistentDoc(UUID linearId, String cor_id,
                             String bill
                             ) {
            this.linearId = linearId;
            this.cor_id = cor_id;
            this.bill = bill;

        }

        //region Геттеры
        public String getCor_id() {
            return cor_id;
        }

        public String getCor_bill_1() {
            return bill;
        }


        //endregion
    }
}