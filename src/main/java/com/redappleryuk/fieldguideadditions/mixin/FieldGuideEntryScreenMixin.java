package com.redappleryuk.fieldguideadditions.mixin;

import com.evandev.fieldguide.client.ClientFieldGuideManager;
import com.evandev.fieldguide.client.gui.screens.FieldGuideEntryScreen;
import com.evandev.fieldguide.client.gui.util.Bounds;
import com.redappleryuk.fieldguideadditions.RecipeEntryData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapedRecipe;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FieldGuideEntryScreen.class)
public class FieldGuideEntryScreenMixin {

    @Shadow(remap = false)
    @Final
    private Object entry;

    @Inject(method = "render", at = @At("TAIL"))
    private void fieldguideadditions$renderRecipeIngredients(
            GuiGraphics guiGraphics,
            int mouseX,
            int mouseY,
            float partialTick,
            CallbackInfo ci
    ) {
        ResourceLocation entryId = ClientFieldGuideManager.getEntryId(this.entry);
        if (entryId == null) return;

        ResourceLocation recipeId = RecipeEntryData.getRecipeForEntry(entryId);
        if (recipeId == null) return;

        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (level == null) return;

        Recipe<?> recipe = level.getRecipeManager().byKey(recipeId).orElse(null);
        if (recipe == null) return;

        NonNullList<Ingredient> ingredients = recipe.getIngredients();
        if (ingredients.isEmpty()) return;

        FieldGuideEntryScreen screen = (FieldGuideEntryScreen) (Object) this;
        Bounds leftPage = screen.getLeftPageBounds();

        int gridSize = 112;
        int slotSpacing = gridSize / 3;

        int gridLeft = leftPage.x_center() - gridSize / 2;
        int gridTop = leftPage.y_center() - 15 - gridSize / 2;

        ResourceLocation gridTexture = new ResourceLocation(
                "fieldguideadditions",
                "textures/fieldguide/entries/variant_overview_bg.png"
        );

        guiGraphics.blit(
                gridTexture,
                gridLeft,
                gridTop,
                0,
                0,
                gridSize,
                gridSize,
                gridSize,
                gridSize
        );

        int recipeWidth = 3;
        int recipeHeight = 3;

        if (recipe instanceof ShapedRecipe shapedRecipe) {
            recipeWidth = shapedRecipe.getWidth();
            recipeHeight = shapedRecipe.getHeight();
        }

        long time = System.currentTimeMillis() / 1000L;

        for (int i = 0; i < ingredients.size(); i++) {
            Ingredient ingredient = ingredients.get(i);
            if (ingredient.isEmpty()) continue;

            int col = i % recipeWidth;
            int row = i / recipeWidth;

            if (row >= recipeHeight) continue;

            ItemStack[] options = ingredient.getItems();
            if (options.length == 0) continue;

            ItemStack stack = options[(int) (time % options.length)];
            if (stack.isEmpty()) continue;

            int x = gridLeft + col * slotSpacing
                    + (row == 0 ? 12 : 13)
                    + (col == 0 ? 1 : 0)
                    - (col == 1 ? 1 : 0)
                    - (col == 2 ? 3 : 0)
                    - (row > 0 ? 1 : 0);

            int y = gridTop + row * 31 + (row == 0 ? 9 : 6);

            guiGraphics.renderItem(stack, x, y);
            guiGraphics.renderItemDecorations(minecraft.font, stack, x, y);
        }
    }
}