package br.com.alura.LiterAlura.main;

import br.com.alura.LiterAlura.models.*;
import br.com.alura.LiterAlura.repository.AutorRepository;
import br.com.alura.LiterAlura.repository.LivroRepository;
import br.com.alura.LiterAlura.services.ConsumoAPI;
import br.com.alura.LiterAlura.services.ConverterDados;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Main {
    private final Scanner scanner = new Scanner(System.in);
    private final ConsumoAPI consumoAPI = new ConsumoAPI();
    private final ConverterDados converterDados = new ConverterDados();
    private final String ENDERECO = "https://gutendex.com/books/";
    private LivroRepository livroRepository;
    private AutorRepository autorRepository;
    private List<Livro> livros = new ArrayList<>();
    private List<Autor> autores = new ArrayList<>();

    public Main(LivroRepository livroRepository, AutorRepository autorRepository) {

        this.livroRepository = livroRepository;
        this.autorRepository = autorRepository;

    }

    public void exibirMenu() {

        int op = -1;

        while (op != 0) {

            System.out.println("""
                    1- Inserir livro
                    2- Listar livros cadastrados
                    3- Listar autores cadastrados
                    4- Listar autores de um determinado ano
                    5- Listar livros de um determinado idioma
                    6- Deletar tudo
                                    
                    0- SAIR
                    """);
            System.out.print("> ");
            op = scanner.nextInt();
            scanner.nextLine();

            switch (op) {

                case 1:
                    buscarLivro();
                    break;

                case 2:
                    listarLivrosCadastrados();
                    break;

                case 3:
                    listarAutores();
                    break;

                case 4:
                    listarAutoresDeUmDeterminadoAno();
                    break;

                case 5:
                    listarLivrosDeUmDeterminadoIdioma();
                    break;

                case 6:
                    deletarTudo();
                    break;


                case 0:
                    System.out.println("Saindo...");
                    break;

                default:
                    System.out.println("Opção inválida.");
                    break;

            }

        }


    }

    /**
     * Inserir livro - FEITO
     * listar livros - FEITO
     * listar autores - FEITO
     * listar autores de um determinado ano - FEITO
     * listar livros de um determinado idioma - FEITO
     * Fazer validacoes
     */

    private DadosResultado getDadosLivro(String endereco) {

        var json = consumoAPI.obterDados(ENDERECO + "?search="
                + endereco.replace(" ", "+"));

        System.out.println(json);

        DadosResultado dadosResultado = converterDados.obterDados(json, DadosResultado.class);

        System.out.println(dadosResultado);

        return dadosResultado;

    }

    private void buscarLivro() {

        System.out.println("Insira o nome do livro para buscar");
        System.out.print("> ");
        var pesquisa = scanner.nextLine();

        DadosResultado dadosResultado = getDadosLivro(pesquisa);

        List<DadosLivro> dadosLivros = dadosResultado.dadosLivro().stream()
                .map(l -> new DadosLivro(l.titulo(), l.quantidadeDownloads(), l.idiomas(),
                        l.autores()))
                .toList();

        livros = dadosLivros.stream()
                .map(Livro::new).toList();

        //Pegando mapeando dadosLivros para DadosAutor
        List<DadosAutor> dadosAutores = dadosLivros.stream()
                .map(d -> new DadosAutor(d.autores().get(0).nome(), d.autores().get(0).anoNascimento(),
                        d.autores().get(0).anoFalecimento()))
                .toList();

        autores = dadosAutores.stream()
                .map(Autor::new).toList();

        if (!livros.isEmpty() && !autores.isEmpty()) {

            //Inserindo o livro para poder apresentar o nome do livro
            autores.get(0).setLivro(livros.get(0));
            salvarLivro(dadosLivros, dadosAutores);

        }


    }

    private void salvarLivro(List<DadosLivro> dadosLivros, List<DadosAutor> dadosAutores) {

        Livro livro = new Livro();
        livro.setTitulo(dadosLivros.get(0).titulo());
        livro.setIdioma(dadosLivros.get(0).idiomas().get(0));
        livro.setQuantidadeDownloads(dadosLivros.get(0).quantidadeDownloads());

        Autor autor = new Autor();
        autor.setNome(dadosAutores.get(0).nome());
        autor.setAnoFalecimento(dadosAutores.get(0).anoFalecimento());
        autor.setAnoNascimento(dadosAutores.get(0).anoNascimento());
        autor.setLivro(livro);

        //Salvando Livro e Autor no banco de dados
        livroRepository.save(livro);
        autorRepository.save(autor);

    }

    private void listarLivrosCadastrados() {

        List<Livro> books = livroRepository.findAll();

        System.out.println("----Livros----");
        books.forEach(l -> System.out.println(

                "\nTitulo: " + l.getTitulo() +
                        "\nIdioma: " + ConverterDados.obterIdioma(l.getIdioma()) +
                        "\nDownloads: " + l.getQuantidadeDownloads()

        ));

    }

    private void listarAutores() {

        List<Autor> autors = autorRepository.findAll();

        System.out.println("----Autores----");

        autors.forEach(a -> System.out.println(
                "\nNome: " + a.getNome() +
                        "\nNascimento: " + a.getAnoNascimento() +
                        "\nFalecimento: " + a.getAnoFalecimento()));
        System.out.println();

    }

    private void listarAutoresDeUmDeterminadoAno() {

        System.out.println("Insira o ano para buscar");
        System.out.print("> ");
        var ano = scanner.nextInt();
        scanner.nextLine();

        List<Autor> filtroAutores = autorRepository.buscarAutoresAte(ano);

        System.out.println("----Autores vivos até " + ano + "----");

        filtroAutores.forEach(a -> System.out.println("\nNome: " + a.getNome() +
                "\nNascimento: " + a.getAnoNascimento() +
                "\nFalecimento: " + a.getAnoFalecimento()

        ));
        System.out.println("\n");

    }

    private void listarLivrosDeUmDeterminadoIdioma() {

        System.out.println("Insira o idioma (abreviado ou completo)");
        System.out.print("> ");
        var idioma = scanner.nextLine();

        if (idioma.length() > 2) {
            idioma = ConverterDados.converterIdioma(idioma);
        }

        List<Livro> books = livroRepository.buscarLivroPorIdioma(idioma);

        System.out.println("---Autores com idioma '" + idioma + "'---");

        books.forEach(b -> System.out.println(
                "\nTitulo: " + b.getTitulo() +
                        "\nIdioma: " + b.getIdioma() +
                        "\nDownloads: " + b.getQuantidadeDownloads()
        ));
        System.out.println("\n");


    }


    private void deletarTudo() {

        autorRepository.deleteAll();
        livroRepository.deleteAll();

    }
}

