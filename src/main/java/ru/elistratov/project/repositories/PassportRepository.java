package ru.elistratov.project.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.elistratov.project.model.InvalidPassport;

import java.util.List;
import java.util.Optional;

@Repository
public interface PassportRepository extends JpaRepository<InvalidPassport, Integer> {
    Optional<InvalidPassport> findByNumber(String number);
    List<InvalidPassport> findAllByNumberIn(List<String> numbers);
}
