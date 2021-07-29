package com.pluralsight.conferencedemo.controllers;

import com.pluralsight.conferencedemo.models.Session;
import com.pluralsight.conferencedemo.repositories.SessionRepository;
import org.hibernate.internal.SessionOwnerBehavior;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sessions")
public class SessionsController {
    @Autowired
    private SessionRepository sessionRepository;

    @GetMapping
    public List<Session> list() {
        return sessionRepository.findAll();
    }

    @GetMapping
    @RequestMapping("{id}")
    public Session get(@PathVariable Long id) {
        return sessionRepository.getById(id);
    }

    // @PostMapping - HTTP verb POST for the API call
    // The data is send as a JSON to the session object in the RequestBody. The Spring MVC takes
    // all these attributes in a JSON payload and automatically marshalls into a session object
    // By default the Rest controllers will return 200 (ok) as the response status for all the calls
    // even though @PostMapping annotation is used. Typically when you create something or post something
    // you get 201 back. But Spring Rest controller will return 200. To override add the @ResponseStatus
    // annotation you can send the exact repose you want to send back.
    // One more thing to remember while working with JPA and Entities is that if you save the object
    // it doesn't commit to the database until you flush it.
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Session create(@RequestBody final Session session) {
        return sessionRepository.saveAndFlush(session);
    }

    // Spring provides only @GetMapping and @PostMapping. There is no @DeleteMapping
    // Apart from GET and POST you need to mention the HTTP VERB in the request mapping attribute.
    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable Long id) {
        // Need to check for children record before deleting to deal with cascades.
        sessionRepository.deleteById(id);
    }

    @RequestMapping(value = "{id}", method = RequestMethod.PUT)
    public Session update(@PathVariable Long id, @RequestBody Session session) {
        // because this is PUT, we expect all attributes to be passed in. A PATCH would only need what is updated
        // TODO: Add validation that all attributes are passed in otherwise return a 400 bad paylod.
        Session existingSession = sessionRepository.getById(id);
        // The BeanUtils copies incoming session to the existing session. Ignore properties allows us to ignore
        // entities or java objects that we do not want to copy over from one to the other
        // here the session_id is ignored as that is the PK and we don't want to replace it.
        BeanUtils.copyProperties(session, existingSession,"session_id");
        return sessionRepository.saveAndFlush(existingSession);
    }
}
