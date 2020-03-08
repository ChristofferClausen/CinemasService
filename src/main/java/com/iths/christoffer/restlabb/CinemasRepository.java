package com.iths.christoffer.restlabb;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CinemasRepository extends JpaRepository<Cinema, Long> {

//    Optional<Cinema> findFirstByNameContaining(String name);
    Optional<Cinema>[] findAllByNameContaining(String name);
    Optional<Cinema> findByName(String name);
}
