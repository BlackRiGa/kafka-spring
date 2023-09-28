package ru.demo.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DivisionsModelResponseFromHr {
    @JsonProperty("id")
    private int id;
    @JsonProperty("parent_id")
    private int parent_id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("prefix")
    private String prefix;
    @JsonProperty("is_not_active")
    private String isNotActive;
    @JsonProperty("is_processed")
    private String isProcessed;


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

    @Override
    public String toString() {
        return "DirectionsModelResponseFromHr{" +
                "id=" + id +
                ", parent_id=" + parent_id +
                ", name='" + name + '\'' +
                ", prefix='" + prefix + '\'' +
                ", isNotActive='" + isNotActive + '\'' +
                ", isProcessed='" + isProcessed + '\'' +
                '}';
    }
}