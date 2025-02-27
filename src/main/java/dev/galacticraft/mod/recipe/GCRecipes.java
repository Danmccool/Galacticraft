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

package dev.galacticraft.mod.recipe;

import dev.galacticraft.mod.Constant;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeType;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class GCRecipes {
    public static final RecipeType<FabricationRecipe> FABRICATION_TYPE = new GCRecipeType<>();
    public static final RecipeType<CompressingRecipe> COMPRESSING_TYPE = new GCRecipeType<>();

    public static final FabricationRecipe.Serializer FABRICATION_SERIALIZER = FabricationRecipe.Serializer.INSTANCE;
    public static final ShapelessCompressingRecipe.Serializer SHAPELESS_COMPRESSING_SERIALIZER = ShapelessCompressingRecipe.Serializer.INSTANCE;
    public static final ShapedCompressingRecipe.Serializer SHAPED_COMPRESSING_SERIALIZER = ShapedCompressingRecipe.Serializer.INSTANCE;

    public static void register() {
        Registry.register(BuiltInRegistries.RECIPE_TYPE, new ResourceLocation(Constant.MOD_ID, Constant.Recipe.FABRICATION), FABRICATION_TYPE);
        Registry.register(BuiltInRegistries.RECIPE_TYPE, new ResourceLocation(Constant.MOD_ID, Constant.Recipe.COMPRESSING), COMPRESSING_TYPE);
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, new ResourceLocation(Constant.MOD_ID, Constant.Recipe.Serializer.FABRICATION), FABRICATION_SERIALIZER);
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, new ResourceLocation(Constant.MOD_ID, Constant.Recipe.Serializer.COMPRESSING_SHAPELESS), SHAPELESS_COMPRESSING_SERIALIZER);
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, new ResourceLocation(Constant.MOD_ID, Constant.Recipe.Serializer.COMPRESSING_SHAPED), SHAPED_COMPRESSING_SERIALIZER);
    }
}