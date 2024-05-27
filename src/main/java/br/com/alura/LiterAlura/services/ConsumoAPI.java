package br.com.alura.LiterAlura.services;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class ConsumoAPI {

    public String obterDados(String enderecoAPI){

        HttpClient client = HttpClient.newHttpClient();

        //Passando o endereco da API
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(enderecoAPI))
                .build();

        //Dizendo que a resposta vai ser do tipo String
        HttpResponse<String> response = null;

        try {

            //Armazena em formato .json
            response = client
                    .send(request, HttpResponse.BodyHandlers.ofString());

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        //Retorna o .json
        return response.body();


    }



}
