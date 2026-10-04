package com.jquinss.passwordmanager.vault.metadata;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class VaultMetadataIO {
    public static final ObjectMapper MAPPER = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    private VaultMetadataIO() {}

    public static void createNew(VaultMetadata metaData, Path metaFile) throws IOException {
        if (Files.exists(metaFile)) {
            throw new IllegalStateException(metaFile + " already exists");
        }

        MAPPER.writeValue(metaFile.toFile(), metaData);
    }

    public static VaultMetadata read(Path metaFile) throws IOException {
        if (!Files.exists(metaFile)) {
            throw new IllegalStateException(metaFile + " doesn't exist");
        }

        return MAPPER.readValue(metaFile.toFile(), VaultMetadata.class);
    }
}
