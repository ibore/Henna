package me.ibore.http.progress;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/2/6.
 */

public class Progress implements Serializable {

    /**
     * 模式——下载
     */
    public final static int DOWNLOAD = 1;
    /**
     * 模式——上传
     */
    public final static int UPLOAD = 2;
    /**
     * 模式
     */
    private int mode = DOWNLOAD;
    /**
     * 请求网址
     */
    private String url;
    /**
     * 当前长度
     */
    private long current;
    /**
     * 总长度
     */
    private long total;
    /**
     * 当前进度（以10000为单位）
     */
    private double progress;
    /**
     * 网速bytes/m
     */
    private long speed;

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getCurrent() {
        return current;
    }

    public void setCurrent(long current) {
        this.current = current;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }

    public long getSpeed() {
        return speed;
    }

    public void setSpeed(long speed) {
        this.speed = speed;
    }

    @Override
    public String toString() {
        return "ProgressInfo{" +
                "mode=" + (mode == 1 ? "DOWNLOAD" : "UPLOAD") +
                ", url='" + url + '\'' +
                ", current=" + current +
                ", total=" + total +
                ", progress=" + progress +
                ", speed=" + speed +
                '}';
    }
}
