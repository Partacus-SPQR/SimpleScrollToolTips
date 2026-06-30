package com.simplescrolltooltips.mixin;

import com.simplescrolltooltips.SimpleScrollToolTipsClient;
//? if <26.1 {
/*import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(GuiGraphics.class)
public class MixinGuiGraphics {

    @ModifyVariable(
        method = "renderTooltip",
        at = @At("STORE"),
        ordinal = 0
    )
    private int modifyTooltipX(int x) {
        SimpleScrollToolTipsClient main = SimpleScrollToolTipsClient.getInstance();
        if (main == null || !main.config().enable()) return x;
        int offset = main.scrollSession().horizontalAmount() * main.config().sensitivity();
        if (main.config().invertHorizontal()) offset = -offset;
        return x + offset;
    }

    @ModifyVariable(
        method = "renderTooltip",
        at = @At("STORE"),
        ordinal = 1
    )
    private int modifyTooltipY(int y) {
        SimpleScrollToolTipsClient main = SimpleScrollToolTipsClient.getInstance();
        if (main == null || !main.config().enable()) return y;
        int offset = main.scrollSession().verticalAmount() * main.config().sensitivity();
        if (main.config().invertVertical()) offset = -offset;
        return y + offset;
    }
}
*///?}
//? if >=26.1 {
public final class MixinGuiGraphics {
    private MixinGuiGraphics() {}
}
//?}
