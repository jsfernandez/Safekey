package utilidades;

//BUENAS PRACTICAS SEGÚN DOCUMENTACIÓN OFICIAL DE ANDROID
public class Utilidad {
    //Constantes de campos
    public static final String TABLA_CLAVE = "clave";
    public static final String CAMPO_IDENTIFICADOR = "identificador";
    public static final String CAMPO_TITULO = "titulo";
    public static final String CAMPO_SOFTWARE = "software";
    public static final String CAMPO_LLAVE = "llave";
    public static final String CAMPO_COMENTARIO = "comentario";

    public static final String CREAR_TABLA_CLAVE = "CREATE TABLE " + TABLA_CLAVE +
            " (" + CAMPO_IDENTIFICADOR + " INTEGER, " + CAMPO_TITULO + " TEXT, " + CAMPO_SOFTWARE +
            " TEXT, " + CAMPO_LLAVE + " TEXT, " + CAMPO_COMENTARIO + " TEXT)";
}
