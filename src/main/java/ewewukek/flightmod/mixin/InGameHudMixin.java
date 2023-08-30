package ewewukek.flightmod.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import ewewukek.flightmod.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    private static final Text warningText = Text.translatable("flightmod.low_food_warning");

    @Inject(
        method = "render(Lnet/minecraft/client/gui/DrawContext;F)V",
        at = @At(value = "TAIL")
    )
    void renderLowSaturationWarning(DrawContext context, float tickDelta, CallbackInfo ci) {
        InGameHud hud = (InGameHud)(Object)this;

        MinecraftClient client = hud.client;
        ClientPlayerEntity player = client.player;

        if (player.getAbilities().invulnerable) return;

        if (player.getHungerManager().getFoodLevel() <= Config.foodLevelWarning) {
            int x = (hud.scaledWidth - hud.getTextRenderer().getWidth(warningText)) / 2;
            int y = hud.scaledHeight / 2 - 15;
            context.drawTextWithShadow(hud.getTextRenderer(), warningText, x, y, 0xffffffff);
        }
    }
}
