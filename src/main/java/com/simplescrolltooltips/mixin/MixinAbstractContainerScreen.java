package com.simplescrolltooltips.mixin;

import com.simplescrolltooltips.SimpleScrollToolTipsClient;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
//? if >=26.1 {
import net.minecraft.client.gui.GuiGraphicsExtractor;
//?} else {
/*import net.minecraft.client.gui.GuiGraphics;
*///?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractContainerScreen.class)
public class MixinAbstractContainerScreen<T extends AbstractContainerMenu> extends Screen {

    @Shadow
    protected Slot hoveredSlot;

    @Unique
    private Slot simplescrolltooltips_previousSlot;

    //? if >=26.1 {
    @Inject(method = "extractTooltip", at = @At("HEAD"))
    private void onExtractTooltip(GuiGraphicsExtractor context, int x, int y, CallbackInfo ci) {
    //?} else {
    /*@Inject(method = "renderTooltip", at = @At("HEAD"))
    private void onRenderTooltip(GuiGraphics guiGraphics, int x, int y, CallbackInfo ci) {
    *///?}
        SimpleScrollToolTipsClient main = SimpleScrollToolTipsClient.getInstance();
        if (main == null) return;

        Slot currentSlot = this.hoveredSlot;
        if (currentSlot != this.simplescrolltooltips_previousSlot) {
            this.simplescrolltooltips_previousSlot = currentSlot;
            main.onSlotHover(currentSlot);
        }
    }

    // The actual tooltip position offset is applied post-clamp in MixinDefaultTooltipPositioner.
    // Modifying the x/y args here can't move tooltips taller/wider than the screen, because
    // the positioner re-clamps them to the screen bounds afterwards.

    @Inject(
        method = "keyPressed",
        at = @At("HEAD")
    )
    private void onKeyPressed(KeyEvent keyEvent, CallbackInfoReturnable<Boolean> cir) {
        SimpleScrollToolTipsClient main = SimpleScrollToolTipsClient.getInstance();
        if (main == null || !main.config().enable()) return;
        if (!main.scrollSession().hasItemRendering()) return;
        main.handleKeyScroll(keyEvent);
    }

    @Inject(
        method = "mouseScrolled",
        at = @At("TAIL")
    )
    private void onMouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount, CallbackInfoReturnable<Boolean> cir) {
        SimpleScrollToolTipsClient main = SimpleScrollToolTipsClient.getInstance();
        if (main == null || !main.config().enable()) return;
        if (!main.scrollSession().hasItemRendering()) return;
        main.handleMouseScroll(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @SuppressWarnings("unused")
    protected MixinAbstractContainerScreen() {
        super(null);
    }
}
