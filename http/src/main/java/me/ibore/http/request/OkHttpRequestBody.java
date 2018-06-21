package me.ibore.http.request;

import java.io.IOException;

import me.ibore.http.listener.AbsHttpListener;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

/**
 * Created by Administrator on 2017/6/12.
 */

public final class OkHttpRequestBody extends RequestBody {

    public static final MediaType MEDIA_TYPE_PLAIN = MediaType.parse("text/plain;charset=utf-8");
    public static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json;charset=utf-8");
    public static final MediaType MEDIA_TYPE_STREAM = MediaType.parse("application/octet-stream");

    private RequestBody requestBody;
    private static AbsHttpListener listener;
    private CountingSink countingSink;

    private OkHttpRequestBody(RequestBody requestBody, AbsHttpListener listener) {
        this.requestBody = requestBody;
        this.listener = listener;
    }

    @Override
    public MediaType contentType() {
        return requestBody.contentType();
    }

    @Override
    public long contentLength() throws IOException {
        return super.contentLength();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        countingSink = new CountingSink(sink);
        BufferedSink bufferedSink = Okio.buffer(countingSink);
        requestBody.writeTo(bufferedSink);
        bufferedSink.flush();
    }

    public static RequestBody create(RequestBody requestBody, AbsHttpListener listener) {
        return new OkHttpRequestBody(requestBody, listener);
    }

    public static class CountingSink extends ForwardingSink {

        private long bytesWritten = 0;   //当前写入字节数
        private long contentLength = 0;  //总字节长度，避免多次调用contentLength()方法
        private long lastRefreshUiTime;  //最后一次刷新的时间
        private long lastWriteBytes;     //最后一次写入字节数据

        public CountingSink(Sink delegate) {
            super(delegate);
        }

        @Override
        public void write(Buffer source, long byteCount) throws IOException {
            super.write(source, byteCount);
            if (contentLength <= 0) contentLength = byteCount;
            bytesWritten += byteCount;
            long curTime = System.currentTimeMillis();

            if (curTime - lastRefreshUiTime >= 300 || bytesWritten == contentLength) {
                //计算下载速度
                long diffTime = (curTime - lastRefreshUiTime) / 1000;
                if (diffTime == 0) diffTime += 1;
                long diffBytes = bytesWritten - lastWriteBytes;
                long networkSpeed = diffBytes / diffTime;
                if (listener != null) listener.onProgress(null);
                lastRefreshUiTime = System.currentTimeMillis();
                lastWriteBytes = bytesWritten;
            }
        }
    }
}
