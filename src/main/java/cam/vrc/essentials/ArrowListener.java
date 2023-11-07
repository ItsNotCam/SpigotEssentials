package cam.vrc.essentials;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Hashtable;

public class ArrowListener implements Listener {
    Hashtable<Player, BossBar> arrows;
    final int maxItemSlots = 34;

    public ArrowListener() {
        arrows = new Hashtable<>();
    }

    @EventHandler
    public void onShoot(EntityShootBowEvent e) {
        if(e.getEntity() instanceof Player) {
            Player shooter = (Player) e.getEntity();
            Inventory inventory = shooter.getInventory();

            int totalArrows = 0;

            for(int i = 0; i < maxItemSlots; i++) {
                ItemStack item = inventory.getItem(i);
                if(item != null && item.getType() == Material.ARROW) {
                    totalArrows += item.getAmount();
                }
            }

            BossBar bb;
            if (arrows.containsKey(shooter)) {
                bb = arrows.get(shooter);
            } else {
                bb = Bukkit.createBossBar("Arrows: " + (totalArrows - 1), BarColor.BLUE, BarStyle.SOLID);
                bb.addPlayer(shooter);
                bb.setProgress(1);

                arrows.put(shooter, bb);
            }

            bb.setTitle("Arrows: " + (totalArrows - 1));
        }
    }
}
