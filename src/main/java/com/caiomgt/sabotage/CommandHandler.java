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
    boolean checkPerms(CommandSender sender, boolean checkTag) {
        if (sender instanceof Player) {
            Player plr = (Player) sender;
            if (checkTag) {
                if (checkTags(plr)) {
                    return plr.isOp();
                }
            } else {
                return plr.isOp();
            }
        }
        return false;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equals("forcestart")) {
            if (checkPerms(sender, false)) {
                Player plr = (Player) sender;
                if (!manager.gameStarted) {
                    manager.Start(plr.getWorld());
                } else {
                    plr.sendMessage(ChatColor.YELLOW + "Could not start game, game already started.");
                }
            }
        } else {
            if (checkPerms(sender, true)) {
                if (command.getName().equals("saboteur")) {
                    Player plr = (Player) sender;
                    if (manager.AddSab(plr)) {
                        plr.addScoreboardTag("forcePicked");
                    }
                }
                if (command.getName().equals("detective")) {
                    Player plr = (Player) sender;
                    if (!manager.AddDet(plr)) {
                        plr.sendMessage(ChatColor.YELLOW + "Could not add you as a detective due to there not being enough players.");
                    } else {
                        plr.addScoreboardTag("forcePicked");
                    }
                }
                if (command.getName().equals("innocent")) {
                    Player plr = (Player) sender;
                    manager.AddInno(plr);
                    plr.addScoreboardTag("forcePicked");
                }
            } else {
                sender.sendMessage(ChatColor.YELLOW + "You do not have permissions for this command.");
            }
        }
        return true;
    }
}