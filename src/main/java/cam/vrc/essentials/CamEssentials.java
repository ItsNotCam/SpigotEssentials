package cam.vrc.essentials;

import org.bukkit.plugin.java.JavaPlugin;

public final class CamEssentials extends JavaPlugin {
    DurabilityListener durabilityListener;
    MobHealthListener mobHealthListener;
    ArrowListener arrowListener;
    TimeTracker ap;

    @Override
    public void onEnable() {
        // Plugin startup logic
        durabilityListener = new DurabilityListener();
        mobHealthListener = new MobHealthListener();
        //ap = new TimeTracker();

        getServer().getPluginManager().registerEvents(durabilityListener, this);
        getServer().getPluginManager().registerEvents(mobHealthListener, this);
        getServer().getPluginManager().registerEvents(new ArrowListener(), this);
        getServer().getPluginManager().registerEvents(new ChatListener(), this);
        //getServer().getPluginManager().registerEvents(ap, this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        durabilityListener.destroy();
        mobHealthListener.destroy();
        ap.destroy();
    }
}
