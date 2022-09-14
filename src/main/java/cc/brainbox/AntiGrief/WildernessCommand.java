package cc.brainbox.AntiGrief;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

public class WildernessCommand implements CommandExecutor {

    ProtectedChecker pc = new ProtectedChecker();

    private final HashMap<String, Player> teleportsInProgress = new HashMap<String, Player>();

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (commandSender instanceof Player p) {

            if (teleportsInProgress.containsKey(p.getName())) {
                /* Check if teleport is in progress */
                commandSender.sendMessage(ChatColor.RED + "Teleportation is already in progress. Please be patient.");
                return true;
            } else if (!pc.isProtected(p.getLocation().getBlockX(), p.getLocation().getBlockZ(), p.getWorld())) {
                /* Check if the player is already in the wilderness, we don't want them using it to get out of combat etc */
                commandSender.sendMessage(ChatColor.RED + "You are already in the wilderness! You can only issue this command from within a " + ChatColor.GREEN + "safe zone" + ChatColor.RED + ".");
                return true;
            } else if (p.getLocation().getWorld().getEnvironment() != World.Environment.NORMAL) {
                /* Check if in the overworld */
                commandSender.sendMessage(ChatColor.RED + "This command is not available in the nether or the end.");
                return true;
            }

            /* Save in-progress status */
            teleportsInProgress.put(p.getName(), p);

            /* Construct a random location, at present only the X and Z are known until the chunk is loaded.
             * If by some random fluke we pick a random location that it also a safe zone, choose again.
             */
            final Location teleportLocation = new Location(p.getWorld(), ThreadLocalRandom.current().nextDouble(2000, 100000), 0, ThreadLocalRandom.current().nextDouble(2000, 100000));
            Bukkit.getLogger().info("Preparing to teleport " + p.getName() + " to " + teleportLocation);

            commandSender.sendMessage("Starting teleportation to " + ChatColor.RED + "the wilderness" + ChatColor.RESET + "...");
            p.getWorld().getChunkAtAsync(teleportLocation).thenAccept((Chunk chunk) -> {
                /* Find out the Y point they'll arrive at, so they don't fall out of the sky, or get suffocated underground */
                teleportLocation.setY(p.getWorld().getHighestBlockYAt(teleportLocation.getBlockX(), teleportLocation.getBlockZ()) + 1);

                /* Begin asynchronous teleport */
                p.teleportAsync(teleportLocation);
                commandSender.sendMessage("You have been teleported to " + ChatColor.RED + "the wilderness" + ChatColor.RESET + "! Good luck...");
                Bukkit.getLogger().info("Wilderness teleport of " + p.getName() + " to " + teleportLocation + " completed.");
                teleportsInProgress.remove(p.getName());
            });
        } else {
            commandSender.sendMessage(ChatColor.RED + "This command must be executed by a player, not from the server console");
        }
        return true;
    }
}