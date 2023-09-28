package ru.demo.service.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LinkJ {
    @JsonProperty("link")
    private String link;
}
