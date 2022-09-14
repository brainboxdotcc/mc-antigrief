package cc.brainbox.AntiGrief;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CommandProtected implements CommandExecutor {

    ProtectedChecker pc = new ProtectedChecker();

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (commandSender.isOp()) {
            if (args.length > 0) {
                Player player = commandSender.getServer().getPlayer(args[0]);
                if (player != null) {
                    Location l = player.getLocation();
                    commandSender.sendMessage(
                            "Player " + args[0] + " is in " +
                                    (pc.isProtected(l.getBlockX(), l.getBlockZ(), player.getWorld()) ?
                                            ChatColor.GREEN + "a safe zone" : ChatColor.RED + "the wilderness"
                                    )
                    );
                } else {
                    commandSender.sendMessage("Player " + args[0] + " does not exist!");
                }
            } else {
                commandSender.sendMessage("Missing parameter");
            }
        } else {
            commandSender.sendMessage("You must be an op to execute this command.");
        }
        return true;
    }
}
