package com.simplescrolltooltips.config;

import com.simplescrolltooltips.SimpleScrollToolTipsClient;
//? if >=26.1 {
import net.minecraft.client.gui.GuiGraphicsExtractor;
import com.simplescrolltooltips.compat.ScreenCompat;
//?} else {
/*import net.minecraft.client.gui.GuiGraphics;
*///?}
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ConfigScreen extends Screen {
    private final Screen parent;
    private final SimpleScrollToolTipsConfig config;

    public ConfigScreen(Screen parent, SimpleScrollToolTipsConfig config) {
        super(Component.translatable("simplescrolltooltips.config.title"));
        this.parent = parent;
        this.config = config;
    }

    @Override
    protected void init() {
        this.addRenderableWidget(
            Button.builder(
                translateBool("simplescrolltooltips.config.enable", this.config.enable()),
                button -> {
                    boolean newVal = !this.config.enable();
                    this.config.enable(newVal);
                    this.config.save();
                    button.setMessage(translateBool("simplescrolltooltips.config.enable", newVal));
                }
            )
            .pos(this.width / 2 - 100, this.height / 2 - 60)
            .size(200, 20)
            .build()
        );

        this.addRenderableWidget(
            Button.builder(
                translateBool("simplescrolltooltips.config.auto_reset", this.config.autoReset()),
                button -> {
                    boolean newVal = !this.config.autoReset();
                    this.config.autoReset(newVal);
                    this.config.save();
                    button.setMessage(translateBool("simplescrolltooltips.config.auto_reset", newVal));
                }
            )
            .tooltip(Tooltip.create(Component.translatable("simplescrolltooltips.config.auto_reset.tooltip")))
            .pos(this.width / 2 - 100, this.height / 2 - 35)
            .size(200, 20)
            .build()
        );

        this.addRenderableWidget(
            new AbstractSliderButton(
                this.width / 2 - 100, this.height / 2 - 10,
                200, 20,
                Component.translatable("simplescrolltooltips.config.sensitivity", this.config.sensitivity()),
                new BigDecimal(this.config.sensitivity())
                    .subtract(BigDecimal.ONE)
                    .divide(new BigDecimal(99), 2, RoundingMode.UP)
                    .doubleValue()
            ) {
                @Override
                protected void updateMessage() {
                    this.setMessage(
                        Component.translatable(
                            "simplescrolltooltips.config.sensitivity",
                            BigDecimal.valueOf(this.value)
                                .multiply(new BigDecimal(99))
                                .add(BigDecimal.ONE)
                                .setScale(0, RoundingMode.DOWN)
                                .intValue()
                        )
                    );
                }

                @Override
                protected void applyValue() {
                    ConfigScreen.this.config.sensitivity(
                        BigDecimal.valueOf(this.value)
                            .multiply(new BigDecimal(99))
                            .add(BigDecimal.ONE)
                            .setScale(0, RoundingMode.DOWN)
                            .intValue()
                    );
                    ConfigScreen.this.config.save();
                }
            }
        );

        this.addRenderableWidget(
            Button.builder(
                translateBool("simplescrolltooltips.config.invert_x", this.config.invertHorizontal()),
                button -> {
                    boolean newVal = !this.config.invertHorizontal();
                    this.config.invertHorizontal(newVal);
                    this.config.save();
                    button.setMessage(translateBool("simplescrolltooltips.config.invert_x", newVal));
                }
            )
            .pos(this.width / 2 - 150, this.height / 2 + 15)
            .size(150, 20)
            .build()
        );

        this.addRenderableWidget(
            Button.builder(
                translateBool("simplescrolltooltips.config.invert_y", this.config.invertVertical()),
                button -> {
                    boolean newVal = !this.config.invertVertical();
                    this.config.invertVertical(newVal);
                    this.config.save();
                    button.setMessage(translateBool("simplescrolltooltips.config.invert_y", newVal));
                }
            )
            .pos(this.width / 2, this.height / 2 + 15)
            .size(150, 20)
            .build()
        );

        this.addRenderableWidget(
            Button.builder(
                translateBool("simplescrolltooltips.config.overflow_only", this.config.overflowOnly()),
                button -> {
                    boolean newVal = !this.config.overflowOnly();
                    this.config.overflowOnly(newVal);
                    this.config.save();
                    button.setMessage(translateBool("simplescrolltooltips.config.overflow_only", newVal));
                }
            )
            .tooltip(Tooltip.create(Component.translatable("simplescrolltooltips.config.overflow_only.tooltip")))
            .pos(this.width / 2 - 100, this.height / 2 + 40)
            .size(200, 20)
            .build()
        );

        AbstractSliderButton scaleSlider = new AbstractSliderButton(
            this.width / 2 - 100, this.height / 2 + 65,
            200, 20,
            tooltipScaleMessage(this.config.tooltipScale()),
            (this.config.tooltipScale() - SCALE_MIN) / SCALE_RANGE
        ) {
            @Override
            protected void updateMessage() {
                this.setMessage(tooltipScaleMessage(sliderToScale(this.value)));
            }

            @Override
            protected void applyValue() {
                ConfigScreen.this.config.tooltipScale(sliderToScale(this.value));
                ConfigScreen.this.config.save();
            }
        };
        scaleSlider.setTooltip(Tooltip.create(Component.translatable("simplescrolltooltips.config.tooltip_scale.tooltip")));
        this.addRenderableWidget(scaleSlider);

        this.addRenderableWidget(
            Button.builder(
                Component.translatable("simplescrolltooltips.config.done"),
                //? if >=26.1 {
                button -> ScreenCompat.open(Minecraft.getInstance(), this.parent)
                //?} else {
                /*button -> Minecraft.getInstance().setScreen(this.parent)*/
                //?}
            )
            .pos(this.width / 2 - 100, this.height / 2 + 90)
            .size(200, 20)
            .build()
        );
    }

    private static final double SCALE_MIN = 0.25;
    private static final double SCALE_MAX = 2.0;
    private static final double SCALE_RANGE = SCALE_MAX - SCALE_MIN;

    private static double sliderToScale(double sliderValue) {
        double scale = SCALE_MIN + sliderValue * SCALE_RANGE;
        return Math.round(scale / 0.05) * 0.05;
    }

    private static Component tooltipScaleMessage(double scale) {
        return Component.translatable(
            "simplescrolltooltips.config.tooltip_scale",
            String.format(java.util.Locale.ROOT, "%.2f", scale)
        );
    }

    //? if >=26.1 {
    @Override
    public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        super.extractRenderState(context, mouseX, mouseY, delta);
        context.centeredText(
            this.font,
            Component.translatable("simplescrolltooltips.config.title"),
            this.width / 2, 20, 0xFFFFFF
        );
    }
    //?} else {
    /*@Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        super.render(graphics, mouseX, mouseY, delta);
        graphics.drawCenteredString(
            this.font,
            Component.translatable("simplescrolltooltips.config.title"),
            this.width / 2, 20, 0xFFFFFF
        );
    }
    *///?}

    private static Component translateBool(String key, boolean value) {
        Component boolText = value
            ? Component.translatable("simplescrolltooltips.constant.true").withStyle(ChatFormatting.GREEN)
            : Component.translatable("simplescrolltooltips.constant.false").withStyle(ChatFormatting.RED);
        return Component.translatable(key, boolText);
    }
}
