package xyz.n7mn.dev.playerlistsync.command;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
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
            Connection con = plugin.getConnect();

            try {
                try {
                    PreparedStatement statement = con.prepareStatement("SELECT * FROM ServerList");
                    statement.execute();
                    statement.close();
                } catch (Exception ex1){
                    con = DriverManager.getConnection("jdbc:mysql://" + config.getMySQLServer() + ":" + config.getMySQLPort() + "/" + config.getMySQLDatabase() + config.getMySQLOption(), config.getMySQLUsername(), config.getMySQLPassword());
                    con.setAutoCommit(true);
                }

                PreparedStatement statement1 = con.prepareStatement("SELECT * FROM ServerList, ServerNameList WHERE ServerList.UUID = ServerNameList.UUID AND ServerList.ServerName = ? ORDER BY ServerList.ServerName ASC, ServerList.ServerNo ASC");
                statement1.setString(1, config.getServerName());
                int tempCount = 0;
                ResultSet set = statement1.executeQuery();
                while (set.next()){
                    tempCount = tempCount + set.getInt("PlayerCount");
                    sender.sendMessage(new TextComponent(ChatColor.GREEN+"["+set.getString("ServerNameList.ServerName")+"] "+ChatColor.YELLOW+"("+set.getInt("PlayerCount")+")"+ChatColor.RESET));
                }
                sender.sendMessage(new TextComponent("Total players online: "+tempCount));
                set.close();
                statement1.close();

            } catch (SQLException ex){
                ex.printStackTrace();
            }

        }).start();

    }
}
