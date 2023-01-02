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

package dev.galacticraft.mod.content.fluid;

import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.item.GCItem;
import dev.galacticraft.mod.particle.GCParticleType;
import dev.galacticraft.mod.content.GCFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public abstract class CrudeOilFluid extends BasicFluid {
    protected CrudeOilFluid() {
        super(false, true, 2, 1, 30, 100);
    }

    @Override
    public Fluid getFlowing() {
        return GCFluids.FLOWING_CRUDE_OIL;
    }

    @Override
    public Fluid getSource() {
        return GCFluids.CRUDE_OIL;
    }

    public ParticleOptions getDripParticle() {
        return GCParticleType.DRIPPING_CRUDE_OIL_PARTICLE;
    }

    @Override
    public Item getBucket() {
        return GCItem.CRUDE_OIL_BUCKET;
    }

    @Override
    public void animateTick(Level world, BlockPos blockPos, FluidState fluidState, RandomSource random) {
        if (random.nextInt(10) == 0) {
            world.addParticle(GCParticleType.DRIPPING_CRUDE_OIL_PARTICLE,
                    (double) blockPos.getX() + 0.5D - random.nextGaussian() + random.nextGaussian(),
                    (double) blockPos.getY() + 1.1F,
                    (double) blockPos.getZ() + 0.5D - random.nextGaussian() + random.nextGaussian(),
                    0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    protected LiquidBlock getBlock() {
        return GCBlocks.CRUDE_OIL;
    }

    public static class Still extends CrudeOilFluid {
        @Override
        public boolean isStill() {
            return true;
        }
    }

    public static class Flowing extends CrudeOilFluid {
        @Override
        public boolean isStill() {
            return false;
        }
    }
}
