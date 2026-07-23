package com.simplescrolltooltips;

import com.simplescrolltooltips.config.SimpleScrollToolTipsConfig;
import com.simplescrolltooltips.object.ScrollSession;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
//? if >=26.1 {
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import com.simplescrolltooltips.compat.ScreenCompat;
//?} else {
/*import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
*///?}
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.Slot;
import org.lwjgl.glfw.GLFW;

public class SimpleScrollToolTipsClient implements ClientModInitializer {
    public static final String MOD_ID = "simplescrolltooltips";
    /** Diagnostics: launch with -Dsimplescrolltooltips.debug=true to trace tooltip tracking and input gating. */
    static final boolean DEBUG = Boolean.getBoolean("simplescrolltooltips.debug");
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger("SimpleScrollToolTips");

    private static SimpleScrollToolTipsClient instance;
    private String lastTooltipDebug;

    private SimpleScrollToolTipsConfig config;
    private ScrollSession scrollSession;

    private KeyMapping keyScrollUp;
    private KeyMapping keyScrollDown;
    private KeyMapping keyScrollLeft;
    private KeyMapping keyScrollRight;

    private Screen previousScreen;

    @Override
    public void onInitializeClient() {
        instance = this;
        this.scrollSession = new ScrollSession();

        this.config = new SimpleScrollToolTipsConfig();
        this.config.init();

        this.registerKeyBindings();
        this.registerTickHandler();
    }

    private void registerKeyBindings() {
        KeyMapping.Category category = new KeyMapping.Category(
            Identifier.fromNamespaceAndPath(MOD_ID, "key_category")
        );

        //? if >=26.1 {
        this.keyScrollUp = KeyMappingHelper.registerKeyMapping(
            new KeyMapping("simplescrolltooltips.key.scroll_up", GLFW.GLFW_KEY_UP, category)
        );
        this.keyScrollDown = KeyMappingHelper.registerKeyMapping(
            new KeyMapping("simplescrolltooltips.key.scroll_down", GLFW.GLFW_KEY_DOWN, category)
        );
        this.keyScrollLeft = KeyMappingHelper.registerKeyMapping(
            new KeyMapping("simplescrolltooltips.key.scroll_left", GLFW.GLFW_KEY_LEFT, category)
        );
        this.keyScrollRight = KeyMappingHelper.registerKeyMapping(
            new KeyMapping("simplescrolltooltips.key.scroll_right", GLFW.GLFW_KEY_RIGHT, category)
        );
        //?} else {
        /*this.keyScrollUp = KeyBindingHelper.registerKeyBinding(
            new KeyMapping("simplescrolltooltips.key.scroll_up", GLFW.GLFW_KEY_UP, category)
        );
        this.keyScrollDown = KeyBindingHelper.registerKeyBinding(
            new KeyMapping("simplescrolltooltips.key.scroll_down", GLFW.GLFW_KEY_DOWN, category)
        );
        this.keyScrollLeft = KeyBindingHelper.registerKeyBinding(
            new KeyMapping("simplescrolltooltips.key.scroll_left", GLFW.GLFW_KEY_LEFT, category)
        );
        this.keyScrollRight = KeyBindingHelper.registerKeyBinding(
            new KeyMapping("simplescrolltooltips.key.scroll_right", GLFW.GLFW_KEY_RIGHT, category)
        );
        *///?}
    }

