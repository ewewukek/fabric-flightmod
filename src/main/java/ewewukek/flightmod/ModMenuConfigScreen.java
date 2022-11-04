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
                    .setDefaultValue(Config.ENABLE_FLYING_DEFAULT)
                    .build());

                category.addEntry(entryBuilder.startBooleanToggle(
                    Text.translatable("flightmod.options.server.enable_fall_damage"), Config.doFallDamage)
                    .setSaveConsumer(value -> Config.doFallDamage = value)
                    .setDefaultValue(Config.DO_FALL_DAMAGE_DEFAULT)
                    .build());

                category.addEntry(entryBuilder.startBooleanToggle(
                    Text.translatable("flightmod.options.server.fly_in_water"), Config.flyInWater)
                    .setSaveConsumer(value -> Config.flyInWater = value)
                    .setDefaultValue(Config.FLY_IN_WATER_DEFAULT)
                    .build());

                category.addEntry(entryBuilder.startBooleanToggle(
                    Text.translatable("flightmod.options.server.fly_in_lava"), Config.flyInLava)
                    .setSaveConsumer(value -> Config.flyInLava = value)
                    .setDefaultValue(Config.FLY_IN_LAVA_DEFAULT)
                    .build());

                category.addEntry(entryBuilder.startFloatField(
                    Text.translatable("flightmod.options.server.flying_cost"), Config.flyingCost)
                    .setSaveConsumer(value -> Config.flyingCost = value)
                    .setDefaultValue(Config.FLYING_COST_DEFAULT)
                    .setMin(0).setMax(1)
                    .build());

                category.addEntry(entryBuilder.startFloatField(
                    Text.translatable("flightmod.options.server.flying_horizontal_cost"), Config.flyingHorizontalCost)
                    .setSaveConsumer(value -> Config.flyingHorizontalCost = value)
                    .setMin(0).setMax(1)
                    .setDefaultValue(Config.FLYING_HORIZONTAL_COST_DEFAULT)
                    .build());

                category.addEntry(entryBuilder.startFloatField(
                    Text.translatable("flightmod.options.server.flying_up_cost"), Config.flyingUpCost)
                    .setSaveConsumer(value -> Config.flyingUpCost = value)
                    .setMin(0).setMax(1)
                    .setDefaultValue(Config.FLYING_UP_COST_DEFAULT)
                    .build());

                category.addEntry(entryBuilder.startIntSlider(
                    Text.translatable("flightmod.options.server.food_level_warning"), Config.foodLevelWarning, -1, 10)
                    .setSaveConsumer(value -> Config.foodLevelWarning = value)
                    .setDefaultValue(Config.FOOD_LEVEL_WARNING_DEFAULT)
                    .build());
            }

            category.addEntry(entryBuilder.startEnumSelector(
                Text.translatable("flightmod.options.client.movement_mode"), Config.MovementMode.class, Config.movementMode)
                .setSaveConsumer(value -> Config.movementMode = value)
                .setDefaultValue(Config.currentServer == null ? Config.MOVEMENT_MODE_DEFAULT_SINGLEPLAYER : Config.MOVEMENT_MODE_DEFAULT_MULTIPLAYER)
                .setEnumNameProvider(value -> Text.translatable("flightmod.options.client.movement_mode." + value))
                .build());

            category.addEntry(entryBuilder.startBooleanToggle(
                Text.translatable("flightmod.options.client.compensate_side_inertia"), Config.compensateSideInertia)
                .setSaveConsumer(value -> Config.compensateSideInertia = value)
                .setDefaultValue(Config.currentServer == null ? Config.COMPENSATE_SIDE_INERTIA_DEFAULT_SINGLEPLAYER : Config.COMPENSATE_SIDE_INERTIA_DEFAULT_MULTIPLAYER)
                .build());

            category.addEntry(entryBuilder.startBooleanToggle(
                Text.translatable("flightmod.options.client.air_jump_fly"), Config.airJumpFly)
                .setSaveConsumer(value -> Config.airJumpFly = value)
                .setDefaultValue(Config.currentServer == null ? Config.AIR_JUMP_FLY_DEFAULT_SINGLEPLAYER : Config.AIR_JUMP_FLY_DEFAULT_MULTIPLAYER)
                .build());

            category.addEntry(entryBuilder.startBooleanToggle(
                Text.translatable("flightmod.options.client.sneak_jump_drop"), Config.sneakJumpDrop)
                .setDefaultValue(Config.currentServer == null ? Config.SNEAK_JUMP_DROP_DEFAULT_SINGLEPLAYER : Config.SNEAK_JUMP_DROP_DEFAULT_MULTIPLAYER)
                .setSaveConsumer(value -> Config.sneakJumpDrop = value)
                .build());

            builder.setSavingRunnable(() -> {
                Config.save();
            });

            return builder.build();
        };
    }
}
