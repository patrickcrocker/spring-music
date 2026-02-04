package org.cloudfoundry.samples.music.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cloudfoundry.samples.music.domain.Album;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AlbumController.class)
class AlbumControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CrudRepository<Album, String> repository;

    @Test
    void albumsReturnsAllAlbums() throws Exception {
        Album album = album();
        when(repository.findAll()).thenReturn(List.of(album));

        mockMvc.perform(get("/albums"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(album.getId()))
                .andExpect(jsonPath("$[0].title").value(album.getTitle()))
                .andExpect(jsonPath("$[0].artist").value(album.getArtist()))
                .andExpect(jsonPath("$[0].genre").value(album.getGenre()));
    }

    @Test
    void addAlbumPersistsAndReturnsSavedAlbum() throws Exception {
        Album album = album();
        when(repository.save(any(Album.class))).thenReturn(album);

        mockMvc.perform(put("/albums")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(album)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(album.getId()))
                .andExpect(jsonPath("$.title").value(album.getTitle()))
                .andExpect(jsonPath("$.artist").value(album.getArtist()));

        verify(repository).save(argThat(matchesAlbum(album)));
    }

    @Test
    void updateAlbumPersistsChanges() throws Exception {
        Album album = album();
        album.setTitle("Updated Title");
        when(repository.save(any(Album.class))).thenReturn(album);

        mockMvc.perform(post("/albums")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(album)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"));

        verify(repository).save(argThat(matchesAlbum(album)));
    }

    @Test
    void getAlbumByIdReturnsAlbum() throws Exception {
        Album album = album();
        when(repository.findById(album.getId())).thenReturn(Optional.of(album));

        mockMvc.perform(get("/albums/{id}", album.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(album.getId()))
                .andExpect(jsonPath("$.title").value(album.getTitle()));
    }

    @Test
    void deleteAlbumByIdDelegatesToRepository() throws Exception {
        Album album = album();

        mockMvc.perform(delete("/albums/{id}", album.getId()))
                .andExpect(status().isOk());

        verify(repository).deleteById(album.getId());
    }

    private Album album() {
        Album album = new Album("The Album", "Test Artist", "2024", "Test Genre");
        album.setId("album-123");
        album.setTrackCount(10);
        album.setAlbumId("external-1");
        return album;
    }

    private ArgumentMatcher<Album> matchesAlbum(Album expected) {
        return actual -> actual != null
                && expected.getId().equals(actual.getId())
                && expected.getTitle().equals(actual.getTitle())
                && expected.getArtist().equals(actual.getArtist())
                && expected.getGenre().equals(actual.getGenre())
                && expected.getReleaseYear().equals(actual.getReleaseYear());
    }
}
