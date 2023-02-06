package com.caiomgt.sabotage;
import java.util.UUID;

public class Data {
    public Data(){

    }
    public Data(UUID uuid, int karma) {
        this.uuid = uuid;
        this.karma = karma;
    }
    public UUID uuid;
    public int karma;
    public transient boolean success;
}