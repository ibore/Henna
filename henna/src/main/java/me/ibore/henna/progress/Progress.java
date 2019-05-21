package me.ibore.henna.progress;

import android.os.Parcel;
import android.os.Parcelable;

public final class Progress implements Parcelable {

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

    private Parcelable tag;

    public Progress() {
    }


    protected Progress(Parcel in) {
        currentBytes = in.readLong();
        contentLength = in.readLong();
        intervalTime = in.readLong();
        eachBytes = in.readLong();
        usedTime = in.readLong();
        finish = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(currentBytes);
        dest.writeLong(contentLength);
        dest.writeLong(intervalTime);
        dest.writeLong(eachBytes);
        dest.writeLong(usedTime);
        dest.writeByte((byte) (finish ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Progress> CREATOR = new Creator<Progress>() {
        @Override
        public Progress createFromParcel(Parcel in) {
            return new Progress(in);
        }

        @Override
        public Progress[] newArray(int size) {
            return new Progress[size];
        }
    };

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

    public Parcelable getTag() {
        return tag;
    }

    public void setTag(Parcelable tag) {
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
