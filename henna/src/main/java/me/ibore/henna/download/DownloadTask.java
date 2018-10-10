package me.ibore.henna.download;

import android.text.TextUtils;

import java.io.File;

import me.ibore.henna.HennaUtils;
import me.ibore.henna.convert.FileConverter;
import me.ibore.henna.progress.Progress;
import me.ibore.henna.progress.ProgressListener;

public class DownloadTask implements Runnable {

    private Download mDownload;
    private HennaDownload mHennaDownload;
    private DownloadListener mListener;
    Progress progress = new Progress();
    public DownloadTask(Download download) {
        this.mDownload = download;
    }

    public void setHennaDownload(HennaDownload hennaDownload) {
        mHennaDownload = hennaDownload;
    }

    @Override
    public void run() {
        try {
            String fileName = TextUtils.isEmpty(mDownload.getFileName()) ? HennaUtils.getFileNameFromUrl(mDownload.getUrl()) : mDownload.getFileName();
            String fileDir = TextUtils.isEmpty(mDownload.getFileDir()) ? HennaUtils.getDefaultFileDir() : mDownload.getFileDir();
            mDownload.setFileName(fileName);
            mDownload.setFileDir(fileDir);
            if (mDownload.getProgress() == null) mDownload.setProgress(new Progress());
            File tempFile = new File(fileDir, fileName);
            if (tempFile.exists()) {
                mDownload.getProgress().setCurrentBytes(tempFile.length());
            } else {
                mDownload.getProgress().setCurrentBytes(0L);
            }
            mDownload.setTaskStatus(Download.TASK_STATUS_START);
            HennaUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mListener.onStart();
                    mListener.onProgress(mDownload.getProgress());
                }
            });
            mHennaDownload.getHenna().<File>get(mDownload.getUrl())
                    .header("RANGE", "bytes=" + progress.getCurrentBytes() + "-")
                    .uiThread(true)
                    .download(new ProgressListener() {
                        @Override
                        public void onProgress(Progress progress) {
                            mDownload.setProgress(progress);
                            mListener.onProgress(mDownload.getProgress());
                        }
                    })
                    .converter(FileConverter.create(tempFile, false))
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
