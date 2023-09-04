package ru.demo.service.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;
import lombok.*;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
public class CompaniesRequestModel {
    private String url;
    private int status;
    private List<DataItem> data;
    private Links _links;
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
    public Links getLinks() {
        return _links;
    }

    @JsonProperty("_links")
    public void setLinks(Links _links) {
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
        return "CompanyResponseModel{" +
                "url='" + url + '\'' +
                ", status=" + status +
                ", data=" + data +
                ", _links=" + _links +
                ", _meta=" + _meta +
                '}';
    }

    public static CompaniesRequestModel fromJson(String jsonString) {
        Gson gson = new Gson();
        System.out.println(jsonString);
        return gson.fromJson(jsonString, CompaniesRequestModel.class);
    }

    public static class DataItem {
        private int id;
        private String name;
        private int org_code_mdm;
        private String prefix;
        private String is_not_active;
        private String is_processed;
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
            return org_code_mdm;
        }

        public void setOrgCodeMdm(int orgCodeMdm) {
            this.org_code_mdm = orgCodeMdm;
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

        public String getHash() {
            return hash;
        }

        public void setHash(String hash) {
            this.hash = hash;
        }

        @Override
        public String toString() {
            return "DataItem{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", org_code_mdm=" + org_code_mdm +
                    ", prefix='" + prefix + '\'' +
                    ", is_not_active='" + is_not_active + '\'' +
                    ", is_processed='" + is_processed + '\'' +
                    ", hash='" + hash + '\'' +
                    '}';
        }
    }

    public static class Links {
        private String self;
        private String first;
        private String last;
        private String prev;
        private String next;

        public String getSelf() {
            return self;
        }

        public void setSelf(String self) {
            this.self = self;
        }

        public String getFirst() {
            return first;
        }

        public void setFirst(String first) {
            this.first = first;
        }

        public String getLast() {
            return last;
        }

        public void setLast(String last) {
            this.last = last;
        }

        public String getPrev() {
            return prev;
        }

        public void setPrev(String prev) {
            this.prev = prev;
        }

        public String getNext() {
            return next;
        }

        public void setNext(String next) {
            this.next = next;
        }

        @Override
        public String toString() {
            return "Links{" +
                    "self='" + self + '\'' +
                    ", first='" + first + '\'' +
                    ", last='" + last + '\'' +
                    ", prev='" + prev + '\'' +
                    ", next='" + next + '\'' +
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