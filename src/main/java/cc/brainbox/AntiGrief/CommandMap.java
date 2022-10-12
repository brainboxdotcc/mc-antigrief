package cc.brainbox.AntiGrief;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;
import org.jetbrains.annotations.NotNull;

public class CommandMap implements CommandExecutor {

    private int LEVEL_4_MAP_SIZE = 2048;

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (commandSender.isOp() && commandSender instanceof Player p) {
            Server server = p.getServer();
            ItemStack i = new ItemStack(Material.FILLED_MAP, 1);
            MapView mapView = server.createMap(p.getWorld());
            mapView.setScale(MapView.Scale.FARTHEST);
            mapView.setTrackingPosition(true);
            int xTemp = (int) Math.floor((p.getLocation().getBlockX() + 64.0) / (double)LEVEL_4_MAP_SIZE);
            int zTemp = (int) Math.floor((p.getLocation().getBlockZ() + 64.0) / (double)LEVEL_4_MAP_SIZE);
            int centerX = xTemp * LEVEL_4_MAP_SIZE + LEVEL_4_MAP_SIZE / 2 - 64;
            int centerZ = zTemp * LEVEL_4_MAP_SIZE + LEVEL_4_MAP_SIZE / 2 - 64;
            mapView.setCenterX(centerX);
            mapView.setCenterZ(centerZ);
            MapMeta meta = (MapMeta) i.getItemMeta();
            if (meta == null) {
                commandSender.sendMessage("Could not add a map to your inventory: Item meta is null");
                return true;
            }
            meta.setMapView(mapView);
            i.setItemMeta(meta);
            try {
                p.getInventory().addItem(i);
            }
            catch (IllegalArgumentException e) {
                commandSender.sendMessage("Could not add a map to your inventory: " + e.getMessage());
                return true;
            }
            commandSender.sendMessage("A level 4 map was added to your inventory");
        } else {
            commandSender.sendMessage("You must be an op and not on the console to execute this command.");
        }
        return true;
    }
}
