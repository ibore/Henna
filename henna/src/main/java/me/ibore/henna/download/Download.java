package me.ibore.henna.download;

import java.io.Serializable;

import me.ibore.henna.progress.Progress;


public class Download {

    /**
     * queue download
     */
    public static final int TASK_STATUS_QUEUE = 0;

    /**
     * start download
     */
    public static final int TASK_STATUS_START = TASK_STATUS_QUEUE + 1;

    /**
     * downloading download
     */
    public static final int TASK_STATUS_PROGRESS = TASK_STATUS_START + 1;

    /**
     * pause download
     */
    public static final int TASK_STATUS_PAUSE = TASK_STATUS_PROGRESS + 1;

    /**
     * cancel download
     */
    public static final int TASK_STATUS_CANCEL = TASK_STATUS_PAUSE + 1;

    /**
     * error download
     */
    public static final int TASK_STATUS_ERROR = TASK_STATUS_CANCEL + 1;

    /**
     * finish download
     */
    public static final int TASK_STATUS_FINISH = TASK_STATUS_ERROR + 1;

    /**
     * 当前下载的地址
     */
    private String url;
    /**
     * TaskId
     */
    private Long taskId;
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
     * 进度信息
     */
    private Progress progress;
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

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
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

    public Progress getProgress() {
        return progress;
    }

    public void setProgress(Progress progress) {
        this.progress = progress;
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

}
