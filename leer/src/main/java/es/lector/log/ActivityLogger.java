package es.lector.log;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Registra en un archivo de log la actividad del usuario: que ficheros ha
 * leido y que ficheros ha buscado sin exito.
 *
 * <p>La creacion del archivo de log puede fallar (permisos, carpeta no
 * escribible). En ese caso el programa sigue funcionando: solo se pierde el
 * registro en disco.</p>
 *
 * @author JCRodriguez93
 */
public class ActivityLogger {

    private static final String LOGGER_NAME = "es.lector";
    private static final String LOG_FILE = "lectura.log";

    private final Logger logger;

    /**
     * @param loggerName nombre del logger a utilizar
     */
    public ActivityLogger(String loggerName) {
        this.logger = Logger.getLogger(loggerName);
    }

    /**
     * Crea el logger por defecto de la aplicacion. Si no se puede abrir el
     * archivo de log, el logger sigue funcionando en memoria.
     *
     * @return logger configurado
     */
    public static ActivityLogger createDefault() {
        ActivityLogger activityLogger = new ActivityLogger(LOGGER_NAME);
        activityLogger.logger.setLevel(Level.ALL);
        activityLogger.logger.setUseParentHandlers(false);
        FileHandler handler = tryCreateHandler(LOG_FILE);
        if (handler != null) {
            handler.setFormatter(new SimpleFormatter());
            activityLogger.logger.addHandler(handler);
        }
        return activityLogger;
    }

    /**
     * Registra que el usuario ha leido un archivo con exito.
     *
     * @param username nombre del usuario
     * @param filename nombre del archivo leido
     */
    public void logFileRead(String username, String filename) {
        logger.log(Level.INFO, "Usuario: {0} ha leido el fichero: {1}",
                new Object[]{username, filename});
    }

    /**
     * Registra que el usuario ha buscado un archivo que no ha podido leer.
     *
     * @param username nombre del usuario
     * @param filename nombre del archivo buscado
     */
    public void logFileSearched(String username, String filename) {
        logger.log(Level.WARNING, "Usuario: {0} ha buscado el fichero: {1}",
                new Object[]{username, filename});
    }

    /**
     * Intenta crear un manejador de archivo, devolviendo {@code null} si no
     * es posible.
     *
     * @param filename nombre del archivo de log
     * @return manejador o {@code null} si no se pudo crear
     */
    public static FileHandler tryCreateHandler(String filename) {
        try {
            return new FileHandler(filename, true);
        } catch (IOException | SecurityException e) {
            System.err.println("Aviso: no se pudo abrir el archivo de log " + filename);
            return null;
        }
    }
}
