package ru.demo.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Subdivisions {
    @JsonProperty("id")
    private Long ids;
    @JsonProperty("name")
    private String name;
    @JsonProperty("parentId")
    private Long parentId;
    @JsonProperty("prefix")
    private String prefix;
    @JsonProperty("isEntity")
    private boolean isEntity;
    @JsonProperty("isService")
    private boolean isService;
    @JsonProperty("chiefId")
    private String chiefId;
    @JsonProperty("mdmCode")
    private Long mdmCode;
    @JsonProperty("updatedAt")
    private String updatedAt;
    @JsonProperty("TotalCount")
    private int totalCount;

//    @JsonProperty("link")
//    private LinkJ link;

}





