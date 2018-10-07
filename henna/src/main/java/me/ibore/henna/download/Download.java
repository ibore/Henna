package me.ibore.henna.download;

import java.io.Serializable;


public class Download {

    /**
     * 当前下载的地址
     */
    private String url;
    /**
     * TaskId
     */
    private String taskId;
    /**
     * 文件目录
     */
    private String fileDir;
    /**
     * 下载的文件名
     */
    private String fileName;
    /**
     * 下载的状态
     */
    private int taskStatus;
    /**
     * 当前下载的进度
     */
    private Long currentBytes;
    /**
     * 总进度
     */
    private Long contentLength;
    /**
     * 本次调用距离上一次被调用所间隔的时间(毫秒)
     */
    private long intervalTime;
    /**
     * 本次调用距离上一次被调用的间隔时间内上传或下载的byte长度
     */
    private long eachBytes;
    /**
     * 下载的日期
     */
    private Long date;
    /**
     * 额外的信息
     */
    private Serializable extra1;
    private Serializable extra2;


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getFileDir() {
        return fileDir;
    }

    public void setFileDir(String fileDir) {
        this.fileDir = fileDir;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(int taskStatus) {
        this.taskStatus = taskStatus;
    }

    public Long getCurrentBytes() {
        return currentBytes;
    }

    public void setCurrentBytes(Long currentBytes) {
        this.currentBytes = currentBytes;
    }

    public Long getContentLength() {
        return contentLength;
    }

    public void setContentLength(Long contentLength) {
        this.contentLength = contentLength;
    }

    public long getIntervalTime() {
        return intervalTime;
    }

    public void setIntervalTime(long intervalTime) {
        this.intervalTime = intervalTime;
    }

    public long getEachBytes() {
        return eachBytes;
    }

    public void setEachBytes(long eachBytes) {
        this.eachBytes = eachBytes;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public Serializable getExtra1() {
        return extra1;
    }

    public void setExtra1(Serializable extra1) {
        this.extra1 = extra1;
    }

    public Serializable getExtra2() {
        return extra2;
    }

    public void setExtra2(Serializable extra2) {
        this.extra2 = extra2;
    }

    /**
     * 获取百分比,该计算舍去了小数点,如果你想得到更精确的值,请自行计算
     *
     * @return 获取已下载百分比
     */
    public int getPercent() {
        if (getCurrentBytes() <= 0 || getContentLength() <= 0) return 0;
        return (int) ((100 * getCurrentBytes()) / getContentLength());
    }

    /**
     * 获取上传或下载网络速度,单位为byte/s,如果你想得到更精确的值,请自行计算
     *
     * @return byte/s
     */
    public long getSpeed() {
        if (getEachBytes() <= 0 || getIntervalTime() <= 0) return 0;
        return getEachBytes() * 1000 / getIntervalTime();
    }
}