    private void registerTickHandler() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            //? if >=26.1 {
            Screen currentScreen = ScreenCompat.current(client);
            //?} else {
            /*Screen currentScreen = client.screen;*/
            //?}
            if (this.previousScreen != currentScreen) {
                this.onScreenClosed(this.previousScreen);
                this.previousScreen = currentScreen;
            }
        });
    }

    /**
     * Called from the universal tooltip hook every frame a tooltip is positioned.
     * When a differently-sized tooltip appears outside a container slot (custom
     * mod GUIs), the scroll is auto-reset, mirroring the slot-change reset.
     */
    public void onTooltipPositioned(int screenWidth, int screenHeight, int tooltipWidth, int tooltipHeight, int baseX, int baseY) {
        boolean newTooltip = this.scrollSession.trackTooltip(screenWidth, screenHeight, tooltipWidth, tooltipHeight, baseX, baseY);
        if (newTooltip && this.config.autoReset() && !this.scrollSession.hasItemRendering()) {
            this.scrollSession.resetScrollAmount();
        }
        if (DEBUG) {
            String state = "tooltip base=" + baseX + "," + baseY + " size=" + tooltipWidth + "x" + tooltipHeight
                + " screen=" + screenWidth + "x" + screenHeight
                + " overflow=" + this.scrollSession.isTooltipOverflowing()
                + " offset=" + this.scrollSession.horizontalAmount() + "," + this.scrollSession.verticalAmount()
                + (newTooltip ? " NEW" : "");
            if (!state.equals(this.lastTooltipDebug)) {
                LOGGER.info("[SSTT-debug] {}", state);
                this.lastTooltipDebug = state;
            }
        }
    }

    /** Wheel event observed while a screen is open (called for every screen type). */
    public void onScreenScroll(Screen screen, double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        boolean scrollable = isScrollableContext();
        if (DEBUG) {
            LOGGER.info("[SSTT-debug] wheel dx={} dy={} screen={} gate={} (slot={} visible={} overflow={} offset={})",
                horizontalAmount, verticalAmount, screen.getClass().getName(), scrollable,
                this.scrollSession.hasItemRendering(), this.scrollSession.isTooltipVisible(),
                this.scrollSession.isTooltipOverflowing(), this.scrollSession.hasScrollOffset());
        }
        if (scrollable) {
            handleMouseScroll(mouseX, mouseY, horizontalAmount, verticalAmount);
        }
    }

    /** Key event observed on any screen (called from the Screen keyPressed hook). */
    public void onScreenKey(Screen screen, KeyEvent keyEvent) {
        boolean scrollable = isScrollableContext();
        if (DEBUG) {
            LOGGER.info("[SSTT-debug] key screen={} gate={} (slot={} visible={} overflow={} offset={})",
                screen.getClass().getName(), scrollable,
                this.scrollSession.hasItemRendering(), this.scrollSession.isTooltipVisible(),
                this.scrollSession.isTooltipOverflowing(), this.scrollSession.hasScrollOffset());
        }
        if (scrollable) {
            handleKeyScroll(keyEvent);
        }
    }

    /**
     * Whether scroll/key input should currently move a tooltip: any hovered
     * container slot, or any visible tooltip on any screen. With the
     * overflow_only config enabled, tooltips outside containers only scroll
     * when they stick out past a screen edge (for users who don't want the
     * wheel shared with GUIs that scroll their own content).
     */
    public boolean isScrollableContext() {
        if (!this.config.enable()) return false;
        if (this.scrollSession.hasItemRendering()) return true;
        if (!this.scrollSession.isTooltipVisible()) return false;
        if (!this.config.overflowOnly()) return true;
        return this.scrollSession.isTooltipOverflowing() || this.scrollSession.hasScrollOffset();
    }

    public void onSlotHover(Slot slot) {
        if (slot != this.scrollSession.lastSlotRendered()) {
            this.scrollSession.lastSlotRendered(slot);
            if (this.config.autoReset()) {
                this.scrollSession.resetScrollAmount();
            }
        }
    }

    private void onScreenClosed(Screen oldScreen) {
        if (oldScreen != null) {
            this.scrollSession.lastSlotRendered(null);
            this.scrollSession.resetScrollAmount();
        }
    }

    public void handleMouseScroll(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        boolean shiftDown = isShiftDown();

        if (horizontalAmount != 0.0) {
            int direction = (int) Math.signum(horizontalAmount);
            if (this.config.invertHorizontal()) direction = -direction;
            if (!shiftDown) {
                this.scrollSession.plusHorizontalAmount(direction);
            } else {
                this.scrollSession.plusVerticalAmount(direction);
            }
        }

        if (verticalAmount != 0.0) {
            int direction = (int) Math.signum(verticalAmount);
            if (this.config.invertVertical()) direction = -direction;
            if (!shiftDown) {
                this.scrollSession.plusVerticalAmount(direction);
            } else {
                this.scrollSession.plusHorizontalAmount(direction);
            }
        }
    }

    public void handleKeyScroll(KeyEvent keyEvent) {
        if (this.keyScrollUp.matches(keyEvent)) {
            this.scrollSession.increaseVerticalAmount();
        } else if (this.keyScrollDown.matches(keyEvent)) {
            this.scrollSession.decreaseVerticalAmount();
        } else if (this.keyScrollLeft.matches(keyEvent)) {
            this.scrollSession.increaseHorizontalAmount();
        } else if (this.keyScrollRight.matches(keyEvent)) {
            this.scrollSession.decreaseHorizontalAmount();
        }
    }

    private static boolean isShiftDown() {
        long window = org.lwjgl.glfw.GLFW.glfwGetCurrentContext();
        return org.lwjgl.glfw.GLFW.glfwGetKey(window, GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS
            || org.lwjgl.glfw.GLFW.glfwGetKey(window, GLFW.GLFW_KEY_RIGHT_SHIFT) == GLFW.GLFW_PRESS;
    }

    public static SimpleScrollToolTipsClient getInstance() {
        return instance;
    }

    public SimpleScrollToolTipsConfig config() {
        return this.config;
    }

    public ScrollSession scrollSession() {
        return this.scrollSession;
    }

    /** Tooltip render scale (independent of the global GUI scale). */
    public float tooltipScale() {
        return (float) this.config.tooltipScale();
    }
}
