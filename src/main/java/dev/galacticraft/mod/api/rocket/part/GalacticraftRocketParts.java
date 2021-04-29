/*
 * Copyright (c) 2019-2021 Team Galacticraft
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

package dev.galacticraft.mod.api.rocket.part;

import dev.galacticraft.api.event.RegistrationEvent;
import dev.galacticraft.api.registry.AddonRegistry;
import dev.galacticraft.api.rocket.part.RocketPart;
import dev.galacticraft.api.rocket.part.RocketPartRendererRegistry;
import dev.galacticraft.api.rocket.part.RocketPartType;
import dev.galacticraft.api.rocket.part.travel.ConfiguredTravelPredicate;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.rocket.travel.GalacticraftConfiguredTravelPredicates;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

public class GalacticraftRocketParts {
    public static final RocketPart DEFAULT_CONE = RocketPart.Builder.create(new Identifier(Constant.MOD_ID, "default_cone"))
            .name(new TranslatableText("rocket_part.galacticraft.default_cone"))
            .travelPredicate(GalacticraftConfiguredTravelPredicates.TIER_ONE)
            .type(RocketPartType.CONE) //todo: predicates
            .build();

    public static final RocketPart DEFAULT_BODY = RocketPart.Builder.create(new Identifier(Constant.MOD_ID, "default_body"))
            .name(new TranslatableText("rocket_part.galacticraft.default_body"))
            .travelPredicate(GalacticraftConfiguredTravelPredicates.TIER_ONE)
            .type(RocketPartType.BODY)
            .build();

    public static final RocketPart DEFAULT_FIN = RocketPart.Builder.create(new Identifier(Constant.MOD_ID, "default_fin"))
            .name(new TranslatableText("rocket_part.galacticraft.default_fin"))
            .travelPredicate(GalacticraftConfiguredTravelPredicates.TIER_ONE)
            .type(RocketPartType.FIN)
            .build();

    public static final RocketPart NO_BOOSTER = RocketPart.Builder.create(new Identifier(Constant.MOD_ID, "default_booster"))
            .name(new TranslatableText("rocket_part.galacticraft.default_booster"))
            .travelPredicate(GalacticraftConfiguredTravelPredicates.TIER_ONE)
            .type(RocketPartType.BOOSTER)
            .recipe(false)
            .build();

    public static final RocketPart DEFAULT_BOTTOM = RocketPart.Builder.create(new Identifier(Constant.MOD_ID, "default_bottom"))
            .name(new TranslatableText("rocket_part.galacticraft.default_bottom"))
            .travelPredicate(GalacticraftConfiguredTravelPredicates.TIER_ONE)
            .type(RocketPartType.BOTTOM)
            .build();

    public static final RocketPart ADVANCED_CONE = RocketPart.Builder.create(new Identifier(Constant.MOD_ID, "advanced_cone"))
            .name(new TranslatableText("rocket_part.galacticraft.advanced_cone"))
            .travelPredicate(ConfiguredTravelPredicate.PASS)
            .type(RocketPartType.CONE)
            .build();

    public static final RocketPart SLOPED_CONE = RocketPart.Builder.create(new Identifier(Constant.MOD_ID, "sloped_cone"))
            .name(new TranslatableText("rocket_part.galacticraft.cone_sloped"))
            .travelPredicate(ConfiguredTravelPredicate.PASS)
            .type(RocketPartType.CONE)
            .build();

    public static final RocketPart BOOSTER_TIER_1 = RocketPart.Builder.create(new Identifier(Constant.MOD_ID, "booster_1"))
            .name(new TranslatableText("rocket_part.galacticraft.booster_1"))
            .travelPredicate(GalacticraftConfiguredTravelPredicates.TIER_TWO)
            .type(RocketPartType.BOOSTER)
            .build();

    public static final RocketPart BOOSTER_TIER_2 = RocketPart.Builder.create(new Identifier(Constant.MOD_ID, "booster_2"))
            .name(new TranslatableText("rocket_part.galacticraft.booster_2"))
            .travelPredicate(GalacticraftConfiguredTravelPredicates.TIER_THREE)
            .type(RocketPartType.BOOSTER)
            .build();

    public static final RocketPart NO_UPGRADE = RocketPart.Builder.create(new Identifier(Constant.MOD_ID, "default_upgrade"))
            .name(new TranslatableText("rocket_part.galacticraft.default_upgrade"))
            .travelPredicate(ConfiguredTravelPredicate.PASS)
            .type(RocketPartType.UPGRADE)
            .build();

    public static final RocketPart STORAGE_UPGRADE = RocketPart.Builder.create(new Identifier(Constant.MOD_ID, "storage_upgrade"))
            .name(new TranslatableText("rocket_part.galacticraft.storage_upgrade"))
            .travelPredicate(ConfiguredTravelPredicate.PASS)
            .type(RocketPartType.UPGRADE)
            .build();

    public static void register() {
        RegistrationEvent.ROCKET_PART.register(registry -> {
            Registry.register(registry, DEFAULT_CONE.getId(), DEFAULT_CONE);
            Registry.register(registry, DEFAULT_BODY.getId(), DEFAULT_BODY);
            Registry.register(registry, DEFAULT_FIN.getId(), DEFAULT_FIN);
            Registry.register(registry, NO_BOOSTER.getId(), NO_BOOSTER);
            Registry.register(registry, DEFAULT_BOTTOM.getId(), DEFAULT_BOTTOM);
            Registry.register(registry, ADVANCED_CONE.getId(), ADVANCED_CONE);
            Registry.register(registry, SLOPED_CONE.getId(), SLOPED_CONE);
            Registry.register(registry, BOOSTER_TIER_1.getId(), BOOSTER_TIER_1);
            Registry.register(registry, BOOSTER_TIER_2.getId(), BOOSTER_TIER_2);
            Registry.register(registry, NO_UPGRADE.getId(), NO_UPGRADE);
            Registry.register(registry, STORAGE_UPGRADE.getId(), STORAGE_UPGRADE);
        });
    }

    @NotNull
    public static RocketPart getDefaultPartForType(RocketPartType type) {
        switch (type) {
            case BODY:
                return DEFAULT_BODY;
            case CONE:
                return DEFAULT_CONE;
            case FIN:
                return DEFAULT_FIN;
            case BOTTOM:
                return DEFAULT_BOTTOM;
            case BOOSTER:
                return NO_BOOSTER;
            case UPGRADE:
                return NO_UPGRADE;
            default:
                throw new IllegalArgumentException("invalid part type");
        }
    }

    @NotNull
    @Environment(EnvType.CLIENT)
    public static RocketPartRendererRegistry.RocketPartRenderer getPartToRenderForType(@NotNull RocketPartType type) {
        switch (type) {
            case BODY:
                return RocketPartRendererRegistry.getRenderer(DEFAULT_BODY);
            case CONE:
                return RocketPartRendererRegistry.getRenderer(DEFAULT_CONE);
            case FIN:
                return RocketPartRendererRegistry.getRenderer(DEFAULT_FIN);
            case BOTTOM:
                return RocketPartRendererRegistry.getRenderer(DEFAULT_BOTTOM);
            case BOOSTER:
                return RocketPartRendererRegistry.getRenderer(BOOSTER_TIER_1);
            case UPGRADE:
                return RocketPartRendererRegistry.getRenderer(STORAGE_UPGRADE);
        }
        throw new AssertionError();
    }

    public static List<RocketPart> getUnlockedParts(PlayerEntity player, RocketPartType type) {
        List<RocketPart> parts = new LinkedList<>();
        for (RocketPart part : RocketPart.getAll(player.world.getRegistryManager())) {
            if (part.getType() == type && part.isUnlocked(player)) {
                parts.add(part);
            }
        }
        return parts;
    }

    public static List<RocketPart> getUnlockedParts(PlayerEntity player) {
        List<RocketPart> parts = new LinkedList<>();
        for (RocketPart part : RocketPart.getAll(player.world.getRegistryManager())) {
            if (part.isUnlocked(player)) {
                parts.add(part);
            }
        }
        return parts;
    }
}
