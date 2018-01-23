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
package org.tools4j.metric.basic;

import org.tools4j.metric.api.Printable;
import org.tools4j.metric.api.Printer;

import java.util.Objects;

/**
 * Printable that reports a ratio calculated by dividing two {@link SumMetric} values.
 */
public class RatioPrintable implements Printable {

    private final SumMetric numerator;
    private final SumMetric denominator;
    private final Printer<? super RatioPrintable> printer;

    public RatioPrintable(final SumMetric numerator, final SumMetric denominator) {
        this("ratio", numerator, denominator);
    }

    public RatioPrintable(final String name, final SumMetric numerator, final SumMetric denominator) {
        this(numerator, denominator, (metric, output) -> output.append(name).append('=').append((float)metric.ratio()));
    }

    public RatioPrintable(final SumMetric numerator, final SumMetric denominator,
                          final Printer<? super RatioPrintable> printer) {
        this.numerator = Objects.requireNonNull(numerator);
        this.denominator = Objects.requireNonNull(denominator);
        this.printer = Objects.requireNonNull(printer);
    }

    public double numerator() {
        return numerator.sum();
    }

    public double denominator() {
        return denominator.sum();
    }

    public double ratio() {
        return numerator() / denominator();
    }

    @Override
    public void print(final StringBuilder output) {
        printer.print(this, output);
    }
}
