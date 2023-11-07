package cam.vrc.essentials;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.Damageable;

import java.util.HashMap;

public class DurabilityListener implements Listener {
    public HashMap<Player, BossBar> mainHandDurability;
    public HashMap<Player, BossBar> elytraDurability;
    public HashMap<Player, BossBar> shieldDurability;

    public DurabilityListener() {
        mainHandDurability = new HashMap<>();
        elytraDurability = new HashMap<>();
        shieldDurability = new HashMap<>();

        System.out.println("Registering OnWeaponChange");
    }

    @EventHandler
    public void OnDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if(mainHandDurability.containsKey(player)) {
            mainHandDurability.get(player).removeAll();
            mainHandDurability.remove(player);
        }
    }

    @EventHandler
    public void OnDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if(mainHandDurability.containsKey(player)) {
            mainHandDurability.get(player).removeAll();
            mainHandDurability.remove(player);
        }
    }

    @EventHandler
    public void OnPickup(EntityPickupItemEvent event) {
        if(!(event.getEntity() instanceof Player))
            return;
        Player player = (Player) event.getEntity();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        if(itemInHand.getType() == Material.AIR)
            UpdateBossBar(event.getItem().getItemStack(), player);
    }

    @EventHandler
    public void OnDamage(PlayerItemDamageEvent event) {
        ItemStack item = event.getItem();
        Player player = event.getPlayer();

        if(player.getInventory().getItemInMainHand().equals((item))
                || item.getType() == Material.ELYTRA || item.getType() == Material.SHIELD) {
            UpdateBossBar(item, player, event.getDamage());
        }
    }

    @EventHandler
    public void OnChange(PlayerItemHeldEvent event) {
        // get requisite information
        Player player = event.getPlayer();
        ItemStack item = event.getPlayer().getInventory().getItem(event.getNewSlot());

        if(item != null) {
            UpdateBossBar(item, player);
        } else if(mainHandDurability.containsKey(player)){
            mainHandDurability.get(player).removeAll();
            mainHandDurability.remove(player);
        }
    }

    @EventHandler
    public void OnBreak(PlayerItemBreakEvent event) {
        Player player = event.getPlayer();
        if(mainHandDurability.containsKey(player)) {
            mainHandDurability.get(player).removeAll();
            mainHandDurability.remove(player);
        }
    }

    @EventHandler
    public void OnMend(PlayerItemMendEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if(mainHandDurability.containsKey(player) && player.getInventory().getItemInMainHand().equals(item)) {
            UpdateBossBar(event.getItem(), player, -event.getRepairAmount());
        }
    }


    @EventHandler
    public void OnItemEquip(InventoryClickEvent event) {
        int clickedSlot = event.getSlot();
        if(event.getClickedInventory() instanceof PlayerInventory) {
            Player player = (Player) event.getClickedInventory().getHolder();
            if(player == null)
                return;

            ItemStack droppedItem = event.getCursor();
            if(droppedItem == null)
                return;

            InventoryAction action = event.getAction();
            switch(action) {
                case HOTBAR_SWAP:
                case PLACE_ALL:
                case PLACE_ONE:
                case PLACE_SOME:
                case SWAP_WITH_CURSOR:
                    if(clickedSlot == player.getInventory().getHeldItemSlot()) {
                        UpdateBossBar(droppedItem, player);
                    }
                    break;
                case PICKUP_ALL:
                case PICKUP_HALF:
                case PICKUP_ONE:
                case PICKUP_SOME:
                    if(clickedSlot == player.getInventory().getHeldItemSlot()) {
                        UpdateBossBar(new ItemStack(Material.AIR), player);
                    }
                    break;
            }

            Bukkit.getServer().getConsoleSender().sendMessage(action + " (" + clickedSlot + ") " + droppedItem + " - " + player.getInventory().getHeldItemSlot());
        }
    }

    public void UpdateBossBar(ItemStack item, Player player) {
        UpdateBossBar(item, player, 0);
    }

    public void UpdateBossBar(ItemStack item, Player player, int addedDamage) {
        if(item.getType() == Material.AIR && mainHandDurability.containsKey(player)) {
            mainHandDurability.get(player).removeAll();
            mainHandDurability.remove(player);
            return;
        }


        // if the item can be damaged (is a tool/armor/etc)
        if(item.getItemMeta() instanceof Damageable) {
            // get durability and turn it into progress for the boss bar
            Damageable dmg = (Damageable) item.getItemMeta();
            int maxDurability = item.getType().getMaxDurability();
            int curDurability = Math.max(dmg.getDamage() + addedDamage, 0);

            // if max durability is less than 1 clear the boos bar and return
            if(maxDurability < 1) {
                if(mainHandDurability.containsKey(player)) {
                    mainHandDurability.get(player).removeAll();
                    mainHandDurability.remove(player);
                }
                return;
            }

            double progress = curDurability / (double)maxDurability;
            // set color based on thresholds
            BarColor barColor = BarColor.WHITE;
            if(progress > 0.9) {
                barColor = BarColor.RED;
            } else if(progress > 0.6) {
                barColor = BarColor.YELLOW;
            } else if (progress > 0.1) {
                barColor = BarColor.GREEN;
            }

            // set name
            String name;
            if(item.getItemMeta().hasDisplayName()) {
                name = item.getItemMeta().getDisplayName();
            } else {
                name = Utilities.toTitle(item.getType().name());
            }

            name = String.format("%s (%d / %d)", name, maxDurability - curDurability, maxDurability);

            BossBar bb;
            /*
            if(item.getType() == Material.ELYTRA) {
                bb = UpdateOrCreateAndReturnBossBar(elytraDurability, player, name, barColor);
            } else if(item.getType() == Material.SHIELD) {
                bb = UpdateOrCreateAndReturnBossBar(shieldDurability, player, name, barColor);
            } else {*/
            if(mainHandDurability.containsKey(player)) {
                bb = mainHandDurability.get(player);
                bb.setTitle(name);
                bb.setColor(barColor);
            } else {
                // create the boss bar and assign it to the player
                bb = Bukkit.createBossBar(name, barColor, BarStyle.SEGMENTED_10);
                bb.addPlayer(player);
                mainHandDurability.put(player, bb);
            }
            //}

            bb.setProgress(1 - progress);
        }
    }

    static BossBar UpdateOrCreateAndReturnBossBar(HashMap<Player, BossBar>bossbarHashMap, String title, Player player) {
        return UpdateOrCreateAndReturnBossBar(bossbarHashMap, player, title, BarColor.WHITE);
    }

    static BossBar UpdateOrCreateAndReturnBossBar(HashMap<Player, BossBar> bossbarHashMap, Player player,
                                                  String title, BarColor barColor) {
        BossBar bb;
        if(bossbarHashMap.containsKey(player)) {
            bb = bossbarHashMap.get(player);
            bb.setTitle(title);
        } else {
            // create the boss bar and assign it to the player
            bb = Bukkit.createBossBar(title, barColor, BarStyle.SEGMENTED_10);
            bb.addPlayer(player);
            bossbarHashMap.put(player, bb);
        }

        return bb;
    }

    public void destroy() {
        mainHandDurability.keySet().forEach(player -> mainHandDurability.get(player).removeAll());
        mainHandDurability.clear();
    }
}
