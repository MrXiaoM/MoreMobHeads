package com.github.joelgodofwar.mmh;

import com.github.joelgodofwar.mmh.util.StrUtils;
import com.github.joelgodofwar.mmh.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import static com.github.joelgodofwar.mmh.MoreMobHeads.get;
import static com.github.joelgodofwar.mmh.MoreMobHeads.getMCVersion;
import static com.github.joelgodofwar.mmh.MoreMobHeads.debug;

@SuppressWarnings({"deprecation"})
public class Heads {
    private static MoreMobHeads mmh;
    
    protected static void onEnable(MoreMobHeads mmh) {
        Heads.mmh = mmh;
    }

    public static void giveMobHead(LivingEntity mob, String name) {
        ItemStack helmet = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta meta = Utils.getItemMeta(helmet);
        meta.setDisplayName(name + "'s Head");
        meta.setOwner(name);
        helmet.setItemMeta(meta);
        if (mob.getEquipment() != null) mob.getEquipment().setHelmet(helmet);

        if (Bukkit.getPluginManager().getPlugin("WildStacker") != null) {
            @Nonnull
            PersistentDataContainer pdc = mob.getPersistentDataContainer();
            pdc.set(mmh.NAMETAG_KEY, PersistentDataType.STRING, "nametag");
        }
    }

    public static void givePlayerHead(Player player, String playerName) {
        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta meta = Utils.getItemMeta(playerHead);
        meta.setDisplayName(playerName + "'s Head");
        meta.setOwner(playerName); //.setOwner(name);
        ArrayList<String> lore = new ArrayList<>();
        if (mmh.getConfig().getBoolean("lore.show_plugin_name", true)) {
            lore.add(ChatColor.AQUA + "" + MoreMobHeads.THIS_NAME);
        }
        meta.setLore(lore);
        playerHead.setItemMeta(meta);

        playerHead.setItemMeta(meta);

        player.getWorld().dropItemNaturally(player.getLocation(), MoreMobHeadsLib.addSound(playerHead, EntityType.PLAYER));
    }

    public static void giveBlockHead(Player player, String blockName) {
        if (debug) {
            mmh.logDebug("giveBlockHead START");
        }
        ItemStack blockStack = null;
        int isBlock = isBlockHeadName(blockName);
        int isBlock2 = isBlockHeadName2(blockName);
        int isBlock3 = isBlockHeadName3(blockName);
        int isBlock4 = isBlockHeadName4(blockName);
        int isBlock5 = isBlockHeadName5(blockName);
        if (isBlock != -1) {
            if (debug) {
                mmh.logDebug("GBH isBlock=" + isBlock);
            }
            blockStack = mmh.blockHeads.getItemStack("blocks.block_" + isBlock + ".itemstack", new ItemStack(Material.AIR));
        } else if (isBlock2 != -1) {
            if (debug) {
                mmh.logDebug("GBH isBlock2=" + isBlock2);
            }
            blockStack = mmh.blockHeads2.getItemStack("blocks.block_" + isBlock2 + ".itemstack", new ItemStack(Material.AIR));
        } else if (isBlock3 != -1) {
            if (debug) {
                mmh.logDebug("GBH isBlock3=" + isBlock3);
            }
            blockStack = mmh.blockHeads3.getItemStack("blocks.block_" + isBlock3 + ".itemstack", new ItemStack(Material.AIR));
        } else if (isBlock4 != -1) {
            if (debug) {
                mmh.logDebug("GBH isBlock4=" + isBlock4);
            }
            blockStack = mmh.blockHeads4.getItemStack("blocks.block_" + isBlock4 + ".itemstack", new ItemStack(Material.AIR));
        } else if (isBlock5 != -1) {
            if (debug) {
                mmh.logDebug("GBH isBlock5=" + isBlock5);
            }
            blockStack = mmh.blockHeads5.getItemStack("blocks.block_" + isBlock5 + ".itemstack", new ItemStack(Material.AIR));
        } else {
            player.sendMessage(get("mmh.command.give.blockhead.notfound")
                    .replace("<plugin>", mmh.THIS_NAME).replace("<version>", mmh.THIS_VERSION)
                    .replace("<block>", blockName));
        }
        if ((blockStack != null) && (blockStack.getType() != Material.AIR)) {
            player.getWorld().dropItemNaturally(player.getLocation(), blockStack);
            if (debug) {
                mmh.logDebug("GBH BlockHead given to " + player.getName());
            }
        }
        if (debug) {
            mmh.logDebug("giveBlockHead END");
        }
    }

