# Challenge LiterAlura

Este repositório tem como propósito aplicar os conhecimentos adquiridos durante o conhecimento adquirido com os cursos do programa ONE. Senti muita dificuldade para pegar os dados da [API](https://gutendex.com), mas depois que consegui armazená-los no objeto, foi mais dificuldade ainda, mas com ajuda consegui concluir com sucesso o desafio.

Está aplicação busca dados de livros no Gutendex e armazena os dados do livro no banco de dados para fazer as devidas consultas aos dados que o usuário necessita.

As tecnologias (dependências) usadas para faze está aplicação foram:

-  Spring Boot;
-  JPA;
-  PostgreSQL;
-  Jackson e;
-  Maven.

Por mais que neste curso tenhamos aprendido a conectar ao Front-end, não foi necessário aplicar. Então, foi utilizado o Spring sem web.
O projeto foi separado em pacotes, dos quais são:

-  main;
-  models;
-  repository e;
-  services.

Não achei necessidade de utilizar os pacotes _dto_ e nem _controller_, já que não tivemos que enviar dados para o Front-end.

# Links úteis
-  [Gutendex API](https://gutendex.com)
-  [PostgreSQL](https://www.postgresql.org/download/)
-  [Spring Initializr](https://start.spring.io) - utilizado para inserir as depêndencias
-  [IntelliJ IDEA Community Edition](https://www.jetbrains.com/idea/download/)
-  [Jackson Databind](https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind)
-  [Jackson Core](https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-core)
