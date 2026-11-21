package fr.ece.autolocapi.repository;

import fr.ece.autolocapi.domain.Contrat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContratRepository extends JpaRepository<Contrat, Long> {
}
