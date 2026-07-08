package com.simplescrolltooltips.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simplescrolltooltips.SimpleScrollToolTipsClient;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Captures the mouse wheel for ANY open screen (not just AbstractContainerScreen),
 * by wrapping the single point where MouseHandler dispatches scroll events to the
 * current screen. This is what makes scrolling work in custom mod GUIs
 * (e.g. profile viewers) that don't extend the vanilla container screen.
 *
 * The screen's own handling still runs first; we never consume the event.
 */
@Mixin(MouseHandler.class)
public class MixinMouseHandler {

    @WrapOperation(
        method = "onScroll",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screens/Screen;mouseScrolled(DDDD)Z"
        )
    )
    private boolean simplescrolltooltips$scrollTooltip(Screen screen, double mouseX, double mouseY, double horizontalAmount, double verticalAmount, Operation<Boolean> original) {
        boolean handled = original.call(screen, mouseX, mouseY, horizontalAmount, verticalAmount);

        SimpleScrollToolTipsClient main = SimpleScrollToolTipsClient.getInstance();
        if (main != null) {
            main.onScreenScroll(screen, mouseX, mouseY, horizontalAmount, verticalAmount);
        }
        return handled;
    }
}
