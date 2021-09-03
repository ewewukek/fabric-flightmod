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
        public static final Text VANILLA_VERTICAL_VELOCITY_ON = new TranslatableText("flightmod.options.vanilla_vertical_velocity.on");
        public static final Text VANILLA_VERTICAL_VELOCITY_OFF = new TranslatableText("flightmod.options.vanilla_vertical_velocity.off");

        private Screen parent;

        public ConfigScreen(Screen parent) {
            super(TITLE);
            this.parent = parent;
        }

        @Override
        public void init() {
            super.init();
            addButton(new ButtonWidget(
                width / 2 - 100, height / 2 - 25, 200, 20,
                Config.compensateInertia ? COMPENSATE_INERTIA_ON : COMPENSATE_INERTIA_OFF,
                    (button) -> {
                        Config.compensateInertia = !Config.compensateInertia;
                        button.setMessage(Config.compensateInertia ? COMPENSATE_INERTIA_ON : COMPENSATE_INERTIA_OFF);
                    }
                ));
            addButton(new ButtonWidget(
                width / 2 - 100, height / 2 + 5, 200, 20,
                Config.vanillaVerticalVelocity ? VANILLA_VERTICAL_VELOCITY_ON : VANILLA_VERTICAL_VELOCITY_OFF,
                (button) -> {
                    Config.vanillaVerticalVelocity = !Config.vanillaVerticalVelocity;
                    button.setMessage(Config.vanillaVerticalVelocity ? VANILLA_VERTICAL_VELOCITY_ON : VANILLA_VERTICAL_VELOCITY_OFF);
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
