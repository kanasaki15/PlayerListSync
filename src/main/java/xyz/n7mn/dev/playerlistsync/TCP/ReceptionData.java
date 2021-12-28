package xyz.n7mn.dev.playerlistsync.TCP;

public class ReceptionData {

    private String Status = "ok";
    private String Mode = "";
    private long PlayerList = 0;

    public ReceptionData(String status, String mode, long playerList){
        this.Status = status;
        this.Mode = mode;
        this.PlayerList = playerList;
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

}
