package br.com.alura.LiterAlura.services;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ConverterDados implements IConverteDados{

    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public <T> T obterDados(String json, Class<T> classe) {

        try {
            return mapper.readValue(json, classe);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao converter .json para objeto", e);
        }

    }

    public static String obterIdioma(String abreviacao){

        if(abreviacao.equalsIgnoreCase("pt")){
            return "Português";
        }else if(abreviacao.equalsIgnoreCase("en")){
            return "Inglês";
        } else if (abreviacao.equalsIgnoreCase("fr")) {
            return "Francês";
        }else {
            return abreviacao;
        }

    }

    public static String converterIdioma(String idioma){

        if(idioma.equalsIgnoreCase("portugues")){
            return "pt";
        }else if(idioma.equalsIgnoreCase("ingles")){
            return "en";
        } else if (idioma.equalsIgnoreCase("frances")) {
            return "fr";
        }else if(idioma.length() == 2){
            return idioma;
        }else {
            System.out.println("Nao convertido");
            return idioma;
        }

    }

}
