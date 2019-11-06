package fr.station47.scoreboard;

import com.earth2me.essentials.api.Economy;
import com.earth2me.essentials.api.UserDoesNotExistException;
import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.api.JobsJoinEvent;
import com.gamingmesh.jobs.api.JobsLeaveEvent;
import com.gamingmesh.jobs.container.JobProgression;
import fr.station47.cosmovotes.Cosmovotes;
import fr.station47.cosmovotes.events.VoteUpdate;
import net.ess3.api.events.UserBalanceUpdateEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

public class PlayerScoreboardSidebar implements Listener{
    private Player player;
    private Scoreboard sb;
    private Objective objective;
    private DecimalFormat df;
    private int voteCount = 0;
    private int voteObjective = 0;
    public boolean hidden = false;

    public PlayerScoreboardSidebar(Player player){
        this.player = player;
        sb = Bukkit.getServer().getScoreboardManager().getNewScoreboard();
        objective = sb.registerNewObjective("_sidebar","dummy");
        sb.getScores("_sidebar").forEach(s -> sb.resetScores(s.getEntry()));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(ChatColor.YELLOW+""+ChatColor.UNDERLINE+"FREEBUILD");
        objective.getScore(" ").setScore(13);
        String balance = "";
        this.df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);
        df.setGroupingUsed(false);
        try {

            if (Economy.playerExists(this.player.getName())) {
                BigDecimal bd = Economy.getMoneyExact(this.player.getName());
                balance = df.format(bd);
            } else {
                balance = "0";
            }
        } catch (UserDoesNotExistException e) {
            e.printStackTrace();
        }
        objective.getScore(this.join("Balance:",balance)).setScore(12);
        String job;
        List<JobProgression> jobs;
        if (Jobs.getPlayerManager().getJobsPlayer(player) !=null && (jobs = Jobs.getPlayerManager().getJobsPlayer(player).getJobProgression()).size() >0){
            job = jobs.get(0).getJob().getName();
        } else {
            job = "aucune";
        }
        objective.getScore(ChatColor.YELLOW+"Métier(s)").setScore(11);
        if(Jobs.getPlayerManager().getJobsPlayer(player).getJobProgression() !=null) {
            Jobs.getPlayerManager().getJobsPlayer(player).getJobProgression().forEach(pjob -> objective.getScore("-"+ChatColor.AQUA+pjob.getJob().getName()).setScore(10));
        }
        objective.getScore(this.join("Monde:",this.player.getWorld().getName())).setScore(8);
        voteCount = Cosmovotes.voteManager.getVoteCount();
        voteObjective = Cosmovotes.voteManager.getObjective();
        objective.getScore(this.join("Cosmovotes:", voteCount+"/"+voteObjective)).setScore(7);
        objective.getScore("  ").setScore(2);
        objective.getScore(ChatColor.BOLD+"play.station47.net").setScore(1);
        player.setScoreboard(sb);
    }

    @EventHandler
    public void updateWorldOnScoreboard(PlayerChangedWorldEvent event){
        if (event.getPlayer().equals(this.player)) {
            sb.resetScores(this.join("Monde:", event.getFrom().getName()));
            objective.getScore(this.join("Monde:", event.getPlayer().getWorld().getName())).setScore(10);
        }
    }

    @EventHandler
    public void updateJobOnLeave(JobsLeaveEvent leaveEvent){
        if (leaveEvent.getPlayer().getPlayer().equals(this.player)) {
            sb.resetScores("-"+ChatColor.AQUA+leaveEvent.getJob().getName());
        }
    }

    @EventHandler
    public void updateJobOnJoin(JobsJoinEvent joinEvent){
        if (joinEvent.getPlayer().getPlayer().equals(player) && !joinEvent.isCancelled()) {
            objective.getScore("-"+ChatColor.AQUA+joinEvent.getJob().getName()).setScore(10);
        }
    }
    @EventHandler
    public void updateMoney(UserBalanceUpdateEvent event){
        if (event.getPlayer().equals(this.player)){
            sb.resetScores(this.join("Balance:",df.format(event.getOldBalance())));
            objective.getScore(this.join("Balance:",df.format(event.getNewBalance()))).setScore(12);
        }
    }

    @EventHandler
    public void updateTotalVote(VoteUpdate voteUpdate){
        sb.resetScores(this.join("Cosmovotes:",voteCount+"/"+voteObjective));
        voteCount = voteUpdate.getVoteCount();
        voteObjective = voteUpdate.getObjective();
        objective.getScore(this.join("Cosmovotes:",voteCount+"/"+voteObjective)).setScore(8);
    }

    public void hide(){
        objective.setDisplaySlot(null);
        hidden = true;
    }

    public void show(){
        player.setScoreboard(sb);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        hidden = false;
    }



    public boolean isHidden(){
        return hidden;
    }

    public void unregister(){
        HandlerList.unregisterAll(this);
        objective.unregister();
    }

    private String join(String s1, String s2){
        return ChatColor.YELLOW+s1+" "+ChatColor.AQUA+s2;
    }

}
