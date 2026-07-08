package com.simplescrolltooltips.mixin;

import com.simplescrolltooltips.SimpleScrollToolTipsClient;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Arrow-key scrolling for ANY screen. AbstractContainerScreen.keyPressed calls
 * super.keyPressed, and custom mod screens inherit or super-call this too, so a
 * single hook here covers containers and custom GUIs alike.
 */
@Mixin(Screen.class)
public class MixinScreen {

    @Inject(method = "keyPressed", at = @At("RETURN"))
    private void simplescrolltooltips$keyScroll(KeyEvent keyEvent, CallbackInfoReturnable<Boolean> cir) {
        SimpleScrollToolTipsClient main = SimpleScrollToolTipsClient.getInstance();
        if (main != null) {
            main.onScreenKey((Screen) (Object) this, keyEvent);
        }
    }
}
