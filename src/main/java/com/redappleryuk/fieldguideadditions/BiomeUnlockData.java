package com.redappleryuk.fieldguideadditions;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class BiomeUnlockData {
    private static final Map<ResourceLocation, List<ResourceLocation>> BIOME_TO_ENTRIES = new HashMap<>();
    private static final Map<TagKey<Biome>, List<ResourceLocation>> BIOME_TAG_TO_ENTRIES = new HashMap<>();

    public static void loadBiomeUnlocks(ResourceManager resourceManager) {
        BIOME_TO_ENTRIES.clear();
        BIOME_TAG_TO_ENTRIES.clear();

        Map<ResourceLocation, Resource> files = resourceManager.listResources(
                "fieldguide_additions/biome_unlocks",
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

                    ResourceLocation unlock = new ResourceLocation(entry.get("unlock").getAsString());

                    if (entry.has("biomes")) {
                        JsonArray biomes = entry.getAsJsonArray("biomes");

                        for (int b = 0; b < biomes.size(); b++) {
                            ResourceLocation biome = new ResourceLocation(biomes.get(b).getAsString());

                            BIOME_TO_ENTRIES
                                    .computeIfAbsent(biome, key -> new ArrayList<>())
                                    .add(unlock);
                        }
                    }

                    if (entry.has("biome_tags")) {
                        JsonArray biomeTags = entry.getAsJsonArray("biome_tags");

                        for (int t = 0; t < biomeTags.size(); t++) {
                            ResourceLocation tagId = new ResourceLocation(biomeTags.get(t).getAsString());
                            TagKey<Biome> tagKey = TagKey.create(Registries.BIOME, tagId);

                            BIOME_TAG_TO_ENTRIES
                                    .computeIfAbsent(tagKey, key -> new ArrayList<>())
                                    .add(unlock);
                        }
                    }
                }

                FieldGuideAdditions.LOGGER.info("Loaded biome unlock file: {}", file.getKey());

            } catch (Exception e) {
                FieldGuideAdditions.LOGGER.error("Failed to load biome unlock file: {}", file.getKey(), e);
            }
        }

        FieldGuideAdditions.LOGGER.info(
                "Loaded {} biome unlock mappings and {} biome tag unlock mappings",
                BIOME_TO_ENTRIES.size(),
                BIOME_TAG_TO_ENTRIES.size()
        );
    }

    public static List<ResourceLocation> getEntriesForBiome(ResourceLocation biome) {
        return BIOME_TO_ENTRIES.getOrDefault(biome, Collections.emptyList());
    }

    public static List<ResourceLocation> getEntriesForBiomeTags(ServerPlayer player, BlockPos pos) {
        List<ResourceLocation> results = new ArrayList<>();

        var biomeHolder = player.serverLevel().getBiome(pos);

        for (Map.Entry<TagKey<Biome>, List<ResourceLocation>> entry : BIOME_TAG_TO_ENTRIES.entrySet()) {
            if (biomeHolder.is(entry.getKey())) {
                results.addAll(entry.getValue());
            }
        }

        return results;
    }
}