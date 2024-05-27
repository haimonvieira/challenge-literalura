package br.com.alura.LiterAlura.services;

public interface IConverteDados {

    <T> T obterDados(String json, Class<T> classe);

}
