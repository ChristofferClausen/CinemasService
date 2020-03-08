package com.iths.christoffer.restlabb;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.net.http.HttpClient;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

//@EnableEurekaClient
@RestController
@Slf4j
@RequestMapping("/api/v1/cinemas")
public class CinemasController {

    @Autowired
    RestTemplate restTemplate;
    HttpClient client;

    final CinemasRepository repository;
    final ModelAssembler assembler;

    public CinemasController(CinemasRepository repository, ModelAssembler assembler) {
        this.repository = repository;
        this.assembler = assembler;
    }

    @GetMapping()
    public CollectionModel<EntityModel<Cinema>> all() {
        log.debug("called: all()");
        return assembler.toCollectionModel(repository.findAll());
    }

    @GetMapping(value = "/{id:[0-9]}")
    public ResponseEntity<EntityModel<Cinema>> one(@PathVariable long id) {
        log.info("Called: one");
        return repository.findById(id)
                .map(assembler::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/admin")
    public ResponseEntity<EntityModel<Cinema>> createCinema(@RequestBody Cinema cinema) {
        log.info("Called: createCinema()");
        var c = repository.save(cinema);
        log.info("Saved: " + c);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(linkTo(CinemasController.class).slash(c.getId()).toUri());
        return new ResponseEntity<>(assembler.toModel(c), headers, HttpStatus.CREATED);
    }

    @PatchMapping("/admin/{id}")
    public ResponseEntity<EntityModel<Cinema>> updateCinema(@RequestBody Cinema newCinema, @PathVariable long id) {
        log.info("Called: updateCinema()");
        return repository.findById(id).map(c -> {
            if (!newCinema.getName().isEmpty())
                c.setName(newCinema.getName());
            if (!newCinema.getCity().isEmpty())
                c.setCity(newCinema.getCity());
            if (!newCinema.getAdress().isEmpty())
                c.setAdress(newCinema.getAdress());
            repository.save(c);
            log.debug("Object updated");
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(linkTo(CinemasController.class).slash(c.getId()).toUri());
            return new ResponseEntity<>(assembler.toModel(c), headers, HttpStatus.OK);
        }).orElseGet(() -> {
            log.debug("No fields updated in cinema with id: " + repository.getOne(id).getId());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        });
    }

    @PutMapping("/admin/{id}")
    public ResponseEntity<EntityModel<Cinema>> replaceCinema(@RequestBody Cinema newCinema, @PathVariable long id) {
        log.info("Called: replaceCinema()");
        return repository.findById(id).map(c -> {
            c.setName(newCinema.getName());
            c.setCity(newCinema.getCity());
            c.setAdress(newCinema.getAdress());
            repository.save(c);
            log.debug("Object replaced");
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(linkTo(CinemasController.class).slash(c.getId()).toUri());
            return new ResponseEntity<>(assembler.toModel(c), headers, HttpStatus.OK);
        }).orElseGet(() ->
                new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/admin/{id}")
    public ResponseEntity<?> deleteCinema(@PathVariable long id) {
        log.info("Called: deleteCinema");
        if (repository.existsById(id)) {
            repository.deleteById(id);
            log.debug("Object deleted");
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

}
