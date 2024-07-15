package com.cursoalura.literalura.model;

public enum Language {
    ES("es"),
    EN("en"),
    FR("fr"),
    PT("pt"),
    UNKNOWN("");

    private String language;

    Language(String language) {
        this.language = language;
    }

    public static Language fromString(String text) {
        for (Language lenguaje : Language.values()) {
            if (lenguaje.language.equalsIgnoreCase(text)) {
                return lenguaje;
            }
        }
        throw new IllegalArgumentException("Lenguaje no encontrado: " + text);
    }

    public String getlanguage() {
        return this.language;
    }
}
