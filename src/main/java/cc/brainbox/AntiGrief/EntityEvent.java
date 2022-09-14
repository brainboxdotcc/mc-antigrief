package cc.brainbox.AntiGrief;

import io.papermc.paper.event.entity.EntityMoveEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntitySpawnEvent;

public class EntityEvent implements Listener {

    private final ProtectedChecker pc = new ProtectedChecker();

    /**
     * Prevent PVP in safe zones by cancelling entity damage when caused by a player against a player.
     * Still allows combat with NPC mobs.
     * @param event
     */
    @EventHandler
    public void onTestEntityDamage(EntityDamageByEntityEvent event) {
        Location l = event.getEntity().getLocation();
        if (pc.isProtected(l.getBlockX(), l.getBlockZ(), event.getEntity().getWorld())) {
            if (event.getEntity() instanceof Player && event.getDamager() instanceof Player p) {
                event.setCancelled(true);
                p.sendMessage(ChatColor.RED + "You cannot PVP inside a safe zone!");
            }
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
