package com.example.core.helpers;

import net.corda.core.contracts.CommandData;

public class Commands implements CommandData {


    public static class Create extends Commands {
        @Override
        public boolean equals(Object obj) {
            return obj instanceof Create;
        }
    }


    public static class Edit extends Commands {
        @Override
        public boolean equals(Object obj) {
            return obj instanceof Edit;
        }
    }


    public static class Delete extends Commands {
        @Override
        public boolean equals(Object obj) { return obj instanceof Delete; }
    }


}