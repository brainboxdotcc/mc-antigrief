package cc.brainbox.AntiGrief;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Set;

public class PlayerEvent implements Listener {

    /**
     * List of items a non-op is never allowed to pick up
     */
    Set<Material> disallowedMaterials = Set.of(
        Material.BARRIER,
        Material.BEDROCK,
        Material.JIGSAW,
        Material.COMMAND_BLOCK,
        Material.DEBUG_STICK,
        Material.COMMAND_BLOCK_MINECART,
        Material.REPEATING_COMMAND_BLOCK,
        Material.SPAWNER,
        Material.STRUCTURE_BLOCK,
        Material.STRUCTURE_VOID
    );

    /**
     * List of materials a non-op is never allowed to bring into a safe zone
     */
    Set<Material> unsafeMaterials = Set.of(
        Material.GUNPOWDER,
        Material.TNT,
        Material.TNT_MINECART,
        Material.WITHER_SKELETON_SKULL,
        Material.DRAGON_EGG,
        Material.END_CRYSTAL,
        Material.LAVA_BUCKET,
        Material.LAVA_CAULDRON,
        Material.LAVA,
        Material.WATER
    );


    /**
     * Checks if a region is protected or not
     */
    private final ProtectedChecker pc = new ProtectedChecker();

    /**
     * Get a player's name as string
     * @param player Player object
     * @return Name string
     */
    private String getPlayerName(Player player) {
        return PlainTextComponentSerializer.plainText().serialize(player.displayName());
    }

    /**
     * Set a player to adventure mode for safe zones, strip them of any harmful items
     * @param player player to update
     */
    private void setAdventure(Player player) {
        player.setGameMode(GameMode.ADVENTURE);
        player.sendMessage(ChatColor.GREEN + "You are in a safe zone. Please head away from this area for survival mode and PVP.");
        Bukkit.getLogger().info("Entered " + ChatColor.GREEN + "safe zone" + ChatColor.RESET + ": " + this.getPlayerName(player));

        /* Remove items from inventory that are banned in safe zones */
        PlayerInventory pi = player.getInventory();
        for (Material m : unsafeMaterials) {
            pi.remove(m);
        }
    }

    /**
     * Set a player to survival mode on exit of the safe zone, and message them
     * @param player player to update
     */
    private void setSurvival(Player player) {
        player.setGameMode(GameMode.SURVIVAL);
        player.sendMessage(ChatColor.RED + "You are now in survival mode! Good luck!");
        Bukkit.getLogger().info("Left " + ChatColor.GREEN + "safe zone " + ChatColor.RESET + ": " + this.getPlayerName(player));
    }

    /**
     * Check player is in the correct game mode for the zone they are in, if not, update and message them
     * @param player player to check
     */
    private void checkGameMode(Player player) {
        int x = player.getLocation().getBlockX();
        int z = player.getLocation().getBlockZ();

        World world = player.getWorld();
        boolean isProtected = pc.isProtected(x, z, world);
        GameMode gm = player.getGameMode();

        if (gm == GameMode.SURVIVAL || gm == GameMode.ADVENTURE) {
            if (isProtected && gm != GameMode.ADVENTURE) {
                this.setAdventure(player);
            } else if (!isProtected && gm != GameMode.SURVIVAL) {
                this.setSurvival(player);
            }
        }

    }

    /**
     * Check for player picking up items non-ops are now allowed to have
     * @param event attempt pickup event
     */
    @EventHandler
    public void onAttemptPickupItem(PlayerAttemptPickupItemEvent event) {
        ItemStack itemStack = event.getItem().getItemStack();
        if (!event.getPlayer().isOp() && disallowedMaterials.contains(itemStack.getType())) {
                event.getPlayer().sendMessage(ChatColor.DARK_RED + "You are not permitted to pick up " + itemStack.getType() + " blocks");
                event.setCancelled(true);
        }
    }

    /**
     * Check for player movement into and out of safe zones
     * @param event move event
     */
    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();
        if (from.getBlockX() != to.getBlockX() || from.getBlockZ() != to.getBlockZ()) {
            this.checkGameMode(event.getPlayer());
        }

    }

    @EventHandler
    public void onChangeDimension(PlayerChangedWorldEvent event) {
        String name = this.getPlayerName(event.getPlayer());
        Bukkit.getLogger().info("AG: Player Dimension Warp: " + name);
        this.checkGameMode(event.getPlayer());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String name = this.getPlayerName(player);
        int x = player.getLocation().getBlockX();
        int z = player.getLocation().getBlockZ();

        World world = player.getWorld();
        boolean isProtected = pc.isProtected(x, z, world);

        Bukkit.getLogger().info("AG: Player Join: " + name);
        this.checkGameMode(player);

        if (isProtected) {
            player.sendMessage(ChatColor.GREEN + "You are in a safe zone. Please head away from this area for survival mode and PVP.");
        } else {
            player.sendMessage(ChatColor.RED + "You are in the wilderness! PVP and building are enabled.");
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        String name = this.getPlayerName(event.getPlayer());
        Bukkit.getLogger().info("AG: Player Respawn: " + name);
        this.checkGameMode(event.getPlayer());
    }

    @EventHandler
    public void onPlayerGameModeChange(PlayerGameModeChangeEvent event) {
        String name = this.getPlayerName(event.getPlayer());
        Bukkit.getLogger().info("AG: Player GameMode Change: " + name);
        //this.checkGameMode(event.getPlayer());
    }
}
