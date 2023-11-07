package cam.vrc.essentials;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.time.LocalDateTime;

public class MobHealth {
    private LivingEntity entity;
    private String originalName;
    private LocalDateTime timeout;
    public double maxHealth;
    final int DEFAULT_TIMEOUT = 5;

    public MobHealth(LivingEntity entity, String name) {
        this.entity = entity;
        this.originalName = name;
        this.timeout = LocalDateTime.now().plusSeconds(DEFAULT_TIMEOUT);

        this.maxHealth = entity.getMaxHealth();
        /*this.maxHealth = Objects.requireNonNull(entity.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getDefaultValue();
        Bukkit.getServer().getConsoleSender().sendMessage(
                String.format("The max health of %s is %f", entity.getType().name(), this.maxHealth));
         */
    }

    public Entity getEntity() {
        return entity;
    }

    public String getOriginalName() {
        return originalName;
    }

    public LocalDateTime getTimeout() {
        return timeout;
    }

    public void updateHealth(double damage) {
        double tempHealth = entity.getHealth() - damage;

        int newHealth;
        if(tempHealth < 1) {
            newHealth = 0;
        } else {
            newHealth = (int) Math.max(1, tempHealth / 2);
        }

        StringBuilder health = new StringBuilder("§c§l");
        for(int i = 0; i < maxHealth / 2; i++) {
            if(i < newHealth) {
                health.append("❤");
            } else {
                health.append("♡");
            }
        }

        entity.setCustomName(health.toString());
        entity.setCustomNameVisible(true);
        //entity.setCustomName(String.format("Health: %.2f / %.2f - %s", newHealth, maxHealth, healthName));
    }

    public void clearHealthbar() {
        entity.setCustomName(originalName);
        entity.setCustomNameVisible(false);
    }

    public void resetTime() {
        this.timeout = LocalDateTime.now().plusSeconds(DEFAULT_TIMEOUT);
    }
}
