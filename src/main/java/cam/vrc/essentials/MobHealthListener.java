package cam.vrc.essentials;

import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static java.lang.Thread.sleep;

public class MobHealthListener implements Listener {

    HashMap<LivingEntity, MobHealth> mobs;
    Thread healthDisplayTimer;
    ReentrantReadWriteLock lock;
    boolean stopThread;

    public MobHealthListener() {
        mobs = new HashMap<>();

        // setup timer
        lock = new ReentrantReadWriteLock();
        healthDisplayTimer = new Thread(this::updateHeathDisplays);
        healthDisplayTimer.start();
    }

    @EventHandler
    public void OnDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();

        if(!isValidEntity(entity))
            return;

        LivingEntity mob = (LivingEntity) entity;
        MobHealth mh;
        if(!mobs.containsKey(entity)) {
            mh = new MobHealth(mob, entity.getCustomName());
            mobs.put(mob, mh);
        } else {
            mh = mobs.get(mob);
            mh.resetTime();
        }

        mh.updateHealth(event.getDamage());
    }

    @EventHandler
    public void OnPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        if(!(player.getLastDamageCause() instanceof EntityDamageByEntityEvent))
            return;

        String killerName;
        Entity damager = ((EntityDamageByEntityEvent) player.getLastDamageCause()).getDamager();
        if(damager instanceof Projectile) {
            Projectile p = (Projectile) damager;
            Entity shooter = (Entity) p.getShooter();
            killerName = Utilities.toTitle(shooter.getType().name());
        } else {
            killerName = Utilities.toTitle(damager.getType().name());
        }

        String adjective = Utilities.isVowel(killerName.charAt(0)) ? "an" : "a";

        event.setDeathMessage(
            String.format("%s was killed by %s %s", player.getDisplayName(), adjective, Utilities.toTitle(killerName))
        );
    }


    boolean isValidEntity(Entity entity) {
        boolean isBoss = entity instanceof Wither || entity instanceof EnderDragon;
        boolean isMob = entity instanceof LivingEntity && !(entity instanceof Player);

        return isMob && !isBoss;
    }

    void updateHeathDisplays() {
        while(!stopThread) {
            ArrayList<LivingEntity> entitiesToRemove = new ArrayList<>();

            for(LivingEntity mob : mobs.keySet()) {
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime timeout = mobs.get(mob).getTimeout();
                if(now.isAfter(timeout)) {
                    mobs.get(mob).clearHealthbar();
                    entitiesToRemove.add(mob);
                }
            }

            for (LivingEntity mob : entitiesToRemove) {
                mobs.remove(mob);
            }

            try { sleep(1000); }
            catch (java.lang.InterruptedException e) { System.out.println("Failed to sleep"); }
        }
    }

    void destroy() {
        try {
            stopThread = true;
            healthDisplayTimer.join();
        } catch (InterruptedException e) {
            System.out.println("Failed to end thread");
        } finally {
            for(LivingEntity mob : mobs.keySet()) {
                mobs.get(mob).clearHealthbar();
            }
        }
    }
}
