package com.simplescrolltooltips.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simplescrolltooltips.SimpleScrollToolTipsClient;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Universal tooltip hook: every tooltip rendered through the vanilla pipeline
 * (containers, custom mod GUIs, widget tooltips - any ClientTooltipPositioner)
 * funnels through the single positionTooltip call in GuiGraphicsExtractor.tooltip().
 *
 * Wrapping that call lets us (1) track that a tooltip is on screen and how big it
 * is, and (2) apply the scroll offset AFTER the positioner's screen-clamp, so
 * oversized tooltips can be moved off-screen to reveal their clipped lines.
 */
@Mixin(GuiGraphicsExtractor.class)
public class MixinGuiGraphicsExtractor {

    @WrapOperation(
        method = "tooltip",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipPositioner;positionTooltip(IIIIII)Lorg/joml/Vector2ic;"
        )
    )
    private Vector2ic simplescrolltooltips$offsetTooltip(ClientTooltipPositioner positioner, int screenWidth, int screenHeight, int x, int y, int tooltipWidth, int tooltipHeight, Operation<Vector2ic> original) {
        Vector2ic pos = original.call(positioner, screenWidth, screenHeight, x, y, tooltipWidth, tooltipHeight);

        SimpleScrollToolTipsClient main = SimpleScrollToolTipsClient.getInstance();
        if (main == null || !main.config().enable()) return pos;

        main.onTooltipPositioned(screenWidth, screenHeight, tooltipWidth, tooltipHeight, pos.x(), pos.y());

        int hx = main.scrollSession().horizontalAmount() * main.config().sensitivity();
        int vy = main.scrollSession().verticalAmount() * main.config().sensitivity();
        if (hx == 0 && vy == 0) return pos;

        return new Vector2i(pos.x() + hx, pos.y() + vy);
    }
}
