package com.noctiva.noxway.shared.application.result;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Functional result container separating expected domain/application failures from unexpected exceptions.
 *
 * @param <T> success value type
 * @param <E> failure error type
 */
public sealed interface Result<T, E> permits Result.Success, Result.Failure {

  record Success<T, E>(T value) implements Result<T, E> {}

  record Failure<T, E>(E error) implements Result<T, E> {
    public Failure {
      Objects.requireNonNull(error, "Failure error cannot be null");
    }
  }

  static <T, E> Result<T, E> success(T value) {
    return new Success<>(value);
  }

  static <T, E> Result<T, E> failure(E error) {
    return new Failure<>(error);
  }

  default boolean isSuccess() {
    return this instanceof Success<T, E>;
  }

  default boolean isFailure() {
    return this instanceof Failure<T, E>;
  }

  default T value() {
    return switch (this) {
      case Success<T, E>(var value) -> value;
      case Failure<T, E>(var error) ->
          throw new IllegalStateException("Result does not contain a value: " + error);
    };
  }

  default E error() {
    return switch (this) {
      case Success<T, E> ignored ->
          throw new IllegalStateException("Result does not contain an error");
      case Failure<T, E>(var error) -> error;
    };
  }

  default <U> Result<U, E> map(Function<? super T, ? extends U> mapper) {
    return switch (this) {
      case Success<T, E>(var value) -> Result.success(mapper.apply(value));
      case Failure<T, E>(var error) -> Result.failure(error);
    };
  }

  default <U> Result<U, E> flatMap(Function<? super T, Result<U, E>> mapper) {
    return switch (this) {
      case Success<T, E>(var value) -> mapper.apply(value);
      case Failure<T, E>(var error) -> Result.failure(error);
    };
  }

  default <R> R fold(Function<? super T, ? extends R> onSuccess, Function<? super E, ? extends R> onFailure) {
    return switch (this) {
      case Success<T, E>(var value) -> onSuccess.apply(value);
      case Failure<T, E>(var error) -> onFailure.apply(error);
    };
  }

  default <X extends RuntimeException> T orElseThrow(Function<? super E, ? extends X> mapper) {
    return switch (this) {
      case Success<T, E>(var value) -> value;
      case Failure<T, E>(var error) -> throw mapper.apply(error);
    };
  }

  default T orElseGet(Supplier<? extends T> fallback) {
    return switch (this) {
      case Success<T, E>(var value) -> value;
      case Failure<T, E> ignored -> fallback.get();
    };
  }
}
