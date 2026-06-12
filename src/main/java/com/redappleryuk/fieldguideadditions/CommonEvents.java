package com.redappleryuk.fieldguideadditions;

import com.evandev.fieldguide.server.ServerFieldGuideManager;
import com.evandev.fieldguide.server.progress.FieldGuideProgressManager;
import com.evandev.fieldguide.server.progress.PlayerFieldGuideProgress;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = FieldGuideAdditions.MODID)
public class CommonEvents {

    private static final Map<UUID, ResourceLocation> LAST_BIOME = new HashMap<>();

    @SubscribeEvent
    public static void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(new BiomeUnlockReloadListener());
        event.addListener(new RecipeEntryReloadListener());
    }


    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!(event.player instanceof ServerPlayer player)) return;

        if (player.tickCount % 100 != 0) return;

        BlockPos pos = player.blockPosition();

        ResourceLocation biomeId = player.serverLevel()
                .registryAccess()
                .registryOrThrow(net.minecraft.core.registries.Registries.BIOME)
                .getKey(player.serverLevel().getBiome(pos).value());

        if (biomeId == null) return;

        ResourceLocation previousBiome = LAST_BIOME.get(player.getUUID());

        if (biomeId.equals(previousBiome)) {
            return;
        }

        LAST_BIOME.put(player.getUUID(), biomeId);

        List<ResourceLocation> entries = new java.util.ArrayList<>();
        entries.addAll(BiomeUnlockData.getEntriesForBiome(biomeId));
        entries.addAll(BiomeUnlockData.getEntriesForBiomeTags(player, pos));

        if (!entries.isEmpty()) {
            PlayerFieldGuideProgress progress =
                    FieldGuideProgressManager.getInstance().getProgress(player);

            if (progress == null) return;

            for (ResourceLocation entry : entries) {
                String entryId = entry.toString();

                if (!ServerFieldGuideManager.getInstance().hasEntry(entry)) {

                    FieldGuideAdditions.LOGGER.warn(
                            "Biome unlock attempted to unlock missing Field Guide entry: {}",
                            entryId
                    );

                    continue;
                }

                if (!progress.isUnlocked(entryId)) {

                    progress.unlock(player, entry, null, true);

                    FieldGuideAdditions.LOGGER.info(
                            "Unlocked Field Guide entry {} for player {} from biome {}",
                            entryId,
                            player.getName().getString(),
                            biomeId
                    );
                }
            }
        }
    }
}