/*
 * Copyright (c) 2019-2024 Team Galacticraft
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

package dev.galacticraft.mod.network.packets;

import dev.galacticraft.mod.Constant.Packet;
import dev.galacticraft.mod.content.entity.ControllableEntity;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

public record ControlEntityPacket(float leftImpulse, float forwardImpulse, boolean up, boolean down, boolean left, boolean right, boolean jumping, boolean shiftKeyDown) implements GCPacket {
    public static final PacketType<ControlEntityPacket> TYPE = PacketType.create(Packet.CONTROLLABLE_ENTITY, ControlEntityPacket::new);

    public ControlEntityPacket(FriendlyByteBuf buf) {
        this(buf.readFloat(), buf.readFloat(), buf.readBoolean(), buf.readBoolean(), buf.readBoolean(), buf.readBoolean(), buf.readBoolean(), buf.readBoolean());
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeFloat(leftImpulse);
        buf.writeFloat(forwardImpulse);
        buf.writeBoolean(up);
        buf.writeBoolean(down);
        buf.writeBoolean(left);
        buf.writeBoolean(right);
        buf.writeBoolean(jumping);
        buf.writeBoolean(shiftKeyDown);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }

    @Override
    public void handle(Player player, PacketSender responseSender) {
        if (player.isPassenger())
            if (player.getVehicle() instanceof ControllableEntity controllable)
                controllable.inputTick(leftImpulse(), forwardImpulse(), up(), down(), left(), right(), jumping(), shiftKeyDown());
    }
}
