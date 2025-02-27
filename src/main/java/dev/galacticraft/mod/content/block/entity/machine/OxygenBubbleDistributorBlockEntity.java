/*
 * Copyright (c) 2019-2023 Team Galacticraft
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

package dev.galacticraft.mod.content.block.entity.machine;

import dev.galacticraft.machinelib.api.block.entity.MachineBlockEntity;
import dev.galacticraft.api.gas.Gases;
import dev.galacticraft.machinelib.api.machine.MachineStatus;
import dev.galacticraft.machinelib.api.machine.MachineStatuses;
import dev.galacticraft.machinelib.api.storage.slot.FluidResourceSlot;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.Galacticraft;
import dev.galacticraft.mod.content.GCEntityTypes;
import dev.galacticraft.mod.content.GCMachineTypes;
import dev.galacticraft.mod.content.entity.BubbleEntity;
import dev.galacticraft.mod.machine.GCMachineStatuses;
import dev.galacticraft.mod.machine.storage.io.GCSlotGroupTypes;
import dev.galacticraft.mod.screen.OxygenBubbleDistributorMenu;
import dev.galacticraft.mod.util.FluidUtil;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class OxygenBubbleDistributorBlockEntity extends MachineBlockEntity {
    public static final long MAX_OXYGEN = FluidUtil.bucketsToDroplets(50);
    public boolean bubbleVisible = true;
    private double size = 0;
    private byte targetSize = 1;
    private int players = 0;
    private int bubbleId = -1;
    private double prevSize;

    public OxygenBubbleDistributorBlockEntity(BlockPos pos, BlockState state) {
        super(GCMachineTypes.OXYGEN_BUBBLE_DISTRIBUTOR, pos, state);
    }

    @Override
    protected void tickConstant(@NotNull ServerLevel world, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ProfilerFiller profiler) {
        super.tickConstant(world, pos, state, profiler);
        profiler.push("extract_resources");
        this.chargeFromStack(GCSlotGroupTypes.ENERGY_TO_SELF);
        this.takeFluidFromStack(GCSlotGroupTypes.OXYGEN_TO_SELF, GCSlotGroupTypes.OXYGEN_INPUT, Gases.OXYGEN);
        profiler.pop();
    }

    @Override
    protected @NotNull MachineStatus tick(@NotNull ServerLevel world, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ProfilerFiller profiler) {
        profiler.push("transaction");
        MachineStatus status;
        distributeOxygenToArea(this.prevSize, false);
        try {
            if (this.energyStorage().canExtract(Galacticraft.CONFIG_MANAGER.get().oxygenCollectorEnergyConsumptionRate())) { //todo: config
                profiler.push("bubble");
                if (this.size > this.targetSize) {
                    this.setSize(Math.max(this.size - 0.1F, this.targetSize));
                }
                if (this.size > 0.0D && this.bubbleVisible && this.bubbleId == -1) {
                    BubbleEntity entity = GCEntityTypes.BUBBLE.create(world);
                    entity.setPosRaw(this.getBlockPos().getX(), this.getBlockPos().getY(), this.getBlockPos().getZ());
                    entity.xo = this.getBlockPos().getX();
                    entity.yo = this.getBlockPos().getY();
                    entity.zo = this.getBlockPos().getZ();
                    world.addFreshEntity(entity);
                    this.bubbleId = entity.getId();
                    for (ServerPlayer player : world.players()) {
                        player.connection.send(entity.getAddEntityPacket());
                    }
                }
                profiler.pop();
                if (this.prevSize != this.size || this.players != world.players().size()) {
                    this.players = world.players().size();
                    this.prevSize = this.size;
                    profiler.push("network");
                    for (ServerPlayer player : world.players()) {
                        ServerPlayNetworking.send(player, Constant.Packet.BUBBLE_SIZE, new FriendlyByteBuf(new FriendlyByteBuf(Unpooled.buffer()).writeBlockPos(pos).writeDouble(this.size)));
                    }
                    profiler.pop();
                }
                profiler.push("bubbler_distributor_transfer");
                long oxygenRequired = ((long) ((4.0 / 3.0) * Math.PI * this.size * this.size * this.size));
                FluidResourceSlot slot = this.fluidStorage().getSlot(GCSlotGroupTypes.OXYGEN_INPUT);
                if (slot.canExtract(oxygenRequired)) {
                    slot.extract(oxygenRequired);
                    this.energyStorage().extract(Galacticraft.CONFIG_MANAGER.get().oxygenCollectorEnergyConsumptionRate());
                    if (this.size < this.targetSize) {
                        setSize(this.size + 0.05D);
                    }
                    profiler.pop();
                    distributeOxygenToArea(this.size, true);
                    return GCMachineStatuses.DISTRIBUTING;
                } else {
                    status = GCMachineStatuses.NOT_ENOUGH_OXYGEN;
                    profiler.pop();
                }
            } else {
                status = MachineStatuses.NOT_ENOUGH_ENERGY;
            }
        } finally {
            profiler.pop();
        }
        profiler.push("size");
        if (this.bubbleId != -1 && this.size <= 0) {
            world.getEntity(bubbleId).remove(Entity.RemovalReason.DISCARDED);
            this.bubbleId = -1;
        }

        if (this.size > 0) {
            this.setSize(this.size - 0.2D);
        }

        if (this.size < 0) {
            this.setSize(0);
        }
        profiler.pop();
        return status;
    }

    public int getDistanceFromServer(int par1, int par3, int par5) {
        final int d3 = this.getBlockPos().getX() - par1;
        final int d4 = this.getBlockPos().getY() - par3;
        final int d5 = this.getBlockPos().getZ() - par5;
        return d3 * d3 + d4 * d4 + d5 * d5;
    }

    public void distributeOxygenToArea(double size, boolean oxygenated) {
        int radius = Mth.floor(size) + 4;
        int bubbleR2 = (int) (size * size);
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int x = this.getBlockPos().getX() - radius; x <= this.getBlockPos().getX() + radius; x++) {
            for (int y = this.getBlockPos().getY() - radius; y <= this.getBlockPos().getY() + radius; y++) {
                for (int z = this.getBlockPos().getZ() - radius; z <= this.getBlockPos().getZ() + radius; z++) {
                    if (getDistanceFromServer(x, y, z) <= bubbleR2) {
                        getLevel().setBreathable(pos.set(x, y, z), oxygenated);
                    }
                }
            }
        }
    }

    public byte getTargetSize() {
        return this.targetSize;
    }

    public void setTargetSize(byte targetSize) {
        this.targetSize = targetSize;
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putByte(Constant.Nbt.MAX_SIZE, this.targetSize);
        tag.putDouble(Constant.Nbt.SIZE, this.size);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        this.size = nbt.getDouble(Constant.Nbt.SIZE);
        if (this.size < 0) this.size = 0;
        this.targetSize = nbt.getByte(Constant.Nbt.MAX_SIZE);
        if (this.targetSize < 1) this.targetSize = 1;
    }

    public double getSize() {
        return this.size;
    }

    public void setSize(double size) {
        this.size = size;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory inv, Player player) {
        if (this.getSecurity().hasAccess(player)) return new OxygenBubbleDistributorMenu(syncId, (ServerPlayer) player, this);
        return null;
    }
}