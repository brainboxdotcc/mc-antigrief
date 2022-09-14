package cc.brainbox.AntiGrief;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

/**
 * Checks if a region is protected by looking at position Y=0 or Y=-63 (depending upon the dimension)
 * If the block at that position is BARRIER this is a protected region.
 */
public class ProtectedChecker {

    public boolean isProtected(int x, int z, World world) {
        int minimalY = world.getEnvironment() == World.Environment.NORMAL ? -63 : 0;
        Block c = world.getBlockAt(x, minimalY, z);
        return (c.getType() == Material.BARRIER);
    }
}
