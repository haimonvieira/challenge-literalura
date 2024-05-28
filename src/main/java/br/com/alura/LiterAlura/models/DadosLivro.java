package br.com.alura.LiterAlura.models;

import br.com.alura.LiterAlura.services.ConverterDados;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DadosLivro(@JsonAlias("title") String titulo,
                         @JsonAlias("download_count") Integer quantidadeDownloads,
                         @JsonAlias("languages") List<String> idiomas,
                         @JsonAlias("authors") List<DadosAutor> autores){

    @Override
    public String toString() {
        return "\nTitulo: " + titulo +
                "\nDownloads: " + quantidadeDownloads +
                "\nIdioma: " + ConverterDados.converterAbreviacao(idiomas.get(0));
    }
}
