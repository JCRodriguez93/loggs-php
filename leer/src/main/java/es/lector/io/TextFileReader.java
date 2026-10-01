package es.lector.io;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Optional;

/**
 * Lee el contenido de un archivo de texto plano.
 *
 * <p>No decide que hacer cuando el archivo no existe: devuelve un
 * {@link Optional} vacio y deja que la capa superior lo comunique al
 * usuario.</p>
 *
 * @author JCRodriguez93
 */
public class TextFileReader {

    /**
     * Propiedad de sistema para indicar la carpeta de ejemplos.
     */
    public static final String EXAMPLES_PROPERTY = "leer.ejemplos";

    private static final String[] CANDIDATE_FOLDERS = {"ejemplos", "../ejemplos"};

    private final Path baseDir;

    /**
     * Crea un lector que busca los archivos en el directorio indicado por la
     * propiedad de sistema {@code leer.ejemplos}. Si no esta definida, se
     * prueban las carpetas habituales del proyecto.
     */
    public TextFileReader() {
        this(resolveExamplesFolder());
    }

    /**
     * Localiza la carpeta de ejemplos probando rutas relativas habituales.
     *
     * @return carpeta de ejemplos, o {@code Path.of("")} si no se encuentra
     */
    public static Path resolveExamplesFolder() {
        String configured = System.getProperty(EXAMPLES_PROPERTY);
        if (configured != null && !configured.isBlank()) {
            return Path.of(normalizeSeparators(configured.trim()));
        }
        for (String candidate : CANDIDATE_FOLDERS) {
            Path path = Path.of(normalizeSeparators(candidate));
            if (Files.isDirectory(path)) {
                return path;
            }
        }
        return Path.of("");
    }

    /**
     * @param baseDir directorio donde se buscan los archivos
     */
    public TextFileReader(Path baseDir) {
        this.baseDir = baseDir;
    }

    /**
     * @return carpeta donde se buscan los archivos
     */
    public Path getBaseDir() {
        return baseDir;
    }

    /**
     * Lee el archivo indicado respetando sus saltos de linea.
     *
     * @param filename nombre del archivo a leer
     * @return el contenido del archivo, o vacio si no existe o no es legible
     */
    public Optional<String> read(String filename) {
        Optional<Path> path = toPath(filename);
        if (path.isEmpty() || !Files.isRegularFile(path.get())) {
            return Optional.empty();
        }
        try {
            return Optional.of(Files.readString(path.get(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    /**
     * Convierte los separadores de la entrada al separador del sistema.
     *
 * <p>En Windows {@code \/} y {@code \} funcionan por igual, pero en Linux la
 * barra invertida es un caracter normal y no un separador. Sustituirla por la
 * barra permite escribir la ruta igual en los dos sistemas.</p>
     *
     * @param filename ruta escrita por el usuario
     * @return ruta con los separadores del sistema
     */
    public static String normalizeSeparators(String filename) {
        return filename.replace('\\', '/');
    }

    private Optional<Path> toPath(String filename) {
        if (filename == null || filename.isBlank()) {
            return Optional.empty();
        }
        try {
            return Optional.of(baseDir.resolve(normalizeSeparators(filename)));
        } catch (InvalidPathException e) {
            return Optional.empty();
        }
    }
}
