package com.iths.christoffer.restlabb;

import lombok.Data;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ModelAssembler implements RepresentationModelAssembler<Cinema, EntityModel<Cinema>> {

    @Override
    public EntityModel<Cinema> toModel(Cinema cinema) {
            return  new EntityModel<>(cinema,
                    linkTo(methodOn(CinemasController.class).one(cinema.getId())).withSelfRel(),
                    linkTo(methodOn(CinemasController.class).all()).withRel("cinemas"));
    }

    @Override
    public CollectionModel<EntityModel<Cinema>> toCollectionModel(Iterable<? extends Cinema> entities) {
         var collection = StreamSupport.stream(entities.spliterator(), false)
                 .map(this::toModel)
                 .collect(Collectors.toList());

         return new CollectionModel<>(collection,
                 linkTo(methodOn(CinemasController.class).all()).withSelfRel());
    }
}
