package ru.elistratov.project.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.elistratov.project.model.ListVersion;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface ListVersionRepository extends JpaRepository<ListVersion, Integer>  {

    ListVersion findTopByOrderByUploadDateDesc();

    Optional<ListVersion> findListVersionByUploadDate(LocalDate uploadDate);

}
