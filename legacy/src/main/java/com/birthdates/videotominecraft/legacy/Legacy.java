package com.birthdates.videotominecraft.legacy;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapView;

public class Legacy {

    public static ItemStack getMapItem(MapView mapView) {
        return new ItemStack(Material.MAP, 1, mapView.getId());
    }

    public static boolean isMainHandAir(Player player) {
        return player.getItemInHand() != null && player.getItemInHand().getType() == Material.AIR;
    }

    public static void setItemInHand(Player player, ItemStack itemStack) {
        player.setItemInHand(itemStack);
    }

}
