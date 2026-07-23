package com.simplescrolltooltips.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simplescrolltooltips.SimpleScrollToolTipsClient;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import org.joml.Matrix3x2fStack;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Universal tooltip hook: every tooltip rendered through the vanilla pipeline
 * (containers, custom mod GUIs, widget tooltips - any ClientTooltipPositioner)
 * funnels through GuiGraphicsExtractor.tooltip().
 *
 * We wrap two calls inside that single method:
 *  - positionTooltip(): reserve the SCALED footprint so the positioner clamps and
 *    anchors correctly, then apply the scroll offset AFTER the screen-clamp so
 *    oversized tooltips can move off-screen to reveal their clipped lines.
 *  - pose().pushMatrix(): push a scale transform (pivoted on the tooltip's top-left)
 *    so the tooltip renders at its own scale, independent of the global GUI scale.
 *    The matching popMatrix() at the end of tooltip() cleans it up automatically.
 */
@Mixin(GuiGraphicsExtractor.class)
public class MixinGuiGraphicsExtractor {

    @Unique
    private int simplescrolltooltips$posX;
    @Unique
    private int simplescrolltooltips$posY;
    @Unique
    private float simplescrolltooltips$scale = 1.0f;

    @WrapOperation(
        method = "tooltip",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipPositioner;positionTooltip(IIIIII)Lorg/joml/Vector2ic;"
        )
    )
    private Vector2ic simplescrolltooltips$offsetTooltip(ClientTooltipPositioner positioner, int screenWidth, int screenHeight, int x, int y, int tooltipWidth, int tooltipHeight, Operation<Vector2ic> original) {
        SimpleScrollToolTipsClient main = SimpleScrollToolTipsClient.getInstance();
        boolean active = main != null && main.config().enable();

        float scale = active ? main.tooltipScale() : 1.0f;
        this.simplescrolltooltips$scale = scale;

        // Reserve the on-screen size the tooltip will actually occupy once scaled,
        // so the positioner anchors it to the cursor and clamps it correctly.
        int width = scale == 1.0f ? tooltipWidth : Math.max(1, Math.round(tooltipWidth * scale));
        int height = scale == 1.0f ? tooltipHeight : Math.max(1, Math.round(tooltipHeight * scale));

        Vector2ic pos = original.call(positioner, screenWidth, screenHeight, x, y, width, height);

        int px = pos.x();
        int py = pos.y();
        if (active) {
            main.onTooltipPositioned(screenWidth, screenHeight, width, height, px, py);
            px += main.scrollSession().horizontalAmount() * main.config().sensitivity();
            py += main.scrollSession().verticalAmount() * main.config().sensitivity();
        }

        // Remember the final draw origin for the scale pivot in the pushMatrix wrap.
        this.simplescrolltooltips$posX = px;
        this.simplescrolltooltips$posY = py;

        return (px == pos.x() && py == pos.y()) ? pos : new Vector2i(px, py);
    }

    @WrapOperation(
        method = "tooltip",
        at = @At(
            value = "INVOKE",
            target = "Lorg/joml/Matrix3x2fStack;pushMatrix()Lorg/joml/Matrix3x2fStack;"
        )
    )
    private Matrix3x2fStack simplescrolltooltips$scaleTooltip(Matrix3x2fStack pose, Operation<Matrix3x2fStack> original) {
        Matrix3x2fStack result = original.call(pose);
        float scale = this.simplescrolltooltips$scale;
        if (scale != 1.0f) {
            result.translate(this.simplescrolltooltips$posX, this.simplescrolltooltips$posY);
            result.scale(scale, scale);
            result.translate(-this.simplescrolltooltips$posX, -this.simplescrolltooltips$posY);
        }
        return result;
    }
}
