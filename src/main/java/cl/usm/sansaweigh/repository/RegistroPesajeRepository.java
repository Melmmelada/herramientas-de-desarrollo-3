package cl.usm.sansaweigh.repository;

import cl.usm.sansaweigh.model.RegistroPesaje;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistroPesajeRepository extends MongoRepository<RegistroPesaje, String> {
}
