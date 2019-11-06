package fr.station47.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;

public class ScoreboardManager implements Listener, CommandExecutor{

    private static HashMap<Player, PlayerScoreboardSidebar> sidebars;
    private Plugin plugin;

    public ScoreboardManager(Plugin plugin){
        sidebars = new HashMap<>();
        this.plugin = plugin;
    }

    public void createScoreboard(Player player){
        PlayerScoreboardSidebar scoreboard = new PlayerScoreboardSidebar(player);
        Bukkit.getServer().getPluginManager().registerEvents(scoreboard,this.plugin);
        sidebars.put(player,scoreboard);
    }

    @EventHandler
    public void displaySidebarOnJoin(PlayerJoinEvent joinEvent){
        this.createScoreboard(joinEvent.getPlayer());
    }

    @EventHandler
    public void deleteScoreboardOnLeave(PlayerQuitEvent quitEvent){
        sidebars.get(quitEvent.getPlayer()).unregister();
        sidebars.remove(quitEvent.getPlayer());
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player){
            PlayerScoreboardSidebar scoreboardSidebar;
            if ((scoreboardSidebar = sidebars.get((Player)commandSender)).isHidden()){
                scoreboardSidebar.show();
            } else {
                scoreboardSidebar.hide();
            }
        }
        return true;
    }
}
