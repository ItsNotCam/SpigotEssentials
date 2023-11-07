package cam.vrc.essentials;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class TimeTracker implements Listener {
    int minutesAlive;
    LocalDateTime joinTime;


    public TimeTracker() {
        System.out.println("Starting Time Tracker");
        minutesAlive = getMinutes();
    }

    @EventHandler
    public void OnJoin(PlayerJoinEvent event) {
        joinTime = LocalDateTime.now();
    }

    @EventHandler
    public void OnLeave(PlayerQuitEvent event) {
        WriteTimeAlive();
    }

    void WriteTimeAlive() {
        LocalDateTime leaveTime = LocalDateTime.now();
        long delta = ChronoUnit.MINUTES.between(joinTime, leaveTime);
        long fin = delta + minutesAlive;

        try {
            FileWriter writer = new FileWriter("./hours.txt");
            writer.write("" + fin);
            writer.close();
        } catch (Exception e) { }
    }

    void destroy() {
        WriteTimeAlive();
    }

    int getMinutes()  {
        try {
            File file = new File("./hours.txt");
            FileInputStream fin = new FileInputStream(file);

            StringBuilder number = new StringBuilder();
            int ch = 0;
            while ((ch = fin.read()) > 0) {
                number.append((char) ch);
            }

            if(number.length() < 1)
                return 0;

            return Integer.parseInt(number.toString());
        } catch (Exception e) {
            return -1;
        }
    }
}
