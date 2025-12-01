package com.academy.academymanager.repository;

import com.academy.academymanager.domain.entity.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for UsuarioRepository.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@TestPropertySource(locations = "classpath:application-test.properties")
class UsuarioRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private UsuarioRepository usuarioRepository;
    private Usuario testUsuario;
    @BeforeEach
    void setUp() {
        testUsuario = Usuario.builder()
                .email("test@example.com")
                .passwordHash("hashedPassword")
                .rol(Usuario.Rol.ALUMNO)
                .activo(true)
                .fechaCreacion(LocalDateTime.now())
                .build();
        entityManager.persistAndFlush(testUsuario);
    }
    @Test
    void findByEmailShouldReturnUsuarioWhenExists() {
        Optional<Usuario> found = usuarioRepository.findByEmail("test@example.com");
        assertTrue(found.isPresent());
        assertEquals("test@example.com", found.get().getEmail());
    }
    @Test
    void findByEmailShouldReturnEmptyWhenNotExists() {
        Optional<Usuario> found = usuarioRepository.findByEmail("nonexistent@example.com");
        assertFalse(found.isPresent());
    }
    @Test
    void existsByEmailShouldReturnTrueWhenExists() {
        boolean exists = usuarioRepository.existsByEmail("test@example.com");
        assertTrue(exists);
    }
    @Test
    void existsByEmailShouldReturnFalseWhenNotExists() {
        boolean exists = usuarioRepository.existsByEmail("nonexistent@example.com");
        assertFalse(exists);
    }
    @Test
    void saveShouldPersistUsuario() {
        Usuario newUsuario = Usuario.builder()
                .email("new@example.com")
                .passwordHash("hash")
                .rol(Usuario.Rol.PROFESOR)
                .activo(true)
                .fechaCreacion(LocalDateTime.now())
                .build();
        Usuario saved = usuarioRepository.save(newUsuario);
        assertNotNull(saved.getIdUsuario());
        assertTrue(usuarioRepository.existsById(saved.getIdUsuario()));
    }
}

