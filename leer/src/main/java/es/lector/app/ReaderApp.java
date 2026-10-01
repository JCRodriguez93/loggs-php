package es.lector.app;

import es.lector.Messages;
import es.lector.input.ConsolePrompt;
import es.lector.input.NameValidator;
import es.lector.io.TextFileReader;
import es.lector.log.ActivityLogger;

import java.io.PrintStream;
import java.util.Optional;

/**
 * Orquesta el flujo de la aplicacion: pide un nombre de usuario, pide un
 * archivo, lo muestra y registra la actividad.
 *
 * <p>No contiene logica de validacion, de lectura de ficheros ni de
 * configuracion del log: solo coordina las tres piezas.</p>
 *
 * @author JCRodriguez93
 */
public class ReaderApp {

    private final ConsolePrompt prompt;
    private final TextFileReader fileReader;
    private final ActivityLogger activityLogger;
    private final PrintStream out;

    /**
     * @param prompt         consola de entrada y salida
     * @param fileReader     lector de archivos
     * @param activityLogger registro de actividad
     * @param out            flujo de salida donde se informa al usuario
     */
    public ReaderApp(ConsolePrompt prompt, TextFileReader fileReader,
                     ActivityLogger activityLogger, PrintStream out) {
        this.prompt = prompt;
        this.fileReader = fileReader;
        this.activityLogger = activityLogger;
        this.out = out;
    }

    /**
     * Ejecuta el flujo completo de lectura.
     *
     * @return el contenido mostrado, o vacio si el archivo no se pudo leer
     */
    public Optional<String> run() {
        String username = askUsername();
        String filename = askFilename();

        Optional<String> content = fileReader.read(filename);
        if (content.isEmpty()) {
            out.println(String.format(
                    Messages.FILE_NOT_FOUND, filename, fileReader.getBaseDir()));
            activityLogger.logFileSearched(username, filename);
            return Optional.empty();
        }

        out.println(String.format(Messages.FILE_CONTENT_HEADER, filename.toUpperCase()));
        out.println(content.get());
        activityLogger.logFileRead(username, filename);
        return content;
    }

    private String askUsername() {
        return prompt.askUntilValid(
                Messages.ASK_USERNAME,
                Messages.INVALID_USERNAME_FORMAT,
                NameValidator::isValidUsername);
    }

    private String askFilename() {
        return prompt.askUntilValid(
                String.format(Messages.ASK_FILENAME, fileReader.getBaseDir()),
                Messages.INVALID_FILENAME_FORMAT,
                NameValidator::isValidFilename);
    }
}
