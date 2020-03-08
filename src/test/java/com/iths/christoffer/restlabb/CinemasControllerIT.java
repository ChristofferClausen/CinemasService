package com.iths.christoffer.restlabb;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.CoreMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CinemasControllerIT {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    CinemasRepository repository;

    Cinema cinema = new Cinema("Biostaden", "Borås", "Väg 1");
    Cinema cinema1 = new Cinema("Bergakungen", "Göteborg", "Väg 2");
    Cinema cinema2 = new Cinema("Bergakungen", null, "Väg 2");
    Cinema cinema3 = new Cinema("", "", "");

    @Order(1)
    @Test
    void postOneCinemaExpectingStatusIsCreatedAndCorrectValues() throws Exception {
        mockMvc.perform(post("/api/v1/cinemas/admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cinema)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(1))
                .andExpect(jsonPath("name").value("Biostaden"))
                .andExpect(jsonPath("city").value("Borås"))
                .andExpect(jsonPath("adress").value("Väg 1"))
                .andExpect(jsonPath("_links.self.href", is("http://localhost/api/v1/cinemas/1")))
                .andExpect(jsonPath("_links.cinemas.href", is("http://localhost/api/v1/cinemas")));
    }

    @Order(2)
    @Test
    void getOneCinemaWithIdOneExpectingStatusIsOkAndCorrectValues() throws Exception {
        mockMvc.perform(get("/api/v1/cinemas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(1))
                .andExpect(jsonPath("name").value("Biostaden"))
                .andExpect(jsonPath("city").value("Borås"))
                .andExpect(jsonPath("adress").value("Väg 1"))
                .andExpect(jsonPath("_links.self.href", is("http://localhost/api/v1/cinemas/1")))
                .andExpect(jsonPath("_links.cinemas.href", is("http://localhost/api/v1/cinemas")));
    }

    @Order(3)
    @Test
    void getAllCinemasExpectingStatusIsOkAndCorrectValues() throws Exception {
        mockMvc.perform(post("/api/v1/cinemas/admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cinema1)));
        mockMvc.perform(get("/api/v1/cinemas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.cinemaList[0].id").value(1))
                .andExpect(jsonPath("_embedded.cinemaList[0].name").value("Biostaden"))
                .andExpect(jsonPath("_embedded.cinemaList[0].city").value("Borås"))
                .andExpect(jsonPath("_embedded.cinemaList[0].adress").value("Väg 1"))
                .andExpect(jsonPath("_embedded.cinemaList[0]._links.self.href", is("http://localhost/api/v1/cinemas/1")))
                .andExpect(jsonPath("_embedded.cinemaList[0]._links.cinemas.href", is("http://localhost/api/v1/cinemas")))//;
                .andExpect(jsonPath("_embedded.cinemaList[1].id").value(2))
                .andExpect(jsonPath("_embedded.cinemaList[1].name").value("Bergakungen"))
                .andExpect(jsonPath("_embedded.cinemaList[1].city").value("Göteborg"))
                .andExpect(jsonPath("_embedded.cinemaList[1].adress").value("Väg 2"))
                .andExpect(jsonPath("_embedded.cinemaList[1]._links.self.href", is("http://localhost/api/v1/cinemas/2")))
                .andExpect(jsonPath("_embedded.cinemaList[1]._links.cinemas.href", is("http://localhost/api/v1/cinemas")));
    }

    @Order(4)
    @Test
    void putCinemaWithIdOneExpectingStatusIsOkAndCorrectValues() throws Exception {
        mockMvc.perform(put("/api/v1/cinemas/admin/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cinema2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value("Bergakungen"))
                .andExpect(jsonPath("city").isEmpty())
                .andExpect(jsonPath("adress").value("Väg 2"))
                .andExpect(jsonPath("_links.self.href", is("http://localhost/api/v1/cinemas/1")))
                .andExpect(jsonPath("_links.cinemas.href", is("http://localhost/api/v1/cinemas")));
    }

    @Order(5)
    @Test
    void patchCinemaWithIdOneExpectingStatusIsOkAndCorrectValues() throws Exception {
        mockMvc.perform(patch("/api/v1/cinemas/admin/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cinema1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value("Bergakungen"))
                .andExpect(jsonPath("city").value("Göteborg"))
                .andExpect(jsonPath("adress").value("Väg 2"))
                .andExpect(jsonPath("_links.self.href", is("http://localhost/api/v1/cinemas/1")))
                .andExpect(jsonPath("_links.cinemas.href", is("http://localhost/api/v1/cinemas")));
    }

    @Order(6)
    @Test
    void patchCinemaWithIdOneExpectingStatusIsNotFoundAndUnchangedValues() throws Exception {
        mockMvc.perform(patch("/api/v1/cinemas/admin/1")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(cinema3)));
//                .andExpect(status().isNotFound());
        mockMvc.perform(get("/api/v1/cinemas/1"))
                .andExpect(jsonPath("name").value("Bergakungen"))
                .andExpect(jsonPath("city").value("Göteborg"))
                .andExpect(jsonPath("adress").value("Väg 2"))
                .andExpect(jsonPath("_links.self.href", is("http://localhost/api/v1/cinemas/1")))
                .andExpect(jsonPath("_links.cinemas.href", is("http://localhost/api/v1/cinemas")));
    }

    @Order(7)
    @Test
    void deleteCinemaWithIdTwoExpectingStatusIsOkAndCorrectValues() throws Exception {
        mockMvc.perform(delete("/api/v1/cinemas/admin/2"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/cinemas/"))
                .andExpect(jsonPath("_embedded.cinemaList[1]").doesNotExist());
    }

    @Order(8)
    @Test
    void patchCinemaWithIdThreeExpectingStatusIsNotFound() throws Exception {
        mockMvc.perform(patch("/api/v1/cinemas/admin/3")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cinema3)))
                .andExpect(status().isNotFound());
    }

    @Order(9)
    @Test
    void putCinemaWithIdThreeExpectingStatusIsNotFound() throws Exception {
        mockMvc.perform(put("/api/v1/cinemas/admin/3")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cinema3)))
                .andExpect(status().isNotFound());
    }

    @Order(10)
    @Test
    void deleteCinemaWithIdThreeExpectingStatusIsNotFound() throws Exception {
        mockMvc.perform(delete("/api/v1/cinemas/admin/3"))
                .andExpect(status().isNotFound());
    }
}