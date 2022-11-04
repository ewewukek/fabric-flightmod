package ewewukek.flightmod;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class ModMenuConfigScreen implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            Config.setServer(MinecraftClient.getInstance().getCurrentServerEntry());

            Text title = Config.currentServer != null ? Text.translatable("flightmod.options.for", Text.literal(Config.currentServer))
                                                      : Text.translatable("flightmod.options.for.singleplayer");
            ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(title);
            ConfigEntryBuilder entryBuilder = builder.entryBuilder();

            ConfigCategory category = builder.getOrCreateCategory(Text.literal("category"));

            if (Config.currentServer == null) {
                category.addEntry(entryBuilder.startBooleanToggle(
                    Text.translatable("flightmod.options.server.enable_flying"), Config.enableFlying)
                    .setSaveConsumer(value -> Config.enableFlying = value)
                    .build());

                category.addEntry(entryBuilder.startBooleanToggle(
                    Text.translatable("flightmod.options.server.enable_fall_damage"), Config.doFallDamage)
                    .setSaveConsumer(value -> Config.doFallDamage = value)
                    .build());

                category.addEntry(entryBuilder.startBooleanToggle(
                    Text.translatable("flightmod.options.server.fly_in_water"), Config.flyInWater)
                    .setSaveConsumer(value -> Config.flyInWater = value)
                    .build());

                category.addEntry(entryBuilder.startBooleanToggle(
                    Text.translatable("flightmod.options.server.fly_in_lava"), Config.flyInLava)
                    .setSaveConsumer(value -> Config.flyInLava = value)
                    .build());

                category.addEntry(entryBuilder.startFloatField(
                    Text.translatable("flightmod.options.server.flying_cost"), Config.flyingCost)
                    .setSaveConsumer(value -> Config.flyingCost = value)
                    .setMin(0).setMax(1)
                    .build());

                category.addEntry(entryBuilder.startFloatField(
                    Text.translatable("flightmod.options.server.flying_horizontal_cost"), Config.flyingHorizontalCost)
                    .setSaveConsumer(value -> Config.flyingHorizontalCost = value)
                    .setMin(0).setMax(1)
                    .build());

                category.addEntry(entryBuilder.startFloatField(
                    Text.translatable("flightmod.options.server.flying_up_cost"), Config.flyingUpCost)
                    .setSaveConsumer(value -> Config.flyingUpCost = value)
                    .setMin(0).setMax(1)
                    .build());

                category.addEntry(entryBuilder.startIntSlider(
                    Text.translatable("flightmod.options.server.food_level_warning"), Config.foodLevelWarning, -1, 10)
                    .setSaveConsumer(value -> Config.foodLevelWarning = value)
                    .build());
            }

            category.addEntry(entryBuilder.startEnumSelector(
                Text.translatable("flightmod.options.client.movement_mode"), Config.MovementMode.class, Config.movementMode)
                .setSaveConsumer(value -> Config.movementMode = value)
                .setEnumNameProvider(value -> Text.translatable("flightmod.options.client.movement_mode." + value))
                .build());

            category.addEntry(entryBuilder.startEnumSelector(
                Text.translatable("flightmod.options.client.inertia_compensation"), Config.InertiaCompensationMode.class, Config.inertiaCompensation)
                .setSaveConsumer(value -> Config.inertiaCompensation = value)
                .setEnumNameProvider(value -> Text.translatable("flightmod.options.client.inertia_compensation." + value))
                .build());

            category.addEntry(entryBuilder.startBooleanToggle(
                Text.translatable("flightmod.options.client.air_jump_fly"), Config.airJumpFly)
                .setSaveConsumer(value -> Config.airJumpFly = value)
                .build());

            category.addEntry(entryBuilder.startBooleanToggle(
                Text.translatable("flightmod.options.client.sneak_jump_drop"), Config.sneakJumpDrop)
                .setSaveConsumer(value -> Config.sneakJumpDrop = value)
                .build());

            builder.setSavingRunnable(() -> {
                Config.save();
            });

            return builder.build();
        };
    }
}
