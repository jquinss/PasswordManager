package com.jquinss.passwordmanager.vault.metadata;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jquinss.passwordmanager.crypto.Argon2Param;

@JsonInclude(JsonInclude.Include.NON_NULL)
public final class VaultMetadata {
    @JsonProperty("version")
    private final int version;
    @JsonProperty("argon2Param")
    private final Argon2Param argon2Param;
    @JsonProperty("hkdfSalt")
    private final String hkdfSalt;
    @JsonProperty("vaultId")
    private final String vaultId;
    @JsonProperty("authVerifier")
    private final String authVerifier;
    @JsonProperty("mac")
    private String mac;

    @JsonCreator
    public VaultMetadata(
            @JsonProperty("version")
            int version,
            @JsonProperty("argon2Param")
            Argon2Param argon2Param,
            @JsonProperty("hkdfSalt")
            String hkdfSalt,
            @JsonProperty("vaultId")
            String vaultId,
            @JsonProperty("authVerifier")
            String authVerifier){

        this.version = version;
        this.argon2Param = argon2Param;
        this.hkdfSalt = hkdfSalt;
        this.vaultId = vaultId;
        this.authVerifier = authVerifier;
    }

    public int getVersion() {
        return version;
    }

    public Argon2Param getArgon2Param() {
        return argon2Param;
    }

    public String getHkdfSalt() {
        return hkdfSalt;
    }

    public String getVaultId() {
        return vaultId;
    }

    public String getAuthVerifier() {
        return authVerifier;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }
}
