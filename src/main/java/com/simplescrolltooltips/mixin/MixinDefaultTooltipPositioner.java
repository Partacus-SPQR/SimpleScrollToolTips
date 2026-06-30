package com.simplescrolltooltips.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.simplescrolltooltips.SimpleScrollToolTipsClient;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Applies the scroll offset to the FINAL (already screen-clamped) tooltip position.
 *
 * Item tooltips in container screens go through extractTooltip -> setTooltipForNextFrame,
 * which positions them with DefaultTooltipPositioner.INSTANCE. That positioner clamps the
 * result to the screen, so shifting the input mouse coords can't move a tooltip that's
 * taller/wider than the screen. Offsetting the return value here lets it move off-screen,
 * which is what makes scrolling oversized tooltips actually work.
 */
@Mixin(DefaultTooltipPositioner.class)
public class MixinDefaultTooltipPositioner {

    @ModifyReturnValue(method = "positionTooltip(IIIIII)Lorg/joml/Vector2ic;", at = @At("RETURN"))
    private Vector2ic simplescrolltooltips$applyScroll(Vector2ic original) {
        SimpleScrollToolTipsClient main = SimpleScrollToolTipsClient.getInstance();
        if (main == null || !main.config().enable()) return original;

        int hx = main.scrollSession().horizontalAmount() * main.config().sensitivity();
        int vy = main.scrollSession().verticalAmount() * main.config().sensitivity();
        if (main.config().invertHorizontal()) hx = -hx;
        if (main.config().invertVertical()) vy = -vy;
        if (hx == 0 && vy == 0) return original;

        return new Vector2i(original.x() + hx, original.y() + vy);
    }
}
