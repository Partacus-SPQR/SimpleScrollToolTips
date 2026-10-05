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
 * Ctrl + vertical wheel resizes a visible tooltip and consumes the event.
 * All other wheel input keeps the screen's own handling first.
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
        SimpleScrollToolTipsClient main = SimpleScrollToolTipsClient.getInstance();
        if (main != null && main.tryResizeTooltip(verticalAmount)) {
            return true;
        }
        boolean handled = original.call(screen, mouseX, mouseY, horizontalAmount, verticalAmount);

        if (main != null) {
            main.onScreenScroll(screen, mouseX, mouseY, horizontalAmount, verticalAmount);
        }
        return handled;
    }
}
