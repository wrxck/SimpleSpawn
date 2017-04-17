package pro.wrxck.simplespawn;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import org.bukkit.configuration.file.FileConfiguration;

import org.bukkit.entity.Player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

import org.bukkit.inventory.PlayerInventory;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;

public class SimpleSpawn extends org.bukkit.plugin.java.JavaPlugin implements org.bukkit.event.Listener {

    public String prefix;
    public boolean spawn;
    public double spawnX;
    public double spawnY;
    public double spawnZ;
    public float spawnYaw;
    public float spawnPitch;
    public String spawnWorld;
    public boolean announceJoin;
    public String joinMessage;
    public boolean clearInventory;

    public void onEnable() {
        getConfig().options().copyDefaults(true);
        saveConfig();
        configReload();
        getServer().getPluginManager().registerEvents(this, this);
        checkConfig();
    }

    public void configReload() {
        this.prefix = getConfig().getString("prefix", "&8[&eSimple&6Spawn&8]");
        this.spawnX = getConfig().getDouble("spawn.x");
        this.spawnY = getConfig().getDouble("spawn.y");
        this.spawnZ = getConfig().getDouble("spawn.z");
        this.spawnYaw = getConfig().getInt("spawn.yaw");
        this.spawnPitch = getConfig().getInt("spawn.pitch");
        this.spawnWorld = getConfig().getString("spawn.world");
        this.announceJoin = getConfig().getBoolean("login.announce-join");
        this.joinMessage = getConfig().getString("login.join-message");
        this.clearInventory = getConfig().getBoolean("clear-inventory-on-join");
        saveConfig();
        reloadConfig();
    }

    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player)sender;
            Location location = player.getLocation();
            World world = player.getLocation().getWorld();
            String worldName = world.getName();
            if ((commandLabel.equalsIgnoreCase("setspawn")) && (sender.hasPermission("simplespawn.setspawn"))) {
                getConfig().set("spawn.world", worldName);
                getConfig().set("spawn.x", Double.valueOf(location.getBlockX() + 0.5D));
                getConfig().set("spawn.y", Double.valueOf(location.getBlockY() + 0.5D));
                getConfig().set("spawn.z", Double.valueOf(location.getBlockZ() + 0.5D));
                getConfig().set("spawn.yaw", Float.valueOf(location.getYaw()));
                getConfig().set("spawn.pitch", Float.valueOf(location.getPitch()));
                getConfig().set("worlds." + worldName, Boolean.valueOf(true));
                getConfig().set("worlds." + worldName + ".x", Double.valueOf(location.getBlockX() + 0.5D));
                getConfig().set("worlds." + worldName + ".y", Double.valueOf(location.getBlockY() + 0.5D));
                getConfig().set("worlds." + worldName + ".z", Double.valueOf(location.getBlockZ() + 0.5D));
                getConfig().set("worlds." + worldName + ".yaw", Float.valueOf(location.getYaw()));
                getConfig().set("worlds." + worldName + ".pitch", Float.valueOf(location.getPitch()));
                getConfig().set("worlds." + worldName + ".enabled", true);
                configReload();
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.prefix) + " §r§aThe global spawn point for all worlds on this server has been updated!");
            } else if ((commandLabel.equalsIgnoreCase("setworldspawn")) && (sender.hasPermission("simplespawn.setworldspawn"))) {
                int x = location.getBlockX();
                int y = location.getBlockY();
                int z = location.getBlockZ();
                player.getWorld().setSpawnLocation(x, y, z);
                getConfig().set("worlds." + worldName, Boolean.valueOf(true));
                getConfig().set("worlds." + worldName + ".x", Double.valueOf(location.getBlockX() + 0.5D));
                getConfig().set("worlds." + worldName + ".y", Double.valueOf(location.getBlockY() + 0.5D));
                getConfig().set("worlds." + worldName + ".z", Double.valueOf(location.getBlockZ() + 0.5D));
                getConfig().set("worlds." + worldName + ".yaw", Float.valueOf(location.getYaw()));
                getConfig().set("worlds." + worldName + ".pitch", Float.valueOf(location.getPitch()));
                getConfig().set("worlds." + worldName + ".enabled", true);
                configReload();
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.prefix) + " §r§aThe spawn point for this world has successfully been updated!");
            } else if ((commandLabel.equalsIgnoreCase("spawn")) && (sender.hasPermission("simplespawn.spawn"))) {
                if (args.length == 0) {
                    sendToSpawn(player); // The player hasn't specified another player, so we're
                    // going to teleport them to spawn.
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.prefix) + " §r§aTeleporting...");
                } else if ((args.length == 1) && (sender.hasPermission("simplespawn.spawn.others"))) {
                    if (sender.getServer().getPlayer(args[0]) != null) {
                        Player otherPlayer = sender.getServer().getPlayer(args[0]);
                        sendToSpawn(otherPlayer);
                    } else {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.prefix) + " §r§4That player does not exist!");
                    }
                }
            } else if ((commandLabel.equalsIgnoreCase("simplespawn")) && (sender.hasPermission("simplespawn.info"))) {
                player.sendMessage("§8[§eSimple§6Spawn§8] §bSimpleSpawn v1.0.0, created by §3@wrxck§b.");
                if (player.hasPermission("simplespawn.reload")) {
                    configReload();
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.prefix) + " §r§aReloaded the plugin's configuration file!");
                }
            } else if ((commandLabel.equalsIgnoreCase("worldspawn")) && (sender.hasPermission("simplespawn.worldspawn"))) {
                if (getConfig().getBoolean("worlds." + worldName + ".enabled")) {
                    Location spawnLocation = new Location(world, getConfig().getDouble("worlds." + worldName + ".x"), getConfig().getDouble("worlds." + worldName + ".y"), getConfig().getDouble("worlds." + worldName + ".z"), getConfig().getInt("worlds." + worldName + ".yaw"), getConfig().getInt("worlds." + worldName + ".pitch"));
                    player.teleport(spawnLocation);
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.prefix) + " §r§2You have been teleported to the world's spawn point!");
                } else {
                    player.teleport(player.getWorld().getSpawnLocation().add(0.5D, 0.5D, 0.5D));
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.prefix) + " §r§cNo spawn point has been set for this world, so you've been sent to the default spawn point instead!");
                }
            }
        } else {
            Bukkit.getConsoleSender().sendMessage("§4[SimpleSpawn] This plugin can only be used in-game!");
        }
        return true;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Location location = player.getLocation();
        if ((player.hasPermission("simplespawn.joinmessage")) && (this.announceJoin)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.joinMessage.replace("%player%", player.getName())));
        }
        if (getConfig().getBoolean("sound.enabled")) {
            player.playSound(location, org.bukkit.Sound.valueOf(getConfig().getString("sounds.sound")), getConfig().getInt("sound.volume"), getConfig().getInt("sound.pitch"));
        }
        if ((this.clearInventory) && (player.hasPermission("simplespawn.clearinventory"))) {
            clearInventory(player);
        }
    }

    public void checkConfig() {
        if (getConfig().getInt("version") != 1) {
            Bukkit.getConsoleSender().sendMessage("§4[SimpleSpawn] Your configuration file appears to be outdated/corrupt!");
            Bukkit.getConsoleSender().sendMessage("§4[SimpleSpawn] Please delete plugins/SimpleSpawn/config.yml and try again!");
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    public void clearInventory(Player player) {
        player.getInventory().clear();
        player.getInventory().setArmorContents(new org.bukkit.inventory.ItemStack[4]);
        player.updateInventory();
    }

    public void setSpawn(String configLocation, String location) {
        getConfig().set(configLocation, location);
    }

    public void sendToSpawn(Player player) {
        if (this.spawnWorld == null) {
            player.teleport(player.getWorld().getSpawnLocation().add(0.5D, 0.5D, 0.5D));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.prefix) + " §r§cThere was no configured spawn point found, so you were sent to the world's default spawn point instead.");
        } else {
            Location spawnLocation = new Location(Bukkit.getServer().getWorld(this.spawnWorld), this.spawnX, this.spawnY, this.spawnZ, this.spawnYaw, this.spawnPitch);
            player.teleport(spawnLocation);
        }
    }

}
