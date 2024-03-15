package ru.cyanoriss.groupcolor;

import me.clip.placeholderapi.expansion.Configurable;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public final class Main extends PlaceholderExpansion implements Configurable {
    private Permission permission;

    public Main() {
        setupPermissions();
    }

    @Override
    public Map<String, Object> getDefaults() {
        Map<String, Object> defaults = new HashMap<>();
        defaults.put("group.vip", "&e");
        defaults.put("group.default", "&7");
        return defaults;
    }

    private void setupPermissions() {
        RegisteredServiceProvider<Permission> rsp;
        try {
            rsp = Bukkit.getServer().getServicesManager().getRegistration(Permission.class);
        } catch (NoClassDefFoundError e) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    Bukkit.getLogger().severe("[groupcolor] Установите Vault и любой плагин на права для корректной работы");
                    Bukkit.getPluginManager().disablePlugin(getPlaceholderAPI());
                }
            }.runTaskLater(getPlaceholderAPI(), 100L);
            return;
        }
        if (rsp != null) {
            permission = rsp.getProvider();
        }
    }

    private String getHighestGroup(Player player) {
        if (permission == null || player == null) throw new IllegalArgumentException();

        for (String key : getConfigSection("group").getKeys(false)) {
            if (permission.playerInGroup(player, key)) {
                return key;
            }
        }
        throw new NoSuchElementException();
    }


    @Override
    public @NotNull String getIdentifier() {
        return "groupcolor";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Cyanoriss";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        if (!params.equalsIgnoreCase("color")) return "";

        try {
            return getString("group." + getHighestGroup(player), "&fНе настроено");
        } catch (IllegalArgumentException e) {
            return "&fИгрок не найден";
        } catch (NoSuchElementException e) {
            return "";
        } catch (UnsupportedOperationException e) {
            Bukkit.getLogger().severe("Установите Vault и любой плагин на права для корректной работы");
            Bukkit.getPluginManager().disablePlugin(getPlaceholderAPI());
            return "";
        }
    }
}
