package ibore.android.henna.download;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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

import ibore.android.henna.FileConverter;
import ibore.android.henna.Henna;
import ibore.android.henna.HttpUtils;
import ibore.android.henna.Progress;
import ibore.android.henna.ProgressListener;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class DownloadTask implements Runnable {

    private OkHttpClient mClient;

    private Henna henna;

    private Download mDownload;

    private DownloadTaskListener mListener;

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            int code = msg.what;
            switch (code) {
                case DownloadTaskStatus.TASK_STATUS_QUEUE:
                    mListener.onQueue(DownloadTask.this);
                    break;
                case DownloadTaskStatus.TASK_STATUS_CONNECTING:
                    mListener.onConnecting(DownloadTask.this);
                    break;
                case DownloadTaskStatus.TASK_STATUS_DOWNLOADING:
                    mListener.onStart(DownloadTask.this);
                    break;
                case DownloadTaskStatus.TASK_STATUS_PAUSE:
                    mListener.onPause(DownloadTask.this);
                    break;
                case DownloadTaskStatus.TASK_STATUS_CANCEL:
                    mListener.onCancel(DownloadTask.this);
                    break;
                case DownloadTaskStatus.TASK_STATUS_REQUEST_ERROR:
                    mListener.onError(DownloadTask.this, DownloadTaskStatus.TASK_STATUS_REQUEST_ERROR);
                    break;
                case DownloadTaskStatus.TASK_STATUS_STORAGE_ERROR:
                    mListener.onError(DownloadTask.this, DownloadTaskStatus.TASK_STATUS_STORAGE_ERROR);
                    break;
                case DownloadTaskStatus.TASK_STATUS_FINISH:
                    mListener.onFinish(DownloadTask.this);
                    break;

            }
        }
    };


    public DownloadTask(Download download) {
        mDownload = download;
    }

    @Override
    public void run() {
        InputStream inputStream = null;
        BufferedInputStream bis = null;
        RandomAccessFile tempFile = null;

        try {
            String fileName = TextUtils.isEmpty(mDownload.getFileName()) ? HttpUtils.getFileNameFromUrl(mDownload.getUrl()) : mDownload.getFileName();
            String filePath = TextUtils.isEmpty(mDownload.getFilePath()) ? HttpUtils.getDefaultFilePath() : mDownload.getFilePath();
            mDownload.setFileName(fileName);
            mDownload.setFilePath(filePath);
            tempFile = new RandomAccessFile(new File(filePath, fileName), "rwd");

            mDownload.setTaskStatus(DownloadTaskStatus.TASK_STATUS_CONNECTING);
            handler.sendEmptyMessage(DownloadTaskStatus.TASK_STATUS_CONNECTING);

            if (DownloadDb.queryWidthId(mDownload.getTaskId()) != null) {
                DownloadDb.update(mDownload);
            }

            long completedSize = mDownload.getCompletedSize();
            try {
                ibore.android.henna.Response<File> response = henna.<File>get(mDownload.getUrl())
                        .headers("RANGE", "bytes=" + completedSize + "-")
                        .uiThread(true)
                        .download(new ProgressListener() {
                            @Override
                            public void onProgress(Progress progress) {

                            }
                        })
                        .converter(FileConverter.create())
                        .execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Request request;
            try {
                request = new Request.Builder().url(mDownload.getUrl()).header("RANGE", "bytes=" + completedSize + "-").build();
            } catch (IllegalArgumentException e) {
                mDownload.setTaskStatus(DownloadTaskStatus.TASK_STATUS_REQUEST_ERROR);
                handler.sendEmptyMessage(DownloadTaskStatus.TASK_STATUS_REQUEST_ERROR);
                Log.d("DownloadTask", e.getMessage());
                return;
            }

            if (tempFile.length() == 0) {
                completedSize = 0;
            }
            tempFile.seek(completedSize);

            Response response = mClient.newCall(request).execute();
            if (response.isSuccessful()) {
                ResponseBody responseBody = response.body();
                if (responseBody != null) {
                    if (DownloadDb.queryWidthId(mDownload.getTaskId()) == null) {
                        DownloadDb.insertOrReplace(mDownload);
                        mDownload.setTotalSize(responseBody.contentLength());
                    }
                    mDownload.setTaskStatus(DownloadTaskStatus.TASK_STATUS_DOWNLOADING);

                    double updateSize = mDownload.getTotalSize() / 100;
                    inputStream = responseBody.byteStream();
                    bis = new BufferedInputStream(inputStream);
                    byte[] buffer = new byte[1024];
                    int length;
                    int buffOffset = 0;
                    while ((length = bis.read(buffer)) > 0 && mDownload.getTaskStatus() != DownloadTaskStatus.TASK_STATUS_CANCEL && mDownload.getTaskStatus() != DownloadTaskStatus.TASK_STATUS_PAUSE) {
                        tempFile.write(buffer, 0, length);
                        completedSize += length;
                        buffOffset += length;
                        mDownload.setCompletedSize(completedSize);
                        // 避免一直调用数据库
                        if (buffOffset >= updateSize) {
                            buffOffset = 0;
                            DownloadDb.update(mDownload);
                            handler.sendEmptyMessage(DownloadTaskStatus.TASK_STATUS_DOWNLOADING);
                        }

                        if (completedSize == mDownload.getTotalSize()) {
                            handler.sendEmptyMessage(DownloadTaskStatus.TASK_STATUS_DOWNLOADING);
                            mDownload.setTaskStatus(DownloadTaskStatus.TASK_STATUS_FINISH);
                            handler.sendEmptyMessage(DownloadTaskStatus.TASK_STATUS_FINISH);
                            DownloadDb.update(mDownload);
                        }
                    }
                }
            } else {
                mDownload.setTaskStatus(DownloadTaskStatus.TASK_STATUS_REQUEST_ERROR);
                handler.sendEmptyMessage(DownloadTaskStatus.TASK_STATUS_REQUEST_ERROR);
            }


        } catch (FileNotFoundException e) {
            mDownload.setTaskStatus(DownloadTaskStatus.TASK_STATUS_STORAGE_ERROR);
            handler.sendEmptyMessage(DownloadTaskStatus.TASK_STATUS_STORAGE_ERROR);
        } catch (SocketTimeoutException | ConnectException e) {
            mDownload.setTaskStatus(DownloadTaskStatus.TASK_STATUS_REQUEST_ERROR);
            handler.sendEmptyMessage(DownloadTaskStatus.TASK_STATUS_REQUEST_ERROR);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            HttpUtils.close(bis, inputStream, tempFile);
        }
    }

    public Download getDownload() {
        return mDownload;
    }

    void pause() {
        mDownload.setTaskStatus(DownloadTaskStatus.TASK_STATUS_PAUSE);
        DownloadDb.update(mDownload);
        handler.sendEmptyMessage(DownloadTaskStatus.TASK_STATUS_PAUSE);
    }

    void queue() {
        mDownload.setTaskStatus(DownloadTaskStatus.TASK_STATUS_QUEUE);
        handler.sendEmptyMessage(DownloadTaskStatus.TASK_STATUS_QUEUE);
    }

    void cancel() {
        mDownload.setTaskStatus(DownloadTaskStatus.TASK_STATUS_CANCEL);
        DownloadDb.delete(mDownload);
        handler.sendEmptyMessage(DownloadTaskStatus.TASK_STATUS_CANCEL);
    }

    void setClient(OkHttpClient mClient) {
        this.mClient = mClient;
    }

    public void setListener(DownloadTaskListener listener) {
        mListener = listener;
    }


}
