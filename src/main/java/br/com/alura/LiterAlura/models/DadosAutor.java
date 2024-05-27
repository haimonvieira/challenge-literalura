package br.com.alura.LiterAlura.models;

import com.fasterxml.jackson.annotation.JsonAlias;

import java.util.List;

public record DadosAutor(@JsonAlias("name") String nome,
                         @JsonAlias("birth_year") Integer anoNascimento,
                         @JsonAlias("death_year") Integer anoFalecimento) {
}
