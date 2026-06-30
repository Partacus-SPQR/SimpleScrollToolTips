package com.simplescrolltooltips.object;

import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;

public class ScrollSession {
    private int horizontalAmount;
    private int verticalAmount;
    @Nullable
    private Slot lastSlotRendered;

    public int horizontalAmount() {
        return this.horizontalAmount;
    }

    public int verticalAmount() {
        return this.verticalAmount;
    }

    @Nullable
    public Slot lastSlotRendered() {
        return this.lastSlotRendered;
    }

    public ScrollSession horizontalAmount(int horizontalAmount) {
        this.horizontalAmount = horizontalAmount;
        return this;
    }

    public ScrollSession verticalAmount(int verticalAmount) {
        this.verticalAmount = verticalAmount;
        return this;
    }

    public int increaseHorizontalAmount() {
        return ++this.horizontalAmount;
    }

    public int decreaseHorizontalAmount() {
        return --this.horizontalAmount;
    }

    public int plusHorizontalAmount(int amount) {
        return this.horizontalAmount += amount;
    }

    public int increaseVerticalAmount() {
        return ++this.verticalAmount;
    }

    public int decreaseVerticalAmount() {
        return --this.verticalAmount;
    }

    public int plusVerticalAmount(int amount) {
        return this.verticalAmount += amount;
    }

    public ScrollSession resetScrollAmount() {
        this.horizontalAmount = 0;
        this.verticalAmount = 0;
        return this;
    }

    public ScrollSession lastSlotRendered(@Nullable Slot lastSlotRendered) {
        this.lastSlotRendered = lastSlotRendered;
        return this;
    }

    public boolean hasItemRendering() {
        return this.lastSlotRendered() != null && this.lastSlotRendered().hasItem();
    }
}
