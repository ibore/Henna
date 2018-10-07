package me.ibore.henna.download;

import java.io.File;

import me.ibore.henna.Response;
import me.ibore.henna.convert.FileConverter;

public class DownloadTask implements Runnable {

    private Download mDownload;
    private HennaDownload mHennaDownload;
    private DownloadListener mListener;

    public DownloadTask(Download download) {
        this.mDownload = download;
    }

    public void setHennaDownload(HennaDownload hennaDownload) {
        mHennaDownload = hennaDownload;
    }

    @Override
    public void run() {
        try {
            Response<File> file = mHennaDownload.getHenna().<File>get(mDownload.getUrl())
                    .converter(FileConverter.create(mDownload.getFileDir() + mDownload.getFileName()))
                    .execute();
        } catch (Exception e) {

        }
    }

    void pause() {
        mDownload.setTaskStatus(DownloadTaskStatus.TASK_STATUS_PAUSE);
        mHennaDownload.getSQLite().update(mDownload);
    }

    void queue() {
        mDownload.setTaskStatus(DownloadTaskStatus.TASK_STATUS_QUEUE);
    }

    void cancel() {
        mDownload.setTaskStatus(DownloadTaskStatus.TASK_STATUS_CANCEL);
        mHennaDownload.getSQLite().delete(mDownload);
    }

    public Download getDownload() {
        return mDownload;
    }
}
