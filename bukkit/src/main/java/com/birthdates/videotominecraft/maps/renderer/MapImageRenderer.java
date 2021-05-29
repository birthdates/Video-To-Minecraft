package com.birthdates.videotominecraft.maps.renderer;

import com.birthdates.videotominecraft.maps.Maps;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

/**
 * Class used to render images quickly to a map canvas
 */
public class MapImageRenderer extends MapRenderer {

    private MapView mapView;
    private MapCanvas mapCanvas;
    private boolean imageDrawn;

    /**
     * Draw raw pixels to the canvas
     *
     * @param pixels {@link Maps#getResolution()}x{@link Maps#getResolution()} pixel array
     */
    public void drawRawPixels(byte[] pixels) {
        int mapRes = Maps.getResolution();
        for (int x = 0; x < mapRes; ++x) {
            for (int y = 0; y < mapRes; ++y) {
                mapCanvas.setPixel(x, y, pixels[y * mapRes + x]);
            }
        }
        imageDrawn = true;
    }

    public void sendToPlayer(Player player) {
        if (!imageDrawn)
            return;
        player.sendMap(mapView);
    }

    @Override
    public void render(MapView mapView, MapCanvas mapCanvas, Player player) {
        this.mapCanvas = mapCanvas;
        this.mapView = mapView;
    }
}
