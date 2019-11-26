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

package me.piggypiglet.framework.nukkit.user;


import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.ConsoleCommandSender;
import me.piggypiglet.framework.minecraft.user.MinecraftUser;
import me.piggypiglet.framework.nukkit.binding.player.NukkitPlayer;

public final class NukkitUser extends MinecraftUser {
    private final CommandSender sender;

    public NukkitUser(CommandSender sender) {
        super(
                sender.getName(),
                sender instanceof Player ? ((Player) sender).getUniqueId().toString() : "console"
        );

        this.sender = sender;
    }

    //todo Handle color formatting
    @Override
    protected void sendMessage(String message) {
        sender.sendMessage(message);
    }

    @Override
    public boolean hasPermission(String permission) {
        return sender.hasPermission(permission);
    }

    public boolean isPlayer() {
        return sender instanceof Player;
    }

    public ConsoleCommandSender getAsConsole() {
        return (ConsoleCommandSender) sender;
    }

    @Override
    public me.piggypiglet.framework.minecraft.player.Player getAsPlayer() {
        return new NukkitPlayer((Player) sender);
    }
}
