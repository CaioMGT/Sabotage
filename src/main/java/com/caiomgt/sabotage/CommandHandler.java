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
    SaveManager saveManager;
    public CommandHandler(Plugin plugin, GameManager manager, SaveManager saveManager) {
        this.plugin = plugin;
        this.manager = manager;
        this.saveManager = saveManager;
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
                return checkTags(plr) && plr.isOp() && !manager.gameStarted;
            } else {
                return plr.isOp();
            }
        }
        return false;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // It's kind of weird how sendMessage isn't deprecated...
        if (command.getName().equals("forcestart")) {
            if (checkPerms(sender, false)) {
                Player plr = (Player) sender;
                if (!manager.gameStarted) {
                    if (!manager.Start(plr.getWorld())) {
                        plr.sendMessage(ChatColor.YELLOW + "Could not start game, too little players.");
                    }
                } else {
                    plr.sendMessage(ChatColor.YELLOW + "Could not start game, game already started.");
                }
            }
        } else if (command.getName().equals("setkarma")){
            if (checkPerms(sender, false)) {
                Player plr;
                if (args.length > 1) {
                    plr = plugin.getServer().getPlayer(args[0]);
                } else {
                    plr = (Player) sender;
                }
                try {
                    if (args.length > 1) {
                        saveManager.setKarma(plr, Integer.parseInt(args[1]));
                    } else if (args.length == 0) {
                        return false;
                    } else {
                        saveManager.setKarma(plr, Integer.parseInt(args[0]));
                    }
                    sender.sendMessage(ChatColor.YELLOW + "That player now has " + ChatColor.GREEN + saveManager.getKarma(plr) + ChatColor.YELLOW + " Karma");
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.YELLOW + "The karma you entered is not a number");
                }
            }
        } else if (command.getName().equals("getkarma")){
            if (checkPerms(sender, false)) {
                Player plr;
                if (args.length == 0) {
                    plr = (Player) sender;
                } else {
                    plr = plugin.getServer().getPlayer(args[0]);
                }
                if (plr == null) {
                    plr = (Player) sender;
                }
                sender.sendMessage(ChatColor.YELLOW + "That player has " + ChatColor.GREEN + saveManager.getKarma(plr) + ChatColor.YELLOW + " Karma");
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