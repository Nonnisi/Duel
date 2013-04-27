package com.me.tft_02.duel.datatypes.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.me.tft_02.duel.Config;
import com.me.tft_02.duel.Duel;
import com.me.tft_02.duel.util.Misc;

public class PlayerData {

    public static HashMap<String, String> duels = new HashMap<String, String>();
    public static HashMap<String, DuelInvitationKey> duelInvitations = new HashMap<String, DuelInvitationKey>();
    public static HashMap<String, List<ItemStack>> savedItems = new HashMap<String, List<ItemStack>>();
    public static HashMap<String, Boolean> duelRespawn = new HashMap<String, Boolean>();

    public void setDuel(Player player, Player target) {
        duels.put(player.getName(), target.getName());
        duels.put(target.getName(), player.getName());
    }

    public static String getDuelTargetName(Player player) {
        String target = "null";
        if (duels.containsKey(player.getName())) {
            return duels.get(player.getName());
        }
        return target;
    }

    public static Player getDuelTarget(Player player) {
        String targetName = getDuelTargetName(player);
        return Duel.getInstance().getServer().getPlayer(targetName);
    }

    public static boolean removeDuelTarget(Player player) {
        if (duels.containsKey(player.getName())) {
            duels.remove(player.getName());
            return true;
        }
        return false;
    }

    public static boolean isInDuel(Player player) {
        if (getDuelTarget(player) == null || getDuelTarget(player).equals("null")) {
            return false;
        }
        return true;
    }

    public static boolean wasInDuel(Player player) {
        if (duelRespawn.containsKey(player.getName())) {
            return duelRespawn.get(player.getName());
        }
        return false;
    }

    public static boolean areDueling(Player player, Player target) {
        Player duelTargetPlayer = getDuelTarget(target);
        Player duelTargetTarget = getDuelTarget(player);

        if (duelTargetPlayer == null || duelTargetTarget == null) {
            return false;
        }

        if (player.getName().equals(duelTargetPlayer.getName()) && target.getName().equals(duelTargetTarget.getName())) {
            return true;

        }
        return false;

    }

    public String getDuelInvite(Player player) {
        String target = "null";

        if (!duelInvitations.containsKey(player.getName())) {
            return target;
        }

        DuelInvitationKey key = duelInvitations.get(player.getName());
        return key.getPlayerName();
    }
    
    public boolean duelInviteIsTimedout(Player player) {
        DuelInvitationKey key = duelInvitations.get(player.getName());
        if (key.getTimestamp() + Config.getInviteTimeout() <= Misc.getSystemTime()) {
            return false;
        }

        return true;
    }

    public boolean removeDuelInvite(Player player) {
        if (duelInvitations.containsKey(player.getName())) {
            duelInvitations.remove(player.getName());
            return true;
        }
        return false;
    }

    public void setDuelInvite(Player player, Player target) {
        if (getDuelInvite(target).equals(player.getName())) {
            return;
        }

        int timestamp = (int) (System.currentTimeMillis() / 1000);

        player.sendMessage(ChatColor.GREEN + "You have challenged " + ChatColor.GOLD + target.getName() + ChatColor.GREEN + " to a duel!");
        duelInvitations.put(target.getName(), new DuelInvitationKey(player.getName(), timestamp));
        target.sendMessage(ChatColor.GOLD + player.getName() + ChatColor.GREEN + " has just challenged you to a duel!");
        target.sendMessage(ChatColor.GREEN + "To accept right-click " + ChatColor.GOLD + player.getName());
    }

    public static List<Player> getDuelingPlayers() {
        Player[] onlinePlayers = Duel.getInstance().getServer().getOnlinePlayers();
        List<Player> duelingPlayers = new ArrayList<Player>();

        for (Player onlinePlayer : onlinePlayers) {
            if (duels.containsKey(onlinePlayer.getName())) {
                duelingPlayers.add(onlinePlayer);
            }
        }
        return duelingPlayers;
    }

    public static void storeItemsDeath(Player player, List<ItemStack> items) {
        String playerName = player.getName();

        if (savedItems.containsKey(playerName)) {
            savedItems.put(playerName, null);
        }

        savedItems.put(playerName, items);
    }

    public static List<ItemStack> retrieveItemsDeath(Player player) {
        String playerName = player.getName();

        if (savedItems.containsKey(playerName)) {
            return savedItems.get(playerName);
        }
        else {
            return null;
        }
    }
}