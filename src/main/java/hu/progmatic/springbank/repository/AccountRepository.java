package hu.progmatic.springbank.repository;

import hu.progmatic.springbank.model.Account;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends CrudRepository<Account, Long> {

    List<Account> findByOrderByName();

    Optional<Account> findByName(String name);

}
