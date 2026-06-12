package com.redappleryuk.fieldguideadditions.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.redappleryuk.fieldguideadditions.FieldGuideAdditions;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public class FgaBiomeUnlockProvider implements DataProvider {

    private final PackOutput output;

    public FgaBiomeUnlockProvider(PackOutput output) {
        this.output = output;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        CompletableFuture<?>[] unlocks = FgaBiomeLists.VANILLA_BIOMES.stream()
                .map(biome -> saveBiomeUnlock(cache, biome))
                .toArray(CompletableFuture[]::new);

        CompletableFuture<?> category = saveBiomeCategory(cache);
        CompletableFuture<?> moddedCategory = saveModdedBiomeCategory(cache);

        CompletableFuture<?>[] all = new CompletableFuture[unlocks.length + 2];

        System.arraycopy(unlocks, 0, all, 0, unlocks.length);

        all[unlocks.length] = category;
        all[unlocks.length + 1] = moddedCategory;

        return CompletableFuture.allOf(all);
    }

    private CompletableFuture<?> saveBiomeCategory(CachedOutput cache) {
        JsonObject root = new JsonObject();

        root.addProperty("sort_index", 1);
        root.addProperty("icon", "minecraft:grass_block");

        JsonArray contents = new JsonArray();

        for (String biome : FgaBiomeLists.VANILLA_BIOMES) {
            JsonObject entry = new JsonObject();

            entry.addProperty("type", "virtual_entry");
            entry.addProperty("id", FieldGuideAdditions.MODID + ":" + biome);
            entry.addProperty("virtual_type", "tutorial");

            entry.addProperty(
                    "icon",
                    FieldGuideAdditions.MODID +
                            ":textures/fieldguide/entries/" +
                            biome +
                            "_page.png"
            );

            contents.add(entry);
        }

        root.add("contents", contents);

        Path path = output.getOutputFolder().resolve(
                "data/" + FieldGuideAdditions.MODID +
                        "/fieldguide/categories/biomes.json"
        );

        return DataProvider.saveStable(cache, root, path);
    }

    private CompletableFuture<?> saveModdedBiomeCategory(CachedOutput cache) {
        JsonObject root = new JsonObject();

        root.addProperty("sort_index", 2);
        root.addProperty("icon", "minecraft:grass_block");

        JsonArray contents = new JsonArray();

        root.add("contents", contents);

        Path path = output.getOutputFolder().resolve(
                "data/" + FieldGuideAdditions.MODID +
                        "/fieldguide/categories/modded_biomes.json"
        );

        return DataProvider.saveStable(cache, root, path);
    }

    private CompletableFuture<?> saveBiomeUnlock(CachedOutput cache, String biome) {
        JsonObject root = new JsonObject();

        JsonArray entries = new JsonArray();

        JsonObject entry = new JsonObject();

        JsonArray biomes = new JsonArray();
        biomes.add("minecraft:" + biome);

        entry.add("biomes", biomes);
        entry.addProperty("unlock", FieldGuideAdditions.MODID + ":" + biome);

        entries.add(entry);

        root.add("entries", entries);

        Path path = output.getOutputFolder().resolve(
                "data/" + FieldGuideAdditions.MODID +
                        "/fieldguide_additions/biome_unlocks/" +
                        biome + ".json"
        );

        return DataProvider.saveStable(cache, root, path);
    }

    @Override
    public String getName() {
        return "Field Guide Additions Biome Unlocks";
    }
}