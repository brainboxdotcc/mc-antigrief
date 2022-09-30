package cc.brainbox.AntiGrief;

import io.papermc.paper.event.entity.EntityMoveEvent;
import io.papermc.paper.event.player.PlayerItemFrameChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntitySpawnEvent;

public class EntityEvent implements Listener {

    private final ProtectedChecker pc = new ProtectedChecker();

    /**
     * Prevent PVP in safe zones by cancelling entity damage when caused by a player against a player.
     * Still allows combat with NPC mobs.
     * @param event damage event
     */
    @EventHandler
    public void onTestEntityDamage(EntityDamageByEntityEvent event) {
        Location l = event.getEntity().getLocation();
        if (pc.isProtected(l.getBlockX(), l.getBlockZ(), event.getEntity().getWorld())) {
            if (
                    event.getDamager() instanceof Player p &&
                    (
                            event.getEntity() instanceof Player ||
                            event.getEntity() instanceof ItemFrame ||
                            event.getEntity() instanceof Villager
                    )
                ) {
                event.setCancelled(true);
                if (event.getEntity() instanceof Player) {
                    p.sendMessage(ChatColor.RED + "Calm down! You cannot PVP inside a " + ChatColor.GREEN + "safe zone" + ChatColor.RED + "!");
                } else if (event.getEntity() instanceof ItemFrame) {
                    p.sendMessage(ChatColor.RED + "Hands off! You cannot destroy item frames inside a " + ChatColor.GREEN + "safe zone" + ChatColor.RED + "!");
                } else if (event.getEntity() instanceof Villager) {
                    p.sendMessage(ChatColor.RED + "Not cool! Don't attack villagers inside a " + ChatColor.GREEN + "safe zone" + ChatColor.RED + "!");
                }
            }
        }
    }

    @EventHandler
    public void onPlayerItemFrameChange(PlayerItemFrameChangeEvent event) {
        Location l = event.getItemFrame().getLocation();
        if (pc.isProtected(l.getBlockX(), l.getBlockZ(), event.getItemFrame().getWorld())) {
            Player p = event.getPlayer();
            event.setCancelled(true);
            p.sendMessage(ChatColor.RED + "You cannot loot item frames inside a " + ChatColor.GREEN + "safe zone" + ChatColor.RED + "!");
        }
    }

    /**
     * Prevent spawn of wither and ender dragon in protected areas, oe zombie pigmen from nether portals in
     * protected areas.
     * @param event entity spawn event
     */
    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        Location l = event.getEntity().getLocation();
        boolean isProtected = pc.isProtected(l.getBlockX(), l.getBlockZ(), event.getEntity().getWorld());
        if (isProtected &&
                (
                    (event.getEntity() instanceof EnderDragon || event.getEntity() instanceof Wither) ||
                    ((event.getEntity() instanceof PigZombie) && event.getEntity().getEntitySpawnReason() == CreatureSpawnEvent.SpawnReason.NETHER_PORTAL)
                )
        ) {
            Bukkit.getLogger().info("AG: Prevented spawn of " + event.getEntity().getName() + " in safe zone");
            event.setCancelled(true);
        }
    }

    /**
     * Prevent movement of wither and ender dragon into protected areas
     * @param event entity movement event
     */
    @EventHandler
    public void onEntityMove(EntityMoveEvent event) {
        Location l = event.getEntity().getLocation();
        if (
                pc.isProtected(l.getBlockX(), l.getBlockZ(), event.getEntity().getWorld())
                        && (event.getEntity() instanceof EnderDragon || event.getEntity() instanceof Wither)
        ) {
            Bukkit.getLogger().info("AG: movement of " + event.getEntity().getName() + " into safe zone");
            event.getEntity().damage(event.getEntity().getHealth());
        }
    }
}
