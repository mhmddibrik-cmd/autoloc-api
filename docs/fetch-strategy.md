# Fetch and cascade strategy — AutoLoc

All associations use `FetchType.LAZY`. `@ManyToOne` and `@OneToOne` are EAGER by default in JPA,
so LAZY is set explicitly to avoid loading chains of related objects on every read.
When related data is needed outside a transaction, use an explicit fetch query (`JOIN FETCH`) or a DTO projection.

| Association | Mapping / owning side | Fetch | Cascade | Justification |
|---|---|---|---|---|
| Contrat → Paiement | `@OneToMany(mappedBy)` / `@ManyToOne`; Paiement owns FK `contrat_id_contrat` | LAZY | ALL + orphanRemoval | A payment has no life without its contract (composition): it is saved and deleted with it, and removing it from the list deletes it. |
| Agence → Vehicule | `@OneToMany(mappedBy)` / `@ManyToOne`; Vehicule owns FK `id_agence` | LAZY | None | A vehicle has its own lifecycle and survives the deletion of its agency (it can be reassigned). |
| Agence → Employe | `@OneToMany(mappedBy)` / `@ManyToOne`; Employe owns FK `id_agence` | LAZY | None | Employees are managed independently (hiring, transfer); deleting an agency must not delete them. |
| Vehicule ↔ Equipement | `@ManyToMany`; Vehicule owns join table `vehicule_equipement` | LAZY | None | Equipment is shared between vehicles; cascading would delete or modify equipment used by other vehicles. `Set` avoids duplicates and inefficient bag removal. |
| Client → Reservation | `@OneToMany(mappedBy)` / `@ManyToOne`; Reservation owns FK `id_client` | LAZY | PERSIST | Saving a new client with new reservations saves them too, but deleting a client must not silently erase reservation history. |
| Reservation → Vehicule | `@ManyToOne` (unidirectional); Reservation owns FK `id_vehicule` | LAZY | None | The vehicle exists independently of any reservation. |
| Reservation ↔ Contrat | `@OneToOne`; Contrat owns FK `id_reservation` (unique) | LAZY | ALL on Reservation side | A contract only exists for its reservation (0..1); it follows the reservation's lifecycle. |
| Vehicule → Maintenance | `@OneToMany(mappedBy)` / `@ManyToOne`; Maintenance owns FK `id_vehicule` | LAZY | PERSIST | New maintenance records are saved with the vehicle, but maintenance history is kept rather than deleted in cascade. |

## Notes

- **Owning side**: the side holding the foreign key (`@ManyToOne`, or the `@OneToOne` / `@ManyToMany` without `mappedBy`).
  Only changes on the owning side are written to the database, so both sides must be kept in sync in code
  (e.g. `paiement.setContrat(contrat)` and `contrat.getPaiements().add(paiement)`).
- **mappedBy** on the inverse side prevents Hibernate from creating an extra join table or a second foreign key.
- **LAZY on the inverse `@OneToOne`** (`Reservation.contrat`): Hibernate cannot create a proxy for the side
  without the foreign key, so it may still load the contract eagerly unless bytecode enhancement is enabled.
- **Lombok**: entities use `@Getter`/`@Setter` instead of `@Data`, so no generated `toString`/`equals`/`hashCode`
  recurses through bidirectional associations.
