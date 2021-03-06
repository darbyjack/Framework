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

package me.piggypiglet.framework.jda;

import me.piggypiglet.framework.bootstrap.BootPriority;
import me.piggypiglet.framework.jda.annotation.Bot;
import me.piggypiglet.framework.jda.shutdown.JDAShutdown;
import me.piggypiglet.framework.jda.startup.JDARegisterable;
import me.piggypiglet.framework.utils.annotations.addon.Addon;
import me.piggypiglet.framework.utils.annotations.addon.Config;
import me.piggypiglet.framework.utils.annotations.addon.File;
import me.piggypiglet.framework.utils.annotations.registerable.Startup;

@Addon(
        startup = {
                @Startup(
                        value = JDARegisterable.class,
                        priority = BootPriority.AFTER_IMPL
                )/*,
                @Startup(
                        value = GuildBindingRegisterable.class,
                        priority = BootPriority.AFTER_IMPL
                )*/
        },
        files = {@File(
                config = true,
                name = "bot",
                externalPath = "bot.json",
                internalPath = "/bot.json",
                annotation = Bot.class
        )},
        config = @Config(
                name = "bot",
                keys = {"token", "activity.type", "activity.activity"}
        ),
        shutdown = JDAShutdown.class
)
public final class JDAAddon {
}
