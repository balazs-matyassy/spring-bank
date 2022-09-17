package hu.progmatic.springbank.repository;

import hu.progmatic.springbank.model.Account;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AccountRepository extends CrudRepository<Account, Long> {

    List<Account> findByOrderByName();

}
