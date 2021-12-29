package xyz.n7mn.dev.playerlistsync.TCP;

import java.util.List;

public class ReceptionData {

    private String Status;
    private String Mode;
    private long PlayerList;
    private List<ServerPlayerList> ServerPlayerList;

    public ReceptionData(String status, String mode, long playerList, List<ServerPlayerList> serverPlayerList){
        this.Status = status;
        this.Mode = mode;
        this.PlayerList = playerList;
        this.ServerPlayerList = serverPlayerList;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getMode() {
        return Mode;
    }

    public void setMode(String mode) {
        Mode = mode;
    }

    public long getPlayerList() {
        return PlayerList;
    }

    public void setPlayerList(long playerList) {
        PlayerList = playerList;
    }

    public List<ServerPlayerList> getServerPlayerList() {
        return ServerPlayerList;
    }

    public void setServerPlayerList(List<ServerPlayerList> serverPlayerList) {
        ServerPlayerList = serverPlayerList;
    }

}
