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
 * Prints a list of printables that logically form a group.
 */
public class GroupPrintable implements Printable {

    private final Printer<? super Printable[]> printer;
    private final Printable[] printables;

    public GroupPrintable(final Printable... printables) {
        this("", " ", printables);
    }

    public GroupPrintable(final String prefix, final String separator, final Printable... printables) {
        this((metric, output) -> {
            output.append(prefix);
            for (int i = 0; i < printables.length; i++) {
                if (i > 0 || prefix.length() > 0) {
                    output.append(separator);
                }
                printables[i].print(output);
            }
        }, printables);
    }

    public GroupPrintable(final Printer<? super Printable[]> printer, final Printable... printables) {
        this.printer = Objects.requireNonNull(printer);
        this.printables = Objects.requireNonNull(printables);
    }

    @Override
    public void print(final StringBuilder output) {
        printer.print(printables, output);
    }
}
