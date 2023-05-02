package com.veeva.vault.sdk.vaultapi.vaultjavasdk.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class VaultSource {

    @JacksonXmlProperty(localName = "vault")
    private String vault;

    @JacksonXmlProperty(localName = "author")
    private String author;

    @JsonGetter
    public String getVault() {
        return vault;
    }

    @JsonSetter
    public void setVault(String vault) {
        this.vault = vault;
    }

    @JsonGetter
    public String getAuthor() {
        return author;
    }

    @JsonSetter
    public void setAuthor(String author) {
        this.author = author;
    }

}
