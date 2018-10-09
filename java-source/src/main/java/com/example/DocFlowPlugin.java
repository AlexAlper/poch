package com.example;

import net.corda.core.serialization.SerializationWhitelist;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DocFlowPlugin implements SerializationWhitelist {


    @NotNull
    @Override
    public List<Class<?>> getWhitelist() {
        List<Class<?>> res = new ArrayList<>();

        res.add(Date.class);

        return res;
    }
}

