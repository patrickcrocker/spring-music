package org.cloudfoundry.samples.music.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@WebMvcTest(AlbumController.class)
public class AlbumControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CrudRepository<Album, String> repository;

    @Test
    public void getAlbumsReturnsList() throws Exception {
        List<Album> albums = Arrays.asList(buildAlbum("album-1"), buildAlbum("album-2"));
        when(repository.findAll()).thenReturn(albums);

        mockMvc.perform(get("/albums"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("album-1"))
                .andExpect(jsonPath("$[1].id").value("album-2"));

        verify(repository).findAll();
    }

    @Test
    public void getAlbumByIdReturnsAlbum() throws Exception {
        Album album = buildAlbum("album-1");
        when(repository.findById("album-1")).thenReturn(Optional.of(album));

        mockMvc.perform(get("/albums/{id}", "album-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("album-1"));
    }

    @Test
    public void putAlbumSavesAlbum() throws Exception {
        Album album = buildAlbum("album-1");
        when(repository.save(any(Album.class))).thenReturn(album);

        mockMvc.perform(put("/albums")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(album)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("album-1"));

        verify(repository).save(any(Album.class));
    }

    @Test
    public void postAlbumCreatesAlbum() throws Exception {
        Album album = buildAlbum("album-1");
        when(repository.save(any(Album.class))).thenReturn(album);

        mockMvc.perform(post("/albums")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(album)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("album-1"));

        verify(repository).save(any(Album.class));
    }

    @Test
    public void deleteAlbumRemovesAlbum() throws Exception {
        mockMvc.perform(delete("/albums/{id}", "album-1"))
                .andExpect(status().isOk());

        verify(repository).deleteById("album-1");
    }

    private Album buildAlbum(String id) {
        Album album = new Album("Title-" + id, "Artist-" + id, "1999", "Genre-" + id);
        album.setId(id);
        album.setTrackCount(10);
        album.setAlbumId("catalog-" + id);
        return album;
    }
}
