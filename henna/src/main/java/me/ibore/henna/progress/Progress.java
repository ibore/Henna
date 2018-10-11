package me.ibore.henna.progress;

import java.io.Serializable;

public final class Progress implements Serializable {

    /**
     * 当前已上传或下载的总长度
     */
    private long currentBytes;
    /**
     * 数据总长度
     */
    private long contentLength;
    /**
     * 本次调用距离上一次被调用所间隔的时间(毫秒)
     */
    private long intervalTime;
    /**
     * 本次调用距离上一次被调用的间隔时间内上传或下载的byte长度
     */
    private long eachBytes;
    /**
     * 下载所花费的时间(毫秒)
     */
    private long usedTime;
    /**
     * 进度是否完成
     */
    private boolean finish;

    private Object tag;

    public Progress() {
    }

    void setCurrentBytes(long currentBytes) {
        this.currentBytes = currentBytes;
    }

    void setContentLength(long contentLength) {
        this.contentLength = contentLength;
    }

    void setIntervalTime(long intervalTime) {
        this.intervalTime = intervalTime;
    }

    void setEachBytes(long eachBytes) {
        this.eachBytes = eachBytes;
    }

    void setUsedTime(long usedTime) {
        this.usedTime = usedTime;
    }

    void setFinish(boolean finish) {
        this.finish = finish;
    }

    public long getCurrentBytes() {
        return currentBytes;
    }

    public long getContentLength() {
        return contentLength;
    }

    public long getIntervalTime() {
        return intervalTime;
    }

    public long getEachBytes() {
        return eachBytes;
    }

    public long getUsedTime() {
        return usedTime;
    }

    public boolean isFinish() {
        return finish;
    }

    public Object getTag() {
        return tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
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
