package ru.demo.service.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
public class DirectionsRequestModel {
    private String url;
    private int status;
    private List<DataItem> data;
    private Map<String, Map<String, String>> _links;
    private Meta _meta;

    @JsonProperty("url")
    public String getUrl() {
        return url;
    }

    @JsonProperty("url")
    public void setUrl(String url) {
        this.url = url;
    }

    @JsonProperty("status")
    public int getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(int status) {
        this.status = status;
    }

    @JsonProperty("data")
    public List<DataItem> getData() {
        return data;
    }

    @JsonProperty("data")
    public void setData(List<DataItem> data) {
        this.data = data;
    }

    @JsonProperty("_links")
    public Map<String, Map<String, String>> getLinks() {
        return _links;
    }

    @JsonProperty("_links")
    public void setLinks(Map<String, Map<String, String>> _links) {
        this._links = _links;
    }

    @JsonProperty("_meta")
    public Meta getMeta() {
        return _meta;
    }

    @JsonProperty("_meta")
    public void setMeta(Meta _meta) {
        this._meta = _meta;
    }

    @Override
    public String toString() {
        return "DirectionsRequestModel{" +
                "url='" + url + '\'' +
                ", status=" + status +
                ", data=" + data +
                ", _links=" + _links +
                ", _meta=" + _meta +
                '}';
    }

    public static DirectionsRequestModel fromJson(String jsonString) {
        ObjectMapper objectMapper = new ObjectMapper();
        DirectionsRequestModel directionsRequestModel = null;
        try {
            directionsRequestModel = objectMapper.readValue(jsonString, DirectionsRequestModel.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return directionsRequestModel;
    }

    @Getter
    @Setter
    @EqualsAndHashCode
    @AllArgsConstructor
    public static class DataItem {
        private int id;
        private int parent_id;
        private String name;
        private String prefix;
        private String is_not_active;
        private String is_processed;
    }

    @Getter
    @Setter
    @EqualsAndHashCode
    @AllArgsConstructor
    public static class Meta {
        private int totalCount;
        private int pageCount;
        private int currentPage;
        private int perPage;
    }
}
