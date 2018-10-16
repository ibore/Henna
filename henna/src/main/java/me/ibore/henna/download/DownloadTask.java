package me.ibore.henna.download;

import android.os.SystemClock;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;

import me.ibore.henna.Converter;
import me.ibore.henna.HennaUtils;
import me.ibore.henna.exception.ConvertException;
import me.ibore.henna.progress.Progress;
import okhttp3.Response;
import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

public class DownloadTask implements Runnable {

    private Download mDownload;
    private HennaDownload mHennaDownload;
    private DownloadListener mListener;
    Progress progress = new Progress();

    public DownloadTask(Download download) {
        this.mDownload = download;
    }

    public Download getDownload() {
        return mDownload;
    }

    public void setListener(DownloadListener mListener) {
        this.mListener = mListener;
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

            final File tempFile = new File(fileDir, fileName);
            if (tempFile.exists()) {
                mDownload.setCurrentBytes(tempFile.length());
            } else {
                mDownload.setCurrentBytes(0L);
            }
            mDownload.setTaskStatus(Download.START);
            HennaUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mListener.onStart();
                    mDownload.setTaskStatus(Download.DOWNLOADING);
                    mListener.onDownloading(mDownload);
                }
            });
            mHennaDownload.getHenna().<File>get(mDownload.getUrl())
                    .headers("RANGE", "bytes=" + progress.getCurrentBytes() + "-")
                    .uiThread(false)
                    .converter(new Converter<File>() {

                        private long lastRefreshTime = 0L;
                        private long tempSize = 0L;

                        @Override
                        public File convert(Response value) throws IOException, ConvertException {
                            BufferedSink sink = null;
                            Buffer buffer = null;
                            BufferedSource source = null;
                            try {
                                if (mDownload.getContentLength() == 0) { //避免重复调用 contentLength()
                                    mDownload.setContentLength(value.body().contentLength());
                                }
                                sink = Okio.buffer(Okio.sink(tempFile));
                                buffer = sink.buffer();
                                source = value.body().source();
                                long bytesRead = 0;
                                while ((bytesRead = source.read(buffer, 200 * 1024)) != -1) {
                                    tempSize += bytesRead;
                                    long curTime = SystemClock.elapsedRealtime();
                                    if (curTime - lastRefreshTime >= mHennaDownload.getHenna().refreshTime() || mDownload.getCurrentBytes() == mDownload.getContentLength()) {
                                        mDownload.setEachBytes(tempSize);
                                        mDownload.setCurrentBytes(mDownload.getCurrentBytes() + bytesRead);
                                        mDownload.setIntervalTime(curTime - lastRefreshTime);
                                        mDownload.setUsedTime(mDownload.getUsedTime() + mDownload.getIntervalTime());
                                        HennaUtils.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                mDownload.setTaskStatus(Download.DOWNLOADING);
                                                mListener.onDownloading(mDownload);
                                            }
                                        });
                                        lastRefreshTime = curTime;
                                        tempSize = 0;
                                    }
                                    sink.emit();
                                }
                                buffer.close();
                                return tempFile;
                            } catch (IOException e) {
                                throw e;
                            } catch (Exception e) {
                                throw new ConvertException("Convert Error", e);
                            } finally {
                                HennaUtils.close(sink, buffer, source);
                            }
                        }
                    })
                    .execute();
        } catch (final Exception e) {
            String message;
            if (e instanceof IOException) {
                message = "";
            }
            mDownload.setTaskStatus(Download.ERROR);
            HennaUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mListener.onError(e);
                }
            });
        }

    }

    void pause() {
        mDownload.setTaskStatus(Download.PAUSE);
        //DownloadTable.getInstance().update(mDownload);
        HennaUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mListener.onPause();
            }
        });
    }

    void queue() {
        mDownload.setTaskStatus(Download.QUEUE);
        HennaUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mListener.onQueue();
            }
        });
    }

    void cancel() {
        mDownload.setTaskStatus(Download.CANCEL);
        //DownloadTable.getInstance().update(mDownload);
        HennaUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mListener.onCancel();
            }
        });
    }
}
