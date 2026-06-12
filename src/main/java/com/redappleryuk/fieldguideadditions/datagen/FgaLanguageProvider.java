package com.redappleryuk.fieldguideadditions.datagen;

import com.redappleryuk.fieldguideadditions.FieldGuideAdditions;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;

public class FgaLanguageProvider extends LanguageProvider {

    public FgaLanguageProvider(PackOutput output) {
        super(output, FieldGuideAdditions.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        add("category.fieldguideadditions.fieldguide.biomes", "Biomes");
        add("category.fieldguideadditions.fieldguide.recipes", "Recipes");
        add("category.fieldguideadditions.fieldguide.modded_biomes", "Modded Biomes");

        for (String biome : FgaBiomeLists.VANILLA_BIOMES) {
            String displayName = titleCase(biome);

            add("fieldguide.name.fieldguideadditions." + biome, displayName);
            add("fieldguide.fieldguideadditions." + biome + ".description", "The " + displayName + " Biome");
        }

        add("fieldguide.name.fieldguideadditions.crafting_table_recipe", "Crafting Table");
        add("fieldguide.fieldguideadditions.crafting_table_recipe.description", "A simple work surface for arranging materials into more complex forms.");

        add("fieldguide.name.fieldguideadditions.hay_block_recipe", "Hay Bale");
        add("fieldguide.fieldguideadditions.hay_block_recipe.description", "A block of hay, used for breeding horses.");

        add("fieldguide.name.fieldguideadditions.slime_block_recipe", "Slime Block");
        add("fieldguide.fieldguideadditions.slime_block_recipe.description", "A block of slime, useful for breaking a fall.");
    }

    private static String titleCase(String id) {
        String[] parts = id.split("_");
        StringBuilder result = new StringBuilder();

        for (String part : parts) {
            if (!result.isEmpty()) {
                result.append(" ");
            }

            result.append(part.substring(0, 1).toUpperCase());
            result.append(part.substring(1));
        }

        return result.toString();
    }
}