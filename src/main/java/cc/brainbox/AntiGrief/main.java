package cc.brainbox.AntiGrief;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class main extends JavaPlugin {
    @Override
    public void onEnable() {
        PluginManager manager = Bukkit.getServer().getPluginManager();

        manager.registerEvents(new PlayerEvent(), this);
        manager.registerEvents(new BlockEvent(), this);
        manager.registerEvents(new WeatherEvent(), this);
        manager.registerEvents(new EntityEvent(), this);

        PluginCommand commandProtected = this.getCommand("protected");
        if (commandProtected != null) {
            commandProtected.setExecutor(new CommandProtected());
            commandProtected.setTabCompleter(new CommandProtectedTabComplete());
        }
        Objects.requireNonNull(this.getCommand("wilderness")).setExecutor(new WildernessCommand());

        Bukkit.getLogger().info("AntiGrief plugin started.");
    }


}
