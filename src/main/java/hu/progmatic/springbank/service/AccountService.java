package hu.progmatic.springbank.service;

import hu.progmatic.springbank.model.Account;
import hu.progmatic.springbank.model.Owner;
import hu.progmatic.springbank.repository.AccountRepository;
import hu.progmatic.springbank.repository.OwnerRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AccountService {

    @PersistenceContext
    private EntityManager entityManager;

    private final AccountRepository accountRepository;

    private final OwnerRepository ownerRepository;

    public AccountService(AccountRepository accountRepository, OwnerRepository ownerRepository) {
        this.accountRepository = accountRepository;
        this.ownerRepository = ownerRepository;
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

    public Optional<Account> getById(long id) {
        return accountRepository.findById(id);
    }

    public List<Account> getByOwnerName(String name) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Account> criteriaQuery = criteriaBuilder.createQuery(Account.class);

        Root<Owner> owner = criteriaQuery.from(Owner.class);
        Join<Owner, Account> account = owner.join("account");

        criteriaQuery.where(criteriaBuilder.like(owner.get("name"), "%" + name + "%"));

        criteriaQuery.select(account).distinct(true);
        TypedQuery<Account> query = entityManager.createQuery(criteriaQuery);
        return query.getResultList();
    }

    public Account saveAccount(Account account) {
        Account savedAccount = accountRepository.save(account);

        if (savedAccount.getOwners().isEmpty()) {
            Owner owner = new Owner();
            owner.setAccount(savedAccount);
            owner.setName(savedAccount.getName());

            ownerRepository.save(owner);

            owner = new Owner();
            owner.setAccount(savedAccount);
            owner.setName(new StringBuilder(savedAccount.getName()).reverse().toString());

            ownerRepository.save(owner);
        }

        return savedAccount;
    }

    // https://www.baeldung.com/spring-transactional-propagation-isolation
    // @Transactional
    public void transfer(String nameFrom, String nameTo, int amount) {
        Account accountFrom = accountRepository.findByName(nameFrom).orElseThrow();
        Account accountTo = accountRepository.findByName(nameTo).orElseThrow();

        accountFrom.setBalance(accountFrom.getBalance() - amount);

        // tranzakciók nélkül -1000
        accountRepository.save(accountFrom);

        accountTo.setBalance(accountTo.getBalance() + amount);

        // Itt megy el az áram... :(
        if (true) {
            throw new RuntimeException();
        }

        // tranzakciók nélkül 0 (elveszik 1000 forint)
        accountRepository.save(accountTo);
    }

}
