package cc.brainbox.AntiGrief;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.LightningStrikeEvent;

public class WeatherEvent implements Listener {
    private final ProtectedChecker pc = new ProtectedChecker();

    /**
     * Prevent lightning strikes on items in protected areas
     * @param event
     */
    @EventHandler
    public void onLightningStrike(LightningStrikeEvent event) {
        Location l = event.getLightning().getLocation();
        if (pc.isProtected(l.getBlockX(), l.getBlockZ(), event.getWorld())) {
            event.setCancelled(true);
        }
    }
}
