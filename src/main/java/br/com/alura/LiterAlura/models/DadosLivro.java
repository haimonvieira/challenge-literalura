package br.com.alura.LiterAlura.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DadosLivro(@JsonAlias("title") String titulo,
                         @JsonAlias("download_count") Integer quantidadeDownloads,
                         @JsonAlias("languages") List<String> idiomas,
                         @JsonAlias("authors") List<DadosAutor> autores){
}