    public static String isPlayerHead(String string) {
        try {
            mmh.playerFile = new File(mmh.getDataFolder(), "player_heads.yml");
            if (!mmh.playerFile.exists()) {
                return null;
            }
            int numOfCustomTrades = mmh.playerHeads.getInt("players.number", 0) + 1;
            if (debug) {
                mmh.logDebug("iPH string=" + string);
            }
            for (int randomPlayerHead = 1; randomPlayerHead < numOfCustomTrades; randomPlayerHead++) {
                ItemStack itemstack = mmh.playerHeads.getItemStack("players.player_" + randomPlayerHead + ".itemstack", new ItemStack(Material.AIR));
                SkullMeta skullmeta = Utils.getItemMeta(itemstack);
                if (skullmeta != null) {
                    if (skullmeta.getOwner() != null) {
                        if (skullmeta.getOwner().contains(string)) {
                            return skullmeta.getDisplayName();
                        }
                    }
                }
            }
        } catch (Exception e) {
            return null;
        }
        //playerHeads
        return null;
    }

    public static String isBlockHead(String string) { // TODO: isBlockHead
        try {
            if (!(Double.parseDouble(StrUtils.Left(getMCVersion(), 4)) >= 1.16)) {
                mmh.blockFile = new File(mmh.getDataFolder(), "block_heads.yml");
                if (!mmh.blockFile.exists()) {                                                                    // checks if the yaml does not exist
                    return null;
                }
            }
            mmh.blockFile116 = new File(mmh.getDataFolder(), "block_heads_1_16.yml");
            if (Double.parseDouble(StrUtils.Left(getMCVersion(), 4)) >= 1.16) {
                if (!mmh.blockFile116.exists()) {
                    return null;
                }
            }
            int numOfCustomTrades = mmh.blockHeads.getInt("blocks.number", 0) + 1;
            if (debug) {
                mmh.logDebug("iBH string=" + string);
            }
            for (int randomBlockHead = 1; randomBlockHead < numOfCustomTrades; randomBlockHead++) {
                ItemStack itemstack = mmh.blockHeads.getItemStack("blocks.block_" + randomBlockHead + ".itemstack", new ItemStack(Material.AIR));
                SkullMeta skullmeta = (SkullMeta) itemstack.getItemMeta();
                if (skullmeta != null) {
                    if (ChatColor.stripColor(skullmeta.getDisplayName()).equals(string)) {
                        return itemstack.getItemMeta().getDisplayName();
                    }
                }
            }
        } catch (Exception e) {
            return null;
        }
        //blockHeads
        return null;
    }

    public static String isBlockHead2(String string) {
        try {
            if (!(Double.parseDouble(StrUtils.Left(getMCVersion(), 4)) >= 1.16)) {                                                                // checks if the yaml does not exist
                return null;
            }
            mmh.blockFile1162 = new File(mmh.getDataFolder(), "block_heads_1_16_2.yml");
            if (getMCVersion().startsWith("1.16") || getMCVersion().startsWith("1.17")) {
                if (!mmh.blockFile1162.exists()) {
                    return null;
                }

            }
            int numOfCustomTrades = mmh.blockHeads2.getInt("blocks.number", 0) + 1;
            if (debug) {
                mmh.logDebug("iBH2 string=" + string);
            }
            for (int randomBlockHead = 1; randomBlockHead < numOfCustomTrades; randomBlockHead++) {
                ItemStack itemstack = mmh.blockHeads2.getItemStack("blocks.block_" + randomBlockHead + ".itemstack", new ItemStack(Material.AIR));
                SkullMeta skullmeta = Utils.getItemMeta(itemstack);
                if (skullmeta != null) {
                    if (skullmeta.getOwner() != null) {
                        if (skullmeta.getOwner().contains(string)) {
                            return skullmeta.getDisplayName();
                        }
                    }
                }
            }
        } catch (Exception e) {
            return null;
        }
        //blockHeads
        return null;
    }

