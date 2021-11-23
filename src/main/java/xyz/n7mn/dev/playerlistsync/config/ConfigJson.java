package xyz.n7mn.dev.playerlistsync.config;

public class ConfigJson {

    private String MySQLServer = "";
    private int MySQLPort = 3306;
    private String MySQLDatabase = "";
    private String MySQLOption = "?allowPublicKeyRetrieval=true&useSSL=false";
    private String MySQLUsername = "";
    private String MySQLPassword = "";

    private int LimitPlayerCount = 50;
    private int LimitPingCount = 15;

    private String ServerName = "";

    public ConfigJson(){

    }

    public ConfigJson(String mySQLServer, int mySQLPort, String mySQLDatabase, String mySQLOption, String mySQLUsername, String mySQLPassword, int limitPlayerCount, int limitPingCount, String serverName){
        this.MySQLServer = mySQLServer;
        this.MySQLPort = mySQLPort;
        this.MySQLDatabase = mySQLDatabase;
        this.MySQLOption = mySQLOption;
        this.MySQLUsername = mySQLUsername;
        this.MySQLPassword = mySQLPassword;

        this.LimitPlayerCount = limitPlayerCount;
        this.LimitPingCount = limitPingCount;
        this.ServerName = serverName;
    }

    public String getMySQLServer() {
        return MySQLServer;
    }

    public void setMySQLServer(String mySQLServer) {
        MySQLServer = mySQLServer;
    }

    public int getMySQLPort() {
        return MySQLPort;
    }

    public void setMySQLPort(int mySQLPort) {
        MySQLPort = mySQLPort;
    }

    public String getMySQLDatabase() {
        return MySQLDatabase;
    }

    public void setMySQLDatabase(String mySQLDatabase) {
        MySQLDatabase = mySQLDatabase;
    }

    public String getMySQLOption() {
        return MySQLOption;
    }

    public int getLimitPlayerCount() {
        return LimitPlayerCount;
    }

    public int getLimitPingCount() {
        return LimitPingCount;
    }

    public String getServerName() {
        return ServerName;
    }

    public void setMySQLOption(String mySQLOption) {
        MySQLOption = mySQLOption;
    }

    public String getMySQLUsername() {
        return MySQLUsername;
    }

    public void setMySQLUsername(String mySQLUsername) {
        MySQLUsername = mySQLUsername;
    }

    public String getMySQLPassword() {
        return MySQLPassword;
    }

    public void setMySQLPassword(String mySQLPassword) {
        MySQLPassword = mySQLPassword;
    }

    public void setLimitPlayerCount(int limitPlayerCount) {
        LimitPlayerCount = limitPlayerCount;
    }

    public void setLimitPingCount(int limitPingCount) {
        LimitPingCount = limitPingCount;
    }

    public void setServerName(String serverName) {
        ServerName = serverName;
    }
}
