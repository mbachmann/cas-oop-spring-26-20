package com.example.demoinitial.config;

import com.example.demoinitial.domain.Person;
import com.example.demoinitial.repository.PersonRepository;
import com.example.demoinitial.utils.HasLogger;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Transactional service responsible for seeding dev data.
 * All seed operations run inside a single transaction:
 * if any step fails the whole batch is rolled back.
 */
@Service
@Profile("dev")
@Transactional
public class DevDataService implements HasLogger {

    @Autowired
    @Qualifier("felixMuster")
    Person felixMuster;

    @Autowired
    @Qualifier("maxMustermann")
    Person maxMustermann;

    @Autowired
    PersonRepository personRepository;

    /**
     * Single transactional entry point for all seed data.
     * Called through the Spring proxy by DevDataInitializer so the
     * transaction boundary is properly enforced.
     */
    public void createData() {
        personRepository.save(felixMuster);
        personRepository.save(maxMustermann);

        Person johnDoe = new Person();
        johnDoe.setFirstName("John");
        johnDoe.setLastName("Doe");
        personRepository.save(johnDoe);

        getLogger().debug("Person felixMuster and maxMustermann saved to DB");

        List<Person> persons = personRepository.findQueryByLastName("Mustermann");
        persons.forEach(p -> getLogger().debug("findQueryByLastName Mustermann = " + p));

        personRepository.findAll(Sort.by(Sort.Direction.ASC, "lastName")).forEach(p ->
                personRepository.findById(p.getId())
                        .ifPresent(found -> getLogger().debug("findAll, Sort by lastName ASC = " + found)));
    }
}

