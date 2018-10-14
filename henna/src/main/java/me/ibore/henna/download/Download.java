package me.ibore.henna.download;

import java.io.Serializable;

import me.ibore.henna.db.Column;


public class Download {

    /**
     * queue download
     */
    public static final int QUEUE = 0;

    /**
     * start download
     */
    public static final int START = QUEUE + 1;

    /**
     * downloading download
     */
    public static final int DOWNLOADING = START + 1;

    /**
     * pause download
     */
    public static final int PAUSE = DOWNLOADING + 1;

    /**
     * cancel download
     */
    public static final int CANCEL = PAUSE + 1;

    /**
     * error download
     */
    public static final int ERROR = CANCEL + 1;

    /**
     * finish download
     */
    public static final int FINISH = ERROR + 1;

    /**
     * 当前下载的地址
     */
    @Column(name = "url", notNull = true)
    private String url;
    /**
     * TaskId
     */
    @Column(name = "taskId", primaryKey = true)
    private Long taskId;
    /**
     * 文件目录
     */
    @Column(name = "fileDir", notNull = true)
    private String fileDir;
    /**
     * 下载的文件名
     */
    @Column(name = "fileDir", notNull = true)
    private String fileName;
    /**
     * 下载的状态
     */
    @Column(name = "taskStatus", notNull = true)
    private int taskStatus;
    /**
     * 当前已上传或下载的总长度
     */
    @Column(name = "currentBytes", notNull = true)
    private long currentBytes;
    /**
     * 数据总长度
     */
    @Column(name = "contentLength", notNull = true)
    private long contentLength;
    /**
     * 本次调用距离上一次被调用所间隔的时间(毫秒)
     */
    @Column(name = "intervalTime")
    private long intervalTime;
    /**
     * 本次调用距离上一次被调用的间隔时间内上传或下载的byte长度
     */
    @Column(name = "eachBytes")
    private long eachBytes;
    /**
     * 下载所花费的时间(毫秒)
     */
    @Column(name = "usedTime", notNull = true)
    private long usedTime;
    /**
     * 下载的日期
     */
    @Column(name = "date", notNull = true)
    private Long date;
    /**
     * 额外的信息
     */
    @Column(name = "extra1")
    private Serializable extra1;
    @Column(name = "extra2")
    private Serializable extra2;

    private Download() {}

    /*public Download(String url, ) {

    }*/

    public String getUrl() {
        return url;
    }

    void setUrl(String url) {
        this.url = url;
    }

    public Long getTaskId() {
        return taskId;
    }

    void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getFileDir() {
        return fileDir;
    }

    void setFileDir(String fileDir) {
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

    void setTaskStatus(int taskStatus) {
        this.taskStatus = taskStatus;
    }

    public long getCurrentBytes() {
        return currentBytes;
    }

    void setCurrentBytes(long currentBytes) {
        this.currentBytes = currentBytes;
    }

    public long getContentLength() {
        return contentLength;
    }

    void setContentLength(long contentLength) {
        this.contentLength = contentLength;
    }

    public long getIntervalTime() {
        return intervalTime;
    }

    void setIntervalTime(long intervalTime) {
        this.intervalTime = intervalTime;
    }

    public long getEachBytes() {
        return eachBytes;
    }

    void setEachBytes(long eachBytes) {
        this.eachBytes = eachBytes;
    }

    public long getUsedTime() {
        return usedTime;
    }

    void setUsedTime(long usedTime) {
        this.usedTime = usedTime;
    }

    public Long getDate() {
        return date;
    }

    void setDate(Long date) {
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
}
