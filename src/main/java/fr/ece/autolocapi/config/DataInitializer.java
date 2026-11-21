package fr.ece.autolocapi.config;

import fr.ece.autolocapi.domain.*;
import fr.ece.autolocapi.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Inserts demo data at startup when the database is empty (dev profile only).
 */
@Slf4j
@Component
@Profile("dev")
@Order(1)
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final AgenceRepository agenceRepository;
    private final EquipementRepository equipementRepository;
    private final VehiculeRepository vehiculeRepository;
    private final EmployeRepository employeRepository;
    private final ClientRepository clientRepository;
    private final ContratRepository contratRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (agenceRepository.count() > 0) {
            log.info("Demo data already present, skipping initialization");
            return;
        }

        Agence paris = agenceRepository.save(agence("AutoLoc Paris", "Paris", "10 rue de Rivoli", "0140000000"));
        Agence lyon = agenceRepository.save(agence("AutoLoc Lyon", "Lyon", "5 place Bellecour", "0470000000"));

        Equipement gps = equipementRepository.save(equipement("GPS"));
        Equipement siegeBebe = equipementRepository.save(equipement("Siege bebe"));

        Vehicule clio = vehicule("AB-123-CD", "Renault", "Clio", CategorieVehicule.CITADINE, "45.00", paris);
        clio.getEquipements().add(gps);
        Vehicule peugeot = vehicule("EF-456-GH", "Peugeot", "3008", CategorieVehicule.SUV, "80.00", paris);
        peugeot.getEquipements().addAll(List.of(gps, siegeBebe));
        Vehicule kangoo = vehicule("IJ-789-KL", "Renault", "Kangoo", CategorieVehicule.UTILITAIRE, "65.00", lyon);
        kangoo.setStatut(StatutVehicule.MAINTENANCE);
        addMaintenance(kangoo, "Vidange et revision");
        vehiculeRepository.save(clio);
        vehiculeRepository.save(peugeot);
        vehiculeRepository.save(kangoo); // CascadeType.PERSIST saves its maintenance

        employeRepository.save(employe("Martin", "Sophie", RoleEmploye.MANAGER, paris));
        employeRepository.save(employe("Bernard", "Lucas", RoleEmploye.AGENT, lyon));

        Client client = client("Dupont", "Jean", "jean.dupont@example.com", "PERMIS-0001");
        Reservation reservation = reservation(client, clio);
        clientRepository.save(client); // CascadeType.PERSIST saves its reservation

        Contrat contrat = contrat(reservation, "135.00");
        addPaiement(contrat, "50.00", ModePaiement.CARTE);
        addPaiement(contrat, "85.00", ModePaiement.VIREMENT);
        contratRepository.save(contrat); // CascadeType.ALL saves its payments

        log.info("Demo data inserted: 2 agences, 3 vehicules, 2 employes, 1 client, 1 contrat with 2 paiements");
    }

    private static Agence agence(String nom, String ville, String adresse, String telephone) {
        Agence agence = new Agence();
        agence.setNom(nom);
        agence.setVille(ville);
        agence.setAdresse(adresse);
        agence.setTelephone(telephone);
        return agence;
    }

    private static Equipement equipement(String libelle) {
        Equipement equipement = new Equipement();
        equipement.setLibelle(libelle);
        return equipement;
    }

    private static Vehicule vehicule(String immatriculation, String marque, String modele,
                                     CategorieVehicule categorie, String tarifJournalier, Agence agence) {
        Vehicule vehicule = new Vehicule();
        vehicule.setImmatriculation(immatriculation);
        vehicule.setMarque(marque);
        vehicule.setModele(modele);
        vehicule.setCategorie(categorie);
        vehicule.setTarifJournalier(new BigDecimal(tarifJournalier));
        vehicule.setStatut(StatutVehicule.DISPONIBLE);
        vehicule.setAgence(agence);
        return vehicule;
    }

    private static void addMaintenance(Vehicule vehicule, String description) {
        Maintenance maintenance = new Maintenance();
        maintenance.setDateDebut(LocalDate.now());
        maintenance.setDescription(description);
        maintenance.setVehicule(vehicule);
        vehicule.getMaintenances().add(maintenance);
    }

    private static Employe employe(String nom, String prenom, RoleEmploye role, Agence agence) {
        Employe employe = new Employe();
        employe.setNom(nom);
        employe.setPrenom(prenom);
        employe.setRole(role);
        employe.setAgence(agence);
        return employe;
    }

    private static Client client(String nom, String prenom, String email, String numPermis) {
        Client client = new Client();
        client.setNom(nom);
        client.setPrenom(prenom);
        client.setEmail(email);
        client.setNumPermis(numPermis);
        client.setDateInscription(LocalDate.now());
        return client;
    }

    private static Reservation reservation(Client client, Vehicule vehicule) {
        Reservation reservation = new Reservation();
        reservation.setDateDebut(LocalDate.now().plusDays(1));
        reservation.setDateFin(LocalDate.now().plusDays(4));
        reservation.setStatut(StatutReservation.CONFIRMEE);
        reservation.setVehicule(vehicule);
        reservation.setClient(client);
        client.getReservations().add(reservation);
        return reservation;
    }

    private static Contrat contrat(Reservation reservation, String montantTotal) {
        Contrat contrat = new Contrat();
        contrat.setDateSignature(LocalDate.now());
        contrat.setMontantTotal(new BigDecimal(montantTotal));
        contrat.setValide(true);
        contrat.setReservation(reservation);
        reservation.setContrat(contrat);
        return contrat;
    }

    private static void addPaiement(Contrat contrat, String montant, ModePaiement mode) {
        Paiement paiement = new Paiement();
        paiement.setMontant(new BigDecimal(montant));
        paiement.setDatePaiement(LocalDate.now());
        paiement.setModePaiement(mode);
        paiement.setContrat(contrat);
        contrat.getPaiements().add(paiement);
    }
}
