package br.com.alura.LiterAlura.main;

import br.com.alura.LiterAlura.models.*;
import br.com.alura.LiterAlura.repository.AutorRepository;
import br.com.alura.LiterAlura.repository.LivroRepository;
import br.com.alura.LiterAlura.services.ConsumoAPI;
import br.com.alura.LiterAlura.services.ConverterDados;

import java.util.*;

public class Main {
    private final Scanner scanner = new Scanner(System.in);
    private final ConsumoAPI consumoAPI = new ConsumoAPI();
    private final ConverterDados converterDados = new ConverterDados();
    private final String ENDERECO = "https://gutendex.com/books/?search=";
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
                    ----CADASTRO----
                    1- Cadastrar livro
                                        
                    ----LISTAR----
                    2- Listar livros cadastrados
                    3- Listar autores cadastrados
                    4- Listar 10 livros mais baixados
                                        
                    ----BUSCAR----
                    5- Buscar autores de um determinado ano
                    6- Buscar livros de um determinado idioma
                    7- Buscar livro por titulo
                    8- Buscar autor
                                        
                    ----ZONA DE PERIGO----
                    9- Deletar tudo (deleta registros de autores e livros)
                    ----------------------
                                    
                    0- SAIR
                    """);
            System.out.print("> ");
            op = scanner.nextInt();
            scanner.nextLine();

            switch (op) {

                case 1:
                    inserirLivro();
                    break;

                case 2:
                    listarLivrosCadastrados();
                    break;

                case 3:
                    listarAutores();
                    break;

                case 4:
                    listarTop10LivrosBaixados();
                    break;

                case 5:
                    listarAutoresDeUmDeterminadoAno();
                    break;

                case 6:
                    listarLivrosDeUmDeterminadoIdioma();
                    break;

                case 7:
                    buscarLivroPorTitulo();
                    break;

                case 8:
                    buscarAutor();
                    break;

                case 9:
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
     * Top 10 livros baixados - FEITO
     * buscar livro por titulo - FEITO
     * buscar autor - FEITO
     * Fazer validacoes - FEITO
     */

    private void inserirLivro() {

        var pesquisa = "";

        System.out.println("Insira o nome do livro para buscar");
        System.out.print("> ");
        pesquisa = scanner.nextLine().toLowerCase();

        cadastrarLivro(pesquisa);

    }

    private DadosResultado getDadosLivro(String pesquisa) {

        System.out.println("Estou fazendo a pesquisa do seu livro...\n");

        var json = consumoAPI.obterDados(ENDERECO +
                pesquisa.replace(" ", "+"));
        System.out.println(json);


        DadosResultado dadosResultado = converterDados.obterDados(json, DadosResultado.class);

        return dadosResultado;

    }

    private void cadastrarLivro(String pesquisa) {

        if (pesquisa.isEmpty()) {

            System.out.println("Sem dados para fazer a pesquisa.");

        } else {

            Optional<Livro> livroExiste = livroRepository.findByTitulo(pesquisa);


            if (livroExiste.isPresent()) {

                System.out.println("\nLivro já existe no BD: " + livroExiste.get());
                System.out.println("\n");

            } else {

                DadosResultado dadosResultado = getDadosLivro(pesquisa);

                System.out.println("Estou convertendo o livro...");

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
        }

    }

    private void salvarLivro(List<DadosLivro> dadosLivros, List<DadosAutor> dadosAutores) {

        System.out.println("Estou inserindo no banco de dados...\n");

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


        System.out.println("\nLivro cadastrado com sucesso!\n");

    }

    private void listarLivrosCadastrados() {

        List<Livro> books = livroRepository.findAll();

        if (!books.isEmpty()) {

            Optional<Livro> livro = livroRepository.findByTitulo(books.get(0).getTitulo());

            if (livro.isPresent()) {
                System.out.println("----Livros----");
                books.forEach(l -> System.out.println(

                        "\nTitulo: " + l.getTitulo() +
                                "\nIdioma: " + ConverterDados.converterAbreviacao(l.getIdioma()) +
                                "\nDownloads: " + l.getQuantidadeDownloads()
                ));
                System.out.println();
            }

        } else {

            System.out.println("\nSem livros cadastrados.\n");

        }

    }

    private void listarAutores() {

        List<Autor> autors = autorRepository.findAll();

        if (!autors.isEmpty()) {

            Optional<Autor> autor = autorRepository.findByNome(autors.get(0).getNome());

            if (autor.isPresent()) {

                System.out.println("----Autores----");

                autors.forEach(a -> System.out.println(
                        "\nNome: " + a.getNome() +
                                "\nNascimento: " + a.getAnoNascimento() +
                                "\nFalecimento: " + a.getAnoFalecimento()));
                System.out.println();

            }

        } else {

            System.out.println("\nSem autores cadastrados.\n");

        }

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
        System.out.println();

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
                        "\nIdioma: " + ConverterDados.converterAbreviacao(b.getIdioma()) +
                        "\nDownloads: " + b.getQuantidadeDownloads()

        ));
        System.out.println("\nQuantidade de livros: " + books.size());
        System.out.println("\n");


    }

    private void buscarLivroPorTitulo() {

        System.out.println("Insira o titulo que deseja buscar no BD");
        System.out.print("> ");
        var titulo = scanner.nextLine().toLowerCase();

        Optional<Livro> livro = livroRepository.findByTitulo(titulo);

        if (livro.isPresent()) {

            System.out.println("\nAqui está seu livro: \n");
            System.out.println(livro.get());

        } else {

            System.out.println("""
                    Não encontramos este livro :(
                                        
                    Deseja cadastrá-lo? (S/N)
                    """);
            System.out.print("> ");
            var cadastro = scanner.nextLine();

            if (cadastro.equalsIgnoreCase("s")) {

                cadastrarLivro(titulo);

            }

        }

    }

    private void buscarAutor() {

        System.out.println("Insira o nome do autor");
        System.out.print("> ");
        var nome = scanner.nextLine();

        Optional<Autor> buscaAutor = autorRepository.findByNome(nome);

        if (buscaAutor.isPresent()) {

            Autor a = autorRepository.buscarAutor(nome);

            System.out.println(

                    "\nNome: " + a.getNome() +
                            "\nNascimento: " + a.getAnoNascimento() +
                            "\nFalecimento: " + a.getAnoFalecimento()

            );
            System.out.println();

        }

    }

    private void listarTop10LivrosBaixados() {

        List<Livro> books = livroRepository.findTop10ByOrderByQuantidadeDownloadsDesc();

        books.forEach(System.out::println);

        System.out.println();


    }

    private void deletarTudo() {

        System.out.println("Tem certeza que deseja deletar todos os livros e autores? (S/N)");
        System.out.print("> ");
        var decisao = scanner.nextLine();

        if (decisao.equalsIgnoreCase("s")) {

            autorRepository.deleteAll();
            livroRepository.deleteAll();

        } else {

            System.out.println("\nVoltando...\n");

        }


    }

}

