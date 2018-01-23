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

import org.tools4j.metric.api.Metric;
import org.tools4j.metric.api.MetricRecorder;
import org.tools4j.metric.api.Printer;

import java.util.Objects;

/**
 * Metric that tracks the avarage (or mean) of a sampled value using a numerically stable algorithm.
 * <p>
 * The implementation is based on Welford’s Algorithm given in Knuth Vol 2, p 232.
 */
public class AvgMetric implements Metric, MetricRecorder {

    private long count = 0;
    private double avg = Double.NaN;
    private final MetricRecorder recorder = this::record;
    private final Printer<? super AvgMetric> printer;

    public AvgMetric() {
        this("avg");
    }

    public AvgMetric(final String name) {
        this((metric, output) -> output.append(name).append('=').append(metric.avg()));
    }

    public AvgMetric(final Printer<? super AvgMetric> printer) {
        this.printer = Objects.requireNonNull(printer);
    }

    @Override
    public MetricRecorder recorder() {
        return recorder;
    }

    @Override
    public void record(final double value) {
        count++;
        avg = count == 1 ? value : avg + (value - avg) / count;
    }

    @Override
    public void reset() {
        count = 0;
        avg = Double.NaN;
    }

    @Override
    public void print(final StringBuilder output) {
        printer.print(this, output);
    }

    public double avg() {
        return avg;
    }
}
