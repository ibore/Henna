package me.ibore.http.progress;

import java.io.IOException;

import me.ibore.http.XHttp;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

/**
 * Created by Administrator on 2018/2/7.
 */

public final class ProgressRequestBody extends RequestBody {

    private RequestBody mDelegate;
    private ProgressListener mListener;
    private Progress mProgress;
    private BufferedSink mBufferedSink;

    public ProgressRequestBody(String url, RequestBody delegate, ProgressListener listener) {
        this.mDelegate = delegate;
        this.mListener = listener;
        this.mProgress = new Progress();
        this.mProgress.setMode(Progress.UPLOAD);
        this.mProgress.setUrl(url);
    }

    @Override
    public MediaType contentType() {
        return mDelegate.contentType();
    }

    @Override
    public long contentLength() {
        try {
            return mDelegate.contentLength();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        if (mBufferedSink == null) {
            mBufferedSink = Okio.buffer(new CountingSink(sink));
        }
        mDelegate.writeTo(mBufferedSink);
        mBufferedSink.flush();
    }

    protected final class CountingSink extends ForwardingSink {
        private long bytesWritten = 0L;
        private long lastRefreshUiTime = 0L;
        private long lastWriteBytes = 0L;

        public CountingSink(Sink delegate) {
            super(delegate);
        }

        @Override
        public void write(Buffer source, long byteCount) throws IOException {
            super.write(source, byteCount);
            if (mProgress.getTotal() == 0) {
                mProgress.setTotal(contentLength());
            }
            bytesWritten += byteCount;
            if (mListener != null) {
                long curTime = System.currentTimeMillis();
                lastWriteBytes = 0L;
                if (curTime - lastRefreshUiTime >= XHttp.REFRESH_TIME || bytesWritten == mProgress.getTotal()) {
                    long diffTime = (curTime - lastRefreshUiTime) / 1000;
                    if (diffTime == 0) diffTime += 1;
                    long diffBytes = bytesWritten - lastWriteBytes;
                    final long networkSpeed = diffBytes / diffTime;
                    lastRefreshUiTime = curTime;
                    lastWriteBytes = bytesWritten;
                    mProgress.setSpeed(networkSpeed);
                    mProgress.setCurrent(bytesWritten);
                    mProgress.setProgress((int) (bytesWritten * 10000 / mProgress.getTotal()));
                    XHttp.Handler.post(() -> mListener.onProgress(mProgress));
                }
            }
        }
    }
}
