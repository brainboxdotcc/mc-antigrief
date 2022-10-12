package cc.brainbox.AntiGrief;

import io.papermc.paper.event.block.BlockBreakBlockEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.block.data.type.TNT;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class BlockEvent implements Listener {

    private final ProtectedChecker pc = new ProtectedChecker();

    @EventHandler
    public void onBlockSpread(BlockSpreadEvent event) {
        Location l = event.getSource().getLocation();
        if (pc.isProtected(l.getBlockX(), l.getBlockZ(), event.getBlock().getWorld()) && event.getSource().getType() != Material.WATER) {
            event.setCancelled(true);
        }
    }

    /**
     * Returns true if this event refers to a block in a protected area
     * @param event block event
     * @return boolean
     */
    private boolean checkShouldProtectBlock(org.bukkit.event.block.BlockEvent event) {
        Location l = event.getBlock().getLocation();
        return (pc.isProtected(l.getBlockX(), l.getBlockZ(), event.getBlock().getWorld()));
    }

    /**
     * Prevent placing of blocks in survival in protected areas.
     * This is a safeguard just in case somehow they manage to get out of adventure mode inside
     * the protected area.
     * @param event block place event
     */
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.SURVIVAL && this.checkShouldProtectBlock(event)) {
            event.setCancelled(true);
        }
    }

    /**
     * Dispenser is dispensing a mine cart in protected area, the player is taking a mine cart,
     * ensure another is in it ready for dispensing.
     * @param event block dispense event
     */
    @EventHandler
    public void onDispenseMineCart(BlockDispenseEvent event)
    {
        if (checkShouldProtectBlock(event) && event.getItem().getType() == Material.MINECART) {
            Dispenser dispenser = (Dispenser) event.getBlock();
            dispenser.getInventory().addItem(new ItemStack(Material.MINECART));
        }
    }

    @EventHandler
    public void onBlockBurn(BlockBurnEvent event) {
        /* Blocks within the safe zone cannot catch fire */
        if (this.checkShouldProtectBlock(event)) {
            event.setCancelled(true);
        }
    }

    /**
     * Prevent damage to blocks when in survival mode in a protected area, even if adventure mode hasn't been set
     * @param event block damage event
     */
    @EventHandler
    public void onBlockDamage(BlockDamageEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.SURVIVAL && this.checkShouldProtectBlock(event)) {
            event.setCancelled(true);
        }
    }

    /**
     * If using a piston to affect a TNT block inside the protected area, replace the TNT block with slime!
     * @param event piston extend event
     */
    @EventHandler
    public void onBlockPistonExtend(BlockPistonExtendEvent event) {
        Location l = event.getBlock().getLocation();
        if (pc.isProtected(l.getBlockX(), l.getBlockZ(), event.getBlock().getWorld())) {
            List<Block> effected = event.getBlocks();
            for (Block block : effected) {
                if (block.getType() == Material.TNT) {
                    event.setCancelled(true);
                    block.setType(Material.SLIME_BLOCK);
                }
            }
        }
    }

    /* Prevent movement of blocks in protected areas */
    @EventHandler
    public void onBlockFromTo(BlockFromToEvent event) {
        Location l = event.getToBlock().getLocation();
        if (pc.isProtected(l.getBlockX(), l.getBlockZ(), event.getToBlock().getWorld()) && event.getBlock().getType() == Material.LAVA) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        Location l = event.getLocation();
        if (pc.isProtected(l.getBlockX(), l.getBlockZ(), event.getEntity().getWorld())) {
            event.setCancelled(true);
            Bukkit.getLogger().info("AG: Cancelled explosion in protected area.");
        }
    }
}
