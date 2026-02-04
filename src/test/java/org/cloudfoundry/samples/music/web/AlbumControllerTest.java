package org.cloudfoundry.samples.music.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cloudfoundry.samples.music.domain.Album;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
public class AlbumControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CrudRepository<Album, String> repository;

    @InjectMocks
    private AlbumController albumController;

    private ObjectMapper objectMapper;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(albumController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testGetAllAlbums() throws Exception {
        Album album1 = new Album("Album 1", "Artist 1", "2020", "Rock");
        Album album2 = new Album("Album 2", "Artist 2", "2021", "Pop");

        when(repository.findAll()).thenReturn(Arrays.asList(album1, album2));

        mockMvc.perform(get("/albums"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].title").value("Album 1"))
                .andExpect(jsonPath("$[0].artist").value("Artist 1"))
                .andExpect(jsonPath("$[1].title").value("Album 2"))
                .andExpect(jsonPath("$[1].artist").value("Artist 2"));

        verify(repository, times(1)).findAll();
    }

    @Test
    public void testGetAlbumById() throws Exception {
        Album album = new Album("Test Album", "Test Artist", "2022", "Jazz");
        album.setId("test-id-123");

        when(repository.findById("test-id-123")).thenReturn(Optional.of(album));

        mockMvc.perform(get("/albums/test-id-123"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("test-id-123"))
                .andExpect(jsonPath("$.title").value("Test Album"))
                .andExpect(jsonPath("$.artist").value("Test Artist"));

        verify(repository, times(1)).findById("test-id-123");
    }

    @Test
    public void testGetAlbumByIdNotFound() throws Exception {
        when(repository.findById("nonexistent-id")).thenReturn(Optional.empty());

        mockMvc.perform(get("/albums/nonexistent-id"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(repository, times(1)).findById("nonexistent-id");
    }

    @Test
    public void testAddAlbum() throws Exception {
        Album album = new Album("New Album", "New Artist", "2023", "Electronic");
        album.setId("new-id-456");

        when(repository.save(any(Album.class))).thenReturn(album);

        mockMvc.perform(put("/albums")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(album)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("new-id-456"))
                .andExpect(jsonPath("$.title").value("New Album"));

        verify(repository, times(1)).save(any(Album.class));
    }

    @Test
    public void testUpdateAlbum() throws Exception {
        Album album = new Album("Updated Album", "Updated Artist", "2024", "Classical");
        album.setId("update-id-789");

        when(repository.save(any(Album.class))).thenReturn(album);

        mockMvc.perform(post("/albums")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(album)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("update-id-789"))
                .andExpect(jsonPath("$.title").value("Updated Album"));

        verify(repository, times(1)).save(any(Album.class));
    }

    @Test
    public void testDeleteAlbum() throws Exception {
        doNothing().when(repository).deleteById("delete-id-999");

        mockMvc.perform(delete("/albums/delete-id-999"))
                .andExpect(status().isOk());

        verify(repository, times(1)).deleteById("delete-id-999");
    }
}
