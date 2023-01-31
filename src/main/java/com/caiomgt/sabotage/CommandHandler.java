package com.caiomgt.sabotage;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class CommandHandler implements CommandExecutor {
    Plugin plugin;
    GameManager manager;
    public CommandHandler(Plugin plugin, GameManager manager) {
        this.plugin = plugin;
        this.manager = manager;
    }
    boolean checkTags(Player plr) {
        for (String tag : plr.getScoreboardTags()) {
            if (tag.equals("forcePicked")) {
                plr.sendMessage(ChatColor.YELLOW + "You already picked a role");
                return false;
            }
        }
        return true;
    }
    boolean checkPerms(CommandSender sender) {
        if (sender instanceof Player) {
            Player plr = (Player) sender;
            if (checkTags(plr)) {
                return plr.isOp();
            }
        }
        return false;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equals("saboteur")) {
            if (checkPerms(sender)) {
                Player plr = (Player) sender;
                if (manager.AddSab(plr)) {
                    plr.addScoreboardTag("forcePicked");
                }
            }
        }
        if (command.getName().equals("detective")) {
            if (checkPerms(sender)) {
                Player plr = (Player) sender;
                if (!manager.AddDet(plr)) {
                    plr.sendMessage(ChatColor.YELLOW + "Could not add you as a detective due to there not being enough players.");
                } else {
                    plr.addScoreboardTag("forcePicked");
                }
            }
        }
        if (command.getName().equals("innocent")) {
            if (checkPerms(sender)) {
                Player plr = (Player) sender;
                manager.AddInno(plr);
                plr.addScoreboardTag("forcePicked");
            }
        }
        return true;
    }
}