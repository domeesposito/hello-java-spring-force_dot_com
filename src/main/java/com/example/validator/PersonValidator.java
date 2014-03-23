package com.example.validator;

import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.example.model.Person;
import com.example.service.PersonService;

public class PersonValidator implements Validator {

    private PersonService personService;

    public PersonValidator(PersonService personService) {
        this.personService = personService;
    }

    @Override
    public boolean supports(Class<?> arg0) {
        return Person.class.equals(arg0);
    }

    @Override
    public void validate(Object arg0, Errors errors) {
        ValidationUtils.rejectIfEmpty(errors, "firstName", "", "First name cannot be blank");
        ValidationUtils.rejectIfEmpty(errors, "lastName", "", "Last name cannot be blank");
        Person person = (Person) arg0;
        person = personService.findPerson(person.getEmail());
        if (person != null) {
            errors.rejectValue("email", "", "Person with this email already exists: " +
                person.getFirstName() + " " + person.getLastName());
        }
    }

}
