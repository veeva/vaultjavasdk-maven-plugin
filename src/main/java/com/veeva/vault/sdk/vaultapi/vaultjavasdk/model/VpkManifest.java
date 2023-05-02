package com.veeva.vault.sdk.vaultapi.vaultjavasdk.model;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.HashMap;
import java.util.Map;

@JacksonXmlRootElement(localName = "vaultpackage")
public class VpkManifest {
    @JacksonXmlProperty(isAttribute = true)
    private String xmlns = "https://veevavault.com/";

    @JsonIgnore
    public final static String VAULTPACKAGE_FILENAME = "vaultpackage.xml";

    @JacksonXmlProperty(localName = "name")
    private String name;

    @JsonGetter
    public String getName() {
        return name;
    }

    @JsonSetter
    public void setName(String name) {
        this.name = name;
    }

    @JacksonXmlProperty(localName = "source")
    private VaultSource vaultSource;

    @JsonGetter
    public VaultSource getVaultSource() {
        return vaultSource;
    }

    @JsonSetter
    public void setVaultSource(VaultSource vaultSource) {
        this.vaultSource = vaultSource;
    }

    public enum PackageType {
        MIGRATION("migration__v"),
        TESTDATA("test_data__sys");

        String value;
        PackageType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    @JacksonXmlProperty(localName = "packagetype")
    private String packageType = "migration__v";

    @JsonGetter
    public String getPackageType() {
        return packageType;
    }

    @JsonAnySetter
    public void setPackageType(String packageType) {
        this.packageType = packageType;
    }

    @JsonIgnore
    public void setPackageType(PackageType packageType) {
        this.packageType = packageType.getValue();
    }

    @JacksonXmlProperty(localName = "summary")
    private String summary;

    @JsonGetter
    public String getSummary() {
        return summary;
    }

    @JsonSetter
    public void setSummary(String summary) {
        this.summary = summary;
    }

    @JacksonXmlProperty(localName = "description")
    private String description;

    @JsonGetter
    public String getDescription() {
        return description;
    }

    @JsonSetter
    public void setDescription(String description) {
        this.description = description;
    }

    @JacksonXmlProperty(localName = "javasdk")
    private JavaSdk javasdk;

    @JsonGetter
    public JavaSdk getJavasdk() {
        return javasdk;
    }

    @JsonSetter
    public void setJavasdk(JavaSdk javasdk) {
        this.javasdk = javasdk;
    }

    @JsonAnySetter
    private Map<String, Object> properties = new HashMap<String, Object>();

    @JsonAnyGetter
    public Map<String, Object> getProperties() {
        return properties;
    }

    @JsonAnySetter
    public void setProperties(String name, Object value) {
        this.properties.put(name, value);
    }
}
