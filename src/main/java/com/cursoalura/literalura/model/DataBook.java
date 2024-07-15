package com.cursoalura.literalura.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DataBook(
        @JsonAlias("title") String title,
        @JsonAlias("authors") List<DataAuthor> authors,
        @JsonAlias("languages") List<String> languages,
        @JsonAlias("copyright") String copyright,
        @JsonAlias("download_count") Integer download) {
}
