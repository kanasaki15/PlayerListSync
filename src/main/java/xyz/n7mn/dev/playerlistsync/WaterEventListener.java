package xyz.n7mn.dev.playerlistsync;

import com.google.gson.Gson;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import xyz.n7mn.dev.playerlistsync.config.ConfigJson;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class WaterEventListener implements Listener {

    private final PlayerListSync plugin;
    private ConfigJson config;

    public WaterEventListener(PlayerListSync plugin) {
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
        ServerPing.Players players = e.getResponse().getPlayers();
        players.setOnline(plugin.getPlayerCount());
    }
}
