package com.misagh.bwquest;

import com.andrei1058.bedwars.api.arena.GameState;
import com.andrei1058.bedwars.api.events.gameplay.GameEndEvent;
import com.andrei1058.bedwars.api.events.player.PlayerBedBreakEvent;
import com.andrei1058.bedwars.api.events.player.PlayerKillEvent;
import com.andrei1058.bedwars.api.events.gameplay.GameStateChangeEvent;
import com.andrei1058.bedwars.api.events.shop.ShopBuyEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class Quests1058 implements Listener {
    private static final Map<Player, Integer> activeQuestPlayers = new HashMap<>();
    private static final Map<Player, Integer> activePlayQuestPlayers = new HashMap<>();
    private static final Map<Player, Integer> activeBedQuestPlayers = new HashMap<>();
    private static final Set<String> completedPlayers = new HashSet<>();
    private static final Set<String> completedPlayersbedbreak = new HashSet<>();
    private static final Set<String> completedPlayersplay  = new HashSet<>();
    private static final Set<String> completedPlayerswin = new HashSet<>();
    private static final Set<String> completedPlayersbuy = new HashSet<>();
    private static final Map<Player, Integer> activeWinQuestPlayers = new HashMap<>();
    private static final Map<Player, Integer> activeBuyQuestPlayers = new HashMap<>();


    private void rewardPlayer(Player player, int amount) {
        if (Bwquest.getEconomy() != null) {
            Bwquest.getEconomy().depositPlayer(player, amount);
            player.sendMessage(ChatColor.GOLD + "You have received " + amount + " coins!");
        } else {
            player.sendMessage(ChatColor.RED + "Economy system not available!");
        }
    }

    private void saveQuestData(Player player, String questName) {
        if (ConfigManager.isMySQLStorage()) {
            MysqlManager.saveQuestData(player.getName(), questName);
        } else {
            YamlManager.saveQuestData(player.getName(), questName);
        }
    }

    private String getQuestData(Player player) {
        if (ConfigManager.isMySQLStorage()) {
            return MysqlManager.getQuestData(player.getName());
        } else {
            return YamlManager.getQuestData(player.getName());
        }
    }

    public static void resetQuests() {
        if (ConfigManager.isMySQLStorage()) {
            MysqlManager.clearQuestData();
        } else {
            YamlManager.clearQuestData();
        }
        Bukkit.getLogger().info("All quest data has been reset.");
        Bukkit.getScheduler().runTaskTimer(Bwquest.getInstance(), () -> {
        }, 0L, 24 * 60 * 60 * 20L);

    }

    public static void openQuestGUI(Player player) {

        Inventory gui = Bukkit.createInventory(null, 27, ChatColor.GREEN + "Quests");

        ItemStack book = new ItemStack(Material.BOOK);
        ItemMeta bookMeta = book.getItemMeta();

        bookMeta.setDisplayName(ChatColor.AQUA + "Quest Information");
        bookMeta.setLore(Arrays.asList(
                ChatColor.GRAY + "Complete quests to earn rewards!",
                ChatColor.GRAY + "Choose a quest to get started."
        ));
        book.setItemMeta(bookMeta);
        gui.setItem(0, book);
        ItemStack finalKillPaper = new ItemStack(Material.PAPER);
        ItemMeta finalKillMeta = finalKillPaper.getItemMeta();
        if (completedPlayers.contains(player.getName())) {
            finalKillMeta.setDisplayName(ChatColor.RED + "Quest Already Completed");
            finalKillMeta.setLore(Arrays.asList(
                    ChatColor.GRAY + "You have already completed this quest.",
                    ChatColor.GRAY + "Try later!"
            ));
        } else if (activeQuestPlayers.containsKey(player)) {
            finalKillMeta. setDisplayName(ChatColor.RED + "Quest In Progress");
            finalKillMeta.setLore(Arrays.asList(
                    ChatColor.GRAY + "You are already in a quest!",
                    ChatColor.GRAY + "Please complete your quest first."
            ));
        } else {
            finalKillMeta.setDisplayName(ChatColor.GOLD + "Start Final Kill Quest");
            finalKillMeta.setLore(Arrays.asList(ChatColor.YELLOW + "Click to start the quest!", ChatColor.GRAY + "Get rewards for final kills."));
            int finalKill = ConfigManager.getReward("final_kill");
            finalKillMeta.setLore(Arrays.asList(ChatColor.AQUA + "+ " + finalKill + " Coins"));

        }
        finalKillPaper.setItemMeta(finalKillMeta);
        gui.setItem(11, finalKillPaper);

        ItemStack bedBreakPaper = new ItemStack(Material.PAPER);
        ItemMeta bedBreakMeta = bedBreakPaper.getItemMeta();
        if (completedPlayersbedbreak.contains(player.getName())) {
            bedBreakMeta.setDisplayName(ChatColor.RED + "Quest Already Completed");
            bedBreakMeta.setLore(Arrays.asList(
                    ChatColor.GRAY + "You have already completed this quest.",
                    ChatColor.GRAY + "Try later!"
            ));
        } else if (activeBedQuestPlayers.containsKey(player)) {
            bedBreakMeta.setDisplayName(ChatColor.RED + "Bed Break Quest In Progress");
            bedBreakMeta.setLore(Arrays.asList(
                    ChatColor.GRAY + "You are already in a quest!",
                    ChatColor.GRAY + "Please complete your quest first."
            ));
        } else {
            bedBreakMeta.setDisplayName(ChatColor.GOLD + "Start Bed Break Quest");
            bedBreakMeta.setLore(Arrays.asList(ChatColor.YELLOW + "Click to start the quest!", ChatColor.GRAY + "Get rewards for breaking beds."));
            int bedbreak = ConfigManager.getReward("bed_break");
            bedBreakMeta.setLore(Arrays.asList(ChatColor.AQUA + "+ " + bedbreak + " Coins"));
        }
        bedBreakPaper.setItemMeta(bedBreakMeta);
        gui.setItem(15, bedBreakPaper);

        player.openInventory(gui);
        ItemStack winPaper = new ItemStack(Material.PAPER);
        ItemMeta winMeta = winPaper.getItemMeta();
        if (completedPlayerswin.contains(player.getName())) {
            winMeta.setDisplayName(ChatColor.RED + "Quest Already Completed");
            winMeta.setLore(Arrays.asList(
                    ChatColor.GRAY + "You have already completed this quest.",
                    ChatColor.GRAY + "Try later!"
            ));
        } else if (activeWinQuestPlayers.containsKey(player)) {
            winMeta.setDisplayName(ChatColor.RED + "Win Quest In Progress");
            winMeta.setLore(Arrays.asList(
                    ChatColor.GRAY + "You are already in a quest!",
                    ChatColor.GRAY + "Please complete your quest first."
            ));
        } else {
            winMeta.setDisplayName(ChatColor.GOLD + "Start Win Quest");
            winMeta.setLore(Arrays.asList(ChatColor.YELLOW + "Click to start the quest!", ChatColor.GRAY + "Get rewards for winning games."));
            int win = ConfigManager.getReward("win");
            winMeta.setLore(Arrays.asList(ChatColor.AQUA + "+ " + win + " Coins"));
        }

        winPaper.setItemMeta(winMeta);
        gui.setItem(13, winPaper);
        player.openInventory(gui);
        ItemStack playQuestPaper = new ItemStack(Material.PAPER);
        ItemMeta playQuestMeta = playQuestPaper.getItemMeta();
        if (completedPlayersplay.contains(player.getName())) {
            playQuestMeta.setDisplayName(ChatColor.RED + "Quest Already Completed");
            playQuestMeta.setLore(Arrays.asList(
                    ChatColor.GRAY + "You have already completed this quest.",
                    ChatColor.GRAY + "Try later!"
            ));
        } else if (activePlayQuestPlayers.containsKey(player)) {
            playQuestMeta.setDisplayName(ChatColor.RED + "Play Quest In Progress");
            playQuestMeta.setLore(Arrays.asList(
                    ChatColor.GRAY + "You are already in a quest!",
                    ChatColor.GRAY + "Please complete your quest first."
            ));
        } else {
            playQuestMeta.setDisplayName(ChatColor.GOLD + "Start Play Quest");
            playQuestMeta.setLore(Arrays.asList(ChatColor.YELLOW + "Click to start the quest!", ChatColor.GRAY + "Get rewards for playing games."));
            int play = ConfigManager.getReward("play");
            playQuestMeta.setLore(Arrays.asList(ChatColor.AQUA + "- " + play + " Coins"));
        }

        playQuestPaper.setItemMeta(playQuestMeta);
        gui.setItem(17, playQuestPaper);
        ItemStack buyQuestPaper = new ItemStack(Material.PAPER);
        ItemMeta buyQuestMeta = buyQuestPaper.getItemMeta();
        if (completedPlayersbuy.contains(player.getName())) {
            buyQuestMeta.setDisplayName(ChatColor.RED + "Quest Already Completed");
            buyQuestMeta.setLore(Arrays.asList(
                    ChatColor.GRAY + "You have already completed this quest.",
                    ChatColor.GRAY + "Try later!"
            ));
        } else if (activeBuyQuestPlayers.containsKey(player)) {
            buyQuestMeta.setDisplayName(ChatColor.RED + "Buy Quest In Progress");
            buyQuestMeta.setLore(Arrays.asList(
                    ChatColor.GRAY + "You are already in a quest!",
                    ChatColor.GRAY + "Please complete your quest first."
            ));
        } else {
            buyQuestMeta.setDisplayName(ChatColor.GOLD + "Start Buy Quest");
            buyQuestMeta.setLore(Arrays.asList(ChatColor.YELLOW + "Click to start the quest!", ChatColor.GRAY + "Get rewards for buying items."));
            int buy = ConfigManager.getReward("buy");
            buyQuestMeta.setLore(Arrays.asList(ChatColor.AQUA + "- " + buy + " Coins"));
        }
        buyQuestPaper.setItemMeta(buyQuestMeta);
        gui.setItem(9, buyQuestPaper);
        ItemStack autoAcceptItem = new ItemStack(Material.EMERALD);
        ItemMeta autoAcceptMeta = autoAcceptItem.getItemMeta();
        autoAcceptMeta.setDisplayName(ChatColor.GOLD + "Enable AutoAccept Quests");
        autoAcceptMeta.setLore(Arrays.asList(
                ChatColor.YELLOW + "Click to automatically accept all quests!",
                ChatColor.GRAY + "Requires permission: bwquest.autoaccept"
        ));
        autoAcceptItem.setItemMeta(autoAcceptMeta);

        gui.setItem(26, autoAcceptItem);

        player.openInventory(gui);
    }

    @EventHandler
    public void onInventoryClick(org.bukkit.event.inventory.InventoryClickEvent event) {

        Player player = (Player) event.getWhoClicked();

        if (event.getView().getTitle().equals(ChatColor.GREEN + "Quests")) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null) return;
            if (event.getSlot() == 26) {
                if (!player.hasPermission("bwquest.autoaccept")) {
                    player.sendMessage(ChatColor.RED + "You do not have permission to use AutoAccept!");
                    return;
                }

                if (!activeQuestPlayers.containsKey(player)) {
                    activeQuestPlayers.put(player, 0);
                    player.sendMessage(ChatColor.GREEN + "Final Kill Quest automatically started!");
                }
                if (!activeBedQuestPlayers.containsKey(player)) {
                    activeBedQuestPlayers.put(player, 0);
                    player.sendMessage(ChatColor.GREEN + "Bed Break Quest automatically started!");
                }
                if (!activeWinQuestPlayers.containsKey(player)) {
                    activeWinQuestPlayers.put(player, 0);
                    player.sendMessage(ChatColor.GREEN + "Win Quest automatically started!");
                }
                if (!activePlayQuestPlayers.containsKey(player)) {
                    activePlayQuestPlayers.put(player, 0);
                    player.sendMessage(ChatColor.GREEN + "Play Quest automatically started!");
                }
                if (!activeBuyQuestPlayers.containsKey(player)) {
                    activeBuyQuestPlayers.put(player, 0);
                    player.sendMessage(ChatColor.GREEN + "Buy Quest automatically started!");
                }

                player.closeInventory();
            }
        }
        if (event.getView().getTitle().equals(ChatColor.GREEN + "Quests")) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null) return;
            if (event.getSlot() == 11) {
                if (completedPlayers.contains(player.getName())) {
                    player.sendMessage(ChatColor.RED + "You have already completed this quest!");
                    return;
                }
                if (activeQuestPlayers.containsKey(player)) {
                    player.sendMessage(ChatColor.RED + "You have already started the quest!");
                    return;
                }
                activeQuestPlayers.put(player, 0);
                player.sendMessage(ChatColor.GREEN + "Final Kill Quest started!");
                player.closeInventory();
            }

            if (event.getSlot() == 15) {
                if (completedPlayersbedbreak.contains(player.getName())) {
                    player.sendMessage(ChatColor.RED + "You have already completed this quest!");
                    return;
                }
                if (activeBedQuestPlayers.containsKey(player)) {
                    player.sendMessage(ChatColor.RED + "You have already started the bed break quest!");
                    return;
                }
                activeBedQuestPlayers.put(player, 0);
                player.sendMessage(ChatColor.GREEN + "Bed Break Quest started!");
                player.closeInventory();


            }
            if (event.getSlot() == 13) {
                if (completedPlayerswin.contains(player.getName())) {
                    player.sendMessage(ChatColor.RED + "You have already completed this quest!");
                    return;
                }
                if (activeWinQuestPlayers.containsKey(player)) {
                    player.sendMessage(ChatColor.RED + "You have already started the win quest!");
                    return;
                }
                activeWinQuestPlayers.put(player, 0);
                player.sendMessage(ChatColor.GREEN + "Win Quest started!");
                player.closeInventory();
            }
            if (event.getSlot() == 17) {
                if (completedPlayersplay.contains(player.getName())) {
                    player.sendMessage(ChatColor.RED + "You have already completed this quest!");
                    return;
                }
                if (activePlayQuestPlayers.containsKey(player)) {
                    player.sendMessage(ChatColor.RED + "You have already started the play quest!");
                    return;
                }
                activePlayQuestPlayers.put(player, 0);
                player.sendMessage(ChatColor.GREEN + "Play Quest started!");
                player.closeInventory();
            }
            if (event.getSlot() == 9) {
                if (completedPlayersbuy.contains(player.getName())) {
                    player.sendMessage(ChatColor.RED + "You have already completed this quest!");
                    return;
                }
                if (activeBuyQuestPlayers.containsKey(player)) {
                    player.sendMessage(ChatColor.RED + "You have already started the buy quest!");
                    return;
                }
                activeBuyQuestPlayers.put(player, 0);
                player.sendMessage(ChatColor.GREEN + "Buy Quest started!");
                player.closeInventory();
            }

        }
    }

    @EventHandler
    public void onFinalKill(PlayerKillEvent event) {
        if (!event.getCause().toString().contains("FINAL_KILL")) return;
        Player killer = event.getKiller();
        if (killer == null || !activeQuestPlayers.containsKey(killer)) return;
        int currentKills = activeQuestPlayers.get(killer) + 1;
        if (currentKills >= 3) {
            int finalkill = ConfigManager.getReward("final_kill");
            String mode = event.getArena().getGroup();
            activeQuestPlayers.remove(killer);
            completedPlayers.add(killer.getName());
            killer.sendMessage(ChatColor.GOLD + "Final Kill Quest complete! Rewards:");
            rewardPlayer(killer, finalkill);
            saveQuestData(killer, "final_kill");
        } else {
            activeQuestPlayers.put(killer, currentKills);
        }
    }
    @EventHandler
    public void onBedBreak(PlayerBedBreakEvent event) {
        Player player = event.getPlayer();
        if (player == null || !activeBedQuestPlayers.containsKey(player)) return;
        int currentBeds = activeBedQuestPlayers.get(player) + 1;
        if (currentBeds >= 3) {
            String mode = event.getArena().getGroup();
            int bedbreak = ConfigManager.getReward("bed_break");
            activeBedQuestPlayers.remove(player);
            completedPlayersbedbreak.add(player.getName());
            player.sendMessage(ChatColor.GOLD + "Bed Break Quest complete! Rewards:");
            rewardPlayer(player, bedbreak);
            saveQuestData(player, "bed_break");
        } else {
            activeBedQuestPlayers.put(player, currentBeds);}
    }

    @EventHandler
    public void onWin(GameStateChangeEvent e) {
        if (e.getNewState() != GameState.restarting) return;

        e.getArena().getPlayers().forEach(player -> {
            if (!activeWinQuestPlayers.containsKey(player)) return;

            int currentWins = activeWinQuestPlayers.get(player) + 1;
            if (currentWins >= 5) {
                activeWinQuestPlayers.remove(player);
                completedPlayerswin.add(player.getName());
                int win = ConfigManager.getReward("win");
                player.sendMessage(ChatColor.GOLD + "Win Quest complete! Rewards:");
                rewardPlayer(player, win);
                saveQuestData(player, "win");
            } else {
                activeWinQuestPlayers.put(player, currentWins);
            }
        });
    }
    @EventHandler
    public void onGameEnd(GameEndEvent e) {
        List<Player> players = e.getArena().getPlayers();
        for (Player player : players) {
            if (!activePlayQuestPlayers.containsKey(player)) continue;

            int currentGames = activePlayQuestPlayers.get(player) + 1;
            if (currentGames >= 5) {
                activePlayQuestPlayers.remove(player);
                completedPlayersplay.add(player.getName());
                int play = ConfigManager.getReward("play");
                player.sendMessage(ChatColor.GOLD + "Play Quest complete! Rewards:");
                rewardPlayer(player, play);
                saveQuestData(player, "play");
            } else {
                activePlayQuestPlayers.put(player, currentGames);
            }
        }
    }
    @EventHandler
    public void onPlayerBuy(ShopBuyEvent e) {
        Player player = e.getBuyer();
        if (!activeBuyQuestPlayers.containsKey(player)) return;
        int currentBuys = activeBuyQuestPlayers.get(player) + 1;
        if (currentBuys >= 10) {
            activeBuyQuestPlayers.remove(player);
            completedPlayersbuy.add(player.getName());
            int buy = ConfigManager.getReward("buy");
            player.sendMessage(ChatColor.GOLD + "Buy Quest complete! Rewards:");
            rewardPlayer(player, buy);
            saveQuestData(player, "buy");
        } else {
            activeBuyQuestPlayers.put(player, currentBuys);
        }
    }
}
