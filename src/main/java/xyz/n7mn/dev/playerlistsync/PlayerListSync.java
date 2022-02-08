package xyz.n7mn.dev.playerlistsync;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import xyz.n7mn.dev.playerlistsync.TCP.ReceptionData;
import xyz.n7mn.dev.playerlistsync.TCP.SendData;
import xyz.n7mn.dev.playerlistsync.TCP.ServerPlayerListData;
import xyz.n7mn.dev.playerlistsync.command.ServerPlayerList;
import xyz.n7mn.dev.playerlistsync.config.ConfigJson;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public final class PlayerListSync extends Plugin {

    private ConfigJson configJson;
    private Timer timer = new Timer();
    private AtomicLong listCount = new AtomicLong();
    private final UUID[] uuid = {null};

    @Override
    public void onEnable() {
        // Plugin startup logic

        // 設定ファイルがなかったら新規作成する
        File file = getProxy().getPluginManager().getPlugin("PlayerListSync").getDataFolder();
        //System.out.println(file.getPath());
        if (!file.exists()){
            file.mkdir();
        }

        File config = new File(file.getPath()+"/config.json");
        if (!config.exists()){
            try {
                String json = new GsonBuilder().serializeNulls().setPrettyPrinting().create().toJson(new ConfigJson());

                PrintWriter writer = new PrintWriter(file.getPath()+"/config.json");
                writer.print(json);
                writer.close();
            } catch (Exception ex){
                ex.printStackTrace();
            }
        }


        getProxy().getPluginManager().registerListener(this, new WaterEventListener(this));
        getProxy().getPluginManager().registerCommand(this, new ServerPlayerList(this));

        // 設定ファイル読み込んで定期実行タスク生成
        File file1 = new File(this.getDataFolder().getPath()+"/config.json");
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file1), StandardCharsets.UTF_8));
            String text;
            while ((text = reader.readLine()) != null) {
                sb.append(text);
            }
        } catch (IOException ex){
            ex.printStackTrace();
        }
        configJson = new Gson().fromJson(sb.toString(), ConfigJson.class);


        // 自Waterfallの人数
        AtomicInteger myCount = new AtomicInteger();
        getProxy().getServersCopy().forEach((s, serverInfo) -> {
            for (ProxiedPlayer player : serverInfo.getPlayers()){
                myCount.set(myCount.get()+1);
            }
        });

        TimerTask task = new TimerTask() {
            @Override
            public void run() {

                try {
                    if (uuid[0] == null){
                        Socket sock = new Socket(getConfig().getServerIP(), 19009);
                        OutputStream out = sock.getOutputStream();

                        out.write(new Gson().toJson(new SendData("getcode", null, getConfig().getServerName(), getConfig().getServerNo(), getConfig().getServerDisplayName(), sb.substring(0, sb.length() - 1))).getBytes(StandardCharsets.UTF_8));
                        out.flush();

                        InputStream in = sock.getInputStream();

                        byte[] ByteData = new byte[16384];
                        int readSize = in.read(ByteData);
                        ByteData = Arrays.copyOf(ByteData, readSize);
                        String str = new String(ByteData, StandardCharsets.UTF_8);
                        //System.out.println("「"+new String(ByteData, StandardCharsets.UTF_8)+"」を受信しました。");
                        ReceptionData json = new Gson().fromJson(str, ReceptionData.class);
                        if (json.getStatus().toLowerCase().equals("ok")){
                            String[] split = json.getMode().split(",");
                            if (split.length == 2){
                                uuid[0] = UUID.fromString(split[1]);
                                //getProxy().getLogger().info("ServerCode取得完了 : "+ uuid[0]);
                            }
                        }
                        in.close();
                        out.close();
                        sock.close();
                    }
                } catch (Exception e){
                    e.printStackTrace();
                    //timer.cancel();
                    return;
                }

                if (uuid[0] == null){
                    return;
                }

                new Thread(()->{
                    try {
                        Socket sock1 = new Socket(getConfig().getServerIP(), 19009);
                        OutputStream out1 = sock1.getOutputStream();
                        StringBuffer sb = new StringBuffer();
                        getProxy().getServersCopy().forEach((s, serverInfo) -> {
                            for (ProxiedPlayer player : serverInfo.getPlayers()){
                                sb.append(player.getUniqueId().toString());
                                sb.append(",");
                            }
                        });
                        if (sb.length() == 0){
                            sb.append(",");
                        }
                        String json = new Gson().toJson(new SendData("player", uuid[0], "", 0, getConfig().getServerDisplayName(), sb.substring(0, sb.length() - 1)));
                        out1.write(json.getBytes(StandardCharsets.UTF_8));
                        out1.flush();

                        InputStream in1 = sock1.getInputStream();
                        byte[] ByteData1 = new byte[16384];
                        int readSize1 = in1.read(ByteData1);
                        ByteData1 = Arrays.copyOf(ByteData1, readSize1);

                        String str1 = new String(ByteData1, StandardCharsets.UTF_8);
                        ReceptionData json1 = new Gson().fromJson(str1, ReceptionData.class);
                        if (json1.getStatus().toLowerCase().equals("ok")){
                            //getProxy().getLogger().info("プレーヤーリスト同期完了");
                        } else {
                            getProxy().getLogger().info("プレーヤーリスト同期失敗");
                            //timer.cancel();
                        }
                        in1.close();
                        out1.close();
                        sock1.close();
                    } catch (Exception ex){
                        //timer.cancel();
                        ex.printStackTrace();
                        getProxy().getLogger().info("サーバー接続失敗");
                        //uuid[0] = null;
                    }
                }).start();
            }
        };

        timer.scheduleAtFixedRate(task, 0, 1000L);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        timer.cancel();
    }

    public ConfigJson getConfig() {
        return configJson;
    }

    public long getListCount() {
        return listCount.get();
    }

    public UUID getServerUuid() {
        return uuid[0];
    }
}
