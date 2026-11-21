package fr.ece.autolocapi.repository;

import fr.ece.autolocapi.domain.Vehicule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehiculeRepository extends JpaRepository<Vehicule, Long> {
}
