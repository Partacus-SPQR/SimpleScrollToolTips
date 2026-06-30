package com.simplescrolltooltips.compat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

/**
 * Bridges the screen-handling API change introduced in Minecraft 26.2.
 * In 26.2: {@code Minecraft.setScreenAndShow()} and {@code Minecraft.gui.screen()}.
 * Older versions (1.21.x and 26.1) keep the original API.
 */
public final class ScreenCompat {
    private ScreenCompat() {}

    /** Opens {@code screen} (or closes the current screen when {@code null}). */
    public static void open(Minecraft client, Screen screen) {
        //? if >=26.2 {
        client.setScreenAndShow(screen);
        //?} else {
        /*client.setScreen(screen);*/
        //?}
    }

    /** Returns the screen currently open, or {@code null} if none. */
    public static Screen current(Minecraft client) {
        //? if >=26.2 {
        return client.gui.screen();
        //?} else {
        /*return client.screen;*/
        //?}
    }
}
