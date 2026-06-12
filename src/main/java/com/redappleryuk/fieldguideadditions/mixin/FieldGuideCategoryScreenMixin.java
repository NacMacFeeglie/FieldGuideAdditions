package com.redappleryuk.fieldguideadditions.mixin;

import com.evandev.fieldguide.client.ClientFieldGuideManager;
import com.evandev.fieldguide.client.gui.screens.FieldGuideCategoryScreen;
import com.evandev.fieldguide.client.gui.util.EntryRenderHelper;
import com.redappleryuk.fieldguideadditions.RecipeEntryData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FieldGuideCategoryScreen.class)
public class FieldGuideCategoryScreenMixin {

    @Inject(
            method = "renderEntryInGrid",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )

    private void fieldguideadditions$renderRecipeOutputInGrid(
            GuiGraphics guiGraphics,
            Object entry,
            int x,
            int y,
            boolean unlocked,
            CallbackInfo ci
    ) {
        ResourceLocation entryId = ClientFieldGuideManager.getEntryId(entry);
        if (entryId == null) return;

        ResourceLocation recipeId = RecipeEntryData.getRecipeForEntry(entryId);
        if (recipeId == null) return;

        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return;

        Recipe<?> recipe = level.getRecipeManager()
                .byKey(recipeId)
                .orElse(null);

        if (recipe == null) return;

        ItemStack result = recipe.getResultItem(level.registryAccess());
        if (result.isEmpty()) return;

        EntryRenderHelper.renderItemStack(
                guiGraphics,
                result,
                entry,
                x,
                y,
                20.0F,
                unlocked,
                false,
                1.0F
        );

        ci.cancel();
    }
}