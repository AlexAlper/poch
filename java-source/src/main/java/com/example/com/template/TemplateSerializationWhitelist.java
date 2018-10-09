package com.example.com.template;

import com.example.user.StateUser;
import net.corda.core.serialization.SerializationWhitelist;
import org.jetbrains.annotations.NotNull;

import java.util.*;

// Serialization whitelist.
public class TemplateSerializationWhitelist implements SerializationWhitelist {
    @NotNull
    @Override
    public List<Class<?>> getWhitelist() {
        //return Collections.singletonList(TemplateData.class);

        List<Class<?>> whiteList = new ArrayList<>();
        whiteList.add(TemplateData.class);
        whiteList.add(Date.class);
        whiteList.add(HashSet.class);
        whiteList.add(StateUser.class);
        return whiteList;
    }

    // This class is not annotated with @CordaSerializable, so it must be added to the serialization whitelist, above,
    // if we want to send it to other nodes within a flow.
    public static class TemplateData {
        private final String payload;

        public TemplateData(String payload) {
            this.payload = payload;
        }

        public String getPayload() {
            return payload;
        }
    }


}