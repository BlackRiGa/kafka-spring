package ru.demo.service.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DirectionsRequestModel {
    private String url;
    private int status;
    private List<DataItem> data;
    private Map<String, String> _links;
    private Meta _meta;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<DataItem> getData() {
        return data;
    }

    public void setData(List<DataItem> data) {
        this.data = data;
    }

    @JsonProperty("_links")
    public Map<String, String> getLinks() {
        return _links;
    }

    @JsonProperty("_links")
    public void setLinks(Map<String, String> _links) {
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
        return "JsonResponseModel{" +
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

    public static class DataItem {
        private int id;
        private int parent_id;
        private String name;
        private String prefix;
        private String is_not_active;
        private String is_processed;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getParentId() {
            return parent_id;
        }

        public void setParentId(int parentId) {
            this.parent_id = parentId;
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
            return is_not_active;
        }

        public void setIsNotActive(String isNotActive) {
            this.is_not_active = isNotActive;
        }

        public String getIsProcessed() {
            return is_processed;
        }

        public void setIsProcessed(String isProcessed) {
            this.is_processed = isProcessed;
        }

        @Override
        public String toString() {
            return "DataItem{" +
                    "id=" + id +
                    ", parent_id=" + parent_id +
                    ", name='" + name + '\'' +
                    ", prefix='" + prefix + '\'' +
                    ", is_not_active='" + is_not_active + '\'' +
                    ", is_processed='" + is_processed + '\'' +
                    '}';
        }
    }

    public static class Meta {
        private int totalCount;
        private int pageCount;
        private int currentPage;
        private int perPage;

        public int getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(int totalCount) {
            this.totalCount = totalCount;
        }

        public int getPageCount() {
            return pageCount;
        }

        public void setPageCount(int pageCount) {
            this.pageCount = pageCount;
        }

        public int getCurrentPage() {
            return currentPage;
        }

        public void setCurrentPage(int currentPage) {
            this.currentPage = currentPage;
        }

        public int getPerPage() {
            return perPage;
        }

        public void setPerPage(int perPage) {
            this.perPage = perPage;
        }

        @Override
        public String toString() {
            return "Meta{" +
                    "totalCount=" + totalCount +
                    ", pageCount=" + pageCount +
                    ", currentPage=" + currentPage +
                    ", perPage=" + perPage +
                    '}';
        }
    }
}

