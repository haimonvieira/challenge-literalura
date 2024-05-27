package br.com.alura.LiterAlura.repository;

import br.com.alura.LiterAlura.models.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AutorRepository extends JpaRepository<Autor, Long> {

    //Nasceu 1830, Morreu: 1880
    //1880 > 1800 AND 1830 >= 1800
    //Mostrar autores vivos ate este ano
    //1800 < a.anoFalecimento
    //a.anoFalecimento > 1800
    @Query("SELECT a FROM Autor a WHERE a.anoFalecimento > :ano AND a.anoNascimento <= :ano")
    List<Autor> buscarAutoresAte(int ano);
}
