package ewewukek.flightmod;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import net.minecraft.client.font.MultilineText;
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
        public static final Text VANILLA_VERTICAL_VELOCITY_ON = new TranslatableText("flightmod.options.vanilla_vertical_velocity.on");
        public static final Text VENILLA_VERTICAL_VELOCITY_OFF = new TranslatableText("flightmod.options.vanilla_vertical_velocity.off");

        private Screen parent;
        private MultilineText vanillaVerticalVelocityOnDescription = MultilineText.EMPTY;
        private MultilineText vanillaVerticalVelocityOffDescription = MultilineText.EMPTY;

        public ConfigScreen(Screen parent) {
            super(TITLE);
            this.parent = parent;
        }

        @Override
        public void init() {
            super.init();
            addButton(new ButtonWidget(
                width / 2 - 100, height / 2 - 25, 200, 20,
                Config.vanillaVerticalVelocity ? VANILLA_VERTICAL_VELOCITY_ON : VENILLA_VERTICAL_VELOCITY_OFF,
                (button) -> {
                    Config.vanillaVerticalVelocity = !Config.vanillaVerticalVelocity;
                    button.setMessage(Config.vanillaVerticalVelocity ? VANILLA_VERTICAL_VELOCITY_ON : VENILLA_VERTICAL_VELOCITY_OFF);
                }
            ));
            addButton(new ButtonWidget(width / 2 - 75, height - 30, 150, 20, ScreenTexts.DONE, (button) -> {
                Config.save();
                onClose();
            }));
            vanillaVerticalVelocityOnDescription = MultilineText.create(textRenderer,
                new TranslatableText("flightmod.options.vanilla_vertical_velocity.on.description"), width - 50);
            vanillaVerticalVelocityOffDescription = MultilineText.create(textRenderer,
                new TranslatableText("flightmod.options.vanilla_vertical_velocity.off.description"), width - 50);
        }

        @Override
        public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            renderBackground(matrices);
            drawCenteredText(matrices, textRenderer, title, width / 2, 10, 0xffffff);
            if (Config.vanillaVerticalVelocity) {
                vanillaVerticalVelocityOnDescription.drawCenterWithShadow(matrices, width / 2, height / 2 + 5);
            } else {
                vanillaVerticalVelocityOffDescription.drawCenterWithShadow(matrices, width / 2, height / 2 + 5);
            }
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
