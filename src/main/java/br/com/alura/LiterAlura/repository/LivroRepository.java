package br.com.alura.LiterAlura.repository;

import br.com.alura.LiterAlura.models.Livro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LivroRepository extends JpaRepository<Livro, Long> {


    @Query("SELECT l FROM Livro l WHERE l.idioma = :idioma")
    List<Livro> buscarLivroPorIdioma(String idioma);

}
