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

import org.tools4j.metric.api.Repository;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.Function;
import java.util.function.ToIntFunction;

public class AtomicArrayRepository<K,V> implements Repository<K,V> {

    private final ToIntFunction<? super K> ordinalFunction;
    private final Function<? super K, ? extends V> valueFactory;
    private final AtomicReferenceArray<V> values;

    public AtomicArrayRepository(final int length, final ToIntFunction<? super K> ordinalFunction,
                                 final Function<? super K, ? extends V> valueFactory) {
        this.ordinalFunction = Objects.requireNonNull(ordinalFunction);
        this.valueFactory = Objects.requireNonNull(valueFactory);
        this.values = new AtomicReferenceArray<V>(length);
    }

    public static <E extends Enum<E>,V> AtomicArrayRepository<E,V> forEnum(final Class<E> enumClass,
                                                                           final Function<? super E, ? extends V> valueFactory) {
        return new AtomicArrayRepository<>(Enums.enumConstantCount(enumClass), Enum::ordinal, valueFactory);
    }

    @Override
    public V getOrNull(final K key) {
        final int ordinal = ordinalFunction.applyAsInt(key);
        return values.get(ordinal);
    }

    @Override
    public V getOrCreate(final K key) {
        final int ordinal = ordinalFunction.applyAsInt(key);
        V value = values.get(ordinal);
        if (value == null) {
            value = valueFactory.apply(key);
            if (!values.compareAndSet(ordinal, null, value)) {
                value = values.get(ordinal);
            }
        }
        return value;
    }

}
