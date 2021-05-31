package com.birthdates.videotominecraft.configuration;

import com.birthdates.videotominecraft.VideoToMinecraft;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * Class used to handle the configuration
 */
@Getter
public class Configuration {

    private final FileConfiguration fileConfiguration;

    private int gridSize;
    private long fps;
    private boolean disableMovieActions;
    private double maxWatchDistance;

    public Configuration() {
        VideoToMinecraft.getInstance().saveDefaultConfig();
        fileConfiguration = VideoToMinecraft.getInstance().getConfig();
        try {
            loadValues();
        } catch (Exception exception) {
            System.out.println("Failed to load config:");
            throw exception;
        }
    }

    private void loadValues() {
        gridSize = fileConfiguration.getInt("grid-size");
        fps = fileConfiguration.getLong("fps");
        disableMovieActions = fileConfiguration.getBoolean("movie.disable-actions");
        maxWatchDistance = fileConfiguration.getDouble("movie.max-watch-distance");
    }
}
