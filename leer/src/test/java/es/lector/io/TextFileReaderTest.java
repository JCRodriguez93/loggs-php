package es.lector.io;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("TextFileReader")
class TextFileReaderTest {

    private final TextFileReader reader = new TextFileReader();

    @Test
    @DisplayName("devuelve el contenido del archivo conservando saltos de linea")
    void readsContent(@TempDir Path dir) throws IOException {
        Path file = Files.writeString(dir.resolve("nota.txt"), "primera\nsegunda\n");

        Optional<String> content = reader.read(file.toString());

        assertEquals("primera\nsegunda\n", content.orElseThrow());
    }

    @Test
    @DisplayName("devuelve vacio si el archivo no existe")
    void returnsEmptyWhenMissing(@TempDir Path dir) {
        Optional<String> content = reader.read(dir.resolve("nada.txt").toString());

        assertTrue(content.isEmpty());
    }

    @Test
    @DisplayName("devuelve vacio si la ruta apunta a un directorio")
    void returnsEmptyWhenDirectory(@TempDir Path dir) {
        assertTrue(new TextFileReader(dir).read(".").isEmpty());
    }

    @Test
    @DisplayName("busca los archivos en el directorio base configurado")
    void readsFromBaseDirectory(@TempDir Path dir) throws IOException {
        Files.writeString(dir.resolve("nota.txt"), "contenido", StandardCharsets.UTF_8);

        Optional<String> content = new TextFileReader(dir).read("nota.txt");

        assertEquals("contenido", content.orElseThrow());
    }

    @Test
    @DisplayName("expone la carpeta donde busca los archivos")
    void exposesBaseDirectory(@TempDir Path dir) {
        assertEquals(dir, new TextFileReader(dir).getBaseDir());
    }

    @Test
    @DisplayName("usa la carpeta indicada por la propiedad de sistema")
    void usesConfiguredFolder(@TempDir Path dir) {
        String previous = System.getProperty(TextFileReader.EXAMPLES_PROPERTY);
        try {
            System.setProperty(TextFileReader.EXAMPLES_PROPERTY, dir.toString());
            assertEquals(dir, TextFileReader.resolveExamplesFolder());
        } finally {
            if (previous == null) {
                System.clearProperty(TextFileReader.EXAMPLES_PROPERTY);
            } else {
                System.setProperty(TextFileReader.EXAMPLES_PROPERTY, previous);
            }
        }
    }

    @Test
    @DisplayName("devuelve vacio si el nombre es nulo o vacio")
    void returnsEmptyWhenBlank() {
        assertTrue(reader.read(null).isEmpty());
        assertTrue(reader.read("").isEmpty());
        assertTrue(reader.read("   ").isEmpty());
    }

    @Test
    @DisplayName("encuentra el archivo con barra normal")
    void readsWithForwardSlash(@TempDir Path dir) throws IOException {
        Files.createDirectory(dir.resolve("sub"));
        Files.writeString(dir.resolve("sub").resolve("nota.txt"), "contenido");

        assertEquals("contenido", new TextFileReader(dir).read("sub/nota.txt").orElseThrow());
    }

    @Test
    @DisplayName("encuentra el archivo con barra invertida, para Windows")
    void readsWithBackSlash(@TempDir Path dir) throws IOException {
        Files.createDirectory(dir.resolve("sub"));
        Files.writeString(dir.resolve("sub").resolve("nota.txt"), "contenido");

        assertEquals("contenido", new TextFileReader(dir).read("sub\\nota.txt").orElseThrow());
    }

    @Test
    @DisplayName("sustituye la barra invertida por la del sistema")
    void normalizesSeparators() {
        assertEquals("sub/nota.txt", TextFileReader.normalizeSeparators("sub\\nota.txt"));
        assertEquals("sub/nota.txt", TextFileReader.normalizeSeparators("sub/nota.txt"));
        assertEquals("a/b/c.txt", TextFileReader.normalizeSeparators("a\\b\\c.txt"));
    }

    @Test
    @DisplayName("decodifica el contenido como UTF-8")
    void readsUtf8(@TempDir Path dir) throws IOException {
        Path file = Files.writeString(dir.resolve("nota.txt"),
                "acentos: áéíóú ñ", StandardCharsets.UTF_8);

        assertEquals("acentos: áéíóú ñ", reader.read(file.toString()).orElseThrow());
    }
}
