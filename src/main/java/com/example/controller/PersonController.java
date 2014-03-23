package com.example.controller;

import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.model.Person;
import com.example.service.PersonService;
import com.example.validator.PersonValidator;

@Controller
public class PersonController {

    @Autowired
    private PersonService personService;

    @InitBinder(value = "person")
    protected void initBinder(WebDataBinder binder) {
     binder.setValidator(new PersonValidator(personService));
    }

    @RequestMapping("/")
    public String listPeople(Map<String, Object> map) {

        map.put("person", new Person());
        map.put("peopleList", personService.listPeople());

        return "people";
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String addPerson(@ModelAttribute("person") @Valid Person person, BindingResult result, RedirectAttributes redirectAttributes) {

        if (result.hasFieldErrors("email"))
            redirectAttributes.addFlashAttribute("error_message", result.getFieldError("email").getDefaultMessage());
        else if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error_message", "Uh Oh.... Something went wrong.");
        } else {
            personService.addPerson(person);
        }

        return "redirect:/people/";
    }

    @RequestMapping("/delete/{personId}")
    public String deletePerson(@PathVariable("personId") String personId) {

        personService.removePerson(personId);

        return "redirect:/people/";
    }
}
