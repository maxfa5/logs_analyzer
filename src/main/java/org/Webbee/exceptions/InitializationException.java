package org.Webbee.exceptions;

/**
 * Исключение, выбрасываемое при ошибках инициализации LogWriter.
 * <p>
 * Применяется в случаях:
 * <ul>
 *   <li>В случае, если путь пустой/null или если не удалось создать файл</li>
 * </ul>
 */
public class InitializationException extends Exception {
    public InitializationException(String message) {
        super(message);
    }
}