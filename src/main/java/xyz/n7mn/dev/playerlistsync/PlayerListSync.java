package xyz.n7mn.dev.playerlistsync;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Plugin;
import xyz.n7mn.dev.playerlistsync.command.ServerPlayerList;
import xyz.n7mn.dev.playerlistsync.config.ConfigJson;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public final class PlayerListSync extends Plugin {

    private Connection con = null;
    private Timer timer = new Timer();
    private int playerCount = 0;
    private ConfigJson configJson;

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
        new Thread(()->{

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

            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    if (con == null){
                        try {
                            con = DriverManager.getConnection("jdbc:mysql://" + configJson.getMySQLServer() + ":" + configJson.getMySQLPort() + "/" + configJson.getMySQLDatabase() + configJson.getMySQLOption(),configJson.getMySQLUsername(), configJson.getMySQLPassword());
                            con.setAutoCommit(true);
                        } catch (SQLException e){
                            e.printStackTrace();
                        }
                    }

                    new Thread(()->{
                        try {
                            try {
                                PreparedStatement statement = con.prepareStatement("SELECT * FROM ServerList");
                                statement.execute();
                                statement.close();
                            } catch (Exception ex1){
                                con = DriverManager.getConnection("jdbc:mysql://" + configJson.getMySQLServer() + ":" + configJson.getMySQLPort() + "/" + configJson.getMySQLDatabase() + configJson.getMySQLOption(), configJson.getMySQLUsername(), configJson.getMySQLPassword());
                                con.setAutoCommit(true);
                            }

                            PreparedStatement statement1 = con.prepareStatement("SELECT * FROM ServerList WHERE ServerName = ? AND ServerNo = ?");
                            statement1.setString(1, configJson.getServerName());
                            statement1.setInt(2, configJson.getServerNo());
                            ResultSet set = statement1.executeQuery();

                            boolean isFound = false;
                            if (set.next()){
                                isFound = true;
                                set.close();
                                statement1.close();
                            }

                            int tempPlayerCount = 0;
                            for (Map.Entry<String, ServerInfo> entry : getProxy().getServersCopy().entrySet()) {
                                ServerInfo server = entry.getValue();
                                tempPlayerCount = tempPlayerCount + server.getPlayers().size();
                            }

                            PreparedStatement statement2;
                            if (isFound){
                                statement2 = con.prepareStatement("UPDATE `ServerList` SET `PlayerCount`= ? WHERE ServerName = ? AND ServerNo = ?");
                                statement2.setInt(1, tempPlayerCount);
                                statement2.setString(2, configJson.getServerName());
                                statement2.setInt(3, configJson.getServerNo());
                            } else {
                                statement2 = con.prepareStatement("INSERT INTO `ServerList`(`UUID`, `ServerName`, `ServerNo`, `PlayerCount`) VALUES (?,?,?,?)");
                                statement2.setString(1, UUID.randomUUID().toString());
                                statement2.setString(2, configJson.getServerName());
                                statement2.setInt(3, configJson.getServerNo());
                                statement2.setInt(4, tempPlayerCount);
                            }

                            statement2.execute();
                            statement2.close();

                            tempPlayerCount = 0;
                            PreparedStatement statement3 = con.prepareStatement("SELECT * FROM ServerList WHERE ServerName = ?");
                            statement3.setString(1, configJson.getServerName());
                            ResultSet set1 = statement3.executeQuery();
                            while (set1.next()){
                                tempPlayerCount = tempPlayerCount + set1.getInt("PlayerCount");
                            }
                            playerCount = tempPlayerCount;
                            set1.close();
                            statement3.close();

                        } catch (SQLException ex){
                            ex.printStackTrace();
                            timer.cancel();
                        }
                    }).start();
                }
            };


            timer.scheduleAtFixedRate(task, 0L, 100L);
        }).start();

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        try {
            timer.cancel();
            if (con == null){
                return;
            }
            con.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public int getPlayerCount() {
        return playerCount;
    }

    public Connection getConnect() {
        return con;
    }

    public ConfigJson getConfig() {
        return configJson;
    }
}
