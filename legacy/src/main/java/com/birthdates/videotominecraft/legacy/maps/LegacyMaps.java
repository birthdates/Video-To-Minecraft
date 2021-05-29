package com.birthdates.videotominecraft.legacy.maps;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapView;

public class LegacyMaps {

    public static ItemStack getMapItem(MapView mapView) {
        return new ItemStack(Material.MAP, 1, mapView.getId());
    }

}
