package fr.ece.autolocapi.repository;

import fr.ece.autolocapi.domain.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long> {
}
