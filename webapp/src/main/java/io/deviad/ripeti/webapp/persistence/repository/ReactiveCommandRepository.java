package io.deviad.ripeti.webapp.persistence.repository;

import org.reactivestreams.Publisher;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@NoRepositoryBean
public interface ReactiveCommandRepository<T, ID> extends Repository<T, ID> {

  <S extends T> Mono<S> save(S entity);

  /**
   * Saves all given entities.
   *
   * @param entities must not be {@literal null}.
   * @return {@link Flux} emitting the saved entities.
   * @throws IllegalArgumentException in case the given {@link Iterable entities} or one of its
   *     entities is {@literal null}.
   */
  <S extends T> Flux<S> saveAll(Iterable<S> entities);

  /**
   * Saves all given entities.
   *
   * @param entityStream must not be {@literal null}.
   * @return {@link Flux} emitting the saved entities.
   * @throws IllegalArgumentException in case the given {@link Publisher entityStream} is {@literal
   *     null}.
   */
  <S extends T> Flux<S> saveAll(Publisher<S> entityStream);

  /**
   * Deletes the entity with the given id.
   *
   * @param id must not be {@literal null}.
   * @return {@link Mono} signaling when operation has completed.
   * @throws IllegalArgumentException in case the given {@literal id} is {@literal null}.
   */
  Mono<Void> deleteById(ID id);

  /**
   * Deletes the entity with the given id supplied by a {@link Publisher}.
   *
   * @param id must not be {@literal null}.
   * @return {@link Mono} signaling when operation has completed.
   * @throws IllegalArgumentException in case the given {@link Publisher id} is {@literal null}.
   */
  Mono<Void> deleteById(Publisher<ID> id);

  /**
   * Deletes a given entity.
   *
   * @param entity must not be {@literal null}.
   * @return {@link Mono} signaling when operation has completed.
   * @throws IllegalArgumentException in case the given entity is {@literal null}.
   */
  Mono<Void> delete(T entity);

  /**
   * Deletes the given entities.
   *
   * @param entities must not be {@literal null}.
   * @return {@link Mono} signaling when operation has completed.
   * @throws IllegalArgumentException in case the given {@link Iterable entities} or one of its
   *     entities is {@literal null}.
   */
  Mono<Void> deleteAll(Iterable<? extends T> entities);

  /**
   * Deletes the given entities supplied by a {@link Publisher}.
   *
   * @param entityStream must not be {@literal null}.
   * @return {@link Mono} signaling when operation has completed.
   * @throws IllegalArgumentException in case the given {@link Publisher entityStream} is {@literal
   *     null}.
   */
  Mono<Void> deleteAll(Publisher<? extends T> entityStream);

  /**
   * Deletes all entities managed by the repository.
   *
   * @return {@link Mono} signaling when operation has completed.
   */
  Mono<Void> deleteAll();
}
