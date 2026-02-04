package org.cloudfoundry.samples.music.web;

import org.cloudfoundry.samples.music.domain.Album;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(AlbumController.class)
public class AlbumControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CrudRepository<Album, String> repository;

    @Test
    public void testGetAllAlbums() throws Exception {
        Album album1 = new Album("Abbey Road", "The Beatles", "1969", "Rock");
        album1.setId("1");
        Album album2 = new Album("Thriller", "Michael Jackson", "1982", "Pop");
        album2.setId("2");

        when(repository.findAll()).thenReturn(Arrays.asList(album1, album2));

        mockMvc.perform(get("/albums"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].title").value("Abbey Road"))
                .andExpect(jsonPath("$[0].artist").value("The Beatles"))
                .andExpect(jsonPath("$[1].id").value("2"))
                .andExpect(jsonPath("$[1].title").value("Thriller"));

        verify(repository).findAll();
    }

    @Test
    public void testGetAlbumById() throws Exception {
        Album album = new Album("Dark Side of the Moon", "Pink Floyd", "1973", "Rock");
        album.setId("123");

        when(repository.findById("123")).thenReturn(Optional.of(album));

        mockMvc.perform(get("/albums/123"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("123"))
                .andExpect(jsonPath("$.title").value("Dark Side of the Moon"))
                .andExpect(jsonPath("$.artist").value("Pink Floyd"))
                .andExpect(jsonPath("$.releaseYear").value("1973"));

        verify(repository).findById("123");
    }

    @Test
    public void testGetAlbumByIdNotFound() throws Exception {
        when(repository.findById(anyString())).thenReturn(Optional.empty());

        mockMvc.perform(get("/albums/999"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(repository).findById("999");
    }

    @Test
    public void testAddAlbum() throws Exception {
        Album album = new Album("Back in Black", "AC/DC", "1980", "Rock");
        album.setId("456");

        when(repository.save(any(Album.class))).thenReturn(album);

        mockMvc.perform(put("/albums")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Back in Black\",\"artist\":\"AC/DC\",\"releaseYear\":\"1980\",\"genre\":\"Rock\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("456"))
                .andExpect(jsonPath("$.title").value("Back in Black"));

        verify(repository).save(any(Album.class));
    }

    @Test
    public void testUpdateAlbum() throws Exception {
        Album album = new Album("Rumours", "Fleetwood Mac", "1977", "Rock");
        album.setId("789");

        when(repository.save(any(Album.class))).thenReturn(album);

        mockMvc.perform(post("/albums")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"789\",\"title\":\"Rumours\",\"artist\":\"Fleetwood Mac\",\"releaseYear\":\"1977\",\"genre\":\"Rock\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("789"))
                .andExpect(jsonPath("$.title").value("Rumours"));

        verify(repository).save(any(Album.class));
    }

    @Test
    public void testDeleteAlbum() throws Exception {
        mockMvc.perform(delete("/albums/123"))
                .andExpect(status().isOk());

        verify(repository).deleteById("123");
    }
}
