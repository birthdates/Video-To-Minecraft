package com.birthdates.videotominecraft.legacy;

import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapView;

/**
 * Class to handle legacy functionality
 * Since there is little to transform to a legacy version, no class implementations or interface APIs needed.
 */
@UtilityClass
public class Legacy {

    public ItemStack getMapItem(MapView mapView) {
        return new ItemStack(Material.MAP, 1, mapView.getId());
    }

    public boolean isMainHandAir(Player player) {
        return player.getItemInHand() != null && player.getItemInHand().getType() == Material.AIR;
    }

    public void setItemInHand(Player player, ItemStack itemStack) {
        player.setItemInHand(itemStack);
    }

}
