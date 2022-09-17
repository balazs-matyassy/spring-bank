package hu.progmatic.springbank.service;

import hu.progmatic.springbank.model.Account;
import hu.progmatic.springbank.repository.AccountRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AccountService {

    @PersistenceContext
    private EntityManager entityManager;

    private final AccountRepository accountRepository;



    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public List<Account> getAccounts() {
        return accountRepository.findByOrderByName();
    }

    // https://www.baeldung.com/spring-data-criteria-queries
    // Kiegészítés: https://www.baeldung.com/rest-api-search-language-spring-data-querydsl
    // Kiegészítés: https://www.baeldung.com/intro-to-querydsl
    public List<Account> searchAccounts(String name, String number) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Account> criteriaQuery = criteriaBuilder.createQuery(Account.class);

        Root<Account> account = criteriaQuery.from(Account.class);

        List<Predicate> predicates = new ArrayList<>();

        if (!name.isBlank()) {
            Predicate namePredicate = criteriaBuilder.equal(account.get("name"), name);
            predicates.add(namePredicate);
        }

        if (!number.isBlank()) {
            Predicate numberPredicate = criteriaBuilder.like(account.get("number"), "%" + number + "%");
            predicates.add(numberPredicate);
        }

        criteriaQuery.where(predicates.toArray(new Predicate[0]));

        TypedQuery<Account> query = entityManager.createQuery(criteriaQuery);
        return query.getResultList();
    }

    public Account saveAccount(Account account) {
        return accountRepository.save(account);
    }

    // https://www.baeldung.com/spring-transactional-propagation-isolation
    @Transactional
    public void transfer(String nameFrom, String nameTo, int amount) {
        Account accountFrom = accountRepository.findByName(nameFrom).orElseThrow();
        Account accountTo = accountRepository.findByName(nameTo).orElseThrow();

        accountFrom.setBalance(accountFrom.getBalance() - amount);
        accountTo.setBalance(accountTo.getBalance() + amount);

        // tranzakciók nélkül -1000
        accountRepository.save(accountFrom);

        // Itt megy el az áram... :(
        if (true) {
            throw new RuntimeException();
        }

        // tranzakciók nélkül 0 (elveszik 1000 forint)
        accountRepository.save(accountTo);
    }

}
