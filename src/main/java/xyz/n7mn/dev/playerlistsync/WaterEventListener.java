package xyz.n7mn.dev.playerlistsync;

import com.google.gson.Gson;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;
import xyz.n7mn.dev.playerlistsync.config.ConfigJson;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class WaterEventListener implements Listener {

    private final Plugin plugin;
    private final ConfigJson config;
    private Map<UUID, String> list = new HashMap<>();

    public WaterEventListener(Plugin plugin){
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



        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                list.clear();
            }
        }, 0L, 10000L);
    }

    @EventHandler
    public void ProxyPingEvent (ProxyPingEvent e){

        String ip = e.getConnection().getSocketAddress().toString().replaceAll("/", "").split(":")[0];

        AtomicInteger i = new AtomicInteger();
        list.forEach(((uuid, s) -> {
            if (s.equals(ip)){
                i.getAndIncrement();
            }
        }));

        if (i.get() > config.getLimitPingCount()){
            e.getConnection().disconnect(new TextComponent(""));
            return;
        }
        list.put(UUID.randomUUID(), ip);

        int playerCount = 0;

        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://" + config.getMySQLServer() + ":" + config.getMySQLPort() + "/" + config.getMySQLDatabase() + config.getMySQLOption(), config.getMySQLUsername(), config.getMySQLPassword());

            PreparedStatement statement = con.prepareStatement("SELECT * FROM PlayerList WHERE Active = 1 AND ServerName = ?");
            statement.setString(1, config.getServerName());
            ResultSet set = statement.executeQuery();
            while (set.next()){
                playerCount++;
            }
            set.close();
            statement.close();
            con.close();
        } catch (SQLException ex){
            ex.printStackTrace();
        }

        ServerPing.Players players = new ServerPing.Players(config.getLimitPlayerCount(), playerCount, null);
        e.getResponse().setPlayers(players);

    }

    @EventHandler
    public void ServerConnectEvent(ServerConnectEvent e){
        new Thread(()->{
            try {
                Connection con = DriverManager.getConnection("jdbc:mysql://" + config.getMySQLServer() + ":" + config.getMySQLPort() + "/" + config.getMySQLDatabase() + config.getMySQLOption(), config.getMySQLUsername(), config.getMySQLPassword());

                PreparedStatement statement1 = con.prepareStatement("SELECT * FROM `PlayerList` WHERE MinecraftUUID = ? AND ServerName = ? AND Active = 1");
                statement1.setString(1, e.getPlayer().getUniqueId().toString());
                statement1.setString(2, config.getServerName());
                ResultSet set = statement1.executeQuery();
                if (set.next()){
                    set.close();
                    statement1.close();
                    con.close();
                    return;
                }

                PreparedStatement statement2 = con.prepareStatement("INSERT INTO `PlayerList`(`UUID`, `MinecraftUUID`, `ServerName`, `Date`, `Active`) VALUES (?, ?, ?, NOW(), 1)");
                statement2.setString(1, UUID.randomUUID().toString());
                statement2.setString(2, e.getPlayer().getUniqueId().toString());
                statement2.setString(3, config.getServerName());
                statement2.execute();
                statement2.close();
                con.close();
            } catch (SQLException ex){
                ex.printStackTrace();
            }
        }).start();
    }

    @EventHandler
    public void PlayerDisconnectEvent (PlayerDisconnectEvent e){
        new Thread(()->{
            try {
                Connection con = DriverManager.getConnection("jdbc:mysql://" + config.getMySQLServer() + ":" + config.getMySQLPort() + "/" + config.getMySQLDatabase() + config.getMySQLOption(), config.getMySQLUsername(), config.getMySQLPassword());

                PreparedStatement statement = con.prepareStatement("UPDATE `PlayerList` SET `Active`= 0 WHERE MinecraftUUID = ? AND ServerName = ?");
                statement.setString(1, e.getPlayer().getUniqueId().toString());
                statement.setString(2, config.getServerName());
                statement.execute();
                statement.close();
                con.close();
            } catch (SQLException ex){
                ex.printStackTrace();
            }
        }).start();
    }

}
