package fr.ece.autolocapi.repository;

import fr.ece.autolocapi.domain.Agence;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgenceRepository extends JpaRepository<Agence, Long> {
}
