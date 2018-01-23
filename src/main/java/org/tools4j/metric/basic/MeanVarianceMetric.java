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
 * Metric that tracks mean and variance/stddev of a sampled value using a numerically stable algorithm.
 * <p>
 * The implementation is based on Welford’s Algorithm given in Knuth Vol 2, p 232.
 */
public class MeanVarianceMetric implements Metric, MetricRecorder {

    private long count = 0;
    private double mean = Double.NaN;
    private double s = Double.NaN;
    private final MetricRecorder recorder = this::record;
    private final Printer<? super MeanVarianceMetric> printer;

    public MeanVarianceMetric() {
        this("mean", "std", " ");
    }

    public MeanVarianceMetric(final String meanName, final String stdName, final String separator) {
        this((metric, output) -> {
            output.append(meanName).append('=').append(metric.mean());
            output.append(separator);
            output.append(stdName).append('=').append(metric.stdDevUnbiased());
        });
    }

    public MeanVarianceMetric(final Printer<? super MeanVarianceMetric> printer) {
        this.printer = Objects.requireNonNull(printer);
    }

    @Override
    public MetricRecorder recorder() {
        return recorder;
    }

    @Override
    public void record(final double value) {
        count++;
        if (count > 1) {
            final double delta = value - mean;
            mean = mean + delta / count;
            s += delta * (value - mean);
        } else {
            mean = value;
            s = 0;
        }
    }

    @Override
    public void reset() {
        count = 0;
        mean = Double.NaN;
        s = Double.NaN;
    }

    @Override
    public void print(final StringBuilder output) {
        printer.print(this, output);
    }

    /**
     * @return the mean value of the sample
     */
    public double mean() {
        return mean;
    }

    /**
     * @return the bias corrected variance of the sample (using the {@code (n-1)} method), or NaN if {@code n < 2}
     */
    public double varianceUnbiased() {
        return count > 1 ? s / (count - 1) : Double.NaN;
    }

    /**
     * @return the biased variance of the sample (using the {@code (n)} method), or NaN if {@code n == 0}
     */
    public double variance() {
        return s / count;
    }

    /**
     * @return the bias corrected standard deviation of the sample (using the {@code (n-1)} method), or NaN if {@code n < 2}
     */
    public double stdDevUnbiased() {
        return Math.sqrt(varianceUnbiased());
    }

    /**
     * @return the biased standard deviation of the sample (using the {@code (n)} method), or NaN if {@code n == 0}
     */
    public double stdDev() {
        return Math.sqrt(variance());
    }

    /**
     * @return the number of values in the sample
     */
    public long count() {
        return count;
    }
}
