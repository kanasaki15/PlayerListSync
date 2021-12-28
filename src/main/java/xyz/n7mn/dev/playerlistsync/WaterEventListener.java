package xyz.n7mn.dev.playerlistsync;

import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;


public class WaterEventListener implements Listener {

    private final PlayerListSync plugin;

    public WaterEventListener(PlayerListSync plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void ProxyPingEvent (ProxyPingEvent e){
        ServerPing.Players players = e.getResponse().getPlayers();

        if (plugin.getListCount() > 0 && plugin.getListCount() <= Integer.MAX_VALUE){
            players.setOnline((int) plugin.getListCount());
        }
    }
}
