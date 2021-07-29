package com.pluralsight.conferencedemo.controllers;

import com.pluralsight.conferencedemo.models.Session;
import com.pluralsight.conferencedemo.models.Speaker;
import com.pluralsight.conferencedemo.repositories.SpeakerRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/speakers")
public class SpeakersController {
    //@Autowired gives CRUD access to the SpeakerRepository to the database table and data
    @Autowired
    private SpeakerRepository speakerRepository;

    @GetMapping
    public List<Speaker> list() {
        return speakerRepository.findAll();
    }

    @GetMapping
    @RequestMapping("{id}")
    public Speaker get(@PathVariable Long id) {
        return speakerRepository.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Speaker create(@RequestBody final Speaker speaker) {
        return speakerRepository.saveAndFlush(speaker);
    }

    // Spring provides only @GetMapping and @PostMapping. There is no @DeleteMapping
    // Apart from GET and POST you need to mention the HTTP VERB in the request mapping attribute.
    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable Long id) {
        // Need to check for children record before deleting to deal with cascades.
        speakerRepository.deleteById(id);
    }

    @RequestMapping(value = "{id}", method = RequestMethod.PUT)
    public Speaker update(@PathVariable Long id, @RequestBody Speaker speaker) {
        // because this is PUT, we expect all attributes to be passed in. A PATCH would only need what is updated
        // TODO: Add validation that all attributes are passed in otherwise return a 400 bad paylod.
        Speaker existingSpeaker = speakerRepository.getById(id);
        // The BeanUtils copies incoming session to the existing session. Ignore properties allows us to ignore
        // entities or java objects that we do not want to copy over from one to the other
        // here the session_id is ignored as that is the PK and we don't want to replace it.
        BeanUtils.copyProperties(speaker, existingSpeaker,"speaker_id");
        return speakerRepository.saveAndFlush(existingSpeaker);
    }
}
