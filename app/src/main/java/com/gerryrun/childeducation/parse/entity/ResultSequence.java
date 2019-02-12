package com.gerryrun.childeducation.parse.entity;

public class ResultSequence {

    private double currentTime;
    private boolean isOpen;
    private String pitch;
    private String message;

    public ResultSequence() {
    }

    public ResultSequence(double currentTime, String message) {
        this.currentTime = currentTime;
        this.message = message;
    }

    public double getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(double currentTime) {
        this.currentTime = currentTime;
    }

    public String getMessage() {
        return message;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public String getPitch() {
        return pitch;
    }

    public void setMessage(String message) {
        this.message = message;
        if (message.startsWith("音符关闭")) {
            isOpen = false;
        } else if (message.startsWith("音符打开")) {
            isOpen = true;
        }
        pitch = message.substring(6, 8);
    }
}
