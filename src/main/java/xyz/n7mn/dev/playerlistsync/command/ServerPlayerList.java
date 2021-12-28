package xyz.n7mn.dev.playerlistsync.command;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import xyz.n7mn.dev.playerlistsync.PlayerListSync;
import xyz.n7mn.dev.playerlistsync.config.ConfigJson;

import java.sql.*;

public class ServerPlayerList extends Command {

    private final PlayerListSync plugin;

    public ServerPlayerList(PlayerListSync plugin){
        super("wlist");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        ConfigJson config = plugin.getConfig();

        if (!sender.hasPermission("playersync.op")){
            return;
        }

        new Thread(()->{
            //tempCount = tempCount + set.getInt("PlayerCount");
            //sender.sendMessage(new TextComponent(ChatColor.GREEN+"["+set.getString("ServerNameList.ServerName")+"] "+ChatColor.YELLOW+"("+set.getInt("PlayerCount")+")"+ChatColor.RESET));
            //sender.sendMessage(new TextComponent("Total players online: "+tempCount));
        }).start();

    }
}
