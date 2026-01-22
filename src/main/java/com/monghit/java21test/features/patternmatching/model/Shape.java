package com.monghit.java21test.features.patternmatching.model;

/**
 * Sealed interface que representa una forma geométrica.
 *
 * Las sealed interfaces/classes permiten controlar qué clases pueden implementar/extender la interface/clase.
 * Esto es útil para pattern matching exhaustivo en switch statements.
 *
 * Combinado con pattern matching en switch (Java 21), permite escribir código más seguro y expresivo.
 */
public sealed interface Shape permits Circle, Rectangle, Triangle {

    /**
     * Calcula el área de la forma.
     */
    double area();

    /**
     * Calcula el perímetro de la forma.
     */
    double perimeter();

    /**
     * Obtiene el tipo de forma.
     */
    String type();
}
