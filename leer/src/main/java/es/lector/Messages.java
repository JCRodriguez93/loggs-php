package es.lector;

/**
 * Textos que ve el usuario final. Centralizados aqui para poder localizarlos
 * o cambiarlos sin tocar la logica de la aplicacion.
 *
 * @author JCRodriguez93
 */
public final class Messages {

    public static final String ASK_USERNAME = "\nIndique su nombre: ";
    public static final String ASK_FILENAME = "\nIndique archivo a buscar (carpeta %s): ";
    public static final String INVALID_FORMAT = "Formato no admitido";
    public static final String INVALID_USERNAME_FORMAT =
            INVALID_FORMAT + ": use minusculas, de 1 a 8 caracteres.";
    public static final String INVALID_FILENAME_FORMAT =
            INVALID_FORMAT + ": use hasta 8 caracteres alfanumericos y extension de 3 letras.";
    public static final String FILE_NOT_FOUND =
            "No se ha encontrado el archivo '%s' en la carpeta %s";
    public static final String FILE_CONTENT_HEADER = "\n ***CONTENIDO DEL ARCHIVO '%s'***\n";

    private Messages() {
        throw new AssertionError("No se debe instanciar Messages");
    }
}
