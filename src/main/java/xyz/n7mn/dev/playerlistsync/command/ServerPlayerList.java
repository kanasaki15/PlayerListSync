package xyz.n7mn.dev.playerlistsync.command;

import com.google.gson.Gson;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import xyz.n7mn.dev.playerlistsync.PlayerListSync;
import xyz.n7mn.dev.playerlistsync.TCP.ReceptionData;
import xyz.n7mn.dev.playerlistsync.TCP.SendData;
import xyz.n7mn.dev.playerlistsync.TCP.ServerPlayerListData;
import xyz.n7mn.dev.playerlistsync.config.ConfigJson;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;


public class ServerPlayerList extends Command {

    private final PlayerListSync plugin;

    public ServerPlayerList(PlayerListSync plugin){
        super("wlist");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        ConfigJson config = plugin.getConfig();

        new Thread(()->{
            try {
                Socket sock = new Socket(config.getServerIP(), 19009);
                OutputStream out = sock.getOutputStream();

                out.write(new Gson().toJson(new SendData("list", plugin.getServerUuid(), config.getServerName(), config.getServerNo(), config.getServerDisplayName(), "")).getBytes(StandardCharsets.UTF_8));
                out.flush();

                InputStream in = sock.getInputStream();
                byte[] ByteData = new byte[16384];
                int readSize = in.read(ByteData);
                ByteData = Arrays.copyOf(ByteData, readSize);

                String str = new String(ByteData, StandardCharsets.UTF_8);
                ReceptionData json = new Gson().fromJson(str, ReceptionData.class);
                List<ServerPlayerListData> serverPlayerListData = json.getServerPlayerList();

                long count = 0;
                for (ServerPlayerListData data : serverPlayerListData){
                    sender.sendMessage(new TextComponent(ChatColor.GREEN+"["+data.getServerName()+"] "+ChatColor.YELLOW+"("+data.getPlayerList().length+")"+ChatColor.RESET));
                    count = count + data.getPlayerList().length;
                }
                sender.sendMessage(new TextComponent("Total players online: "+count));

            } catch (Exception ex){
                ex.printStackTrace();
            }

        }).start();

    }
}
