package com.veeva.vault.sdk.vaultapi.vaultjavasdk.model.data;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.HashMap;
import java.util.Map;

public class CsvDataStep {
    @JacksonXmlProperty(localName = "object")
    private String object;

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    @JacksonXmlProperty(localName = "idparam")
    private String idParam;

    public String getIdParam() {
        return idParam;
    }

    public void setIdParam(String idParam) {
        this.idParam = idParam;
    }

    @JacksonXmlProperty(localName = "datatype")
    private String dataType;

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    @JacksonXmlProperty(localName = "action")
    private String action;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @JacksonXmlProperty(localName = "recordmigrationmode")
    private Boolean recordMigrationMode;

    public Boolean getRecordMigrationMode() {
        return recordMigrationMode;
    }

    public void setRecordMigrationMode(Boolean recordMigrationMode) {
        this.recordMigrationMode = recordMigrationMode;
    }

    @JacksonXmlProperty(localName = "recordcount")
    private Integer recordCount;

    public Integer getRecordCount() {
        return recordCount;
    }

    public void setRecordCount(Integer recordCount) {
        this.recordCount = recordCount;
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
