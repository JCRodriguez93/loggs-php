package es.lector.input;

import java.util.regex.Pattern;

/**
 * Valida los datos que introduce el usuario por consola.
 *
 * <p>Reglas admitidas:</p>
 * <ul>
 *     <li>Usuario: minusculas, de 1 a 8 caracteres.</li>
 *     <li>Archivo: hasta 8 caracteres alfanumericos, punto y extension de
 *     3 letras. El archivo se busca en el directorio de trabajo.</li>
 * </ul>
 *
 * <p>La clase no interactua con la consola: solo comprueba cadenas ya
 * leidas, de modo que queda trivial de testear.</p>
 *
 * @author JCRodriguez93
 */
public final class NameValidator {

    private static final Pattern USERNAME_PATTERN = Pattern.compile("[a-z]{1,8}");

    /**
     * Nombre de archivo con una carpeta opcional delante.
     *
     * <p>Se aceptan tanto {@code /} como {@code \} como separadores, de modo
     * que la misma entrada vale en Windows y en Linux.</p>
     */
    private static final Pattern FILENAME_PATTERN =
            Pattern.compile("[a-z0-9]{1,8}([\\\\/][a-z0-9]{1,8}){0,4}\\.[a-z]{3}");

    private NameValidator() {
        throw new AssertionError("No se debe instanciar NameValidator");
    }

    /**
     * @param username candidato introducido por el usuario
     * @return {@code true} si cumple el formato de nombre admitido
     */
    public static boolean isValidUsername(String username) {
        return username != null && USERNAME_PATTERN.matcher(username).matches();
    }

    /**
     * @param filename candidato introducido por el usuario
     * @return {@code true} si cumple el formato de archivo admitido
     */
    public static boolean isValidFilename(String filename) {
        return filename != null && FILENAME_PATTERN.matcher(filename).matches();
    }
}
