package hu.progmatic.springbank.repository;

import hu.progmatic.springbank.model.Owner;
import org.springframework.data.repository.CrudRepository;

public interface OwnerRepository extends CrudRepository<Owner, Long> {
}
