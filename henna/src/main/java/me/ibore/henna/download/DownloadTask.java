package me.ibore.henna.download;

import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.ConnectException;
import java.net.SocketTimeoutException;

import me.ibore.henna.HennaUtils;
import me.ibore.henna.Response;
import me.ibore.henna.convert.FileConverter;
import okhttp3.Request;
import okhttp3.ResponseBody;

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
        InputStream inputStream = null;
        BufferedInputStream bis = null;
        RandomAccessFile tempFile = null;
        try {
            String fileName = TextUtils.isEmpty(mDownload.getFileName()) ? HennaUtils.getUrlFileName(mDownload.getUrl()) : mDownload.getFileName();
            String fileDir = TextUtils.isEmpty(mDownload.getFileDir()) ? HennaUtils.getDefaultFileDir() : mDownload.getFileDir();
            mDownload.setFileName(fileName);
            mDownload.setFileDir(fileDir);
            tempFile = new RandomAccessFile(new File(fileDir, fileName), "rwd");

            mDownload.setTaskStatus(TaskStatus.TASK_STATUS_CONNECTING);
            //handler.sendEmptyMessage(TaskStatus.TASK_STATUS_CONNECTING);

            if (mHennaDownload.getSQLite().queryById(mDownload.getTaskId()) != null) {
                mHennaDownload.getSQLite().update(mDownload);
            }

            long currentBytes = mDownload.getCurrentBytes();
            Request request;
            try {
                request = new Request.Builder().url(mDownload.getUrl()).header("RANGE", "bytes=" + currentBytes + "-").build();
            } catch (IllegalArgumentException e) {
                mDownload.setTaskStatus(TaskStatus.TASK_STATUS_REQUEST_ERROR);
                //handler.sendEmptyMessage(TaskStatus.TASK_STATUS_REQUEST_ERROR);
                Log.d("DownloadTask", e.getMessage());
                return;
            }

            if (tempFile.length() == 0) {
                currentBytes = 0;
            }
            tempFile.seek(currentBytes);

            Response response = mClient.newCall(request).execute();
            if (response.isSuccessful()) {
                ResponseBody responseBody = response.body();
                if (responseBody != null) {
                    if (DaoManager.instance().queryWidthId(mDownload.getTaskId()) == null) {
                        DaoManager.instance().insertOrReplace(mDownload);
                        mDownload.setTotalSize(responseBody.contentLength());
                    }
                    mDownload.setTaskStatus(TaskStatus.TASK_STATUS_DOWNLOADING);

                    double updateSize = mDownload.getTotalSize() / 100;
                    inputStream = responseBody.byteStream();
                    bis = new BufferedInputStream(inputStream);
                    byte[] buffer = new byte[1024];
                    int length;
                    int buffOffset = 0;
                    while ((length = bis.read(buffer)) > 0 && mDownload.getTaskStatus() != TaskStatus.TASK_STATUS_CANCEL && mDownload.getTaskStatus() != TaskStatus.TASK_STATUS_PAUSE) {
                        tempFile.write(buffer, 0, length);
                        completedSize += length;
                        buffOffset += length;
                        mDownload.setCompletedSize(completedSize);
                        // 避免一直调用数据库
                        if (buffOffset >= updateSize) {
                            buffOffset = 0;
                            DaoManager.instance().update(mDownload);
                            handler.sendEmptyMessage(TaskStatus.TASK_STATUS_DOWNLOADING);
                        }

                        if (completedSize == mDownload.getTotalSize()) {
                            handler.sendEmptyMessage(TaskStatus.TASK_STATUS_DOWNLOADING);
                            mDownload.setTaskStatus(TaskStatus.TASK_STATUS_FINISH);
                            handler.sendEmptyMessage(TaskStatus.TASK_STATUS_FINISH);
                            DaoManager.instance().update(mDownload);
                        }
                    }
                }
            } else {
                mDownload.setTaskStatus(TaskStatus.TASK_STATUS_REQUEST_ERROR);
                handler.sendEmptyMessage(TaskStatus.TASK_STATUS_REQUEST_ERROR);
            }


        } catch (FileNotFoundException e) {
            mDownload.setTaskStatus(TaskStatus.TASK_STATUS_STORAGE_ERROR);
            handler.sendEmptyMessage(TaskStatus.TASK_STATUS_STORAGE_ERROR);
        } catch (SocketTimeoutException | ConnectException e) {
            mDownload.setTaskStatus(TaskStatus.TASK_STATUS_REQUEST_ERROR);
            handler.sendEmptyMessage(TaskStatus.TASK_STATUS_REQUEST_ERROR);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            IOUtils.close(bis, inputStream, tempFile);
        }
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
