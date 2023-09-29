package ru.demo.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UnitModelResponseFromHr {
    private int id;
    @JsonProperty("parent_id")
    private int parent_id;
    @JsonProperty("org_code_mdm")
    private int orgCodeMdm;
    @JsonProperty("is_entity")
    private int is_entity;
    private String name;
    private String prefix;
    @JsonProperty("is_not_active")
    private String isNotActive;
    @JsonProperty("is_processed")
    private String isProcessed;
    @JsonProperty("company_id")
    private int company_id;
    @JsonProperty("chief_login")
    private String chief_login;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getParent_id() {
        return parent_id;
    }

    public void setParent_id(int parent_id) {
        this.parent_id = parent_id;
    }

    public int getOrgCodeMdm() {
        return orgCodeMdm;
    }

    public void setOrgCodeMdm(int orgCodeMdm) {
        this.orgCodeMdm = orgCodeMdm;
    }

    public int getIs_entity() {
        return is_entity;
    }

    public void setIs_entity(int is_entity) {
        this.is_entity = is_entity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public int getCompany_id() {
        return company_id;
    }

    public void setCompany_id(int company_id) {
        this.company_id = company_id;
    }

    public String getChief_login() {
        return chief_login;
    }

    public void setChief_login(String chief_login) {
        this.chief_login = chief_login;
    }
}
