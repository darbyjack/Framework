/*
 * MIT License
 *
 * Copyright (c) 2019 PiggyPiglet
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

package me.piggypiglet.test;

import me.piggypiglet.framework.Framework;
import me.piggypiglet.framework.jda.JDAAddon;
import me.piggypiglet.framework.utils.annotations.files.Config;
import me.piggypiglet.framework.utils.map.KeyValueSet;

public final class TestApp {
    private TestApp() {
        Framework.builder()
                .pckg("me.piggypiglet.test")
                .main(this)
                .commandPrefix("!")
                .file(true, "test", "/test.json", "./test.json", Config.class)
                .config(JDAAddon.class, "test", KeyValueSet.builder()
                        .key("token").value("token")
                        .key("activity.type").value("activity.type")
                        .key("activity.activity").value("activity.activity")
                        .build().toMap())
                .build()
                .init();
    }

    public static void main(String[] args) {
        new TestApp();
    }
}
