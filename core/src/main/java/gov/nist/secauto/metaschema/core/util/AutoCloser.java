/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.util;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Wraps a resource to make it {@link AutoCloseable}.
 *
 * @param <T>
 *          the resource type
 * @param <E>
 *          the exception type that may be thrown if an error occurs when
 *          closing the resource
 */
public class AutoCloser<T, E extends Exception> implements AutoCloseable {
  @NonNull
  private final T resource;
  @NonNull
  private final Closer<T, E> closeLambda;

  /**
   * Adapt the the provided {@code resource} to be {@link AutoCloseable}, using a
   * provided closer {@code lambda}.
   *
   * @param <T>
   *          the resource's type
   * @param <E>
   *          the exception type that can be thrown when closing
   * @param resource
   *          the object to adapt
   * @param lambda
   *          the lambda to use as a callback on close
   * @return the resource wrapped in an {@link AutoCloseable}
   */
  @NonNull
  public static <T, E extends Exception> AutoCloser<T, E> autoClose(
      @NonNull T resource,
      @NonNull Closer<T, E> lambda) {
    return new AutoCloser<>(resource, lambda);
  }

  /**
   * Adapt the provided {@code resource} to be {@link AutoCloseable}, using a
   * provided closer {@code lambda}.
   *
   * @param resource
   *          the object to adapt
   * @param lambda
   *          the lambda to use as a callback on close
   */
  public AutoCloser(@NonNull T resource, @NonNull Closer<T, E> lambda) {
    this.resource = resource;
    this.closeLambda = lambda;
  }

  /**
   * Get the wrapped resource.
   *
   * @return the resource object
   */
  @NonNull
  public T getResource() {
    return resource;
  }

  @Override
  @SuppressFBWarnings("THROWS_METHOD_THROWS_CLAUSE_BASIC_EXCEPTION")
  public void close() throws E {
    closeLambda.close(getResource());
  }

  @FunctionalInterface
  public interface Closer<T, E extends Exception> {
    /**
     * This method is called to auto-close the resource.
     *
     * @param object
     *          the resource to auto-close
     * @throws E
     *           the exception type that can be thrown when closing
     */
    @SuppressFBWarnings("THROWS_METHOD_THROWS_CLAUSE_BASIC_EXCEPTION")
    void close(@NonNull T object) throws E;
  }
}
