package fr.station47.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Scoreboard extends JavaPlugin {


    private ScoreboardManager sbm;
    public void onEnable(){
        sbm = new ScoreboardManager(this);
        this.getCommand("sb").setExecutor(sbm);
        this.getServer().getPluginManager().registerEvents(sbm,this);
        for (Player p: Bukkit.getOnlinePlayers()){
            sbm.createScoreboard(p);
        }
    }

    public void onDisable(){}

}
