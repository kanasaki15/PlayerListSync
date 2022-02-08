package xyz.n7mn.dev.playerlistsync;

import com.google.gson.Gson;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import xyz.n7mn.dev.playerlistsync.TCP.ReceptionData;
import xyz.n7mn.dev.playerlistsync.TCP.SendData;
import xyz.n7mn.dev.playerlistsync.TCP.ServerPlayerListData;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;


public class WaterEventListener implements Listener {

    private final PlayerListSync plugin;

    public WaterEventListener(PlayerListSync plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void ProxyPingEvent (ProxyPingEvent e){
        ServerPing.Players players = e.getResponse().getPlayers();
        int i = 0;

        players.setMax(plugin.getConfig().getLimitPlayerCount());
        try {
            Socket sock = new Socket(plugin.getConfig().getServerIP(), 19009);
            OutputStream out = sock.getOutputStream();
            out.write(new Gson().toJson(new SendData("ping", null, plugin.getConfig().getServerName(), 0, plugin.getConfig().getServerDisplayName(), "")).getBytes(StandardCharsets.UTF_8));
            out.flush();

            InputStream in = sock.getInputStream();
            byte[] ByteData = new byte[16384];
            int readSize = in.read(ByteData);
            ByteData = Arrays.copyOf(ByteData, readSize);

            if (ByteData.length != 0){
                String str = new String(ByteData, StandardCharsets.UTF_8);
                //System.out.println(str);
                ReceptionData json = new Gson().fromJson(str, ReceptionData.class);
                if (json.getStatus().toLowerCase().equals("ok")){
                    if (json.getPlayerList() <= Integer.MAX_VALUE){
                        i = Math.toIntExact(json.getPlayerList());
                    }
                }
            }

            in.close();
            out.close();
            sock.close();
        } catch (Exception ex){
            ex.printStackTrace();
        }

        players.setOnline(i);

    }
}
