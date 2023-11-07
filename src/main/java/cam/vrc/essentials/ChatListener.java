package cam.vrc.essentials;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

public class ChatListener implements Listener {
    @EventHandler
    public void OnMessage(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        event.setCancelled(true);

        for(Player p : Bukkit.getOnlinePlayers())
            p.sendMessage(String.format("§b§o%s §r§7> §f%s", player.getDisplayName(), message));
    }

    @EventHandler
    public void OnJoin(PlayerJoinEvent event) {
        event.setJoinMessage("§o§7+ §b" + event.getPlayer().getDisplayName());
    }

    @EventHandler
    public void OnLeave(PlayerQuitEvent event) {
        event.setQuitMessage("§o§c- §b" + event.getPlayer().getDisplayName());
    }

    /*
    @EventHandler
    public void OnKill(EntityDeathEvent event) {
        if(!(event.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent))
            return;

        EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event.getEntity().getLastDamageCause();
        if(e.getDamager() instanceof Player) {
            String playerName = ((Player) e.getDamager()).getDisplayName();
            String killedName = Utilities.toTitle(event.getEntity().getType().name());
            String adjective = Utilities.isVowel(killedName.charAt(0)) ? "an" : "a";
            String message = String.format("§b%s §7killed %s %s", playerName, adjective, killedName);

            for(Player player : Bukkit.getOnlinePlayers())
                player.sendMessage(message);
        }
    }
    */

    @EventHandler
    public void OnFish(PlayerFishEvent event) {
        Entity caughtEntity = event.getCaught();
        if(caughtEntity == null)
            return;

        String playerName = event.getPlayer().getDisplayName();
        String message = "§o§b" + playerName + "§7 fished a §e" + caughtEntity.getName() + "§7!";

            /*
    if(caughtEntity.getType().get == Material.ENCHANTED_BOOK) {
        Map<Enchantment, Integer> enchantments = itemStack.getEnchantments();
        if(!enchantments.keySet().isEmpty()) {
            Enchantment bookEnchantment = (Enchantment) enchantments.keySet().toArray()[0];
            Integer value = enchantments.get(bookEnchantment);
            message += "Book enchanted with " + bookEnchantment.toString() + " " + value + "§7!";
        }
    } else {
        message +=  ;
    }

             */

        for(Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(message);
        }
    }
}
