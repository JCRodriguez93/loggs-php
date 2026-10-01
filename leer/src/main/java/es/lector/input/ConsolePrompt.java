package es.lector.input;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UncheckedIOException;
import java.util.Locale;
import java.util.function.Predicate;

/**
 * Pide datos por consola y no devuelve el control hasta que el usuario
 * introduce un valor valido.
 *
 * <p>El flujo de entrada ({@link BufferedReader}) y el de salida
 * ({@link PrintStream}) se inyectan, de forma que en las pruebas se pueden
 * sustituir por memoria.</p>
 *
 * <p>Todo lo que introduce el usuario se normaliza con {@link #normalize} para
 * que escribir en mayusculas no provoque un error de formato.</p>
 *
 * @author JCRodriguez93
 */
public class ConsolePrompt {

    private final BufferedReader reader;
    private final PrintStream out;

    /**
     * @param reader flujo de entrada de la consola
     * @param out    flujo de salida de la consola
     */
    public ConsolePrompt(BufferedReader reader, PrintStream out) {
        this.reader = reader;
        this.out = out;
    }

    /**
     * Repite la peticion hasta que el valor introducido sea valido.
     *
     * @param message texto mostrado al usuario
     * @param error   texto mostrado cuando el valor no es valido
     * @param rule    predicado que determina si el valor es valido
     * @return primer valor valido introducido por el usuario
     */
    public String askUntilValid(String message, String error, Predicate<String> rule) {
        while (true) {
            String value = ask(message);
            if (value == null) {
                throw new IllegalStateException(
                        "Fin de la entrada de consola antes de obtener un valor valido");
            }
            if (rule.test(value)) {
                return value;
            }
            out.println(error);
        }
    }

    /**
     * Lee una linea de la consola mostrando antes la peticion.
     *
     * @param message texto mostrado al usuario
     * @return linea introducida, sin el salto de linea final
     * @throws UncheckedIOException si la lectura de la consola falla
     */
    public String ask(String message) {
        out.print(message);
        out.flush();
        try {
            return normalize(reader.readLine());
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo leer de la consola", e);
        }
    }

    /**
     * Normaliza lo que introduce el usuario para que las mayusculas y
     * minculas no influyan en la validacion.
     *
     * <p>Se recortan los espacios sobrantes y se pasa todo a minusculas usando
     * {@link Locale#ROOT}, de modo que el resultado no dependa del idioma del
     * sistema.</p>
     *
     * @param value texto tal cual lo introduce el usuario
     * @return texto recortado y en minusculas, o {@code null} si no hay entrada
     */
    public static String normalize(String value) {
        if (value == null) {
            return null;
        }
        return value.trim().toLowerCase(Locale.ROOT);
    }
}
