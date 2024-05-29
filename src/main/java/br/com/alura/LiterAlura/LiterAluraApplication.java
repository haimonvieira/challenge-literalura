package br.com.alura.LiterAlura;

import br.com.alura.LiterAlura.main.Main;
import br.com.alura.LiterAlura.repository.AutorRepository;
import br.com.alura.LiterAlura.repository.LivroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
Quero deixar explicado aqui que não foi mapeado corretamente as entidades, então, por isso não consegui inserir varios
livros no autor, por mais que está tivesse sido a idéia inicial.

Também percebi que meu código está dificil de entender, vamos dizer que está sujo e muito "longo"
 */
@SpringBootApplication
public class LiterAluraApplication implements CommandLineRunner {

	//Injetando DatabaseService
	@Autowired
	private LivroRepository livroRepository;
	@Autowired
	private AutorRepository autorRepository;

	public static void main(String[] args){
		SpringApplication.run(LiterAluraApplication.class, args);
	}


	@Override
	public void run(String... args){

		Main main = new Main(livroRepository, autorRepository);
		main.exibirMenu();

	}
}
