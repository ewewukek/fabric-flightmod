package ewewukek.flightmod;

import java.util.function.Supplier;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class ModMenuConfigScreen implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ModMenuConfigScreen::create;
    }

    public static Screen create(Screen parent) {
        return new ConfigScreen(parent);
    }

    public static class ConfigScreen extends Screen {
        public static final Text TITLE = new TranslatableText("flightmod.options.title");
        public static final Text SINGLEPLAYER = new TranslatableText("flightmod.options.for.singleplayer");
        public static final Text MOVEMENT_MODE = new TranslatableText("flightmod.options.movement_mode");
        public static final Text INERTIA_COMPENSATION = new TranslatableText("flightmod.options.inertia_compensation");
        public static final Text AIR_JUMP_FLY = new TranslatableText("flightmod.options.air_jump_fly");
        public static final Text SNEAK_JUMP_DROP = new TranslatableText("flightmod.options.sneak_jump_drop");

        public static final int HEIGHT_STEP = 40;
        public static final int HEIGHT_START = -2 * HEIGHT_STEP;

        private Screen parent;
        private Text header;
        private OptionButton movementModeButton;

        public ConfigScreen(Screen parent) {
            super(TITLE);
            this.parent = parent;
        }

        @Override
        public void init() {
            super.init();
            Config.setServer(client.getCurrentServerEntry());
            header = new TranslatableText("flightmod.options.for", Config.currentServer != null ? new LiteralText(Config.currentServer) : SINGLEPLAYER);

            int x = width / 2 - 60;
            int y = height / 2 + HEIGHT_START + 20;
            movementModeButton = addDrawableChild(new OptionButton(
                x, y, 120, 20,
                () -> { return new TranslatableText("flightmod.options.movement_mode." + Config.movementMode); },
                (button) -> { Config.movementMode = Config.movementMode.next(); }
            ));
            y += HEIGHT_STEP;
            addDrawableChild(new OptionButton(
                x, y, 120, 20,
                () -> { return new TranslatableText("flightmod.options.inertia_compensation." + Config.inertiaCompensation); },
                (button) -> { Config.inertiaCompensation = Config.inertiaCompensation.next(); }
            ));
            y += HEIGHT_STEP;
            addDrawableChild(new OptionButton(
                x, y, 120, 20,
                () -> { return Config.airJumpFly ? ScreenTexts.ON : ScreenTexts.OFF; },
                (button) -> { Config.airJumpFly = !Config.airJumpFly; }
            ));
            y += HEIGHT_STEP;
            addDrawableChild(new OptionButton(
                x, y, 120, 20,
                () -> { return Config.sneakJumpDrop ? ScreenTexts.ON : ScreenTexts.OFF; },
                (button) -> { Config.sneakJumpDrop = !Config.sneakJumpDrop; }
            ));
            addDrawableChild(new ButtonWidget(x, height - 30, 120, 20, ScreenTexts.DONE, (button) -> {
                Config.save();
                close();
            }));
        }

        @Override
        public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            renderBackground(matrices);
            int x = width / 2;
            drawCenteredText(matrices, textRenderer, title, x, 10, 0xffffff);
            int y = height / 2 + HEIGHT_START + 6;
            drawCenteredText(matrices, textRenderer, header, x, y - 24, 0xffffff);
            drawCenteredText(matrices, textRenderer, MOVEMENT_MODE, x, y, 0xffffff);
            y += HEIGHT_STEP;
            drawCenteredText(matrices, textRenderer, INERTIA_COMPENSATION, x, y, 0xffffff);
            y += HEIGHT_STEP;
            drawCenteredText(matrices, textRenderer, AIR_JUMP_FLY, x, y, 0xffffff);
            y += HEIGHT_STEP;
            drawCenteredText(matrices, textRenderer, SNEAK_JUMP_DROP, x, y, 0xffffff);
            super.render(matrices, mouseX, mouseY, delta);
            if (Config.currentServer != null && Config.movementMode.fullSpeed()) {
                drawCenteredText(matrices, textRenderer, movementModeButton.getMessage(),
                movementModeButton.x + movementModeButton.getWidth() / 2,
                movementModeButton.y + (movementModeButton.getHeight() - 8) / 2, 0xff5555);
            }
        }

        @Override
        public void removed() {
            Config.save();
        }

        @Override
        public void close() {
            client.setScreen(parent);
        }

        public class OptionButton extends ButtonWidget {
            Supplier<Text> textSupplier;
            PressAction onPress;

            public OptionButton(int x, int y, int width, int height, Supplier<Text> textSupplier, PressAction onPress) {
                super(x, y, width, height, textSupplier.get(), (button) -> {
                    onPress.onPress(button);
                    button.setMessage(textSupplier.get());
                });
                this.textSupplier = textSupplier;
                this.onPress = onPress;
            }
        }
    }
}
