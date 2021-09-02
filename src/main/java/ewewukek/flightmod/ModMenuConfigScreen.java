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
        public static final Text CONSERVATIVE_MODE_ON = new TranslatableText("flightmod.options.conservative_mode.on");
        public static final Text CONSERVATIVE_MODE_OFF = new TranslatableText("flightmod.options.conservative_mode.off");

        private Screen parent;
        private MultilineText conservativeModeOnDescription = MultilineText.EMPTY;
        private MultilineText conservativeModeOffDescription = MultilineText.EMPTY;

        public ConfigScreen(Screen parent) {
            super(TITLE);
            this.parent = parent;
        }

        @Override
        public void init() {
            super.init();
            addButton(new ButtonWidget(
                width / 2 - 75, 30, 150, 20,
                Config.conservativeMode ? CONSERVATIVE_MODE_ON : CONSERVATIVE_MODE_OFF,
                (button) -> {
                    Config.conservativeMode = !Config.conservativeMode;
                    button.setMessage(Config.conservativeMode ? CONSERVATIVE_MODE_ON : CONSERVATIVE_MODE_OFF);
                }
            ));
            addButton(new ButtonWidget(width / 2 - 75, height - 30, 150, 20, ScreenTexts.DONE, (button) -> {
                Config.save();
                onClose();
            }));
            conservativeModeOnDescription = MultilineText.create(textRenderer,
                new TranslatableText("flightmod.options.conservative_mode.on.description"), width - 50);
            conservativeModeOffDescription = MultilineText.create(textRenderer,
                new TranslatableText("flightmod.options.conservative_mode.off.description"), width - 50);
        }

        @Override
        public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            renderBackground(matrices);
            drawCenteredText(matrices, textRenderer, title, width / 2, 10, 0xffffff);
            if (Config.conservativeMode) {
                conservativeModeOnDescription.drawCenterWithShadow(matrices, width / 2, 60);
            } else {
                conservativeModeOffDescription.drawCenterWithShadow(matrices, width / 2, 60);
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
