package com.simplescrolltooltips.object;

import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;

public class ScrollSession {
    /** How long after its last rendered frame a tooltip still counts as "on screen". */
    private static final long TOOLTIP_VISIBLE_WINDOW_NANOS = 250_000_000L;

    private int horizontalAmount;
    private int verticalAmount;
    @Nullable
    private Slot lastSlotRendered;

    private long lastTooltipNanos;
    private int tooltipWidth;
    private int tooltipHeight;
    private boolean tooltipOverflowing;

    /**
     * Records that a tooltip was positioned this frame. {@code baseX}/{@code baseY}
     * are the position chosen by the positioner BEFORE our scroll offset, so the
     * overflow check reflects where the tooltip naturally sits.
     *
     * @return true if the tooltip size changed since the last tracked tooltip
     *         (a new tooltip appeared), which callers may use to auto-reset.
     */
    public boolean trackTooltip(int screenWidth, int screenHeight, int tooltipWidth, int tooltipHeight, int baseX, int baseY) {
        long now = System.nanoTime();
        // A "new" tooltip = there was a gap with no tooltip rendered. Deliberately
        // NOT size-based: some mod GUIs report fluctuating sizes for the same
        // tooltip, and a size-based reset would zero the scroll every frame.
        boolean appeared = now - this.lastTooltipNanos > TOOLTIP_VISIBLE_WINDOW_NANOS;
        this.tooltipWidth = tooltipWidth;
        this.tooltipHeight = tooltipHeight;
        this.tooltipOverflowing = baseX < 0 || baseY < 0
            || baseX + tooltipWidth > screenWidth
            || baseY + tooltipHeight > screenHeight;
        this.lastTooltipNanos = now;
        return appeared;
    }

    public boolean isTooltipVisible() {
        return System.nanoTime() - this.lastTooltipNanos < TOOLTIP_VISIBLE_WINDOW_NANOS;
    }

    /** Whether the tooltip's natural (un-scrolled) position sticks out past any screen edge. */
    public boolean isTooltipOverflowing() {
        return this.tooltipOverflowing;
    }

    public boolean hasScrollOffset() {
        return this.horizontalAmount != 0 || this.verticalAmount != 0;
    }

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
