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
import org.tools4j.metric.api.Metric;
import org.tools4j.metric.api.Repository;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

public class DefaultDiscriminatingRepository<K,D> implements DiscriminatingRepository<K,D> {

    private final Repository<K, Metric> repository;
    private final Repository<K, Repository<D, Metric>> discriminatingRepository;

    public DefaultDiscriminatingRepository(final Repository<K, Metric> repository,
                                           final Repository<K, Repository<D, Metric>> discriminatingRepository) {
        this.repository = Objects.requireNonNull(repository);
        this.discriminatingRepository = Objects.requireNonNull(discriminatingRepository);
    }

    public static <K extends Enum<K>,D extends Enum<D>> DefaultDiscriminatingRepository<K,D> forEnums(
            final Class<K> keyClass, final Class<D> discriminatorClass, final Supplier<? extends Metric> metricFactory) {
        return forEnums(keyClass, k -> metricFactory.get(), discriminatorClass, d -> metricFactory.get());
    }

    public static <K extends Enum<K>,D extends Enum<D>> DefaultDiscriminatingRepository<K,D> forEnums(
            final Class<K> keyClass, final Function<? super K, ? extends Metric> metricFactory,
            final Class<D> discriminatorClass, final Function<? super D, ? extends Metric> discriminatedMetricFactory) {
        return new DefaultDiscriminatingRepository<>(ArrayRepository.forEnum(keyClass, metricFactory),
                ArrayRepository.forEnum(keyClass, k -> ArrayRepository.forEnum(
                        discriminatorClass, discriminatedMetricFactory)));
    }

    public static <K extends Enum<K>,D extends Enum<D>> DefaultDiscriminatingRepository<K,D> atomicForEnums(
            final Class<K> keyClass, final Class<D> discriminatorClass, final Supplier<? extends Metric> metricFactory) {
        return atomicForEnums(keyClass, k -> metricFactory.get(), discriminatorClass, d -> metricFactory.get());
    }

    public static <K extends Enum<K>,D extends Enum<D>> DefaultDiscriminatingRepository<K,D> atomicForEnums(
            final Class<K> keyClass, final Function<? super K, ? extends Metric> metricFactory,
            final Class<D> discriminatorClass, final Function<? super D, ? extends Metric> discriminatedMetricFactory) {
        return new DefaultDiscriminatingRepository<>(AtomicArrayRepository.forEnum(keyClass, metricFactory),
                AtomicArrayRepository.forEnum(keyClass, k -> AtomicArrayRepository.forEnum(
                        discriminatorClass, discriminatedMetricFactory)));
    }

    @Override
    public Metric getOrNull(final K key) {
        return repository.getOrNull(key);
    }

    @Override
    public Metric getOrCreate(final K key) {
        return repository.getOrCreate(key);
    }

    @Override
    public boolean discriminatingMetricsExist(final K key) {
        return discriminatingRepository.exists(key);
    }

    @Override
    public Repository<D, Metric> discriminatingMetricsOrNull(final K key) {
        return discriminatingRepository.getOrNull(key);
    }

    @Override
    public Repository<D, Metric> discriminatingMetrics(final K key) {
        return discriminatingRepository.getOrCreate(key);
    }
}
