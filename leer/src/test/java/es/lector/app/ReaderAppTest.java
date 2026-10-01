package es.lector.app;

import es.lector.input.ConsolePrompt;
import es.lector.io.TextFileReader;
import es.lector.log.ActivityLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("ReaderApp")
class ReaderAppTest {

    private static final String TEST_LOGGER = "es.lector.test";

    private ByteArrayOutputStream outBuffer;
    private Optional<String> content;

    @BeforeEach
    @DisplayName("desactiva el logger para no ensuciar la salida de las pruebas")
    void silenceLogger() {
        Logger logger = Logger.getLogger(TEST_LOGGER);
        logger.setLevel(Level.OFF);
        logger.setUseParentHandlers(false);
        for (Handler handler : logger.getHandlers()) {
            logger.removeHandler(handler);
        }
    }

    private void run(Path baseDir, String... inputs) {
        outBuffer = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outBuffer, true, StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(new StringReader(String.join("\n", inputs)));

        ReaderApp app = new ReaderApp(
                new ConsolePrompt(reader, out),
                new TextFileReader(baseDir),
                new ActivityLogger(TEST_LOGGER),
                out);

        content = app.run();
    }

    private String output() {
        return outBuffer.toString(StandardCharsets.UTF_8);
    }

    private void writeExample(Path dir) throws IOException {
        Files.writeString(dir.resolve("nota.txt"), "contenido", StandardCharsets.UTF_8);
    }

    @Test
    @DisplayName("muestra el contenido del archivo y lo devuelve")
    void showsFileContent(@TempDir Path dir) throws IOException {
        writeExample(dir);

        run(dir, "juan", "nota.txt");

        assertEquals("contenido", content.orElseThrow());
        assertTrue(output().contains("contenido"));
        assertTrue(output().contains("NOTA.TXT"));
    }

    @Test
    @DisplayName("repite las preguntas hasta obtener valores validos")
    void retriesInvalidInput(@TempDir Path dir) throws IOException {
        writeExample(dir);

        run(dir, "JuanPerez", "mayusculas", "juan", "nota.txt");

        assertEquals("contenido", content.orElseThrow());
        assertTrue(output().contains("Formato no admitido"));
    }

    @Test
    @DisplayName("avisa y devuelve vacio cuando el archivo no existe")
    void reportsMissingFile(@TempDir Path dir) {
        run(dir, "juan", "nada.txt");

        assertTrue(content.isEmpty());
        assertTrue(output().contains("No se ha encontrado el archivo 'nada.txt'"));
    }

    @Test
    @DisplayName("no escribe en disco durante las pruebas")
    void doesNotRequireLogFile(@TempDir Path dir) throws IOException {
        writeExample(dir);

        run(dir, "juan", "nota.txt");

        assertTrue(Files.notExists(Path.of("lectura.log")));
    }
}