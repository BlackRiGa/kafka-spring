package ru.demo.service.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CompanyModelResponseFromHr {
    private int id;
    private String name;
    @JsonProperty("org_code_mdm")
    private int orgCodeMdm;
    private String prefix;
    @JsonProperty("is_not_active")
    private String isNotActive;
    @JsonProperty("is_processed")
    private String isProcessed;
    private String hash;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOrgCodeMdm() {
        return orgCodeMdm;
    }
    public void setOrgCodeMdm(int orgCodeMdm) {
        this.orgCodeMdm = orgCodeMdm;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getIsNotActive() {
        return isNotActive;
    }

    public void setIsNotActive(String isNotActive) {
        this.isNotActive = isNotActive;
    }

    public String getIsProcessed() {
        return isProcessed;
    }

    public void setIsProcessed(String isProcessed) {
        this.isProcessed = isProcessed;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
}