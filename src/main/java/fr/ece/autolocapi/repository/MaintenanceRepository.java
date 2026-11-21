package fr.ece.autolocapi.repository;

import fr.ece.autolocapi.domain.Maintenance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MaintenanceRepository extends JpaRepository<Maintenance, Long> {
}
