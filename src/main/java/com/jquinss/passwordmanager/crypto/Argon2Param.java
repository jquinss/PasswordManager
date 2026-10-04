package com.jquinss.passwordmanager.crypto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Argon2Param(
        @JsonProperty("algorithm")
        int algorithm,
        @JsonProperty("memoryKb")
        int memoryKb,
        @JsonProperty("iterations")
        int iterations,
        @JsonProperty("parallelism")
        int parallelism,
        @JsonProperty("outputLength")
        int outputLength
) {}
