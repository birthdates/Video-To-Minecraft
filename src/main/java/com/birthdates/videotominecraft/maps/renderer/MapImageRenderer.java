package com.birthdates.videotominecraft.maps.renderer;

import com.birthdates.videotominecraft.maps.Maps;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.awt.image.BufferedImage;

/**
 * Class used to render images quickly to a map canvas
 */
public class MapImageRenderer extends MapRenderer {

    private BufferedImage bufferedImage;
    private MapView mapView;
    private MapCanvas mapCanvas;

    /**
     * Use this instead of on {@link #render} for faster update times
     */
    public void update() {
        if (bufferedImage == null)
            return;
        mapCanvas.drawImage(0, 0, bufferedImage);
    }

    public void sendToPlayer(Player player) {
        if (bufferedImage == null)
            return;
        player.sendMap(mapView);
    }

    public void setBufferedImage(BufferedImage image) {
        setBufferedImage(image, true);
    }

    public void setBufferedImage(BufferedImage image, boolean update) {
        if (image == null)
            return;
        this.bufferedImage = image;
        if (!update) return;
        rescaleImage();
        update();
    }

    private boolean isImageInvalidSize(BufferedImage bufferedImage) {
        return bufferedImage != null && (bufferedImage.getWidth() != Maps.getResolution() || bufferedImage.getHeight() != Maps.getResolution());
    }

    private void rescaleImage() {
        if (isImageInvalidSize(bufferedImage))
            bufferedImage = getResizedImage(bufferedImage);
    }

    public BufferedImage getResizedImage(BufferedImage bufferedImage) {
        return isImageInvalidSize(bufferedImage) ? MapPalette.resizeImage(bufferedImage) : bufferedImage;
    }

    @Override
    public void render(MapView mapView, MapCanvas mapCanvas, Player player) {
        this.mapCanvas = mapCanvas;
        this.mapView = mapView;
    }
}
