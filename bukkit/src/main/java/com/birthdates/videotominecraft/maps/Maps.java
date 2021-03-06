package com.birthdates.videotominecraft.maps;

import com.birthdates.videotominecraft.legacy.Legacy;
import com.birthdates.videotominecraft.maps.renderer.MapImageRenderer;
import com.birthdates.videotominecraft.utils.Versioning;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;

@UtilityClass
public class Maps {

    @Getter
    private final String mapName = ChatColor.GRAY + "Video";
    @Getter
    private final int resolution = 128;

    /**
     * This field is the bit needed to manipulate a number to the resolution (i.e i << bit should be resolution)
     */
    @Getter
    private final int bit = 7;

    public ItemStack createMap(Player player, World world, MapImageRenderer imageRenderer) {
        MapView mapView = Bukkit.createMap(world);
        boolean legacy = Versioning.isMapLegacy(); // < 1.13
        mapView.getRenderers().forEach(mapView::removeRenderer);
        mapView.addRenderer(imageRenderer);

        ItemStack map = legacy ? Legacy.getMapItem(mapView) : new ItemStack(Material.FILLED_MAP);
        MapMeta mapMeta = (MapMeta) map.getItemMeta();
        mapMeta.setScaling(false);
        mapMeta.setDisplayName(mapName);

        if (!legacy)
            mapMeta.setMapView(mapView);
        map.setItemMeta(mapMeta);
        if (player != null) player.sendMap(mapView);
        return map;
    }
}
