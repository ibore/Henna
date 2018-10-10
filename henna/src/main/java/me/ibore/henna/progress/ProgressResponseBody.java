package me.ibore.henna.progress;

import android.os.SystemClock;

import java.io.IOException;

import me.ibore.henna.HennaUtils;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

public final class ProgressResponseBody extends ResponseBody {

    private ResponseBody mResponseBody;
    private ProgressListener mListener;
    private boolean isUIThread;
    private int mRefreshTime;
    private Progress mProgress;

    public static ProgressResponseBody create(ResponseBody responseBody, ProgressListener listener, boolean isUIThread) {
        return create(responseBody, listener, isUIThread, 300);
    }

    public static ProgressResponseBody create(ResponseBody responseBody, ProgressListener listener, boolean isUIThread, int refreshTime) {
        return new ProgressResponseBody(responseBody, listener, isUIThread, refreshTime);
    }

    private ProgressResponseBody(ResponseBody responseBody, ProgressListener listener, boolean isUIThread, int refreshTime) {
        this.mResponseBody = HennaUtils.checkNotNull(responseBody, "responseBody can not null");
        this.mListener = HennaUtils.checkNotNull(listener, "ProgressListener can not null");
        this.isUIThread = isUIThread;
        this.mRefreshTime = refreshTime <= 0 ? 300 : refreshTime;
        this.mProgress = new Progress();
    }

    @Override
    public MediaType contentType() {
        return mResponseBody.contentType();
    }

    @Override
    public long contentLength() {
        return mResponseBody.contentLength();
    }

    @Override
    public BufferedSource source() {
        return Okio.buffer(new CountingSource(mResponseBody.source()));
    }


    protected final class CountingSource extends ForwardingSource {

        private long totalBytesRead = 0L;
        private long lastRefreshTime = 0L;  //最后一次刷新的时间
        private long tempSize = 0L;

        public CountingSource(Source delegate) {
            super(delegate);
        }

        @Override
        public long read(Buffer sink, long byteCount) throws IOException {
            long bytesRead = super.read(sink, byteCount);
            if (mProgress.getContentLength() == 0) { //避免重复调用 contentLength()
                mProgress.setContentLength(contentLength());
            }
            // read() returns the number of bytes read, or -1 if this source is exhausted.
            totalBytesRead += bytesRead != -1 ? bytesRead : 0;
            tempSize += bytesRead != -1 ? bytesRead : 0;
            long curTime = SystemClock.elapsedRealtime();
            if (curTime - lastRefreshTime >= mRefreshTime || bytesRead == -1 || totalBytesRead == mProgress.getContentLength()) {
                mProgress.setEachBytes(bytesRead != -1 ? tempSize : -1);
                mProgress.setCurrentBytes(totalBytesRead);
                mProgress.setIntervalTime(curTime - lastRefreshTime);
                mProgress.setUsedTime(mProgress.getUsedTime() + mRefreshTime);
                mProgress.setFinish(bytesRead == -1 && totalBytesRead == mProgress.getContentLength());
                if (isUIThread) {
                    HennaUtils.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mListener.onProgress(mProgress);
                        }
                    });
                } else {
                    mListener.onProgress(mProgress);
                }
                lastRefreshTime = curTime;
                tempSize = 0;
            }
            return bytesRead;
        }
    }
 }
