package es.lector.input;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("ConsolePrompt")
class ConsolePromptTest {

    private ByteArrayOutputStream outBuffer;
    private ConsolePrompt prompt;

    private ConsolePrompt promptWith(String... inputs) {
        outBuffer = new ByteArrayOutputStream();
        BufferedReader reader = new BufferedReader(new StringReader(String.join("\n", inputs)));
        return new ConsolePrompt(reader, new PrintStream(outBuffer, true, StandardCharsets.UTF_8));
    }

    private String output() {
        return outBuffer.toString(StandardCharsets.UTF_8);
    }

    @Test
    @DisplayName("devuelve el valor cuando ya es valido")
    void returnsValidValue() {
        prompt = promptWith("juan");

        assertEquals("juan", prompt.askUntilValid("Nombre: ", "Error", s -> s.equals("juan")));
    }

    @Test
    @DisplayName("repite la peticion hasta obtener un valor valido")
    void retriesUntilValid() {
        prompt = promptWith("Malo", "Otra", "bien");

        String result = prompt.askUntilValid("Nombre: ", "Formato no admitido", s -> s.equals("bien"));

        assertEquals("bien", result);
        assertEquals(2, output().lines()
                .filter(line -> line.endsWith("Formato no admitido"))
                .count());
    }

    @Test
    @DisplayName("muestra el mensaje de peticion en cada intento")
    void showsPromptEveryTime() {
        prompt = promptWith("Malo", "bien");

        prompt.askUntilValid("Nombre: ", "Error", s -> s.equals("bien"));

        assertEquals(2, output().split("Nombre: ", -1).length - 1);
    }

    @Test
    @DisplayName("pasa a minusculas lo que introduce el usuario")
    void normalizesToLowerCase() {
        prompt = promptWith("  PEDRO  ");

        assertEquals("pedro", prompt.ask("Nombre: "));
    }

    @Test
    @DisplayName("acepta un nombre escrito en mayusculas")
    void acceptsUppercaseInput() {
        prompt = promptWith("PEDRO");

        String result = prompt.askUntilValid("Nombre: ", "Error", s -> s.equals("pedro"));

        assertEquals("pedro", result);
    }

    @Test
    @DisplayName("acepta un archivo escrito en mayusculas")
    void acceptsUppercaseFilename() {
        prompt = promptWith("NOTA.TXT");

        String result = prompt.askUntilValid("Archivo: ", "Error", s -> s.equals("nota.txt"));

        assertEquals("nota.txt", result);
    }

    @Test
    @DisplayName("normaliza sin depender del idioma del sistema")
    void normalizesIndependentlyOfLocale() {
        Locale previous = Locale.getDefault();
        try {
            // En turco, 'I' en minuscula es 'ı' (con punto), no 'i'
            Locale.setDefault(Locale.forLanguageTag("tr"));

            assertEquals("juan", ConsolePrompt.normalize("JUAN"));
        } finally {
            Locale.setDefault(previous);
        }
    }

    @Test
    @DisplayName("devuelve null al normalizar si no hay entrada")
    void normalizeKeepsNull() {
        assertNull(ConsolePrompt.normalize(null));
    }

    @Test
    @DisplayName("falla si la consola se agota sin un valor valido")
    void failsOnEndOfInput() {
        prompt = promptWith("Malo");

        assertThrows(IllegalStateException.class,
                () -> prompt.askUntilValid("Nombre: ", "Error", s -> s.equals("bien")));
    }
}
