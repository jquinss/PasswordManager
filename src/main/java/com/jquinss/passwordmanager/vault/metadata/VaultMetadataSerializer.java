package com.jquinss.passwordmanager.vault.metadata;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class VaultMetadataSerializer {
    private static final ObjectMapper MAPPER = new ObjectMapper().
            configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true).
            configure(SerializationFeature.INDENT_OUTPUT, false);

    private VaultMetadataSerializer() {}

    public static byte[] canonalizeWithoutMac(VaultMetadata metadata) throws JsonProcessingException {
        ObjectNode node = MAPPER.valueToTree(metadata);
        node.remove("mac");
        return MAPPER.writeValueAsBytes(node);
    }

    public static byte[] serializeWithMac(VaultMetadata metadata) throws JsonProcessingException {
        return MAPPER.writeValueAsBytes(metadata);
    }
}
