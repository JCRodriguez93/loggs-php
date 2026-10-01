package es.lector;

import es.lector.app.ReaderApp;
import es.lector.input.ConsolePrompt;
import es.lector.io.TextFileReader;
import es.lector.log.ActivityLogger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

/**
 * Punto de entrada de la aplicacion. Solo se encarga de ensamblar las
 * dependencias y delegar en {@link ReaderApp}.
 *
 * @author JCRodriguez93
 */
public final class Main {

    private Main() {
        throw new AssertionError("No se debe instanciar Main");
    }

    /**
     * @param args argumentos de linea de comandos (no se utilizan)
     */
    public static void main(String[] args) {
        PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        BufferedReader console = new BufferedReader(
                new InputStreamReader(System.in, StandardCharsets.UTF_8));

        ReaderApp app = new ReaderApp(
                new ConsolePrompt(console, out),
                new TextFileReader(),
                ActivityLogger.createDefault(),
                out);

        app.run();
    }
}
