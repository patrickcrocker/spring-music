package org.cloudfoundry.samples.music.repositories.redis;

import org.cloudfoundry.samples.music.domain.Album;
import org.cloudfoundry.samples.music.domain.RandomIdGenerator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RedisAlbumRepositoryTest {

    @Mock
    private RedisTemplate<String, Album> redisTemplate;

    @Mock
    private HashOperations<String, String, Album> hashOperations;

    private RedisAlbumRepository repository;

    @Before
    public void setUp() {
        when(redisTemplate.opsForHash()).thenReturn((HashOperations) hashOperations);
        repository = new RedisAlbumRepository(redisTemplate);
    }

    @Test
    public void saveAssignsGeneratedIdWhenMissing() throws Exception {
        injectIdGenerator(new RandomIdGenerator() {
            @Override
            public String generateId() {
                return "generated-id";
            }
        });

        Album album = new Album();
        repository.save(album);

        assertEquals("generated-id", album.getId());
        verify(hashOperations).put(RedisAlbumRepository.ALBUMS_KEY, "generated-id", album);
    }

    @Test
    public void saveUsesExistingIdWhenPresent() {
        Album album = new Album();
        album.setId("existing-id");

        repository.save(album);

        assertEquals("existing-id", album.getId());
        verify(hashOperations).put(RedisAlbumRepository.ALBUMS_KEY, "existing-id", album);
    }

    @Test
    public void findAllByIdDelegatesToMultiGet() {
        List<String> ids = Arrays.asList("1", "2");
        List<Album> expectedAlbums = Arrays.asList(new Album(), new Album());
        when(hashOperations.multiGet(RedisAlbumRepository.ALBUMS_KEY, ids)).thenReturn(expectedAlbums);

        Iterable<Album> result = repository.findAllById(ids);

        assertSame(expectedAlbums, result);
    }

    @Test
    public void countReturnsNumberOfStoredAlbums() {
        when(hashOperations.keys(RedisAlbumRepository.ALBUMS_KEY))
                .thenReturn(new HashSet<>(Arrays.asList("1", "2", "3")));

        assertEquals(3, repository.count());
    }

    @Test
    public void deleteAllRemovesEachAlbum() {
        when(hashOperations.keys(RedisAlbumRepository.ALBUMS_KEY))
                .thenReturn(new HashSet<>(Arrays.asList("1", "2")));

        repository.deleteAll();

        verify(hashOperations).delete(RedisAlbumRepository.ALBUMS_KEY, "1");
        verify(hashOperations).delete(RedisAlbumRepository.ALBUMS_KEY, "2");
    }

    private void injectIdGenerator(RandomIdGenerator idGenerator) throws Exception {
        Field field = RedisAlbumRepository.class.getDeclaredField("idGenerator");
        field.setAccessible(true);
        field.set(repository, idGenerator);
    }
}
