package me.piggypiglet.framework.bukkit.registerables;

import com.google.inject.Inject;
import me.piggypiglet.framework.Framework;
import me.piggypiglet.framework.access.AccessManager;
import me.piggypiglet.framework.bukkit.commands.BaseCommandHandler;
import me.piggypiglet.framework.bukkit.commands.BukkitCommandHandler;
import me.piggypiglet.framework.registerables.StartupRegisterable;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

// ------------------------------
// Copyright (c) PiggyPiglet 2019
// https://www.piggypiglet.me
// ------------------------------
public final class CommandExecutorRegisterable extends StartupRegisterable {
    @Inject private BaseCommandHandler baseCommandHandler;
    @Inject private JavaPlugin main;
    @Inject private Framework framework;
    @Inject private BukkitCommandHandler commandHandler;
    @Inject private AccessManager accessManager;

    @Override
    protected void execute() {
        baseCommandHandler.registerCommands();
        //todo: do a proper null check, throw custom exception if null
        Objects.requireNonNull(main.getCommand(framework.getCommandPrefix())).setExecutor(commandHandler);
        accessManager.newHandler("bukkit", injector);
    }
}