package com.veeva.vault.sdk.vaultapi.vaultjavasdk.model.data;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "stepheader")
public class CsvManifest extends StepManifest {
    @JacksonXmlProperty(localName = "datastepheader")
    private CsvDataStep csvDataStep;

    public CsvDataStep getCsvDataStep() {
        return csvDataStep;
    }

    public void setCsvDataStep(CsvDataStep csvDataStep) {
        this.csvDataStep = csvDataStep;
    }
}
