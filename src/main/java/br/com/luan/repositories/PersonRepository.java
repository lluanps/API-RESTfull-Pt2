package br.com.luan.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.luan.model.Person;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long>{

}
