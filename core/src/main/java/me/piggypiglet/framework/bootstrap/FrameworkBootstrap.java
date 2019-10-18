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

package me.piggypiglet.framework.bootstrap;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import me.piggypiglet.framework.Framework;
import me.piggypiglet.framework.guice.modules.BindingSetterModule;
import me.piggypiglet.framework.guice.modules.InitialModule;
import me.piggypiglet.framework.logging.LoggerFactory;
import me.piggypiglet.framework.scanning.Scanner;
import me.piggypiglet.framework.registerables.StartupRegisterable;
import me.piggypiglet.framework.registerables.startup.ImplementationFinderRegisterable;
import me.piggypiglet.framework.registerables.startup.ManagersRegisterable;
import me.piggypiglet.framework.registerables.startup.ShutdownHookRegisterable;
import me.piggypiglet.framework.registerables.startup.ShutdownRegisterablesRegisterable;
import me.piggypiglet.framework.registerables.startup.addon.DefaultConfigsRegisterable;
import me.piggypiglet.framework.registerables.startup.addon.UserConfigsRegisterable;
import me.piggypiglet.framework.registerables.startup.commands.CommandHandlerRegisterable;
import me.piggypiglet.framework.registerables.startup.commands.CommandsRegisterable;
import me.piggypiglet.framework.registerables.startup.file.FileMappingRegisterable;
import me.piggypiglet.framework.registerables.startup.file.FileTypesRegisterable;
import me.piggypiglet.framework.registerables.startup.file.FilesRegisterable;
import me.piggypiglet.framework.utils.annotations.addon.Addon;
import me.piggypiglet.framework.utils.annotations.registerable.RegisterableData;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Singleton
public final class FrameworkBootstrap {
    private final AtomicReference<Injector> injector = new AtomicReference<>();
    private final Set<StartupRegisterable> registerables = new LinkedHashSet<>();
    private final Map<Class<?>, Addon> addons = new HashMap<>();

    private final Framework config;

    public FrameworkBootstrap(Framework config) {
        this.config = config;

        if (config.getInjector() == null) {
            injector.set(new InitialModule(this, config).createInjector());
        } else {
            injector.set(config.getInjector().createChildInjector(new InitialModule(this, config)));
        }

        start();
    }

    private void start() {
        injector.get().getInstance(Scanner.class).getTypesAnnotatedWith(Addon.class)
                .forEach(c -> addons.put(c, c.getAnnotation(Addon.class)));

        final Multimap<BootPriority, Class<? extends StartupRegisterable>> registerables = ArrayListMultimap.create();

        registerables.putAll(BootPriority.IMPL, linkedHashSet(
                ImplementationFinderRegisterable.class, FileTypesRegisterable.class,
                DefaultConfigsRegisterable.class, FilesRegisterable.class,
                FileMappingRegisterable.class, UserConfigsRegisterable.class
        ));

        if (config.getCommandPrefix() != null) {
            registerables.putAll(BootPriority.COMMANDS, linkedHashSet(
                    CommandsRegisterable.class, CommandHandlerRegisterable.class
            ));
        }

        registerables.putAll(BootPriority.SHUTDOWN, linkedHashSet(
                ManagersRegisterable.class, ShutdownRegisterablesRegisterable.class, ShutdownHookRegisterable.class
        ));


        addons.values().stream()
                .map(Addon::startup)
                .map(Arrays::stream)
                .map(s -> s.map(RegisterableData::new))
                .forEach(s -> processRegisterableData(s, registerables));

        processRegisterableData(config.getStartupRegisterables().stream(), registerables);

        for (BootPriority priority : BootPriority.values()) {
            final Collection<Class<? extends StartupRegisterable>> section = registerables.get(priority);

            section.forEach(r -> {
                StartupRegisterable registerable = injector.get().getInstance(r);
                registerable.run(injector.get());

                if (registerable.getBindings().size() > 0 || registerable.getAnnotatedBindings().size() > 0 || registerable.getStaticInjections().size() > 0) {
                    injector.set(injector.get().createChildInjector(new BindingSetterModule(
                            registerable.getBindings(),
                            registerable.getAnnotatedBindings(),
                            registerable.getStaticInjections().toArray(new Class[]{})
                    )));
                }

                this.registerables.add(registerable);
            });
        }

        LoggerFactory.getLogger("RPF").info("Bootstrap process completed.");
    }

    @SafeVarargs
    private final Set<Class<? extends StartupRegisterable>> linkedHashSet(Class<? extends StartupRegisterable>... registerables) {
        return Arrays.stream(registerables).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private void processRegisterableData(Stream<RegisterableData> data, Multimap<BootPriority, Class<? extends StartupRegisterable>> registerables) {
        data.forEach(r -> registerables.put(r.getPriority(), r.getRegisterable()));
    }

    /**
     * Get an instance of the current injector, as the injectable injector isn't updated through child injectors.
     * @return Injector instance
     */
    public Injector getInjector() {
        return injector.get();
    }

    /**
     * Get all the startup registerable instances that were initialized during startup.
     * @return Set of StartupRegisterable instances
     */
    public Set<StartupRegisterable> getRegisterables() {
        return registerables;
    }

    /**
     * Get all found addons and their data.
     * @return Set of Addon annotation data objects.
     */
    public Map<Class<?>, Addon> getAddons() {
        return addons;
    }
}