package com.redappleryuk.fieldguideadditions.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.redappleryuk.fieldguideadditions.FieldGuideAdditions;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class FgaBiomeFindsProvider implements DataProvider {

    private final PackOutput output;

    public FgaBiomeFindsProvider(PackOutput output) {
        this.output = output;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        JsonObject root = new JsonObject();
        JsonArray additions = new JsonArray();

        for (Map.Entry<String, List<String>> biomeEntry : FgaBiomeFinds.BIOME_FINDS.entrySet()) {
            String biome = biomeEntry.getKey();

            for (String value : biomeEntry.getValue()) {
                JsonObject addition = new JsonObject();

                addition.addProperty("entry", FieldGuideAdditions.MODID + ":" + biome);
                addition.addProperty("value", value);

                additions.add(addition);
            }
        }

        root.add("additions", additions);

        Path path = output.getOutputFolder().resolve(
                "data/" + FieldGuideAdditions.MODID +
                        "/fieldguide/loot_modifiers/biome_finds.json"
        );

        return DataProvider.saveStable(cache, root, path);
    }

    @Override
    public String getName() {
        return "Field Guide Additions Biome Finds";
    }
}