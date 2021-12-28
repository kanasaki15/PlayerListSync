package xyz.n7mn.dev.playerlistsync.config;

public class ConfigJson {

    private String ServerIP = "127.0.0.1";

    private int LimitPlayerCount = 50;
    private int LimitPingCount = 15;

    private String ServerName = "";
    private int ServerNo = 0;

    public ConfigJson(){

    }

    public ConfigJson(String serverIP, int limitPlayerCount, int limitPingCount, String serverName, int serverNo){
        this.ServerIP = serverIP;

        this.LimitPlayerCount = limitPlayerCount;
        this.LimitPingCount = limitPingCount;
        this.ServerName = serverName;
        this.ServerNo = serverNo;
    }


    public String getServerIP() {
        return ServerIP;
    }

    public void setServerIP(String serverIP) {
        ServerIP = serverIP;
    }

    public void setLimitPlayerCount(int limitPlayerCount) {
        LimitPlayerCount = limitPlayerCount;
    }

    public void setLimitPingCount(int limitPingCount) {
        LimitPingCount = limitPingCount;
    }

    public String getServerName() {
        return ServerName;
    }

    public void setServerName(String serverName) {
        ServerName = serverName;
    }

    public int getServerNo() {
        return ServerNo;
    }

    public void setServerNo(int serverNo) {
        ServerNo = serverNo;
    }
}
