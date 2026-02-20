package org.cloudfoundry.samples.music.web;

import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(AlbumController.class)
public class AlbumControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CrudRepository<Album, String> repository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void getAlbumsReturnsRepositoryResults() throws Exception {
        Album album = new Album("Test Title", "Test Artist", "2024", "Rock");
        album.setId("123");
        when(repository.findAll()).thenReturn(Collections.singletonList(album));

        mockMvc.perform(get("/albums"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("123"))
                .andExpect(jsonPath("$[0].title").value("Test Title"))
                .andExpect(jsonPath("$[0].artist").value("Test Artist"));
    }

    @Test
    public void putAlbumSavesAndReturnsAlbum() throws Exception {
        Album album = new Album("Updated Title", "Updated Artist", "2025", "Jazz");
        album.setId("abc");
        when(repository.save(any(Album.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(put("/albums")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(album)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("abc"))
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.artist").value("Updated Artist"));

        verify(repository).save(any(Album.class));
    }

    @Test
    public void getAlbumByIdReturnsAlbumWhenPresent() throws Exception {
        Album album = new Album("Found Title", "Found Artist", "2023", "Pop");
        album.setId("found-id");
        when(repository.findById("found-id")).thenReturn(Optional.of(album));

        mockMvc.perform(get("/albums/found-id"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("found-id"))
                .andExpect(jsonPath("$.title").value("Found Title"));
    }

    @Test
    public void deleteAlbumByIdInvokesRepository() throws Exception {
        mockMvc.perform(delete("/albums/delete-me"))
                .andExpect(status().isOk());

        verify(repository).deleteById("delete-me");
    }
}
