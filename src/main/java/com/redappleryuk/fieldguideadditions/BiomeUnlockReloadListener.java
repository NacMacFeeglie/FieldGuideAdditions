package com.redappleryuk.fieldguideadditions;

import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class BiomeUnlockReloadListener implements PreparableReloadListener {

    @Override
    public CompletableFuture<Void> reload(
            PreparationBarrier barrier,
            ResourceManager resourceManager,
            ProfilerFiller preparationsProfiler,
            ProfilerFiller reloadProfiler,
            Executor backgroundExecutor,
            Executor gameExecutor
    ) {
        return CompletableFuture
                .runAsync(() -> BiomeUnlockData.loadBiomeUnlocks(resourceManager), backgroundExecutor)
                .thenCompose(barrier::wait);
    }
}