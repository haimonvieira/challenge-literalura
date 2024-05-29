package br.com.alura.LiterAlura.main;

import br.com.alura.LiterAlura.exception.ErroDeConversaoDeAutor;
import br.com.alura.LiterAlura.exception.ErroDeConversaoDeLivro;
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
    private final String ENDERECO = "https://gutendex.com/books/?";
    private final LivroRepository livroRepository;
    private final AutorRepository autorRepository;
    private Optional<Livro> livroExiste;
    private Optional<Autor> autorExiste;

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
                    buscarLivro();
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

    //Apenas é inserido a busca do livro para depois ser pesquisado de fato
    private void buscarLivro() {

        var pesquisa = "";

        System.out.println("Insira o nome do livro para buscar");
        System.out.print("> ");
        pesquisa = scanner.nextLine().toLowerCase();

        cadastrarLivro(pesquisa);

    }

    //É feito a pesquisa do livro caso não esteja cadastrado no banco de dados;
    //Retorna objeto to tipo DadosResultado
    private DadosResultado getDadosLivro(String pesquisa) {

        System.out.println("Estou fazendo a pesquisa do seu livro...\n");

        var json = consumoAPI.obterDados(ENDERECO + "search=" +
                pesquisa.replace(" ", "+"));
        System.out.println(json);

        //Aqui a IDE diz que é redundante, mas deixei pois deixa mais fácil de entender
        DadosResultado dadosResultado = converterDados.obterDados(json, DadosResultado.class);

        return dadosResultado;

    }

    //Aqui é transformado o objeto DadosResultado para DadosLivro e DadosAutor
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

                autorExiste = autorRepository.findByNome(dadosResultado
                        .dadosLivro().get(0).autores().get(0).nome().toLowerCase());

                if (autorExiste.isPresent()) {

                    System.out.println("Autor já existe.");

                } else {

                    System.out.println("Estou convertendo o livro...");

                    List<DadosLivro> dadosLivros = dadosResultado.dadosLivro().stream()
                            .map(l -> new DadosLivro(l.titulo(), l.quantidadeDownloads(), l.idiomas(),
                                    l.autores()))
                            .toList();

                    //Pegando mapeando dadosLivros para DadosAutor
                    //Acredito que mapeei errado aqui, pois deveria adicionar o livro do Autor,
                    //deveria ser bidirecional
                    List<DadosAutor> dadosAutores = dadosLivros.stream()
                            .map(d -> new DadosAutor(d.autores().get(0).nome(), d.autores().get(0).anoNascimento(),
                                    d.autores().get(0).anoFalecimento()))
                            .toList();

                    if(!dadosAutores.isEmpty()){

                        //Envia os dados de livro e autor para verificar se pode salva-los
                        salvarLivro(dadosLivros, dadosAutores);

                    }else {

                        System.out.println("Não foi possível cadastrar o livro.");

                    }

                }

            }
        }

    }

    //Aqui é cadastrado o Autor e Livro no banco de dados
    private void salvarLivro(List<DadosLivro> dadosLivros, List<DadosAutor> dadosAutores) {

        System.out.println("Estou inserindo no banco de dados...\n");

        try {
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
        }catch (ErroDeConversaoDeLivro | ErroDeConversaoDeAutor e){

            System.out.println("Houve um erro ao inserir o livro ou o autor no BD: '");
            System.out.println(e.getMessage() + "'");

        }

        System.out.println("\nLivro cadastrado com sucesso!\n");

    }

    //Lista somente os livros
    private void listarLivrosCadastrados() {

        List<Livro> books = livroRepository.findAll();

        if (!books.isEmpty()) {

            livroExiste = livroRepository.findByTitulo(books.get(0).getTitulo());

            if (livroExiste.isPresent()) {
                System.out.println("----Livros----");
                books.forEach(System.out::println);
                System.out.println();
            }

        } else {

            System.out.println("\nSem livros cadastrados.\n");

        }

    }

    //Lista somente os autores
    private void listarAutores() {

        List<Autor> autors = autorRepository.findAll();

        if (!autors.isEmpty()) {

            autorExiste = autorRepository.findByNome(autors.get(0).getNome().toLowerCase());

            if (autorExiste.isPresent()) {

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

    //Lista autores vivos até um ano determinado
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

    //Lista e conta os livros de um determinado idioma
    //O usuário pode digitar a lingua por extendo ou abreviada ou um trecho dela
    //mas caso seja por extenso, tem de ser sem acentos, pois não foi tratado o idioma
    //com acentos
    private void listarLivrosDeUmDeterminadoIdioma() {

        System.out.println("Insira o idioma (abreviado ou completo)");
        System.out.print("> ");
        var idioma = scanner.nextLine().toLowerCase();

        if (idioma.length() > 2) {
            idioma = ConverterDados.converterIdioma(idioma);
        }

        List<Livro> books = livroRepository.buscarLivroPorIdioma(idioma);
        System.out.println("---Autores com idioma '" + idioma + "'---");

        books.forEach(System.out::println);
        System.out.println("\nQuantidade de livros: " + books.size());
        System.out.println("\n");


    }

    //Busca o livro pelo titulo
    private void buscarLivroPorTitulo() {

        System.out.println("Insira o titulo que deseja buscar no BD");
        System.out.print("> ");
        var titulo = scanner.nextLine().toLowerCase();

        livroExiste = livroRepository.findByTitulo(titulo);

        if (livroExiste.isPresent()) {

            System.out.println("\nAqui está seu livro: \n");
            System.out.println(livroExiste.get());

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

    //Busca o autor pelo nome
    private void buscarAutor() {

        System.out.println("Insira o nome do autor");
        System.out.print("> ");
        var nome = scanner.nextLine().toLowerCase();

        autorExiste = autorRepository.findByNome(nome);

        if (autorExiste.isPresent()) {

            Autor a = autorRepository.buscarAutor(nome);

            System.out.println(

                    "\nNome: " + a.getNome() +
                            "\nNascimento: " + a.getAnoNascimento() +
                            "\nFalecimento: " + a.getAnoFalecimento()

            );
            System.out.println();

        }

    }

    //Lista os top 10 da API e os top 'x' do BD
    private void listarTop10LivrosBaixados() {

        System.out.println("Estou buscando os top 10 livros mais baixados do Gutendex...");
        var json = consumoAPI.obterDados(ENDERECO);

        DadosResultado dadosResultado = converterDados.obterDados(json, DadosResultado.class);

        List<DadosLivro> top10Downloads = dadosResultado.dadosLivro().stream()
                .limit(10)
                .toList();

        System.out.println("\n----Top 10 livros mais baixados do Gutendex----");

        for(int i = 0; i < top10Downloads.size(); i++){

            System.out.println();
            System.out.println((i+1) + "- "  + "Titulo: " + top10Downloads.get(i).titulo() +
                    "\nDownloads: " + top10Downloads.get(i).quantidadeDownloads() +
                    "\nIdioma: " + ConverterDados.converterAbreviacao(top10Downloads.get(i).idiomas().get(0)));

        }

        System.out.println();

        List<Livro> books = livroRepository.findTop10ByOrderByQuantidadeDownloadsDesc();

        int top10 = 10;

        if (!books.isEmpty() && books.size() <= 10) {

            top10 = books.size();

        }

        if (!books.isEmpty()) {

            System.out.println("\n----Top " + top10 + " livros mais baixados do BD----");

            books.forEach(b ->

                    System.out.println(
                            "\nTitulo: " + b.getTitulo() +
                                    "\nIdioma: " + ConverterDados.converterAbreviacao(b.getIdioma()) +
                                    "\nDownloads: " + b.getQuantidadeDownloads()

                    )

            );

            System.out.println();

        } else {

            System.out.println("Sem livros cadastrados.");

        }


    }

    //Deletar os registros dos livros e autores;
    //Porém, não reseta os id's
    private void deletarTudo() {

        System.out.println("Tem certeza que deseja deletar todos os livros e autores? (S/N)");
        System.out.print("> ");
        var decisao = scanner.nextLine();

        if (decisao.equalsIgnoreCase("s")) {

            livroRepository.deleteAll();

        } else {

            System.out.println("\nVoltando...\n");

        }


    }

}

