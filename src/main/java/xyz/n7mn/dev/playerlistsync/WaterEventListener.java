package xyz.n7mn.dev.playerlistsync;

import com.google.gson.Gson;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;
import xyz.n7mn.dev.playerlistsync.config.ConfigJson;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;

public class WaterEventListener implements Listener {

    private final Plugin plugin;
    private ConfigJson config;

    public WaterEventListener(Plugin plugin) {
        this.plugin = plugin;
        File file = new File(plugin.getDataFolder().getPath()+"/config.json");
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
            String text;
            while ((text = reader.readLine()) != null) {
                sb.append(text);
            }
        } catch (IOException ex){
            ex.printStackTrace();
        }
        config = new Gson().fromJson(sb.toString(), ConfigJson.class);
    }


    @EventHandler
    public void ProxyPingEvent (ProxyPingEvent e){
        int playerCount = 0;

        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://" + config.getMySQLServer() + ":" + config.getMySQLPort() + "/" + config.getMySQLDatabase() + config.getMySQLOption(), config.getMySQLUsername(), config.getMySQLPassword());
            con.setAutoCommit(true);

            PreparedStatement statement = con.prepareStatement("SELECT * FROM ServerList WHERE ServerName = ?");
            statement.setString(1, config.getServerName());

            ResultSet set = statement.executeQuery();
            while (set.next()){
                playerCount = playerCount + set.getInt("PlayerCount");
            }
            set.close();
            statement.close();
            con.close();
        } catch (SQLException ex){
            ex.printStackTrace();
        }

        ServerPing.Players players = e.getResponse().getPlayers();
        players.setOnline(playerCount);
    }
}
