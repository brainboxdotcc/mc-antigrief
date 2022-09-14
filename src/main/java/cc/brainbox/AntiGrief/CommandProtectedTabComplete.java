package cc.brainbox.AntiGrief;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CommandProtectedTabComplete implements TabCompleter {
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length > 0) {
            for (Player p : sender.getServer().getOnlinePlayers()) {
                String name = p.getName();
                if (args[0].isEmpty() || name.startsWith(args[0])) {
                    completions.add(name);
                }
            }
        }
        return completions;
    }

}
