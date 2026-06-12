package com.redappleryuk.fieldguideadditions.datagen;

import com.redappleryuk.fieldguideadditions.FieldGuideAdditions;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
        modid = FieldGuideAdditions.MODID,
        bus = Mod.EventBusSubscriber.Bus.MOD
)
public class ModDataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();

        generator.addProvider(
                event.includeClient(),
                new FgaLanguageProvider(output)
        );

        generator.addProvider(
                event.includeServer(),
                new FgaBiomeUnlockProvider(output)
        );

        generator.addProvider(
                event.includeServer(),
                new FgaBiomeFindsProvider(output)
        );
    }
}