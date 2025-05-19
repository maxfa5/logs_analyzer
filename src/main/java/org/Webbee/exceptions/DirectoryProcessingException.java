package org.Webbee.exceptions;

/**
 * Исключение, выбрасываемое при ошибках обработки директории.
 * <p>
 * Используется для индикации проблем, связанных с:
 * <ul>
 *   <li>В случае, если путь до директории не существует или не является директорией</li>
 * </ul>
 */
public class DirectoryProcessingException extends Exception {
  public DirectoryProcessingException(String message) {
    super(message);
  }
}