    public static String isBlockHead3(String string) {
        try {
            if (!(Double.parseDouble(StrUtils.Left(getMCVersion(), 4)) >= 1.16)) {                                                                // checks if the yaml does not exist
                return null;
            }
            mmh.blockFile117 = new File(mmh.getDataFolder(), "block_heads_1_17.yml");
            if (getMCVersion().startsWith("1.16") || getMCVersion().startsWith("1.17")) {
                if (!mmh.blockFile117.exists()) {
                    return null;
                }

            }
            int numOfCustomTrades = mmh.blockHeads3.getInt("blocks.number", 0) + 1;
            if (debug) {
                mmh.logDebug("iBH3 string=" + string);
            }
            for (int randomBlockHead = 1; randomBlockHead < numOfCustomTrades; randomBlockHead++) {
                ItemStack itemstack = mmh.blockHeads3.getItemStack("blocks.block_" + randomBlockHead + ".itemstack", new ItemStack(Material.AIR));
                SkullMeta skullmeta = Utils.getItemMeta(itemstack);
                if (skullmeta != null) {
                    if (skullmeta.getOwner() != null) {
                        if (skullmeta.getOwner().contains(string)) {
                            return skullmeta.getDisplayName();
                        }
                    }
                }
            }
        } catch (Exception e) {
            return null;
        }
        //blockHeads
        return null;
    }


    public static int isBlockHeadName(String string) { // TODO: isBlockHeadName
        if (debug) {
            mmh.logDebug("iBHN START");
        }
        try {
            double mcVer = Double.parseDouble(StrUtils.Left(getMCVersion(), 4));
            if (!(mcVer >= 1.16)) {
                mmh.blockFile = new File(mmh.getDataFolder(), "block_heads.yml");
                if (!mmh.blockFile.exists()) {                                                                    // checks if the yaml does not exist
                    return -1;
                }
            } else if (mcVer == 1.16) {
                mmh.blockFile = new File(mmh.getDataFolder(), "block_heads_1_16.yml");
            } else if (mcVer >= 1.17) {
                mmh.blockFile = new File(mmh.getDataFolder(), "block_heads_1_17.yml");
            }

            if (debug) {
                mmh.logDebug("iBH blockFile=" + mmh.blockFile);
            }
            if (mmh.blockHeads.getInt("blocks.number", 0) == 0) {
                try {
                    mmh.blockHeads.load(mmh.blockFile);
                } catch (IOException | InvalidConfigurationException e1) {
                    mmh.stacktraceInfo();
                    e1.printStackTrace();
                }
            }
            int numOfCustomTrades = mmh.blockHeads.getInt("blocks.number", 0) + 1;
            if (debug) {
                mmh.logDebug("iBH number=" + numOfCustomTrades);
            }
            if (debug) {
                mmh.logDebug("iBH string=" + string);
            }
            for (int randomBlockHead = 1; randomBlockHead < numOfCustomTrades; randomBlockHead++) {
                ItemStack itemstack = mmh.blockHeads.getItemStack("blocks.block_" + randomBlockHead + ".itemstack", new ItemStack(Material.AIR));
                SkullMeta skullmeta = (SkullMeta) itemstack.getItemMeta();
                if (skullmeta != null) {
                    if (ChatColor.stripColor(skullmeta.getDisplayName()).equalsIgnoreCase(string)) {
                        if (debug) {
                            mmh.logDebug("iBHN END Sucess!");
                        }
                        return randomBlockHead; //itemstack.getItemMeta().getDisplayName();
                    }
                }
            }
        } catch (Exception e) {
            //stacktraceInfo();
            e.printStackTrace();
            if (debug) {
                mmh.logDebug("iBHN END Failure=Exception");
            }
            return -1;
        }
        //blockHeads
        if (debug) {
            mmh.logDebug("iBHN END Failure!");
        }
        return -1;
    }

