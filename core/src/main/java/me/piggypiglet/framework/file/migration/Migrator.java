/*
 * MIT License
 *
 * Copyright (c) 2019-2020 PiggyPiglet
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

package me.piggypiglet.framework.file.migration;

import me.piggypiglet.framework.file.framework.MutableFileConfiguration;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public abstract class Migrator implements Comparable<Migrator> {
    private final String config;
    private final int priority;
    private final Predicate<MutableFileConfiguration> requirement;

    protected Migrator(String config, Predicate<MutableFileConfiguration> requirement) {
        this(0, config, requirement);
    }

    protected Migrator(int priority, String config, Predicate<MutableFileConfiguration> requirement) {
        this.priority = priority;
        this.config = config;
        this.requirement = requirement;
    }

    protected abstract void migrate(MutableFileConfiguration config);

    public boolean canMigrate(MutableFileConfiguration config) {
        return requirement.test(config);
    }

    public void run(MutableFileConfiguration config) {
        migrate(config);
    }

    @Override
    public int compareTo(@Nonnull Migrator target) {
        return priority - target.priority;
    }

    public String getConfig() {
        return config;
    }
}
