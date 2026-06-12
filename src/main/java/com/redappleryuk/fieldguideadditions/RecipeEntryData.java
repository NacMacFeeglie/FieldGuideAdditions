package com.redappleryuk.fieldguideadditions;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class RecipeEntryData {
    private static final Map<ResourceLocation, ResourceLocation> ENTRY_TO_RECIPE = new HashMap<>();

    public static void loadRecipeEntries(ResourceManager resourceManager) {
        ENTRY_TO_RECIPE.clear();

        Map<ResourceLocation, Resource> files = resourceManager.listResources(
                "fieldguide_additions/recipe_entries",
                path -> path.getPath().endsWith(".json")
        );

        for (Map.Entry<ResourceLocation, Resource> file : files.entrySet()) {
            try (InputStreamReader reader = new InputStreamReader(
                    file.getValue().open(),
                    StandardCharsets.UTF_8
            )) {
                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                JsonArray entries = json.getAsJsonArray("entries");

                if (entries == null) continue;

                for (int i = 0; i < entries.size(); i++) {
                    JsonObject entry = entries.get(i).getAsJsonObject();

                    ResourceLocation fieldGuideEntry = new ResourceLocation(entry.get("entry").getAsString());
                    ResourceLocation recipe = new ResourceLocation(entry.get("recipe").getAsString());

                    ENTRY_TO_RECIPE.put(fieldGuideEntry, recipe);
                }

                FieldGuideAdditions.LOGGER.info("Loaded recipe entry file: {}", file.getKey());

            } catch (Exception e) {
                FieldGuideAdditions.LOGGER.error("Failed to load recipe entry file: {}", file.getKey(), e);
            }
        }

        FieldGuideAdditions.LOGGER.info("Loaded {} recipe entry mappings", ENTRY_TO_RECIPE.size());
    }

    public static ResourceLocation getRecipeForEntry(ResourceLocation entryId) {
        return ENTRY_TO_RECIPE.get(entryId);
    }

    public static boolean hasRecipeEntry(ResourceLocation entryId) {
        return ENTRY_TO_RECIPE.containsKey(entryId);
    }
}