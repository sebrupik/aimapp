package aimapp.objects;

public class ClientInfo {
    String title;
    int cpk;

    public ClientInfo(String title, int cpk) {
        this.title = title;
        this.cpk = cpk;
    }

    public String toString() { return title; }
    public int getCPK() { return cpk; }
}