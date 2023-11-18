package com.github.joelgodofwar.mmh.commands;

import com.github.joelgodofwar.mmh.Config;
import com.github.joelgodofwar.mmh.Heads;
import com.github.joelgodofwar.mmh.MoreMobHeads;
import com.github.joelgodofwar.mmh.MoreMobHeadsLib;
import com.github.joelgodofwar.mmh.enums.*;
import com.github.joelgodofwar.mmh.gui.GuiHeadsList;
import com.github.joelgodofwar.mmh.util.ChatColorUtils;
import com.github.joelgodofwar.mmh.util.StrUtils;
import com.github.joelgodofwar.mmh.util.Utils;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import static com.github.joelgodofwar.mmh.MoreMobHeads.debug;
import static com.github.joelgodofwar.mmh.MoreMobHeads.get;

@SuppressWarnings({"deprecation"})
public class MMHCommand implements CommandExecutor, TabCompleter {
    MoreMobHeads mmh;

    public FileConfiguration blockHeads;
    public FileConfiguration blockHeads2;
    public FileConfiguration blockHeads3;

    public MMHCommand(MoreMobHeads mmh) {
        this.mmh = mmh;
        PluginCommand command = mmh.getCommand("moremobheads");
        if (command != null) {
            command.setExecutor(this);
            command.setTabCompleter(this);
        } else {
            mmh.log(Level.WARNING, "Command /moremobhead not found in plugin.yml! We can't register command.");
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.GREEN + "[]===============[" + ChatColor.YELLOW + mmh.getName()
                    + ChatColor.GREEN + "]===============[]");
            sender.sendMessage(ChatColor.WHITE + " ");
            sender.sendMessage(
                    ChatColor.WHITE + " /mmh reload - " + get("mmh.command.reload", "Reloads this plugin."));// subject

            sender.sendMessage(ChatColor.WHITE + " /mmh toggledebug - "
                    + get("mmh.command.debuguse", "Temporarily toggles debug."));
            if (mmh.getConfig().getBoolean("wandering_trades.custom_wandering_trader", true)) {
                sender.sendMessage(ChatColor.WHITE + " /mmh playerheads - "
                        + get("mmh.command.playerheads", "Shows how to use the playerheads commands"));

                sender.sendMessage(ChatColor.WHITE + " /mmh customtrader - "
                        + get("mmh.command.customtrader", "Shows how to use the customtrader commands"));
            }
            sender.sendMessage(ChatColor.WHITE + " /mmh fixhead - " + get("mmh.command.headfix"));
            sender.sendMessage(ChatColor.WHITE + " /mmh givemh - " + get("mmh.command.give.mobhead"));
            sender.sendMessage(ChatColor.WHITE + " /mmh giveph - " + get("mmh.command.give.playerhead"));
            sender.sendMessage(ChatColor.WHITE + " /mmh givebh - " + get("mmh.command.give.blockhead"));
            sender.sendMessage(ChatColor.WHITE + " /mmh list - " + get("mmh.command.list"));
            sender.sendMessage(
                    ChatColor.WHITE + " /mmh display perms/vars - " + get("mmh.command.display.help"));
            sender.sendMessage(ChatColor.WHITE + " ");
            sender.sendMessage(ChatColor.GREEN + "[]===============[" + ChatColor.YELLOW + mmh.getName()
                    + ChatColor.GREEN + "]===============[]");
            return true;
        }
        if (args[0].equalsIgnoreCase("list")) {
            Player player = null;

            String perm = "moremobheads.list";
            if (sender instanceof Player) {
                player = (Player) sender;
            } else if (args.length == 2) {
                perm = "moremobheads.list.other";
                player = sender.getServer().getPlayer(args[1]);
            }
            boolean hasPerm = sender.hasPermission(perm) || !(sender instanceof Player);
            if (!hasPerm) {
                sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.RED + get("mmh.message.noperm").replace("<perm>", perm));
                return true;
            }
            if (player == null) {
                sender.sendMessage(get("mmh.command.player.offline"));
                return true;
            }
            mmh.getGuiHandler().openGui(new GuiHeadsList(mmh, player, 1));
            return true;
        }
        if (args[0].equalsIgnoreCase("headNBT")) {
            if (!(sender instanceof Player)) {
                return true;
            }
            Player player = (Player) sender;
            ItemStack mainHand = player.getInventory().getItemInMainHand();
            ItemStack offHand = player.getInventory().getItemInOffHand();
            if (mainHand.getType().equals(Material.PLAYER_HEAD)) {
                NBTItem item = new NBTItem(mainHand);
                log("" + item);
                player.sendMessage("" + item);
            } else if (offHand.getType().equals(Material.PLAYER_HEAD)) {
                NBTItem item = new NBTItem(offHand);
                player.sendMessage("" + item);
                log("" + item);
            } else {
                player.sendMessage("" + get("mmh.command.headnbt"));
            }
            return true;
        }
        // /mmh display permvar playername
        if (args[0].equalsIgnoreCase("display")) {
            if (args[1].equalsIgnoreCase("perms") || args[1].equalsIgnoreCase("permissions")) {
                Player player;
                if (sender instanceof Player) {
                    player = (Player) sender;
                } else {
                    player = sender.getServer().getPlayer(args[2]);
                }
                if (player == null) {
                    sender.sendMessage(get("mmh.command.player.offline"));
                    return true;
                }
                sender.sendMessage(
                        "" + get("mmh.command.display.you").replace("<player>", player.getDisplayName()));
                sender.sendMessage("moremobheads.players=" + player.hasPermission("moremobheads.players"));
                sender.sendMessage("moremobheads.mobs=" + player.hasPermission("moremobheads.mobs"));
                sender.sendMessage("moremobheads.nametag=" + player.hasPermission("moremobheads.nametag"));
                sender.sendMessage("moremobheads.reload=" + player.hasPermission("moremobheads.reload"));
                sender.sendMessage(
                        "moremobheads.toggledebug=" + player.hasPermission("moremobheads.toggledebug"));
                sender.sendMessage("moremobheads.showUpdateAvailable="
                        + player.hasPermission("moremobheads.showUpdateAvailable"));
                sender.sendMessage(
                        "moremobheads.customtrader=" + player.hasPermission("moremobheads.customtrader"));
                sender.sendMessage(
                        "moremobheads.playerheads=" + player.hasPermission("moremobheads.playerheads"));
                sender.sendMessage(
                        "moremobheads.blockheads=" + player.hasPermission("moremobheads.blockheads"));
                sender.sendMessage("moremobheads.fixhead=" + player.hasPermission("moremobheads.fixhead"));
                sender.sendMessage("moremobheads.give=" + player.hasPermission("moremobheads.give"));
                sender.sendMessage(
                        "" + mmh.getName() + " " + mmh.getDescription().getVersion() + " display perms end");
                return true;
            }
            if (args[1].equalsIgnoreCase("vars") || args[1].equalsIgnoreCase("variables")) {
                sender.sendMessage(
                        "" + mmh.getName() + " " + mmh.getDescription().getVersion() + " display varss start");
                sender.sendMessage("debug=" + debug);
                sender.sendMessage("daLang=" + MoreMobHeads.languageName);

                String world_whitelist = mmh.getConfig().getString("world.whitelist", "");
                String world_blacklist = mmh.getConfig().getString("world.blacklist", "");
                String mob_whitelist = mmh.getConfig().getString("mob.whitelist", "");
                String mob_blacklist = mmh.getConfig().getString("mob.blacklist", "");

                sender.sendMessage("world_whitelist=" + world_whitelist);
                sender.sendMessage("world_blacklist=" + world_blacklist);
                sender.sendMessage("mob_whitelist=" + mob_whitelist);
                sender.sendMessage("mob_blacklist=" + mob_blacklist);
                sender.sendMessage(mmh.getName() + " " + mmh.getDescription().getVersion() + " display varss end");
                return true;
            }
            if (args[1].equalsIgnoreCase("chance") || args[1].equalsIgnoreCase("chance_percent")) {
                ConfigurationSection cs = mmh.chanceConfig.getConfigurationSection("chance_percent");
                List<String> daSet = new ArrayList<>();
                // log(Level.INFO, "args.lngth=" + args.length);
                if (args.length == 3) {
                    if (cs != null) for (String key : cs.getKeys(true)) {
                        if (key.contains(args[2])) {
                            sender.sendMessage("" + key + "=" + cs.get(key));
                            daSet.add("" + key + "=" + cs.get(key));
                        }
                    }

                    if (!daSet.isEmpty()) {
                        File chanceFile = new File(mmh.getDataFolder(), "logs/chance_dump.log");
                        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(chanceFile), StandardCharsets.UTF_8))) {
                            for (String s : daSet) {
                                pw.println(s);
                            }
                            pw.flush();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        sender.sendMessage("chance_config.yml has been dumped into " + chanceFile);
                        log("chance_config.yml has been dumped into " + chanceFile);
                    } else {
                        sender.sendMessage("" + args[2] + " was not found in chance_percent.yml");
                    }
                } else {
                    if (cs != null) for (String key : cs.getKeys(true)) {
                        String value = cs.get(key, "").toString();
                        if (!value.contains("MemorySection")) {
                            sender.sendMessage("" + key + "=" + value);
                            daSet.add("" + key + "=" + value);
                        }
                    }
                    if (!daSet.isEmpty()) {
                        File chanceFile = new File(mmh.getDataFolder(), "logs/chance_dump.log");
                        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(chanceFile), StandardCharsets.UTF_8))) {
                            for (String s : daSet) {
                                pw.println(s);
                            }
                            pw.flush();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        sender.sendMessage("chance_config.yml has been dumped into " + chanceFile);
                        log("chance_config.yml has been dumped into " + chanceFile);
                    } else {
                        sender.sendMessage("Error dumping chance_percent.yml");
                    }
                }
                return true;
            }
            return true;
        }
        if (args[0].equalsIgnoreCase("reload")) {
            String perm = "moremobheads.reload";
            boolean hasPerm = sender.hasPermission(perm) || !(sender instanceof Player);
            if (debug) {
                mmh.logDebug(sender.getName() + " has the permission " + perm + "=" + hasPerm);
            }
            if (sender.isOp() || hasPerm) {
                Config.reload(mmh);
                sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.RED + get("mmh.message.reloaded"));
            } else {
                sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.RED + get("mmh.message.noperm").replace("<perm>", perm));
            }
            return true;
        }
        if (args[0].equalsIgnoreCase("toggledebug") || args[0].equalsIgnoreCase("td")) {
            String perm = "moremobheads.toggledebug";
            boolean hasPerm = sender.hasPermission(perm) || !(sender instanceof Player);
            if (debug) {
                logDebug(sender.getName() + " has the permission " + perm + "=" + hasPerm);
            }
            if (sender.isOp() || hasPerm) {
                debug = !debug;
                sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.RED + get("mmh.message.debugtrue").replace("boolean", "" + debug));
            } else {
                sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.RED + get("mmh.message.noperm").replace("<perm>", perm));
            }
            return true;
        }
        if (args[0].equalsIgnoreCase("customtrader") || args[0].equalsIgnoreCase("ct")) {
            String perm = "moremobheads.customtrader";
            boolean hasPerm = sender.hasPermission(perm);
            if (debug) {
                logDebug(sender.getName() + " has the permission " + perm + "=" + hasPerm);
            }
            if (hasPerm && (sender instanceof Player)
                    && mmh.getConfig().getBoolean("wandering_trades.custom_wandering_trader", true)) {
                log("has permission");
                Player player = (Player) sender;
                if (!(args.length >= 2)) {
                    sender.sendMessage(ChatColor.GREEN + "[]===============[" + ChatColor.YELLOW + mmh.getName()
                            + ChatColor.GREEN + "]===============[]");
                    sender.sendMessage(ChatColor.WHITE + " ");
                    sender.sendMessage(ChatColor.WHITE + " /mmh ct - " + get("mmh.command.ct.help"));
                    sender.sendMessage(ChatColor.WHITE + " /mmh ct add - " + get("mmh.command.ct.add")
                            + "custom_trades.yml");
                    sender.sendMessage(ChatColor.WHITE + " /mmh ct remove # - " + get("mmh.command.ct.remove"));
                    sender.sendMessage(ChatColor.WHITE + " /mmh ct replace # - "
                            + get("mmh.command.ct.replace").replace("<num>", "#"));
                    sender.sendMessage(ChatColor.WHITE + " ");
                    sender.sendMessage(ChatColor.GREEN + "[]===============[" + ChatColor.YELLOW + mmh.getName()
                            + ChatColor.GREEN + "]===============[]");
                    return true;
                } else if (args[1].equalsIgnoreCase("add")) {
                    if (debug) {
                        logDebug("CMD CT ADD Start -----");
                    }
                    ItemStack itemstack = player.getInventory().getItemInOffHand();
                    ItemStack price1 = player.getInventory().getItem(0);
                    ItemStack price2 = player.getInventory().getItem(1);
                    if (price1 == null) {
                        price1 = new ItemStack(Material.AIR);
                    }
                    if (price2 == null) {
                        price2 = new ItemStack(Material.AIR);
                    }

                    if ((itemstack.getType() == Material.AIR) || (price1.getType() == Material.AIR)) {
                        log("error air");
                        sender.sendMessage(ChatColor.GREEN + "[]===============[" + ChatColor.YELLOW + mmh.getName()
                                + ChatColor.GREEN + "]===============[]");
                        sender.sendMessage(ChatColor.WHITE + " ");
                        sender.sendMessage(
                                ChatColor.WHITE + " " + get("mmh.command.ct.line1") + "custom_trades.yml");
                        sender.sendMessage(ChatColor.WHITE + " " + get("mmh.command.ct.line2"));
                        sender.sendMessage(ChatColor.WHITE + " " + get("mmh.command.ct.line3"));
                        sender.sendMessage(ChatColor.WHITE + " " + get("mmh.command.ct.line4") + "/mmh ct add");
                        sender.sendMessage(
                                ChatColor.WHITE + " " + get("mmh.command.ct.line5") + "custom trade.");
                        sender.sendMessage(ChatColor.WHITE + " ");
                        sender.sendMessage(ChatColor.GREEN + "[]===============[" + ChatColor.YELLOW + mmh.getName()
                                + ChatColor.GREEN + "]===============[]");
                        if (debug) {
                            logDebug("CMD CT ADD End Error -----");
                        }
                        return true;
                    }
                    int tradeNumber = (int) mmh.traderCustom.get("custom_trades.number", 1);
                    mmh.traderCustom.set("custom_trades.trade_" + (tradeNumber + 1) + ".price_1", price1);
                    mmh.traderCustom.set("custom_trades.trade_" + (tradeNumber + 1) + ".price_2", price2);
                    mmh.traderCustom.set("custom_trades.trade_" + (tradeNumber + 1) + ".itemstack", itemstack);
                    mmh.traderCustom.set("custom_trades.trade_" + (tradeNumber + 1) + ".quantity",
                            itemstack.getAmount());
                    mmh.traderCustom.set("custom_trades.trade_" + (tradeNumber + 1) + ".chance", 0.002);
                    mmh.traderCustom.set("custom_trades.number", (tradeNumber + 1));
                    if (debug) {
                        logDebug("CMD CT ADD price1=" + price1.getType());
                    }
                    if (debug) {
                        logDebug("CMD CT ADD price2=" + price2.getType());
                    }
                    if (debug) {
                        logDebug("CMD CT ADD itemstack=" + itemstack.getType());
                    }
                    if (debug) {
                        if (itemstack.getType() == Material.PLAYER_HEAD) {
                            ItemMeta skullMeta = Utils.getItemMeta(itemstack);
                            logDebug("CMD CT ADD IS DisplayName=" + skullMeta.getDisplayName());
                            if (skullMeta.hasLore() && skullMeta.getLore() != null) {
                                logDebug("CMD CT ADD IS lore=" + String.join(",", skullMeta.getLore()));
                            }
                        }
                    }
                    if (debug) {
                        logDebug("CMD CT ADD quantity=" + itemstack.getAmount());
                    }
                    if (debug) {
                        logDebug("CMD CT ADD chance=0.002");
                    }
                    // log("customFile=" + customFile);
                    try {
                        mmh.traderCustom.save(mmh.customFile);
                        mmh.traderCustom.load(mmh.customFile);
                    } catch (IOException | InvalidConfigurationException e) {
                        mmh.stacktraceInfo();
                        e.printStackTrace();
                    }
                    if (debug) {
                        logDebug("CMD CT ADD End -----");
                    }
                    sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.WHITE + " trade_"
                            + (tradeNumber + 1) + " " + get("mmh.message.ct.successadd"));
                    return true;
                } else if (args[1].equalsIgnoreCase("remove")) {
                    if (debug) {
                        logDebug("CMD CT Remove Start -----");
                    }
                    if (!(args.length >= 3)) {
                        sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.RED + " "
                                + get("mmh.command.ct.argument"));
                        return true;
                    } else if (Utils.isInteger(args[2])) {
                        mmh.traderCustom.set("custom_trades.trade_" + args[2] + ".price_1", "");
                        mmh.traderCustom.set("custom_trades.trade_" + args[2] + ".price_2", "");
                        mmh.traderCustom.set("custom_trades.trade_" + args[2] + ".itemstack", "");
                        mmh.traderCustom.set("custom_trades.trade_" + args[2] + ".quantity", "");
                        mmh.traderCustom.set("custom_trades.trade_" + args[2] + ".chance", "");
                        if (debug) {
                            logDebug("customFile=" + mmh.customFile);
                        }
                        try {
                            mmh.traderCustom.save(mmh.customFile);
                            mmh.traderCustom.load(mmh.customFile);
                        } catch (IOException | InvalidConfigurationException e) {
                            if (debug) {
                                logDebug("CMD CT Remove End Exception -----");
                            }
                            sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.RED + " "
                                    + get("mmh.command.ct.error"));
                            return true;
                            // e.printStackTrace();
                        }
                        if (debug) {
                            logDebug("CMD CT Remove End -----");
                        }
                        sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.WHITE + " trade_"
                                + args[2] + " " + get("mmh.message.ct.successrem"));
                        return true;
                    } else {
                        if (debug) {
                            logDebug("CMD CT Remove End 2 -----");
                        }
                        sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.RED + " "
                                + get("mmh.command.ct.numberreq"));
                        return true;
                    }
                } else if (args[1].equalsIgnoreCase("replace")) {
                    if (debug) {
                        logDebug("CMD CT Replace Start -----");
                    }
                    if (args.length != 3) {
                        sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.RED + " "
                                + get("mmh.command.ct.argument"));
                        return true;
                    } else if (Utils.isInteger(args[2])) {
                        ItemStack itemstack = player.getInventory().getItemInOffHand();
                        ItemStack price1 = player.getInventory().getItem(0);
                        ItemStack price2 = player.getInventory().getItem(1);
                        if (price1 == null) {
                            price1 = new ItemStack(Material.AIR);
                        }
                        if (price2 == null) {
                            price2 = new ItemStack(Material.AIR);
                        }
                        if (itemstack.getType() == Material.AIR || price1.getType() == Material.AIR) {
                            sender.sendMessage(ChatColor.GREEN + "[]===============[" + ChatColor.YELLOW
                                    + mmh.getName() + ChatColor.GREEN + "]===============[]");
                            sender.sendMessage(ChatColor.WHITE + " ");
                            sender.sendMessage(ChatColor.WHITE + " " + get("mmh.command.ct.line1")
                                    + "custom_trades.yml");
                            sender.sendMessage(ChatColor.WHITE + " " + get("mmh.command.ct.line2"));
                            sender.sendMessage(ChatColor.WHITE + " " + get("mmh.command.ct.line3"));
                            sender.sendMessage(
                                    ChatColor.WHITE + " " + get("mmh.command.ct.line4") + "/mmh ct add");
                            sender.sendMessage(
                                    ChatColor.WHITE + " " + get("mmh.command.ct.line5") + "custom trade.");
                            sender.sendMessage(ChatColor.WHITE + " ");
                            sender.sendMessage(ChatColor.GREEN + "[]===============[" + ChatColor.YELLOW
                                    + mmh.getName() + ChatColor.GREEN + "]===============[]");
                            if (debug) {
                                logDebug("CMD CT Replace End Error -----");
                            }
                            return true;
                        }
                        int tradeNumber = Integer.parseInt(args[2]);
                        mmh.traderCustom.set("custom_trades.trade_" + (tradeNumber) + ".price_1", price1);
                        mmh.traderCustom.set("custom_trades.trade_" + (tradeNumber) + ".price_2", price2);
                        mmh.traderCustom.set("custom_trades.trade_" + (tradeNumber) + ".itemstack", itemstack);
                        mmh.traderCustom.set("custom_trades.trade_" + (tradeNumber) + ".quantity",
                                itemstack.getAmount());
                        mmh.traderCustom.set("custom_trades.trade_" + (tradeNumber) + ".chance", 0.002);
                        if (debug) {
                            logDebug("CMD CT Replace price1=" + price1.getType());
                        }
                        if (debug) {
                            logDebug("CMD CT Replace price2=" + price2.getType());
                        }
                        if (debug) {
                            logDebug("CMD CT Replace itemstack=" + itemstack.getType());
                        }
                        if (debug) {
                            if (itemstack.getType() == Material.PLAYER_HEAD) {
                                ItemMeta skullMeta = Utils.getItemMeta(itemstack);
                                logDebug("CMD CT Replace IS DisplayName=" + skullMeta.getDisplayName());
                                if (skullMeta.hasLore() && skullMeta.getLore() != null) {
                                    logDebug("CMD CT Replace IS lore=" + String.join(",", skullMeta.getLore()));
                                }
                            }
                        }
                        if (debug) {
                            logDebug("CMD CT Replace quantity=" + itemstack.getAmount());
                        }
                        if (debug) {
                            logDebug("CMD CT Replace chance=0.002");
                        }

                        // log("customFile=" + customFile);
                        try {
                            mmh.traderCustom.save(mmh.customFile);
                            mmh.traderCustom.load(mmh.customFile);
                        } catch (IOException | InvalidConfigurationException e) {
                            if (debug) {
                                logDebug("CMD CT Replace End Exception -----");
                            }
                            sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.RED + " "
                                    + get("mmh.command.ct.error"));
                            return true;
                            // e.printStackTrace();
                        }
                        if (debug) {
                            logDebug("CMD CT Replace End -----");
                        }
                        sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.WHITE + " trade_"
                                + args[2] + " " + get("mmh.message.ct.successrep"));
                        return true;
                    } else {
                        if (debug) {
                            logDebug("CMD CT Replace End 2 -----");
                        }
                        sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.RED + " "
                                + get("mmh.command.ct.numberreq"));
                        return true;
                    }
                }
            } else if (!(sender instanceof Player)) {
                if (debug) {
                    logDebug("CMD CT Replace End Console -----");
                }
                sender.sendMessage(
                        ChatColor.YELLOW + mmh.getName() + ChatColor.RED + " " + get("mmh.message.noconsole"));
                return true;
            } else if (!hasPerm) {
                if (debug) {
                    logDebug("CMD CT Replace End !Perm -----");
                }
                sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.RED + " "
                        + get("mmh.message.nopermordisabled").replace("<perm>", perm));
                return true;
            }
            return true;
        }
        if (args[0].equalsIgnoreCase("playerheads") || args[0].equalsIgnoreCase("ph")) {
            String perm = "moremobheads.playerheads";
            boolean hasPerm = sender.hasPermission(perm);
            if (debug) {
                logDebug(sender.getName() + " has the permission " + perm + "=" + hasPerm);
            }
            if (hasPerm && (sender instanceof Player)
                    && mmh.getConfig().getBoolean("wandering_trades.custom_wandering_trader", true)) {
                Player player = (Player) sender;
                if (!(args.length >= 2)) {
                    sender.sendMessage(ChatColor.GREEN + "[]===============[" + ChatColor.YELLOW + mmh.getName()
                            + ChatColor.GREEN + "]===============[]");
                    sender.sendMessage(ChatColor.WHITE + " ");
                    sender.sendMessage(ChatColor.WHITE + " /mmh ph - " + get("mmh.command.ct.help"));
                    sender.sendMessage(ChatColor.WHITE + " /mmh ph add - " + get("mmh.command.ct.add")
                            + "player_heads.yml");
                    sender.sendMessage(ChatColor.WHITE + " /mmh ph remove # - "
                            + get("mmh.command.ct.remove").replace("custom_trades", "playerheads"));
                    sender.sendMessage(ChatColor.WHITE + " /mmh ph replace # - " + get("mmh.command.ct.replace")
                            .replace("<num>", "#").replace("custom trade", "pleayerhead"));
                    // sender.sendMessage(ChatColor.WHITE + " ");
                    sender.sendMessage(ChatColor.WHITE + " ");
                    sender.sendMessage(ChatColor.GREEN + "[]===============[" + ChatColor.YELLOW + mmh.getName()
                            + ChatColor.GREEN + "]===============[]");
                    return true;
                } else if (args[1].equalsIgnoreCase("add")) {
                    if (debug) {
                        logDebug("CMD PH ADD Start -----");
                    }
                    ItemStack itemstack = player.getInventory().getItemInOffHand();
                    ItemStack price1 = player.getInventory().getItem(0);
                    ItemStack price2 = player.getInventory().getItem(1);
                    if (price1 == null) {
                        price1 = new ItemStack(Material.AIR);
                    }
                    if (price2 == null) {
                        price2 = new ItemStack(Material.AIR);
                    }

                    if (itemstack.getType() == Material.AIR || price1.getType() == Material.AIR || itemstack.getType() != Material.PLAYER_HEAD) {
                        sender.sendMessage(ChatColor.GREEN + "[]===============[" + ChatColor.YELLOW + mmh.getName()
                                + ChatColor.GREEN + "]===============[]");
                        sender.sendMessage(ChatColor.WHITE + " ");
                        if (itemstack.getType() != Material.PLAYER_HEAD) {
                            sender.sendMessage(ChatColor.RED + " MUST BE PLAYERHEAD");
                            sender.sendMessage(ChatColor.WHITE + " ");
                        }
                        sender.sendMessage(
                                ChatColor.WHITE + " " + get("mmh.command.ct.line1") + "player_heads.yml");
                        sender.sendMessage(ChatColor.WHITE + " " + get("mmh.command.ct.line2"));
                        sender.sendMessage(ChatColor.WHITE + " " + get("mmh.command.ct.line3"));
                        sender.sendMessage(ChatColor.WHITE + " " + get("mmh.command.ct.line4") + "/mmh ph add");
                        sender.sendMessage(
                                ChatColor.WHITE + " " + get("mmh.command.ct.line5") + "player head.");
                        sender.sendMessage(ChatColor.WHITE + " ");
                        sender.sendMessage(ChatColor.GREEN + "[]===============[" + ChatColor.YELLOW + mmh.getName()
                                + ChatColor.GREEN + "]===============[]");
                        if (debug) {
                            logDebug("CMD PH ADD End Error -----");
                        }
                        return true;
                    }
                    int tradeNumber = (int) mmh.playerHeads.get("players.number", 1);
                    mmh.playerHeads.set("players.player_" + (tradeNumber + 1) + ".price_1", price1);
                    mmh.playerHeads.set("players.player_" + (tradeNumber + 1) + ".price_2", price2);
                    mmh.playerHeads.set("players.player_" + (tradeNumber + 1) + ".itemstack", itemstack);
                    mmh.playerHeads.set("players.player_" + (tradeNumber + 1) + ".quantity", itemstack.getAmount());
                    if (debug) {
                        logDebug("CMD PH ADD price1=" + price1.getType());
                    }
                    if (debug) {
                        logDebug("CMD PH ADD price2=" + price2.getType());
                    }
                    if (debug) {
                        logDebug("CMD PH ADD itemstack=" + itemstack.getType());
                    }
                    if (debug) {
                        if (itemstack.getType() == Material.PLAYER_HEAD) {
                            ItemMeta skullMeta = Utils.getItemMeta(itemstack);
                            logDebug("CMD PH ADD IS DisplayName=" + skullMeta.getDisplayName());
                            if (skullMeta.hasLore() && skullMeta.getLore() != null) {
                                logDebug("CMD PH ADD IS lore=" + String.join(",", skullMeta.getLore()));
                            }
                        }
                    }
                    if (debug) {
                        logDebug("CMD PH ADD quantity=" + itemstack.getAmount());
                    }
                    // playerHeads.set("players.player_" + (tradeNumber + 1) + ".chance", 0.002);
                    mmh.playerHeads.set("players.number", (tradeNumber + 1));
                    // log("customFile=" + customFile);
                    try {
                        mmh.playerHeads.save(mmh.playerFile);
                        mmh.playerHeads.load(mmh.playerFile);
                    } catch (IOException | InvalidConfigurationException e) {
                        if (debug) {
                            logDebug("CMD PH ADD End Exception -----");
                        }
                        mmh.stacktraceInfo();
                        e.printStackTrace();
                    }
                    if (debug) {
                        logDebug("CMD PH ADD End -----");
                    }
                    sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.WHITE + " player_"
                            + (tradeNumber + 1) + " " + get("mmh.message.ct.successadd"));
                    return true;
                } else if (args[1].equalsIgnoreCase("remove")) {
                    if (debug) {
                        logDebug("CMD PH Remove Start -----");
                    }
                    if (!(args.length >= 3)) {
                        sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.RED + " "
                                + get("mmh.command.ct.argument"));
                        return true;
                    } else if (Utils.isInteger(args[2])) {
                        mmh.playerHeads.set("players.player_" + args[2] + ".price_1", "");
                        mmh.playerHeads.set("players.player_" + args[2] + ".price_2", "");
                        mmh.playerHeads.set("players.player_" + args[2] + ".itemstack", "");
                        mmh.playerHeads.set("players.player_" + args[2] + ".quantity", "");
                        // playerHeads.set("custom_trades.trade_" + args[2] + ".chance", "");
                        if (debug) {
                            logDebug("playerFile=" + mmh.playerFile);
                        }
                        try {
                            mmh.playerHeads.save(mmh.playerFile);
                            mmh.playerHeads.load(mmh.playerFile);
                        } catch (IOException | InvalidConfigurationException e) {
                            if (debug) {
                                logDebug("CMD PH Remove End Exception -----");
                            }
                            sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.RED + " "
                                    + get("mmh.command.ct.error") + "custom_trades.yml!");
                            return true;
                            // e.printStackTrace();
                        }
                        if (debug) {
                            logDebug("CMD PH Remove End -----");
                        }
                        sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.WHITE + " player_"
                                + args[2] + " " + get("mmh.message.ct.successrem"));
                        return true;
                    } else {
                        if (debug) {
                            logDebug("CMD PH Remove End 2 -----");
                        }
                        sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.RED + " "
                                + get("mmh.command.ct.numberreq"));
                        return true;
                    }
                } else if (args[1].equalsIgnoreCase("replace")) {
                    if (debug) {
                        logDebug("CMD PH Replace Start -----");
                    }
                    if (args.length != 3) {
                        sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.RED + " "
                                + get("mmh.command.ct.argument"));
                        return true;
                    } else if (Utils.isInteger(args[2])) {
                        ItemStack itemstack = player.getInventory().getItemInOffHand();
                        ItemStack price1 = player.getInventory().getItem(0);
                        ItemStack price2 = player.getInventory().getItem(1);
                        if (price1 == null) {
                            price1 = new ItemStack(Material.AIR);
                        }
                        if (price2 == null) {
                            price2 = new ItemStack(Material.AIR);
                        }
                        if (itemstack.getType() == Material.AIR || price1.getType() == Material.AIR || itemstack.getType() != Material.PLAYER_HEAD) {
                            sender.sendMessage(ChatColor.GREEN + "[]===============[" + ChatColor.YELLOW
                                    + mmh.getName() + ChatColor.GREEN + "]===============[]");
                            sender.sendMessage(ChatColor.WHITE + " ");
                            if (itemstack.getType() != Material.PLAYER_HEAD) {
                                sender.sendMessage(ChatColor.RED + " " + get("mmh.command.playerhead.msg"));
                                sender.sendMessage(ChatColor.WHITE + " ");
                            }
                            sender.sendMessage(ChatColor.WHITE + " " + get("mmh.command.ct.line1")
                                    + "player_heads.yml");
                            sender.sendMessage(ChatColor.WHITE + " " + get("mmh.command.ct.line2"));
                            sender.sendMessage(ChatColor.WHITE + " " + get("mmh.command.ct.line3"));
                            sender.sendMessage(
                                    ChatColor.WHITE + " " + get("mmh.command.ct.line4") + "/mmh ph add");
                            sender.sendMessage(
                                    ChatColor.WHITE + " " + get("mmh.command.ct.line5") + "player head.");
                            sender.sendMessage(ChatColor.WHITE + " ");
                            sender.sendMessage(ChatColor.GREEN + "[]===============[" + ChatColor.YELLOW
                                    + mmh.getName() + ChatColor.GREEN + "]===============[]");
                            if (debug) {
                                logDebug("CMD PH Replace End Error -----");
                            }
                            return true;
                        }
                        int tradeNumber = Integer.parseInt(args[2]);
                        mmh.playerHeads.set("players.player_" + (tradeNumber) + ".price_1", price1);
                        mmh.playerHeads.set("players.player_" + (tradeNumber) + ".price_2", price2);
                        mmh.playerHeads.set("players.player_" + (tradeNumber) + ".itemstack", itemstack);
                        mmh.playerHeads.set("players.player_" + (tradeNumber) + ".quantity",
                                itemstack.getAmount());
                        if (debug) {
                            logDebug("CMD PH Replace price1=" + price1.getType());
                        }
                        if (debug) {
                            logDebug("CMD PH Replace price2=" + price2.getType());
                        }
                        if (debug) {
                            logDebug("CMD PH Replace itemstack=" + itemstack.getType());
                        }
                        if (debug) {
                            if (itemstack.getType() == Material.PLAYER_HEAD) {
                                ItemMeta skullMeta = Utils.getItemMeta(itemstack);
                                logDebug("CMD PH Replace IS DisplayName=" + skullMeta.getDisplayName());
                                if (skullMeta.hasLore() && skullMeta.getLore() != null) {
                                    logDebug("CMD PH Replace IS lore=" + String.join(",", skullMeta.getLore()));
                                }
                            }
                        }
                        if (debug) {
                            logDebug("CMD PH Replace quantity=" + itemstack.getAmount());
                        }
                        try {
                            mmh.playerHeads.save(mmh.playerFile);
                            mmh.playerHeads.load(mmh.playerFile);
                        } catch (IOException | InvalidConfigurationException e) {
                            if (debug) {
                                logDebug("CMD PH Replace End Exception -----");
                            }
                            sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.RED + " "
                                    + get("mmh.command.ct.error") + "player_heads.yml!");
                            return true;
                            // e.printStackTrace();
                        }
                        if (debug) {
                            logDebug("CMD PH Replace End -----");
                        }
                        sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.WHITE + " player_"
                                + args[2] + " " + get("mmh.message.ct.successrep"));
                        return true;
                    } else {
                        if (debug) {
                            logDebug("CMD PH Replace End 2 -----");
                        }
                        sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.RED + " "
                                + get("mmh.command.ct.numberreq"));
                        return true;
                    }
                }
            } else if (!(sender instanceof Player)) {
                if (debug) {
                    logDebug("CMD PH Replace End Console -----");
                }
                sender.sendMessage(
                        ChatColor.YELLOW + mmh.getName() + ChatColor.RED + " " + get("mmh.message.noconsole"));
                return true;
            } else if (!hasPerm) {
                if (debug) {
                    logDebug("CMD PH Replace End !Perm -----");
                }
                sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.RED + " "
                        + get("mmh.message.nopermordisabled").replace("<perm>", perm));
                return true;
            }
            return true;
        }
        if (args[0].equalsIgnoreCase("fixhead") || args[0].equalsIgnoreCase("fh")) {
            String perm = "moremobheads.fixhead";
            boolean hasPerm = sender.hasPermission(perm);
            if (debug) {
                logDebug(sender.getName() + " has the permission " + perm + "=" + hasPerm);
            }
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (!hasPerm) {
                    if (debug) {
                        logDebug("CMD FH Stack End !Perm -----");
                    }
                    sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.RED + get("mmh.message.noperm").replace("<perm>", perm));
                    return true;
                }
                if (!args[1].isEmpty()) {
                    if (args[1].equalsIgnoreCase("name")) {
                        if (debug) {
                            logDebug("CMD FH Name Start -----");
                        }
                        // FixHead NBT
                        ItemStack mainHand = player.getInventory().getItemInMainHand();
                        if (mainHand.getType().equals(Material.PLAYER_HEAD)) {
                            SkullMeta meta = Utils.getItemMeta(mainHand);

                            if (meta.getOwner() != null) {
                                String name = meta.getOwner();
                                if (debug) {
                                    logDebug("EPIE name=" + name);
                                }
                                if (debug) {
                                    logDebug("EPIE lore=" + meta.getLore());
                                }
                                if (meta.getOwner().length() >= 40) {
                                    if (debug) {
                                        logDebug("EPIE ownerName.lngth >= 40");
                                    }
                                    String daMobName = null;
                                    String isCat = CatHeads.getNameFromTexture(name);
                                    String isHorse = HorseHeads.getNameFromTexture(name);
                                    String isLlama = LlamaHeads.getNameFromTexture(name);
                                    String isMobHead = MobHeads.getNameFromTexture(name);
                                    String isRabbit = RabbitHeads.getNameFromTexture(name);
                                    String isSheep = SheepHeads.getNameFromTexture(name);
                                    String isVillager = VillagerHeads.getNameFromTexture(name);
                                    String isZombieVillager = ZombieVillagerHeads.getNameFromTexture(name);
                                    String isplayerhead = Heads.isPlayerHead(name);
                                    String isblockhead = Heads.isBlockHead(name);
                                    String isblockhead2 = Heads.isBlockHead2(name);
                                    String isblockhead3 = Heads.isBlockHead3(name);
                                    if (isCat != null) {
                                        daMobName = isCat;
                                    }
                                    if (isHorse != null) {
                                        daMobName = isHorse;
                                    }
                                    if (isLlama != null) {
                                        daMobName = isLlama;
                                    }
                                    if (isMobHead != null) {
                                        daMobName = isMobHead;
                                    }
                                    if (isRabbit != null) {
                                        daMobName = isRabbit;
                                    }
                                    if (isSheep != null) {
                                        daMobName = isSheep;
                                    }
                                    if (isVillager != null) {
                                        daMobName = isVillager;
                                    }
                                    if (isZombieVillager != null) {
                                        daMobName = isZombieVillager;
                                    }
                                    if (daMobName == null) {
                                        if (blockHeads != null) {
                                            if (isblockhead != null) {
                                                daMobName = isblockhead;
                                            }
                                        }
                                        if (blockHeads2 != null) {
                                            if (isblockhead2 != null) {
                                                daMobName = isblockhead2;
                                            }
                                        }
                                        if (blockHeads3 != null) {
                                            if (isblockhead3 != null) {
                                                daMobName = isblockhead3;
                                            }
                                        }
                                        if (mmh.playerHeads != null) {
                                            if (isplayerhead != null) {
                                                daMobName = isplayerhead;
                                            }
                                        }
                                    }
                                    ArrayList<String> lore = new ArrayList<>();

                                    if (debug) {
                                        logDebug("EPIE mobname from texture=" + daMobName);
                                    }
                                    List<String> skullLore = meta.getLore();
                                    if (skullLore != null) {
                                        if (skullLore.toString().contains(ChatColorUtils.setColors(mmh.mobNames.getString("killedby", "<RED>Killed <RESET>By <YELLOW><player>")))) {
                                            lore.addAll(meta.getLore());
                                        }
                                    }
                                    if ((skullLore == null) || !meta.getLore().toString()
                                            .contains(mmh.getName())) {
                                        if (mmh.getConfig().getBoolean("lore.show_plugin_name", true)) {
                                            lore.add(ChatColor.AQUA + "" + mmh.getName());
                                        }
                                    }
                                    if (daMobName != null) {
                                        daMobName = mmh.mobNames.getString(
                                                daMobName.toLowerCase().replace(" ", "."), daMobName);
                                    } else {
                                        daMobName = "Name Not Found";
                                    }
                                    meta.setLore(lore);
                                    meta.setDisplayName(daMobName);
                                    mainHand.setItemMeta(meta);
                                    if (debug) {
                                        logDebug("CMD FH Name End -----");
                                    }
                                    sender.sendMessage("" + get("mmh.command.fixhead.name"));
                                    return true;
                                }
                            }
                        } else {
                            if (debug) {
                                logDebug("CMD FH Name End Error -----");
                            }
                            sender.sendMessage("An Error occured.");
                            return true;
                        }
                    }

                    if (args[1].equalsIgnoreCase("stack")) {
                        if (debug) {
                            logDebug("CMD FH Stack Start -----");
                        }
                        // FixHead Stack
                        ItemStack mainHand = player.getInventory().getItemInMainHand();
                        ItemStack offHand = player.getInventory().getItemInOffHand();
                        if (mainHand.getType().equals(Material.PLAYER_HEAD)
                                && offHand.getType().equals(Material.PLAYER_HEAD)) {
                            ItemStack is = mmh.fixHeadStack(offHand, mainHand);
                            // is.setAmount(mainHand.getAmount());
                            if (is != mainHand) {
                                player.getInventory().setItemInMainHand(is);
                                if (debug) {
                                    logDebug("is=" + is.getType());
                                }
                                if (debug) {
                                    logDebug("CMD FH Stack End -----");
                                }
                                sender.sendMessage("" + get("mmh.command.fixhead.stack.success"));
                            } else {
                                if (debug) {
                                    logDebug("CMD FH Stack End Error -----");
                                }
                                sender.sendMessage("" + get("mmh.command.fixhead.stack.error"));
                            }
                            return true;
                        } else if (!mainHand.getType().equals(Material.PLAYER_HEAD)
                                && !offHand.getType().equals(Material.PLAYER_HEAD)) {
                            if (debug) {
                                logDebug("CMD FH Stack End Error Main Off -----");
                            }
                            sender.sendMessage("" + get("mmh.command.fixhead.stack.notph"));
                            return true;
                        } else if (!mainHand.getType().equals(Material.PLAYER_HEAD)
                                && offHand.getType().equals(Material.PLAYER_HEAD)) {
                            if (debug) {
                                logDebug("CMD FH Stack End Error Main -----");
                            }
                            sender.sendMessage("" + get("mmh.command.fixhead.stack.main"));
                            return true;
                        } else if (mainHand.getType().equals(Material.PLAYER_HEAD)
                                && !offHand.getType().equals(Material.PLAYER_HEAD)) {
                            if (debug) {
                                logDebug("CMD FH Stack End Error Off -----");
                            }
                            sender.sendMessage("" + get("mmh.command.fixhead.stack.off"));
                            return true;
                        }
                    }
                }
            }
            return true;
        }
        if (args[0].equalsIgnoreCase("giveMH")) {
            // /mmh giveMH player mob qty
            // cmd 0 1 2 3
            if (args.length == 4) {
                String perm = "moremobheads.give";
                boolean hasPerm = sender.hasPermission(perm) || !(sender instanceof Player);
                if (debug) {
                    logDebug(sender.getName() + " has the permission " + perm + "=" + hasPerm);
                }
                if (!hasPerm) {
                    sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.RED + " "
                            + get("mmh.message.noperm").replace("<perm>", perm));
                    return true;
                }
                Player player = Bukkit.getPlayer(args[1]);
                if (player == null) {
                    sender.sendMessage(get("mmh.command.player.offline"));
                    return true;
                }
                if (!args[2].isEmpty()) {
                    String mob = args[2].toLowerCase();
                    log("mob=" + mob);
                    if (!args[3].isEmpty()) {
                        int number = Integer.parseInt(args[3]);
                        String[] splitmob = mob.split("\\.");
                        if (debug) {
                            logDebug("CMD GMH splitmob[0]=" + splitmob[0]);
                        }
                        switch (splitmob[0]) {
                            case "creeper_charged":
                                player.getWorld().dropItemNaturally(player.getLocation(), MoreMobHeadsLib.addSound(
                                        mmh.makeSkulls(MobHeads.valueOf(splitmob[0].toUpperCase()).getTexture(),
                                                mmh.mobNames.getString(splitmob[0].toLowerCase(), MobHeads.valueOf(splitmob[0].toUpperCase()).getName() + " Head"), number), EntityType.CREEPER)
                                );
                                break;
                            case "creeper":
                                if (mmh.getConfig().getBoolean("vanilla_heads.creeper", false)) {
                                    player.getWorld().dropItemNaturally(player.getLocation(), new ItemStack(Material.CREEPER_HEAD));
                                } else { // mmh.langName
                                    player.getWorld().dropItemNaturally(player.getLocation(), MoreMobHeadsLib.addSound(mmh.makeSkulls(MobHeads.valueOf(splitmob[0].toUpperCase()).getTexture(), mmh.mobNames.getString(splitmob[0].toLowerCase(), MobHeads.valueOf(splitmob[0].toUpperCase()).getName() + " Head"), number), EntityType.CREEPER));
                                } // MobHeads.valueOf(name).getName() + "
                                // Head"
                                break;
                            case "mushroom_cow":
                                player.getWorld().dropItemNaturally(player.getLocation(), MoreMobHeadsLib.addSound(
                                        mmh.makeSkulls(MobHeads.valueOf(splitmob[0].toUpperCase() + "_" + splitmob[1].toUpperCase()).getTexture(),
                                                mmh.mobNames.getString(splitmob[0].toLowerCase() + "." + splitmob[1].toLowerCase(), MobHeads.valueOf(splitmob[0].toUpperCase() + "_" + splitmob[1].toUpperCase()).getName()), number), EntityType.MUSHROOM_COW));
                                break;
                            case "strider_shivering":
                                player.getWorld().dropItemNaturally(player.getLocation(), MoreMobHeadsLib.addSound(
                                        mmh.makeSkulls(MobHeads.valueOf(splitmob[0].toUpperCase()).getTexture(),
                                                mmh.mobNames.getString(splitmob[0].toLowerCase(), MobHeads.valueOf(splitmob[0].toUpperCase()).getName() + " Head"), number), EntityType.STRIDER)
                                );
                                break;
                            case "zombie":
                                if (mmh.getConfig().getBoolean("vanilla_heads.zombie", false)) {
                                    player.getWorld().dropItemNaturally(player.getLocation(), new ItemStack(Material.ZOMBIE_HEAD));
                                } else { // mmh.langName
                                    player.getWorld().dropItemNaturally(player.getLocation(), MoreMobHeadsLib.addSound(mmh.makeSkulls(MobHeads.valueOf(splitmob[0].toUpperCase()).getTexture(), mmh.mobNames.getString(splitmob[0].toLowerCase(), MobHeads.valueOf(splitmob[0].toUpperCase()).getName() + " Head"), number), EntityType.ZOMBIE));
                                } // MobHeads.valueOf(name).getName() + "
                                // Head"
                                break;

                            case "piglin":
                                if (mmh.getConfig().getBoolean("vanilla_heads.piglin", false)) {
                                    player.getWorld().dropItemNaturally(player.getLocation(), MoreMobHeadsLib.getVanilla(EntityType.fromName("PIGLIN")));
                                } else { // mmh.langName
                                    player.getWorld().dropItemNaturally(player.getLocation(), MoreMobHeadsLib.addSound(mmh.makeSkulls(MobHeads.valueOf(splitmob[0].toUpperCase()).getTexture(), mmh.mobNames.getString(splitmob[0].toLowerCase(), MobHeads.valueOf(splitmob[0].toUpperCase()).getName() + " Head"), number), EntityType.PIGLIN));
                                } // MobHeads.valueOf(name).getName() + "
                                // Head"
                                break;
                            case "skeleton":
                                if (mmh.getConfig().getBoolean("vanilla_heads.skeleton", false)) {
                                    player.getWorld().dropItemNaturally(player.getLocation(), new ItemStack(Material.SKELETON_SKULL));
                                } else { // mmh.langName
                                    player.getWorld().dropItemNaturally(player.getLocation(), MoreMobHeadsLib.addSound(mmh.makeSkulls(MobHeads.valueOf(splitmob[0].toUpperCase()).getTexture(), mmh.mobNames.getString(splitmob[0].toLowerCase(), MobHeads.valueOf(splitmob[0].toUpperCase()).getName() + " Head"), number), EntityType.SKELETON));
                                } // MobHeads.valueOf(name).getName() + "
                                // Head"
                                break;
                            case "wither_skeleton":
                                if (mmh.getConfig().getBoolean("vanilla_heads.wither_skeleton", false)) {
                                    player.getWorld().dropItemNaturally(player.getLocation(), new ItemStack(Material.WITHER_SKELETON_SKULL));
                                } else { // mmh.langName
                                    player.getWorld().dropItemNaturally(player.getLocation(), MoreMobHeadsLib.addSound(mmh.makeSkulls(MobHeads.valueOf(splitmob[0].toUpperCase()).getTexture(), mmh.mobNames.getString(splitmob[0].toLowerCase(), MobHeads.valueOf(splitmob[0].toUpperCase()).getName() + " Head"), number), EntityType.WITHER_SKELETON));
                                } // MobHeads.valueOf(name).getName() + "
                                // Head"
                                break;
                            case "ender_dragon":
                                if (mmh.getConfig().getBoolean("vanilla_heads.ender_dragon", false)) {
                                    player.getWorld().dropItemNaturally(player.getLocation(), new ItemStack(Material.DRAGON_HEAD));
                                } else { // mmh.langName
                                    player.getWorld().dropItemNaturally(player.getLocation(), MoreMobHeadsLib.addSound(mmh.makeSkulls(MobHeads.valueOf(splitmob[0].toUpperCase()).getTexture(), mmh.mobNames.getString(splitmob[0].toLowerCase(), MobHeads.valueOf(splitmob[0].toUpperCase()).getName() + " Head"), number), EntityType.ENDER_DRAGON));
                                } // MobHeads.valueOf(name).getName() + "
                                // Head"
                                break;
                            case "cat":
                                player.getWorld().dropItemNaturally(player.getLocation(), MoreMobHeadsLib.addSound(mmh.makeSkulls(CatHeads.valueOf(splitmob[1].toUpperCase()).getTexture(), mmh.mobNames.getString(splitmob[0].toLowerCase() + "." + splitmob[1].toLowerCase(), CatHeads.valueOf(splitmob[1].toUpperCase()).getName() + " Head"), number), EntityType.CAT));
                                break;
                            case "bee":
                                log("splitmob.length=" + splitmob.length);
                                if (splitmob.length == 1) {
                                    player.getWorld().dropItemNaturally(player.getLocation(), MoreMobHeadsLib.addSound(mmh.makeSkulls(MobHeads.valueOf(splitmob[0].toUpperCase()).getTexture(), mmh.mobNames.getString(splitmob[0].toLowerCase() + ".none", MobHeads.valueOf(splitmob[0].toUpperCase()).getName() + " Head"), number), EntityType.BEE));
                                } else {
                                    player.getWorld().dropItemNaturally(player.getLocation(), MoreMobHeadsLib.addSound(mmh.makeSkulls(MobHeads.valueOf(mob.toUpperCase().replace(".", "_")).getTexture(), mmh.mobNames.getString(mob.toLowerCase().replace(".", "_"), MobHeads.valueOf(mob.toUpperCase().replace(".", "_")).getName() + " Head"), number), EntityType.BEE));
                                }
                                break;
                            case "villager": // villager type profession,
                                // villager profession type
                                // name = splitmob[0], type = splitmob[1],
                                // profession = splitmob[2]
                                player.getWorld().dropItemNaturally(player.getLocation(), MoreMobHeadsLib.addSound(mmh.makeSkulls(VillagerHeads.valueOf(splitmob[0].toUpperCase() + "_" + splitmob[2].toUpperCase() + "_" + splitmob[1].toUpperCase()).getTexture(), mmh.mobNames.getString(splitmob[0].toLowerCase() + "." + splitmob[1].toLowerCase() + "." + splitmob[2].toLowerCase(), VillagerHeads.valueOf(splitmob[0].toUpperCase() + "_" + splitmob[2].toUpperCase() + "_" + splitmob[1].toUpperCase()).getName() + " Head"), number), EntityType.VILLAGER));
                                break;
                            case "zombie_villager":
                                player.getWorld().dropItemNaturally(player.getLocation(), MoreMobHeadsLib.addSound(mmh.makeSkulls(ZombieVillagerHeads.valueOf(splitmob[0].toUpperCase() + "_" + splitmob[1].toUpperCase()).getTexture(), mmh.mobNames.getString(splitmob[0].toLowerCase() + "." + splitmob[1].toLowerCase(), ZombieVillagerHeads.valueOf(splitmob[0].toUpperCase() + "_" + splitmob[1].toUpperCase()).getName()), number), EntityType.ZOMBIE_VILLAGER));
                                break;
                            case "llama":
                            case "trader_llama":
                                player.getWorld().dropItemNaturally(player.getLocation(), MoreMobHeadsLib.addSound(mmh.makeSkulls(LlamaHeads.valueOf(splitmob[0].toUpperCase() + "_" + splitmob[1].toUpperCase()).getTexture(), mmh.mobNames.getString(splitmob[0].toLowerCase() + "." + splitmob[1].toLowerCase(), LlamaHeads.valueOf(splitmob[0].toUpperCase() + "_" + splitmob[1].toUpperCase()).getName()), number), EntityType.LLAMA));
                                break;
                            case "horse":
                                player.getWorld().dropItemNaturally(player.getLocation(), MoreMobHeadsLib.addSound(mmh.makeSkulls(HorseHeads.valueOf(splitmob[0].toUpperCase() + "_" + splitmob[1].toUpperCase()).getTexture(), mmh.mobNames.getString(splitmob[0].toLowerCase() + "." + splitmob[1].toLowerCase(), HorseHeads.valueOf(splitmob[0].toUpperCase() + "_" + splitmob[1].toUpperCase()).getName()), number), EntityType.HORSE));
                                break;
                            case "rabbit":
                                if (splitmob[1].equalsIgnoreCase("Toast")) {
                                    player.getWorld().dropItemNaturally(player.getLocation(), MoreMobHeadsLib.addSound(
                                            mmh.makeSkulls(RabbitHeads.valueOf(splitmob[0].toUpperCase() + "_" + StrUtils.toTitleCase(splitmob[1])).getTexture(),
                                                    mmh.mobNames.getString(splitmob[0].toLowerCase() + "." + splitmob[1].toLowerCase(), RabbitHeads.valueOf(splitmob[0].toUpperCase() + "_" + StrUtils.toTitleCase(splitmob[1])).getName()), number), EntityType.RABBIT));
                                    break;
                                }
                                player.getWorld().dropItemNaturally(player.getLocation(), MoreMobHeadsLib.addSound(
                                        mmh.makeSkulls(RabbitHeads.valueOf(splitmob[0].toUpperCase() + "_" + splitmob[1].toUpperCase()).getTexture(),
                                                mmh.mobNames.getString(splitmob[0].toLowerCase() + "." + splitmob[1].toLowerCase(), RabbitHeads.valueOf(splitmob[0].toUpperCase() + "_" + splitmob[1].toUpperCase()).getName()), number), EntityType.RABBIT));
                                break;
                            case "sheep":
                                String sheeptype;
                                if (splitmob[1].equalsIgnoreCase("jeb_")) {
                                    sheeptype = "jeb_";
                                } else {
                                    sheeptype = splitmob[1].toUpperCase();
                                }
                                player.getWorld().dropItemNaturally(player.getLocation(), MoreMobHeadsLib.addSound(mmh.makeSkulls(SheepHeads.valueOf(splitmob[0].toUpperCase() + "_" + sheeptype).getTexture(), mmh.mobNames.getString(splitmob[0].toLowerCase() + "." + splitmob[1].toLowerCase(), SheepHeads.valueOf(splitmob[0].toUpperCase() + "_" + sheeptype).getName()), number), EntityType.SHEEP));
                                break;
                            case "goat":
                                player.getWorld().dropItemNaturally(player.getLocation(), MoreMobHeadsLib.addSound(mmh.makeSkulls(MobHeads117.valueOf(splitmob[0].toUpperCase() + "_" + splitmob[1].toUpperCase()).getTexture(), mmh.mobNames.getString(splitmob[0].toLowerCase() + "." + splitmob[1].toLowerCase(), MobHeads117.valueOf(splitmob[0].toUpperCase() + "_" + splitmob[1].toUpperCase()).getName()), number), EntityType.fromName("goat")));
                                break;
                            case "axolotl":
                                player.getWorld().dropItemNaturally(player.getLocation(), MoreMobHeadsLib.addSound(mmh.makeSkulls(MobHeads117.valueOf(splitmob[0].toUpperCase() + "_" + splitmob[1].toUpperCase()).getTexture(), mmh.mobNames.getString(splitmob[0].toLowerCase() + "." + splitmob[1].toLowerCase(), MobHeads117.valueOf(splitmob[0].toUpperCase() + "_" + splitmob[1].toUpperCase()).getName()), number), EntityType.fromName("axolotl")));
                                break;
                            case "frog":
                                player.getWorld().dropItemNaturally(player.getLocation(), MoreMobHeadsLib.addSound(mmh.makeSkulls(MobHeads119.valueOf(splitmob[0].toUpperCase() + "_" + splitmob[1].toUpperCase()).getTexture(), mmh.mobNames.getString(splitmob[0].toLowerCase() + "." + splitmob[1].toLowerCase(), MobHeads119.valueOf(splitmob[0].toUpperCase() + "_" + splitmob[1].toUpperCase()).getName()), number), EntityType.fromName("frog")));
                                break;
                            case "camel":
                                player.getWorld().dropItemNaturally(player.getLocation(), MoreMobHeadsLib.addSound(mmh.makeSkulls(MobHeads120.valueOf(splitmob[0].toUpperCase()).getTexture(), mmh.mobNames.getString(splitmob[0].toLowerCase(), MobHeads120.valueOf(splitmob[0].toUpperCase()).getName()), number), EntityType.fromName("camel")));
                                break;
                            case "sniffer":
                                player.getWorld().dropItemNaturally(player.getLocation(), MoreMobHeadsLib.addSound(mmh.makeSkulls(MobHeads120.valueOf(splitmob[0].toUpperCase()).getTexture(), mmh.mobNames.getString(splitmob[0].toLowerCase(), MobHeads120.valueOf(splitmob[0].toUpperCase()).getName()), number), EntityType.fromName("sniffer")));
                                break;
                            case "allay":
                            case "tadpole":
                            case "warden":
                                player.getWorld().dropItemNaturally(player.getLocation(), MoreMobHeadsLib.addSound(mmh.makeSkulls(MobHeads119.valueOf(mob.toUpperCase().replace(".", "_")).getTexture(), mmh.mobNames.getString(mob.toLowerCase(), MobHeads119.valueOf(mob.toUpperCase().replace(".", "_")).getName() + " Head"), number), EntityType.fromName(splitmob[0])));
                                break;
                            case "tropical_fish":// TropicalFishHeads
                                String fishType = splitmob[1].toUpperCase();
                                log("splitmob[0]=" + splitmob[0]);
                                log("splitmob[1]=" + splitmob[1]);
                                log("fishType=" + fishType);
                                player.getWorld().dropItemNaturally(player.getLocation(), MoreMobHeadsLib.addSound(mmh.makeSkulls(TropicalFishHeads.valueOf(splitmob[0].toUpperCase() + "_" + fishType.toUpperCase()).getTexture(), mmh.mobNames.getString(splitmob[0].toLowerCase() + "." + splitmob[1].toLowerCase(), TropicalFishHeads.valueOf(splitmob[0].toUpperCase() + "_" + fishType.toUpperCase()).getName()), number), EntityType.TROPICAL_FISH));
                                break;
                            default:
                                player.getWorld().dropItemNaturally(player.getLocation(), MoreMobHeadsLib.addSound(
                                        mmh.makeSkulls(MobHeads.valueOf(mob.toUpperCase().replace(".", "_")).getTexture(),
                                                mmh.mobNames.getString(mob.toLowerCase(), MobHeads.valueOf(mob.toUpperCase().replace(".", "_")).getName() + " Head"), number), EntityType.fromName(splitmob[0])));
                                break;
                        }
                    }
                }

            } else {
                sender.sendMessage("" + get("mmh.command.usage") + ", /mmh givemh playername mobname 1");
                return true;
            }
        }
        // /mmh giveph player
        // /mmh giveph player player
        // 0 1 2 3
        if (args[0].equalsIgnoreCase("givePH")) {
            if (args.length >= 2) {
                String perm = "moremobheads.give";
                boolean hasPerm = sender.hasPermission(perm) || !(sender instanceof Player);
                if (debug) {
                    logDebug(sender.getName() + " has the permission " + perm + "=" + hasPerm);
                }
                if (!hasPerm) {
                    sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.RED + " "
                            + get("mmh.message.noperm").replace("<perm>", perm));
                    return true;
                }
                if (debug) {
                    logDebug("CMD GPH args.length=" + args.length);
                }
                if ((args.length == 2) && (sender instanceof Player)) {
                    Heads.givePlayerHead((Player) sender, args[1]);
                    if (debug) {
                        logDebug("CMD GPH args1=" + args[1]);
                    }
                    return true;
                } else if (args.length == 3) {
                    Player player = Bukkit.getPlayer(args[1]);
                    Heads.givePlayerHead(player, args[2]);
                    if (debug) {
                        logDebug("CMD GPH args1=" + args[1] + ", args2=" + args[2]);
                    }
                    return true;
                } else if (args.length == 2) {
                    sender.sendMessage(
                            "" + get("mmh.command.give.console") + "" + get("mmh.command.usage") + ":");
                    sender.sendMessage(
                            "\"/mmh giveph playername 1\" - " + get("mmh.command.give.playerhead.you"));
                    sender.sendMessage("\"/mmh giveph playername playername 1\" - "
                            + get("mmh.command.give.playerhead.them"));
                    return true;
                }
            }else {
                sender.sendMessage("" + get("mmh.command.usage") + ":");
                sender.sendMessage("\"/mmh giveph playername 1\" - " + get("mmh.command.give.playerhead.you"));
                sender.sendMessage(
                        "\"/mmh giveph playername playername 1\" - " + get("mmh.command.give.playerhead.them"));
                return true;
            }
            return true;
        }
        // /mmh givebh block
        // /mmh givebh player block
        // 0 1 2 3
        if (args[0].equalsIgnoreCase("giveBH")) {
            if (debug) {
                logDebug("Start GiveBH");
            }
            if (debug) {
                logDebug("Command=" + cmd.getName() + ", arguments=" + Arrays.toString(args));
            }
            if (args.length >= 2) {
                String perm = "moremobheads.give";
                boolean hasPerm = sender.hasPermission(perm) || !(sender instanceof Player);
                if (debug) {
                    logDebug(sender.getName() + " has the permission " + perm + "=" + hasPerm);
                }
                if (!hasPerm) {
                    sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.RED + " "
                            + get("mmh.message.noperm").replace("<perm>", perm));
                    if (debug) {
                        logDebug("End GiveBH False 2");
                    }
                    return true;
                }
                if (debug) {
                    logDebug("CMD GBH args.length=" + args.length);
                }
                if ((args.length == 2) && (sender instanceof Player)) {
                    Heads.giveBlockHead((Player) sender, args[1].replace("_", " "));
                    if (debug) {
                        logDebug("CMD GBH args1=" + args[1]);
                    }
                    if (debug) {
                        logDebug("End GiveBH True 1");
                    }
                    return true;
                } else if (args.length == 3) {
                    Player player = Bukkit.getPlayer(args[1]);
                    Heads.giveBlockHead(player, args[2].replace("_", " "));
                    if (debug) {
                        logDebug("CMD GBH args1=" + args[1] + ", args2=" + args[2]);
                    }
                    if (debug) {
                        logDebug("End GiveBH True 2");
                    }
                    return true;
                } else if (args.length == 2) {
                    sender.sendMessage(
                            "" + get("mmh.command.give.console") + " mmh giveBh <player> <block>");
                    if (debug) {
                        logDebug("End GiveBH False 1");
                    }
                    return true;
                }
            } else {
                sender.sendMessage("" + get("mmh.command.usage") + ":");
                sender.sendMessage("\"/mmh givebh <block>\" - " + get("mmh.command.give.blockhead.you"));
                sender.sendMessage(
                        "\"/mmh giveph playername <block>\" - " + get("mmh.command.give.blockhead.them"));
                if (debug) {
                    logDebug("End GiveBH False 3");
                }
                return true;
            }
            if (debug) {
                logDebug("End GiveBH False 4");
            }
            return true;
        }
        if (args[0].equalsIgnoreCase("dev")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (player.isOp()) {
                    mmh.isDev = !mmh.isDev;
                    player.sendMessage("You have toggled isDev to " + mmh.isDev);
                } else {
                    player.sendMessage("You are not the developer.");
                }
                return true;
            }
            return true;
        }
        return true;
    }

    @Override
    @SuppressWarnings("static-access")
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        List<String> autoCompletes = new ArrayList<>();
        if (args.length == 1) {
            autoCompletes.add("reload");
            autoCompletes.add("toggledebug");
            autoCompletes.add("playerheads");
            autoCompletes.add("customtrader");
            autoCompletes.add("fixhead");
            autoCompletes.add("givemh");
            autoCompletes.add("giveph");
            autoCompletes.add("givebh");
            autoCompletes.add("display");
            return autoCompletes; // then return the list
        }
        if (args.length > 1) {
            if (args[0].equalsIgnoreCase("display") && args[1].isEmpty()) {
                autoCompletes.add("permissions");
                autoCompletes.add("variables");
                return autoCompletes; // then return the list
            } else if (args[0].equalsIgnoreCase("display") && args[1].equalsIgnoreCase("permissions")) {
                if (args[1].equalsIgnoreCase("permissions")) {
                    return null;
                }
            }
            if (args[0].equalsIgnoreCase("fixhead") || (args[0].equalsIgnoreCase("fh") && args[1].isEmpty())) {
                autoCompletes.add("name");
                autoCompletes.add("stack");
                return autoCompletes; // then return the list
            }
            if (args[0].equalsIgnoreCase("playerheads") || (args[0].equalsIgnoreCase("ph") && args[1].isEmpty())) {
                autoCompletes.add("add");
                autoCompletes.add("remove");
                autoCompletes.add("replace");
                return autoCompletes; // then return the list
            } else if ((args[0].equalsIgnoreCase("playerheads") || args[0].equalsIgnoreCase("ph"))
                    && (args[1].equalsIgnoreCase("remove") || args[1].equalsIgnoreCase("replace"))) {
                if (args[1].equalsIgnoreCase("remove")) {
                    autoCompletes.add("0");
                    return autoCompletes; // then return the list
                }
                if (args[1].equalsIgnoreCase("replace")) {
                    autoCompletes.add("0");
                    return autoCompletes; // then return the list
                }
            }
            if (args[0].equalsIgnoreCase("customtrader") || (args[0].equalsIgnoreCase("ct") && args[1].isEmpty())) {
                autoCompletes.add("add");
                autoCompletes.add("remove");
                autoCompletes.add("replace");
                return autoCompletes; // then return the list
            } else if ((args[0].equalsIgnoreCase("customtrader") || args[0].equalsIgnoreCase("ct"))
                    && (args[1].equalsIgnoreCase("remove") || args[1].equalsIgnoreCase("replace"))) {
                if (args[1].equalsIgnoreCase("remove")) {
                    autoCompletes.add("0");
                    return autoCompletes; // then return the list
                }
                if (args[1].equalsIgnoreCase("replace")) {
                    autoCompletes.add("0");
                    return autoCompletes; // then return the list
                }
            }
            if (args[0].equalsIgnoreCase("givebh")) {
                if (args.length > 2) {
                    for (int i = 1; i < blockHeads.getInt("blocks.number"); ++i) {
                        ItemStack stack = blockHeads.getItemStack("blocks.block_" + i + ".itemstack");
                        if (stack == null) continue;
                        String name = Utils.getItemMeta(stack).getDisplayName().replace(" ", "_");
                        autoCompletes.add(ChatColor.stripColor(name));
                    }
                    if (Double.parseDouble(StrUtils.Left(mmh.getMCVersion(), 4)) >= 1.16) {
                        for (int i = 1; i < blockHeads2.getInt("blocks.number"); ++i) {
                            ItemStack stack = blockHeads2.getItemStack("blocks.block_" + i + ".itemstack");
                            if (stack == null) continue;
                            String name = Utils.getItemMeta(stack).getDisplayName().replace(" ", "_");
                            autoCompletes.add(ChatColor.stripColor(name));
                        }
                    }
                    if (Double.parseDouble(StrUtils.Left(mmh.getMCVersion(), 4)) >= 1.17) {
                        for (int i = 1; i < blockHeads3.getInt("blocks.number"); ++i) {
                            ItemStack stack = blockHeads3.getItemStack("blocks.block_" + i + ".itemstack");
                            if (stack == null) continue;
                            String name = Utils.getItemMeta(stack).getDisplayName().replace(" ", "_");
                            autoCompletes.add(ChatColor.stripColor(name));
                        }
                    }

                    return autoCompletes;
                }
            }
            if (args[0].equalsIgnoreCase("giveph")) {

                if (args.length == 2) {
                    // /mmh giveph @p @P
                    // /cmd 0 1 2
                    // return null to list all players.
                    return null;
                }

            }
            if (args[0].equalsIgnoreCase("givemh")) {
                if (args.length > 2) {
                    if (debug) {
                        mmh.logDebug("TC arg1!null args.length=" + args.length);
                    }
                    if (args.length == 3) {

                        // /mmh give @p moblist #
                        // /cmd 0 1 2 3
                        ConfigurationSection section = mmh.chanceConfig.getConfigurationSection("chance_percent");
                        if (section != null) for (String key : section.getKeys(true)) {
                            // System.out.println(key);
                            autoCompletes.add(key);
                            // System.out.println(key);
                            if (key.equalsIgnoreCase("wolf")) {
                                autoCompletes.add("wolf.angry");
                            } else if (key.equalsIgnoreCase("wither")) {
                                autoCompletes.add("wither.normal");
                                autoCompletes.add("wither.projectile");
                                autoCompletes.add("wither.blue_projectile");
                                autoCompletes.remove("wither");
                            } else if (key.equalsIgnoreCase("zombie_villager")) {
                                autoCompletes.add("zombie_villager.armorer");
                                autoCompletes.add("zombie_villager.butcher");
                                autoCompletes.add("zombie_villager.cartographer");
                                autoCompletes.add("zombie_villager.cleric");
                                autoCompletes.add("zombie_villager.farmer");
                                autoCompletes.add("zombie_villager.fisherman");
                                autoCompletes.add("zombie_villager.fletcher");
                                autoCompletes.add("zombie_villager.leatherworker");
                                autoCompletes.add("zombie_villager.librarian");
                                autoCompletes.add("zombie_villager.mason");
                                autoCompletes.add("zombie_villager.nitwit");
                                autoCompletes.add("zombie_villager.none");
                                autoCompletes.add("zombie_villager.shepherd");
                                autoCompletes.add("zombie_villager.toolsmith");
                                autoCompletes.add("zombie_villager.weaponsmith");
                                autoCompletes.remove("zombie_villager");
                            }
                        }
                        autoCompletes.remove("axolotl");
                        autoCompletes.remove("bee");
                        autoCompletes.remove("cat");
                        autoCompletes.remove("fox");
                        autoCompletes.remove("goat");
                        autoCompletes.remove("horse");
                        autoCompletes.remove("llama");
                        autoCompletes.remove("panda");
                        autoCompletes.remove("parrot");
                        autoCompletes.remove("rabbit");
                        autoCompletes.remove("sheep");
                        autoCompletes.remove("trader_llama");
                        autoCompletes.remove("mushroom_cow");
                        autoCompletes.remove("tropical_fish");
                        autoCompletes.remove("villager");
                        autoCompletes.remove("villager.desert");
                        autoCompletes.remove("villager.jungle");
                        autoCompletes.remove("villager.plains");
                        autoCompletes.remove("villager.savanna");
                        autoCompletes.remove("villager.snow");
                        autoCompletes.remove("villager.swamp");
                        autoCompletes.remove("villager.taiga");
                        autoCompletes.remove("frog");

                        return autoCompletes;
                    } else if (args.length == 4) {
                        autoCompletes.add("1");
                        return autoCompletes;
                    }
                }
            }
        }
        return null;
    }

    private void log(String msg) {
        mmh.log(msg);
    }

    private void logDebug(String msg) {
        mmh.logDebug(msg);
    }

}
