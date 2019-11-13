package me.piggypiglet.framework.bungeecord.binding.player.inventory.packets.inventory.item.construction;

import io.netty.buffer.ByteBuf;
import me.piggypiglet.framework.minecraft.player.inventory.objects.Item;

public interface ItemConstructor {
    Item from(ByteBuf buf);

    void to(ByteBuf buf, Item item);
}
