package top.tokia.tinenickitem;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class TineNickItem extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        // 在控制台输出插件启用消息
        getLogger().info("准备注册昵称卡插件");
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("昵称卡插件已启用");
    }
    @Override
    public void onDisable() {
        // 在控制台输出插件卸载消息
        getLogger().info("昵称卡插件正在卸载");
    }
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType() == Material.GOLDEN_APPLE && item.getItemMeta().getDisplayName().equals("§6Nick Item")) {
            event.setCancelled(true);
            player.sendMessage("请输入你的昵称（输入 cancel 取消）：");
            getPlayerNickInput(player);
        }
    }

    private void getPlayerNickInput(Player player) {
        getServer().getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onPlayerChat(org.bukkit.event.player.AsyncPlayerChatEvent event) {
                if (event.getPlayer() == player) {
                    String nickName = event.getMessage();
                    event.setCancelled(true);
                    if ("cancel".equalsIgnoreCase(nickName)) {
                        player.sendMessage("取消了昵称设置");
                    } else {

                        int maxNickLength = getMaxNickLength();
                        if (nickName.contains(" ")) {
                            player.sendMessage("昵称不能包含空格");
                        } else if(nickName.length() > maxNickLength){
                            player.sendMessage("昵称长度不能超过" + maxNickLength + "个字符");
                        }else {
                            String nick = nickName.substring(nickName.length() + 1);
                            Bukkit.dispatchCommand(player, "nick " + nickName + " " + nick);
                            player.sendMessage("昵称设置成功");
                            player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
                        }
                    }
                    org.bukkit.event.HandlerList.unregisterAll(this);
                }
            }
        }, this);
    }

    public int getMaxNickLength() {
        // 默认值为5
        int maxNickLength = 5;

        // 获取配置文件中的参数
        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            // 如果配置文件不存在，创建默认的配置文件
            getConfig().addDefault("maxNickLength", maxNickLength);
            getConfig().options().copyDefaults(true);
            saveConfig();
        } else {
            // 如果配置文件存在，读取其中的参数
            maxNickLength = getConfig().getInt("maxNickLength");
        }

        return maxNickLength;
    }

    @EventHandler
    public void onPluginEnable(PluginEnableEvent event) {
        // 在控制台输出插件加载消息
        getLogger().info(String.format("插件 %s 已加载", event.getPlugin().getName()));
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {
        // 在控制台输出插件卸载消息
        getLogger().info(String.format("插件 %s 已卸载", event.getPlugin().getName()));
    }
}
