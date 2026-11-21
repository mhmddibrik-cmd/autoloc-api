package fr.ece.autolocapi.repository;

import fr.ece.autolocapi.domain.Equipement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EquipementRepository extends JpaRepository<Equipement, Long> {
}
