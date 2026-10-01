# leer

Programa de consola que pide un nombre de usuario y el nombre de un archivo de
texto, muestra el contenido del archivo en pantalla y registra la actividad en
un archivo de log.

**Autor:** JCRodriguez93
**Licencia:** MIT (ver [LICENSE](LICENSE))

## Que hace

El programa recorre cuatro pasos:

1. Pide un nombre de usuario y comprueba que cumple el formato admitido.
2. Pide el nombre de un archivo y comprueba que cumple el formato admitido.
3. Busca el archivo dentro de la carpeta `ejemplos` y muestra su contenido.
4. Anade a `lectura.log` si el usuario ha leido el archivo o solo lo ha buscado
   sin exito.

Si el formato no es valido, el programa lo explica y vuelve a pedir el dato.
La entrada se normaliza a minusculas, asi que escribir en mayusculas no
provoca ningun error.

### Requisitos

- JDK 17 o superior
- Maven 3.8 o superior

### Ejecutar

```powershell
cd leer
mvn clean test
mvn exec:java
```

Ejemplo de uso:

```
Indique su nombre: pedro
Indique archivo a buscar (carpeta ejemplos): nota.txt
 ***CONTENIDO DEL ARCHIVO 'NOTA.TXT'***

Hola, esto es un archivo de ejemplo.
Segunda linea del ejemplo.
```

Los archivos se buscan siempre dentro de la carpeta `ejemplos`, situada en la
raiz del proyecto. Para usar otra carpeta:

```powershell
mvn exec:java "-Dleer.ejemplos=C:\ruta\con\mis\archivos"
```

## Formato admitido

| Campo | Regla | Ejemplos validos |
|-------|-------|-----------------|
| Usuario | Minusculas, de 1 a 8 caracteres | `pedro`, `juan` |
| Archivo | Hasta 4 carpetas de 8 caracteres, luego nombre y extension de 3 letras | `nota.txt`, `a1.txt`, `notas/uno.txt` |

La entrada se convierte a minusculas antes de validarse, de modo que `PEDRO`,
`Pedro` y `pedro` son equivalentes. Los espacios sobrantes se descartan.

## Windows y Linux

El programa funciona igual en los dos sistemas. No hay ni una sola ruta escrita
a mano en el codigo: todo se construye con `java.nio.file.Path`, que ya sabe
como funciona cada sistema.

Sobre los separadores de ruta:

- Se puede escribir la carpeta con `/` o con `\`. `notas/uno.txt` y
  `notas\uno.txt` funcionan en Windows y en Linux por igual.
- En Linux la barra invertida es un caracter normal, no un separador. Para que
  ambas notaciones sirvan, el programa las convierte antes de resolver la ruta.
  Ese trabajo lo hace `TextFileReader.normalizeSeparators`.
- La propiedad `leer.ejemplos` acepta las dos notaciones, y el resultado se
  muestra siempre con el separador del sistema en el que se ejecuta.

Para ejecutarlo en Linux:

```bash
cd leer
mvn clean test
mvn exec:java
```

La unica diferencia es la ruta del log y de los ejemplos, que se resuelven
respecto al directorio desde el que se lanza el programa.

## Estructura del proyecto

```
ejemplos/                          archivos de prueba
leer/pom.xml                      configuracion de Maven
leer/src/main/java/es/lector/
  Main.java                       punto de entrada: solo ensambla dependencias
  Messages.java                   textos en espanol que ve el usuario
  app/ReaderApp.java              orquesta el flujo de la aplicacion
  input/NameValidator.java        reglas de formato, sin estado
  input/ConsolePrompt.java        peticiones por consola con reintentos
  io/TextFileReader.java          lectura de archivos de texto
  log/ActivityLogger.java         registro en lectura.log
leer/src/test/java/es/lector/     tests JUnit 5
```

Cada clase tiene una sola responsabilidad:

- `NameValidator` no toca la consola, solo comprueba cadenas.
- `ConsolePrompt` no sabe nada de archivos, solo pide y repite.
- `TextFileReader` no decide que responder al usuario, devuelve el contenido o
  nada.
- `ActivityLogger` no valida ni lee, solo registra.
- `ReaderApp` coordina a las anteriores sin conter reglas propias.

Esa separacion permite probar cada pieza por separado sin levantar la consola.

Los nombres de clases, metodos y variables estan en ingles. Los mensajes del
usuario, en espanol.

## Tests

```powershell
mvn clean test
```

54 tests con JUnit 5 cubren el validador, la normalizacion de la entrada, la
consola, el lector de archivos, los separadores de ruta y el flujo completo de
la aplicacion.

## Registro

La actividad se anade a `lectura.log`, en el directorio desde el que se ejecuta
el programa:

| Situacion | Nivel | Mensaje |
|-----------|-------|---------|
| Archivo leido | `INFO` | Usuario: pedro ha leido el fichero: nota.txt |
| Archivo no encontrado | `WARNING` | Usuario: pedro ha buscado el fichero: nada.txt |

El log se escribe solo en el archivo, no en la consola. Si el archivo de log no
se puede crear, el programa avisa por pantalla y sigue funcionando.

## Sobre el registro de actividad

El proyecto se apoya en `java.util.logging`, que forma parte de la biblioteca
estandar de Java y no necesita dependencias externas. La eleccion es
deliberada: el objetivo es entender como se crea y se configura un archivo de
log.

Por eso el registro se separa en su propia clase, `ActivityLogger`, que crea el
manejador de archivo, aplica el formato y escribe cada mensaje. Cambiar de
biblioteca de log en el futuro solo exigiria reemplazar esa clase, sin tocar el
resto del programa.

## Mejoras futuras

### Lectura concurrente de varios archivos

Hoy el programa lee un unico archivo por ejecucion. La mejora mas util seria
leer varios archivos en paralelo, de modo que el tiempo total se acerque al del
archivo mas lento y no a la suma de todos:

```java
ExecutorService pool = Executors.newFixedThreadPool(4);
try {
    List<Future<String>> resultados = pool.invokeAll(
            archivos.stream().map(this::leerAsync).toList());
    for (Future<String> resultado : resultados) {
        mostrar(resultado.get());
    }
} finally {
    pool.shutdown();
}
```

Los puntos donde encajaria:

- `TextFileReader` podria exponer un metodo que reciba una lista de nombres y
  devuelva un `Map<String, Optional<String>>` con los resultados.
- `ReaderApp` recorreria esa lista en lugar de un unico archivo.
- `ActivityLogger` ya es seguro entre hilos, porque `java.util.logging` sincroniza
  la escritura de cada registro.

Al ser `TextFileReader` una clase sin estado, se puede compartir la misma
instancia entre todos los hilos sin_block de sincronizacion adicional.

### Otras mejoras

- Leer archivos muy grandes por partes en lugar de cargarlos enteros en memoria.
- Medir y mostrar el tiempo que tarda cada lectura.
- Pedir varios archivos en una sola ejecucion en lugar de terminar tras el
  primero.
- Sustituir `java.util.logging` por SLF4J con Logback, mas habitual fuera de la
  biblioteca estandar.
- Añadir una configuracion por archivo de propiedades en lugar de pasar
  `-Dleer.ejemplos` por linea de comandos.