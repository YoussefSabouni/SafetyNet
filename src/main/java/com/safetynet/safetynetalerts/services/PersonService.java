package com.safetynet.safetynetalerts.services;

import com.safetynet.safetynetalerts.entities.Person;
import com.safetynet.safetynetalerts.repositories.PersonRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PersonService {

    private final PersonRepository personRepository;

    PersonService(PersonRepository personRepository) {

        this.personRepository = personRepository;
    }


    public Person update(Person person) {

        Person personToUpdate = personRepository.findByFullName(person.getFirstName(), person.getLastName())
                                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (person.getAddress() != null && !person.getAddress().equals(personToUpdate.getAddress())) {

            personToUpdate.setAddress(person.getAddress()
                                            .isEmpty() ? personToUpdate.getAddress() : person.getAddress());
        }

        if (person.getZip() != null && !person.getZip().equals(personToUpdate.getZip())) {

            personToUpdate.setZip((person.getZip() == 0 ? personToUpdate.getZip() : person.getZip()));
        }

        if (person.getCity() != null && !person.getCity().equals(personToUpdate.getCity())) {

            personToUpdate.setCity((person.getCity().isEmpty()) ? personToUpdate.getCity() : person.getCity());
        }

        if (person.getPhone() != null && !person.getPhone().equals(personToUpdate.getPhone())) {

            personToUpdate.setPhone((person.getPhone().isEmpty() ? personToUpdate.getPhone() : person.getPhone()));
        }

        if (person.getEmail() != null && !person.getEmail().equals(personToUpdate.getEmail())) {

            personToUpdate.setEmail(((person.getEmail().isEmpty()) ? personToUpdate.getEmail() : person.getEmail()));
        }

        return this.personRepository.save(personToUpdate);
    }

    public List<Person> findAll() {

        return personRepository.findAll();
    }

    public Optional<Person> findByFullName(String firstName, String lastName) {

        return this.personRepository.findByFullName(firstName, lastName);
    }

    public Person save(Person person) {

        Optional<Person> oPerson = this.personRepository.findByFullName(person.getFirstName(), person.getLastName());

        if (oPerson.isEmpty()) {

            return this.personRepository.save(person);
        }

        throw new ResponseStatusException(HttpStatus.CONFLICT);
    }

    public void delete(String firstName, String lastName) {

        Optional<Person> person = personRepository.findByFullName(firstName, lastName);

        if (person.isPresent()) {

            personRepository.delete(person.get());
        } else {

            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    public List<Person> findAllByAddress(String address) {

        return this.personRepository.findAll()
                                    .stream()
                                    .filter(item -> address.equals(item.getAddress()))
                                    .map(Person::new)
                                    .collect(Collectors.toList());
    }

    public List<Person> findAllByAddresses(List<String> addresses) {

        return this.personRepository.findAll()
                                    .stream()
                                    .filter(item -> addresses.contains(item.getAddress()))
                                    .map(Person::new)
                                    .collect(Collectors.toList());
    }

    public int getChildrenNumber(List<Person> persons) {

        return (int) persons.stream()
                            .filter(item -> Period.between(item.getBirthDate(), LocalDate.now()).getYears() <= 18)
                            .count();
    }

    public List<Person> findAllByCity(String city) {

        return this.personRepository.findAll()
                                    .stream()
                                    .filter(item -> item.getCity().equals(city))
                                    .map(Person::new)
                                    .collect(Collectors.toList());
    }
}
