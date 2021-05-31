package com.birthdates.videotominecraft.maps.renderer;

import com.birthdates.videotominecraft.maps.Maps;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.lang.reflect.Field;

/**
 * Class used to render images quickly to a map canvas
 */
public class MapImageRenderer extends MapRenderer {

    private MapView mapView;
    private MapCanvas mapCanvas;
    private boolean imageDrawn;
    private Field bufferField;

    /**
     * Draw raw pixels to the canvas
     *
     * @param pixels {@link Maps#getResolution()}x{@link Maps#getResolution()} pixel array
     */
    public void drawRawPixels(byte[] pixels) {
        if (trySetBuffer(pixels)) {
            return;
        }
        int mapRes = Maps.getResolution();
        int bit = Maps.getBit();

        for (int x = 0; x < mapRes; ++x) {
            for (int y = 0; y < mapRes; ++y) {
                mapCanvas.setPixel(x, y, pixels[(y << bit) + x]); //y << bit is the same as y * mapRes
            }
        }
        imageDrawn = true;
    }

    /**
     * Instead of using {@link MapCanvas#setPixel(int, int, byte)} which is a repeat of what calculations we've already done,
     * We can use reflection to set the private buffer field that {@link MapCanvas#setPixel(int, int, byte)} changes.
     *
     * @param pixels New pixels
     * @return If we changed the field
     */
    private boolean trySetBuffer(byte[] pixels) {
        if (bufferField == null)
            return false;
        try {
            bufferField.set(mapCanvas, pixels);
        } catch (IllegalAccessException exception) {
            exception.printStackTrace();
            return false;
        }
        imageDrawn = true;
        return true;
    }

    private void getBufferField() {
        try {
            bufferField = mapCanvas.getClass().getDeclaredField("buffer");
            bufferField.setAccessible(true);
        } catch (NoSuchFieldException ignored) {
        }
    }

    public void sendToPlayer(Player player) {
        if (!imageDrawn)
            return;
        player.sendMap(mapView);
    }

    @Override
    public void render(MapView mapView, MapCanvas mapCanvas, Player player) {
        if (this.mapCanvas != null && this.mapView != null)
            return;
        this.mapCanvas = mapCanvas;
        this.mapView = mapView;
        getBufferField();
    }
}