    public static int isBlockHeadName2(String string) {
        if (debug) {
            mmh.logDebug("iBHN2 START");
        }
        try {
            double mcVer = Double.parseDouble(StrUtils.Left(getMCVersion(), 4));
            if (!(mcVer >= 1.16)) {                                                                // checks if the yaml does not exist
                return -1;
            } else if (mcVer == 1.16) {
                mmh.blockFile1162 = new File(mmh.getDataFolder(), "block_heads_1_16_2.yml");
            } else if (mcVer >= 1.17) {
                mmh.blockFile1162 = new File(mmh.getDataFolder(), "block_heads_1_17_2.yml");
            }

            if (getMCVersion().startsWith("1.16") || getMCVersion().startsWith("1.17")) {
                if (!mmh.blockFile1162.exists()) {
                    return -1;
                }

            }
            if (debug) {
                mmh.logDebug("iBH blockFile1162=" + mmh.blockFile1162);
            }
            if (mmh.blockHeads2.getInt("blocks.number", 0) == 0) {
                try {
                    mmh.blockHeads2.load(mmh.blockFile1162);
                } catch (IOException | InvalidConfigurationException e1) {
                    mmh.stacktraceInfo();
                    e1.printStackTrace();
                }
            }
            int numOfCustomTrades = mmh.blockHeads2.getInt("blocks.number", 0) + 1;
            if (debug) {
                mmh.logDebug("iBH2 number=" + numOfCustomTrades);
            }
            if (debug) {
                mmh.logDebug("iBH2 string=" + string);
            }
            for (int randomBlockHead = 1; randomBlockHead < numOfCustomTrades; randomBlockHead++) {
                ItemStack itemstack = mmh.blockHeads2.getItemStack("blocks.block_" + randomBlockHead + ".itemstack", new ItemStack(Material.AIR));
                SkullMeta skullmeta = (SkullMeta) itemstack.getItemMeta();
                if (skullmeta != null) {
                    if (ChatColor.stripColor(skullmeta.getDisplayName()).equalsIgnoreCase(string)) {
                        if (debug) {
                            mmh.logDebug("iBHN END Sucess!");
                        }
                        return randomBlockHead; //itemstack.getItemMeta().getDisplayName();
                    }
                }
            }
        } catch (Exception e) {
            if (debug) {
                mmh.logDebug("iBHN END Failure=Exception");
            }
            return -1;
        }
        //blockHeads
        if (debug) {
            mmh.logDebug("iBHN2 END Failure!");
        }
        return -1;
    }

    public static int isBlockHeadName3(String string) {
        if (debug) {
            mmh.logDebug("iBHN3 START");
        }
        try {
            double mcVer = Double.parseDouble(StrUtils.Left(getMCVersion(), 4));
            if (!(mcVer >= 1.16)) {                                                                // checks if the yaml does not exist
                return -1;
            } else if (mcVer == 1.16) {
                return -1;
            } else if (mcVer >= 1.17) {
                mmh.blockFile117 = new File(mmh.getDataFolder(), "block_heads_1_17_3.yml");
            }

            if (getMCVersion().startsWith("1.16") || getMCVersion().startsWith("1.17")) {
                if (!mmh.blockFile117.exists()) {
                    return -1;
                }

            }
            if (debug) {
                mmh.logDebug("iBHN3 blockFile117=" + mmh.blockFile117);
            }
            if (mmh.blockHeads3.getInt("blocks.number", 0) == 0) {
                try {
                    mmh.blockHeads3.load(mmh.blockFile117);
                } catch (IOException | InvalidConfigurationException e1) {
                    mmh.stacktraceInfo();
                    e1.printStackTrace();
                }
            }
            int numOfCustomTrades = mmh.blockHeads3.getInt("blocks.number", 0) + 1;
            if (debug) {
                mmh.logDebug("iBH3 number=" + numOfCustomTrades);
            }
            if (debug) {
                mmh.logDebug("iBH3 string=" + string);
            }
            for (int randomBlockHead = 1; randomBlockHead < numOfCustomTrades; randomBlockHead++) {
                ItemStack itemstack = mmh.blockHeads3.getItemStack("blocks.block_" + randomBlockHead + ".itemstack", new ItemStack(Material.AIR));
                SkullMeta skullmeta = (SkullMeta) itemstack.getItemMeta();
                if (skullmeta != null) {
                    if (ChatColor.stripColor(skullmeta.getDisplayName()).equalsIgnoreCase(string)) {
                        if (debug) {
                            mmh.logDebug("iBHN END Sucess!");
                        }
                        return randomBlockHead; //itemstack.getItemMeta().getDisplayName();

                    }
                }
            }
        } catch (Exception e) {
            if (debug) {
                mmh.logDebug("iBHN3 END Failure=Exception");
            }
            return -1;
        }
        //blockHeads
        if (debug) {
            mmh.logDebug("iBHN3 END Failure!");
        }
        return -1;
    }

