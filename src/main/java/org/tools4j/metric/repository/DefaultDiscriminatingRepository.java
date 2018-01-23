/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2018 tools4j.org (Marco Terzer)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.tools4j.metric.repository;

import org.tools4j.metric.api.DiscriminatingRepository;
import org.tools4j.metric.api.Repository;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

public class DefaultDiscriminatingRepository<K,D,V> implements DiscriminatingRepository<K,D,V> {

    private final Repository<K,V> repository;
    private final Repository<K,Repository<D,V>> discriminatingRepository;

    public DefaultDiscriminatingRepository(final Repository<K,V> repository,
                                           final Repository<K,Repository<D,V>> discriminatingRepository) {
        this.repository = Objects.requireNonNull(repository);
        this.discriminatingRepository = Objects.requireNonNull(discriminatingRepository);
    }

    public static <K extends Enum<K>,D extends Enum<D>,V> DefaultDiscriminatingRepository<K,D,V> forEnums(
            final Class<K> keyClass, final Class<D> discriminatorClass, final Supplier<? extends V> valueFactory) {
        return forEnums(keyClass, k -> valueFactory.get(), discriminatorClass, d -> valueFactory.get());
    }

    public static <K extends Enum<K>,D extends Enum<D>,V> DefaultDiscriminatingRepository<K,D,V> forEnums(
            final Class<K> keyClass, final Function<? super K, ? extends V> valueFactory,
            final Class<D> discriminatorClass, final Function<? super D, ? extends V> discriminatedValueFactory) {
        return new DefaultDiscriminatingRepository<>(ArrayRepository.forEnum(keyClass, valueFactory),
                ArrayRepository.forEnum(keyClass, k -> ArrayRepository.forEnum(
                        discriminatorClass, discriminatedValueFactory)));
    }

    public static <K extends Enum<K>,D extends Enum<D>,V> DefaultDiscriminatingRepository<K,D,V> atomicForEnums(
            final Class<K> keyClass, final Class<D> discriminatorClass, final Supplier<? extends V> valueFactory) {
        return atomicForEnums(keyClass, k -> valueFactory.get(), discriminatorClass, d -> valueFactory.get());
    }

    public static <K extends Enum<K>,D extends Enum<D>,V> DefaultDiscriminatingRepository<K,D,V> atomicForEnums(
            final Class<K> keyClass, final Function<? super K, ? extends V> valueFactory,
            final Class<D> discriminatorClass, final Function<? super D, ? extends V> discriminatedValueFactory) {
        return new DefaultDiscriminatingRepository<>(AtomicArrayRepository.forEnum(keyClass, valueFactory),
                AtomicArrayRepository.forEnum(keyClass, k -> AtomicArrayRepository.forEnum(
                        discriminatorClass, discriminatedValueFactory)));
    }

    @Override
    public boolean exists(final K key) {
        return repository.exists(key);
    }

    @Override
    public V getOrNull(final K key) {
        return repository.getOrNull(key);
    }

    @Override
    public V getOrCreate(final K key) {
        return repository.getOrCreate(key);
    }

    @Override
    public V getOrNull(final K key, final D discriminator) {
        final Repository<D, V> repoForKey = discriminatingRepository.getOrNull(key);
        return repoForKey != null ? repoForKey.getOrNull(discriminator) : null;
    }

    @Override
    public V getOrCreate(final K key, final D discriminator) {
        final Repository<D, V> repoForKey = discriminatingRepository.getOrCreate(key);
        return repoForKey.getOrCreate(discriminator);
    }
}
