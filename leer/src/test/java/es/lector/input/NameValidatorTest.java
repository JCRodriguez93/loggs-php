package es.lector.input;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("NameValidator")
class NameValidatorTest {

    @ParameterizedTest
    @ValueSource(strings = {"a", "juan", "abcdefgh"})
    @DisplayName("acepta nombres en minusculas de 1 a 8 caracteres")
    void acceptsValidUsernames(String username) {
        assertTrue(NameValidator.isValidUsername(username));
    }

    @ParameterizedTest
    @ValueSource(strings = {"abcdefghi", "juan1", "juan perez"})
    @NullAndEmptySource
    @DisplayName("rechaza nombres fuera de formato")
    void rejectsInvalidUsernames(String username) {
        assertFalse(NameValidator.isValidUsername(username));
    }

    @ParameterizedTest
    @ValueSource(strings = {"Juan", "PEDRO", "juan perez"})
    @DisplayName("rechaza mayusculas si se valida sin normalizar antes")
    void rejectsUppercaseWhenNotNormalized(String username) {
        assertFalse(NameValidator.isValidUsername(username));
    }

    @ParameterizedTest
    @ValueSource(strings = {"a.txt", "readme.txt", "file123.txt", "a1.txt"})
    @DisplayName("acepta nombres de archivo con extension de 3 letras")
    void acceptsValidFilenames(String filename) {
        assertTrue(NameValidator.isValidFilename(filename));
    }

    @ParameterizedTest
    @ValueSource(strings = {"sub/nota.txt", "sub\\nota.txt", "a/b/c.txt", "a\\b\\c.txt"})
    @DisplayName("acepta una carpeta delante, con cualquiera de los dos separadores")
    void acceptsSubfolders(String filename) {
        assertTrue(NameValidator.isValidFilename(filename));
    }

    @ParameterizedTest
    @ValueSource(strings = {"a.txts", "a.md", "a.b.txt", "a-txt", ".txt", "archivosl.txt",
            "/absoluto.txt", "a//b.txt", "a/../b.txt"})
    @NullAndEmptySource
    @DisplayName("rechaza nombres de archivo fuera de formato")
    void rejectsInvalidFilenames(String filename) {
        assertFalse(NameValidator.isValidFilename(filename));
    }
}
