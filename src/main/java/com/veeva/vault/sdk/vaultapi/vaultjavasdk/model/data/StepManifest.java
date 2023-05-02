package com.veeva.vault.sdk.vaultapi.vaultjavasdk.model.data;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.HashMap;
import java.util.Map;

@JacksonXmlRootElement(localName = "stepheader")
public class StepManifest {
    @JacksonXmlProperty(localName = "label")
    private String label;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @JacksonXmlProperty(localName = "steprequired")
    private Boolean stepRequired = false;

    public Boolean getStepRequired() {
        return stepRequired;
    }

    public void setStepRequired(Boolean stepRequired) {
        this.stepRequired = stepRequired;
    }

    @JacksonXmlProperty(localName = "checksum")
    private String checksum;

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
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
