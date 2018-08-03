package ibore.android.henna;

import android.os.SystemClock;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

public final class ProgressRequestBody extends RequestBody {

    private boolean isUIThread;
    private int mRefreshTime;
    private RequestBody mRequestBody;
    private ProgressListener mListener;
    private Progress mProgress;
    private Sink mSink;

    public static ProgressRequestBody create(RequestBody requestBody, ProgressListener listener, boolean isUIThread) {
        return create(requestBody, listener, isUIThread, 300);
    }

    public static ProgressRequestBody create(RequestBody requestBody, ProgressListener listener, boolean isUIThread, int refreshTime) {
        return new ProgressRequestBody(requestBody, listener, isUIThread, refreshTime);
    }

    public ProgressRequestBody(RequestBody requestBody, ProgressListener listener, boolean isUIThread, int refreshTime) {
        this.mRequestBody = HttpUtils.checkNotNull(requestBody, "requestBody can not null");
        this.mListener = HttpUtils.checkNotNull(listener, "ProgressListener can not null");
        this.mRefreshTime = refreshTime <= 0 ? 300 : refreshTime;
        this.isUIThread = isUIThread;
        this.mProgress = new Progress();
    }

    @Override
    public MediaType contentType() {
        return mRequestBody.contentType();
    }

    @Override
    public long contentLength() throws IOException {
        return mRequestBody.contentLength();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        mSink = new CountingSink(sink);
        BufferedSink bufferedSink = Okio.buffer(mSink);
        mRequestBody.writeTo(bufferedSink);
        bufferedSink.flush();
    }

    protected final class CountingSink extends ForwardingSink {

        private long totalBytesRead = 0L;
        private long lastRefreshTime = 0L;  //最后一次刷新的时间
        private long tempSize = 0L;

        public CountingSink(Sink delegate) {
            super(delegate);
        }

        @Override
        public void write(Buffer source, long byteCount) throws IOException {
            super.write(source, byteCount);
            if (mProgress.getContentLength() == 0) { //避免重复调用 contentLength()
                mProgress.setContentLength(contentLength());
            }
            totalBytesRead += byteCount;
            tempSize += byteCount;
            long curTime = SystemClock.elapsedRealtime();
            if (curTime - lastRefreshTime >= mRefreshTime || totalBytesRead == mProgress.getContentLength()) {
                final long finalTempSize = tempSize;
                final long finalTotalBytesRead = totalBytesRead;
                final long finalIntervalTime = curTime - lastRefreshTime;
                // Runnable 里的代码是通过 Handler 执行在主线程的,外面代码可能执行在其他线程
                // 所以我必须使用 final ,保证在 Runnable 执行前使用到的变量,在执行时不会被修改
                mProgress.setEachBytes(finalTempSize);
                mProgress.setCurrentBytes(finalTotalBytesRead);
                mProgress.setIntervalTime(finalIntervalTime);
                mProgress.setFinish(finalTotalBytesRead == mProgress.getContentLength());
                if (isUIThread) {
                    HttpUtils.runOnUiThread(new Runnable() {
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
        }
    }
}
