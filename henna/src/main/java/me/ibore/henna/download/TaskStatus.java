package me.ibore.henna.download;

public class TaskStatus {
    /**
     * init download
     */
    public static final int TASK_STATUS_INIT = 0;

    /**
     * queue download
     */
    public static final int TASK_STATUS_QUEUE = TASK_STATUS_INIT + 1;

    /**
     * resume download
     */
    public static final int TASK_STATUS_CONNECTING = TASK_STATUS_QUEUE + 1;

    /**
     * start download
     */
    public static final int TASK_STATUS_DOWNLOADING = TASK_STATUS_CONNECTING + 1;

    /**
     * cancel download
     */
    public static final int TASK_STATUS_CANCEL = TASK_STATUS_DOWNLOADING + 1;

    /**
     * pause download
     */
    public static final int TASK_STATUS_PAUSE = TASK_STATUS_CANCEL + 1;

    /**
     * request error
     */
    public static final int TASK_STATUS_REQUEST_ERROR = TASK_STATUS_PAUSE + 1;

    /**
     * storage error
     */
    public static final int TASK_STATUS_STORAGE_ERROR = TASK_STATUS_REQUEST_ERROR + 1;

    /**
     * finish download
     */
    public static final int TASK_STATUS_FINISH = TASK_STATUS_STORAGE_ERROR + 1;
}
