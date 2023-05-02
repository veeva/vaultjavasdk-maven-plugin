/*---------------------------------------------------------------------
 *	Copyright (c) 2023 Veeva Systems Inc.  All Rights Reserved.
 *	This code is based on pre-existing content developed and
 *	owned by Veeva Systems Inc. and may only be used in connection
 *	with the deliverable with which it was provided to Customer.
 *---------------------------------------------------------------------
 */
package com.veeva.vault.sdk.vaultapi.vaultjavasdk.model;

import com.fasterxml.jackson.annotation.*;
import com.veeva.vault.sdk.vaultapi.vaultjavasdk.utilities.ResourceHelper;
import com.veeva.vault.vapil.api.model.VaultModel;
import com.veeva.vault.vapil.api.model.response.VaultResponse;
import org.apache.commons.lang3.StringUtils;

import java.io.File;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({ "packagename", "step",  "summary", "description", "vault", "author", "componenttype", "recordname", "mdl", "action", "responseStatus", "responseErrors" })
public class MdlStep extends VaultModel {

	@JsonProperty("action")
	private String action = "UNDEFINED";

	@JsonProperty("responseStatus")
	private String responseStatus = "UNDEFINED";

	@JsonProperty("responseErrors")
	private String responseErrors;

	@JsonGetter
	public String getAction() {
		return action;
	}

	@JsonSetter
	public void setAction(String action) {
		this.action = action;
	}

	@JsonGetter
	public String getResponseStatus() {
		if (vaultResponse != null)
			return vaultResponse.getResponseStatus();
		else
			return  responseStatus;
	}

	@JsonSetter
	public void setResponseStatus(String responseStatus) {
		this.responseStatus = responseStatus;
	}

	@JsonGetter
	public String getResponseErrors() {

		if ((vaultResponse != null) && (vaultResponse.getErrors() != null)) {
			StringBuilder errorBuilder = new StringBuilder();
			Integer errorCount = 0;
			for (VaultResponse.APIResponseError error : vaultResponse.getErrors()) {
				errorCount++;
				if (errorCount > 1) {
					errorBuilder.append("; ");
				}
				errorBuilder.append(error.getMessage());
			}
			return errorBuilder.toString();
		}

		return responseErrors;
	}

	@JsonIgnore
	private VaultResponse vaultResponse;

	@JsonIgnore
	public VaultResponse getVaultResponse() {
		return vaultResponse;
	}

	@JsonIgnore
	public void setVaultResponse(VaultResponse vaultResponse) {
		this.vaultResponse = vaultResponse;
	}


	@JsonProperty("author")
	private String author;

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	@JsonProperty("componenttype")
	@JsonAlias("component_type__v")
	private String componentType;

	public String getComponentType() {
		return componentType;
	}

	public void setComponentType(String componentType) {
		this.componentType = componentType;
	}

	@JsonProperty("csv")
	private String csv;

	@JsonGetter
	public String getCsv() { return csv; }

	@JsonIgnore
	public File getCsvFile() {
		if (csv != null && !csv.isEmpty()) {
			return ResourceHelper.getFile(ResourceHelper.localToFullPath(csv));
		}
		return null;
	}

	@JsonSetter
	public void setCsv(String csv) { this.csv = csv; }

	@JsonProperty("description")
	private String description;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@JsonProperty("file")
	private String file;

	@JsonGetter
	public String getFile() { return file; }

	@JsonIgnore
	public String getFileName() {
		File file = ResourceHelper.getFile(ResourceHelper.localToFullPath(getFile()));
		if (file != null) {
			return file.getName();
		}
		return null;
	}

	@JsonSetter
	public void setFile(String file) { this.file = file; }

	@JsonProperty("mdl")
	private String mdl;

	public String getMdl() {
		return mdl;
	}

	public void setMdl(String mdl) {
		this.mdl = mdl;
	}

	@JsonProperty("packagename")
	private String packageName;

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packagename) {
		this.packageName = packagename;
	}

	@JsonProperty("step")
	private String step;

	public String getStep() {
		return step;
	}

	public void setStep(String step) {
		this.step = StringUtils.leftPad(step,5,"0");
	}

	@JsonProperty("summary")
	private String summary;

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	@JsonProperty("recordname")
	@JsonAlias("component_name__v")
	private String recordName;

	public String getRecordName() {
		return recordName;
	}

	public void setRecordName(String recordName) {
		this.recordName = recordName;
	}

	@JsonProperty("vault")
	private String vault;

	public String getVault() {
		return vault;
	}

	public void setVault(String vault) {
		this.vault = vault;
	}

	@JsonProperty("xml")
	private String xml;

	@JsonGetter
	public String getXml() { return xml; }

	@JsonIgnore
	public File getXmlFile() {
		if (xml != null && !xml.isEmpty()) {
			return ResourceHelper.getFile(ResourceHelper.localToFullPath(xml));
		}
		return null;
	}

	@JsonSetter
	public void setXml(String xml) { this.xml = xml; }
}