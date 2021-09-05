package ewewukek.flightmod;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
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
        public static final Text COMPENSATE_INERTIA_ON = new TranslatableText("flightmod.options.compensate_inertia.on");
        public static final Text COMPENSATE_INERTIA_OFF = new TranslatableText("flightmod.options.compensate_inertia.off");
        public static final Text AIR_JUMP_FLY_ON = new TranslatableText("flightmod.options.air_jump_fly.on");
        public static final Text AIR_JUMP_FLY_OFF = new TranslatableText("flightmod.options.air_jump_fly.off");

        private Screen parent;

        public ConfigScreen(Screen parent) {
            super(TITLE);
            this.parent = parent;
        }

        @Override
        public void init() {
            super.init();
            addButton(new ButtonWidget(
                width / 2 - 100, height / 2 - 35, 200, 20,
                new TranslatableText("flightmod.options.mode." + Config.mode),
                (button) -> {
                    Config.mode = Config.mode.next();
                    button.setMessage(new TranslatableText("flightmod.options.mode." + Config.mode));
                }
            ));
            addButton(new ButtonWidget(
                width / 2 - 100, height / 2 - 10, 200, 20,
                Config.compensateInertia ? COMPENSATE_INERTIA_ON : COMPENSATE_INERTIA_OFF,
                (button) -> {
                    Config.compensateInertia = !Config.compensateInertia;
                    button.setMessage(Config.compensateInertia ? COMPENSATE_INERTIA_ON : COMPENSATE_INERTIA_OFF);
                }
            ));
            addButton(new ButtonWidget(
                width / 2 - 100, height / 2 + 15, 200, 20,
                Config.airJumpFly ? AIR_JUMP_FLY_ON : AIR_JUMP_FLY_OFF,
                (button) -> {
                    Config.airJumpFly = !Config.airJumpFly;
                    button.setMessage(Config.airJumpFly ? AIR_JUMP_FLY_ON : AIR_JUMP_FLY_OFF);
                }
            ));
            addButton(new ButtonWidget(width / 2 - 75, height - 30, 150, 20, ScreenTexts.DONE, (button) -> {
                Config.save();
                onClose();
            }));
        }

        @Override
        public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            renderBackground(matrices);
            drawCenteredText(matrices, textRenderer, title, width / 2, 10, 0xffffff);
            super.render(matrices, mouseX, mouseY, delta);
        }

        @Override
        public void removed() {
            Config.save();
        }

        @Override
        public void onClose() {
            client.openScreen(parent);
        }
    }
}
