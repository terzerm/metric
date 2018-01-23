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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

final class Enums {

    private static final Method ENUM_CONSTANT_DIRECTORY_METHOD = enumConstantDirectoryMethod();

    static <E extends Enum<E>> int enumConstantCount(final Class<E> enumClass) {
        final Map<?,?> constantMap = enumConstantDirectory(enumClass);
        return constantMap != null ? constantMap.size() : enumClass.getEnumConstants().length;
    }

    private static <E extends Enum<E>> Map<?, ?> enumConstantDirectory(final Class<E> enumClass) {
        try {
            return (Map<?, ?>) ENUM_CONSTANT_DIRECTORY_METHOD.invoke(enumClass);
        } catch (final IllegalAccessException | InvocationTargetException e) {
            return null;
        }
    }

    private static Method enumConstantDirectoryMethod() {
        try {
            final Method method = Class.class.getDeclaredMethod("enumConstantDirectory");
            method.setAccessible(true);
            return method;
        } catch (final NoSuchMethodException e) {
            return null;
        }
    }
}
