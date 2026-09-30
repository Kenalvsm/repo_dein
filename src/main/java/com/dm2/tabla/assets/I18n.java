package com.dm2.tabla.assets;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Clase de utilidad para gestionar la internacionalización (i18n) de la
 * aplicación.
 * <p>
 * Carga el {@link ResourceBundle} de mensajes adecuado según la locale del
 * sistema. Los textos se encuentran en
 * {@code com/dm2/tabla/i18n/messages*.properties}.
 * </p>
 * <p>
 * Uso: {@code I18n.get("button.add")}
 * </p>
 *
 * @author Kenneth
 * @version 1.0
 * @since 1.0
 */
public final class I18n {

    /** Ruta base de los archivos de mensajes. */
    private static final String BUNDLE_BASE = "com.dm2.tabla.i18n.messages";

    /** Locale efectiva usada por la aplicación. */
    private static final Locale LOCALE;

    /** Bundle de mensajes cargado. */
    private static final ResourceBundle BUNDLE;

    static {
        // Detecta la locale del sistema automáticamente
        LOCALE = Locale.getDefault();
        BUNDLE = ResourceBundle.getBundle(BUNDLE_BASE, LOCALE);
    }

    /** Constructor privado para evitar instanciación. */
    private I18n() { }

    /**
     * Devuelve el texto asociado a la clave indicada, en el idioma
     * detectado automáticamente.
     *
     * @param clave clave del mensaje (por ejemplo {@code "button.add"})
     * @return texto traducido o la propia clave si no existe
     */
    public static String get(String clave) {
        try {
            return BUNDLE.getString(clave);
        } catch (Exception e) {
            return "!" + clave + "!";
        }
    }

    /**
     * Devuelve la locale activa de la aplicación.
     *
     * @return locale en uso
     */
    public static Locale getLocale() {
        return LOCALE;
    }
}