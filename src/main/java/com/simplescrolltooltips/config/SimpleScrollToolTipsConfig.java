package com.simplescrolltooltips.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.simplescrolltooltips.SimpleScrollToolTipsClient;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.CrashReport;
import net.minecraft.client.Minecraft;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class SimpleScrollToolTipsConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final int VERSION = 1;

    private JsonObject root;

    private static Path getConfigPath() {
        return FabricLoader.getInstance().getConfigDir().resolve(SimpleScrollToolTipsClient.MOD_ID + ".json");
    }

    public void init() {
        Path configPath = getConfigPath();
        if (!Files.exists(configPath)) {
            this.root = new JsonObject();
            this.root.addProperty("version", VERSION);
            this.root.addProperty("enable", true);
            this.root.addProperty("sensitivity", 10);
            this.root.addProperty("auto_reset", true);
            this.root.addProperty("invert_horizontal", false);
            this.root.addProperty("invert_vertical", false);
            this.root.addProperty("overflow_only", false);
            this.save();
        } else if (!Files.isRegularFile(configPath)) {
            Minecraft.getInstance().emergencySaveAndCrash(
                CrashReport.forThrowable(
                    new IOException("config file " + configPath + " is not a regular file."),
                    "SimpleScrollToolTips config error"
                )
            );
        } else {
            try (Reader reader = Files.newBufferedReader(configPath)) {
                this.root = JsonParser.parseReader(reader).getAsJsonObject();
            } catch (IOException e) {
                Minecraft.getInstance().emergencySaveAndCrash(
                    CrashReport.forThrowable(e, "Failed to read config file")
                );
                return;
            }

            if (!this.root.has("version") || this.root.get("version").getAsInt() != VERSION) {
                try {
                    Files.delete(configPath);
                } catch (IOException e) {
                    Minecraft.getInstance().emergencySaveAndCrash(
                        CrashReport.forThrowable(e, "Unable to delete old config file.")
                    );
                    return;
                }
                this.init();
            }
        }
    }

    public void save() {
        try (Writer writer = Files.newBufferedWriter(getConfigPath())) {
            GSON.toJson(this.root, writer);
        } catch (IOException e) {
            Minecraft.getInstance().emergencySaveAndCrash(
                CrashReport.forThrowable(e, "Failed to save config")
            );
        }
    }

    public boolean enable() {
        return this.root.get("enable").getAsBoolean();
    }

    public void enable(boolean enable) {
        this.root.addProperty("enable", enable);
    }

    public int sensitivity() {
        return this.root.get("sensitivity").getAsInt();
    }

    public void sensitivity(int sensitivity) {
        this.root.addProperty("sensitivity", sensitivity);
    }

    public boolean autoReset() {
        return this.root.get("auto_reset").getAsBoolean();
    }

    public void autoReset(boolean autoReset) {
        this.root.addProperty("auto_reset", autoReset);
    }

    public boolean invertHorizontal() {
        return this.root.get("invert_horizontal").getAsBoolean();
    }

    public void invertHorizontal(boolean invert) {
        this.root.addProperty("invert_horizontal", invert);
    }

    public boolean invertVertical() {
        return this.root.get("invert_vertical").getAsBoolean();
    }

    public void invertVertical(boolean invert) {
        this.root.addProperty("invert_vertical", invert);
    }

    /** When true, tooltips outside container slots only scroll if they stick out past a screen edge. */
    public boolean overflowOnly() {
        return this.root.has("overflow_only") && this.root.get("overflow_only").getAsBoolean();
    }

    public void overflowOnly(boolean overflowOnly) {
        this.root.addProperty("overflow_only", overflowOnly);
    }
}
