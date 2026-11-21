package fr.ece.autolocapi.repository;

import fr.ece.autolocapi.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}