    public static int isBlockHeadName4(String string) {
        if (debug) {
            mmh.logDebug("iBHN4 START");
        }
        try {
            double mcVer = Double.parseDouble(StrUtils.Left(getMCVersion(), 4));
            if (!(mcVer >= 1.16)) {                                                                // checks if the yaml does not exist
                return -1;
            } else if (mcVer == 1.16) {
                return -1;
            } else if (mcVer == 1.19) {
                mmh.blockFile119 = new File(mmh.getDataFolder(), "block_heads_1_19.yml");
            }

            if (getMCVersion().startsWith("1.19")) {
                if (!mmh.blockFile119.exists()) {
                    return -1;
                }

            }
            if (debug) {
                mmh.logDebug("iBHN4 blockFile119=" + mmh.blockFile119);
            }
            if (mmh.blockHeads4.getInt("blocks.number", 0) == 0) {
                try {
                    mmh.blockHeads4.load(mmh.blockFile119);
                } catch (IOException | InvalidConfigurationException e1) {
                    mmh.stacktraceInfo();
                    e1.printStackTrace();
                }
            }
            int numOfCustomTrades = mmh.blockHeads4.getInt("blocks.number", 0) + 1;
            if (debug) {
                mmh.logDebug("iBH4 number=" + numOfCustomTrades);
            }
            if (debug) {
                mmh.logDebug("iBH4 string=" + string);
            }
            for (int randomBlockHead = 1; randomBlockHead < numOfCustomTrades; randomBlockHead++) {
                ItemStack itemstack = mmh.blockHeads4.getItemStack("blocks.block_" + randomBlockHead + ".itemstack", new ItemStack(Material.AIR));
                SkullMeta skullmeta = (SkullMeta) itemstack.getItemMeta();
                if (skullmeta != null) {
                    if (ChatColor.stripColor(skullmeta.getDisplayName()).equalsIgnoreCase(string)) {
                        if (debug) {
                            mmh.logDebug("iBHN4 END Sucess!");
                        }
                        return randomBlockHead; //itemstack.getItemMeta().getDisplayName();
                    }
                }
            }
        } catch (Exception e) {
            if (debug) {
                mmh.logDebug("iBHN4 END Failure=Exception");
            }
            return -1;
        }
        //blockHeads
        if (debug) {
            mmh.logDebug("iBHN4 END Failure!");
        }
        return -1;
    }

    public static int isBlockHeadName5(String string) {
        if (debug) {
            mmh.logDebug("iBHN5 START");
        }
        try {
            double mcVer = Double.parseDouble(StrUtils.Left(getMCVersion(), 4));
            if (!(mcVer >= 1.16)) {                                                                // checks if the yaml does not exist
                return -1;
            } else if (mcVer == 1.16) {
                return -1;
            } else if (mcVer == 1.19) {
                return -1;
            } else if (mcVer == 1.20) {
                mmh.blockFile120 = new File(mmh.getDataFolder(), "block_heads_1_20.yml");
            }

            if (getMCVersion().startsWith("1.20")) {
                if (!mmh.blockFile120.exists()) {
                    return -1;
                }

            }
            if (debug) {
                mmh.logDebug("iBHN5 blockFile120=" + mmh.blockFile120);
            }
            if (mmh.blockHeads5.getInt("blocks.number", 0) == 0) {
                try {
                    mmh.blockHeads5.load(mmh.blockFile120);
                } catch (IOException | InvalidConfigurationException e1) {
                    mmh.stacktraceInfo();
                    e1.printStackTrace();
                }
            }
            int numOfCustomTrades = mmh.blockHeads5.getInt("blocks.number", 0) + 1;
            if (debug) {
                mmh.logDebug("iBH5 number=" + numOfCustomTrades);
            }
            if (debug) {
                mmh.logDebug("iBH5 string=" + string);
            }
            for (int randomBlockHead = 1; randomBlockHead < numOfCustomTrades; randomBlockHead++) {
                ItemStack itemstack = mmh.blockHeads5.getItemStack("blocks.block_" + randomBlockHead + ".itemstack", new ItemStack(Material.AIR));
                SkullMeta skullmeta = (SkullMeta) itemstack.getItemMeta();
                if (skullmeta != null) {
                    if (ChatColor.stripColor(skullmeta.getDisplayName()).equalsIgnoreCase(string)) {
                        if (debug) {
                            mmh.logDebug("iBHN5 END Sucess!");
                        }
                        return randomBlockHead; //itemstack.getItemMeta().getDisplayName();
                    }
                }
            }
        } catch (Exception e) {
            if (debug) {
                mmh.logDebug("iBHN5 END Failure=Exception");
            }
            return -1;
        }
        //blockHeads
        if (debug) {
            mmh.logDebug("iBHN5 END Failure!");
        }
        return -1;
    }

}
