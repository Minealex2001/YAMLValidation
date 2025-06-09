package com.validador.ui.language;

import java.util.Locale;
import java.util.ResourceBundle;

public class TextGestor {
    private ResourceBundle bundle;
    private String idioma;

    public TextGestor(String idioma) {
        setIdioma(idioma);
    }

    public void setIdioma(String idioma) {
        this.idioma = idioma;
        Locale locale;
        switch (idioma) {
            case "en":
                locale = new Locale("en");
                break;
            case "ca":
                locale = new Locale("ca");
                break;
            default:
                locale = new Locale("es");
        }
        bundle = ResourceBundle.getBundle("messages", locale);
    }

    public String get(String clave) {
        try {
            return bundle.getString(clave);
        } catch (Exception e) {
            return "!" + clave + "!";
        }
    }

    public String getIdioma() {
        return idioma;
    }
}

