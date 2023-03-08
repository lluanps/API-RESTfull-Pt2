package br.com.luan.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.luan.model.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>{

}
