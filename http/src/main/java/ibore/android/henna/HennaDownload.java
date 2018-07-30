package ibore.android.henna;

import java.util.concurrent.ConcurrentHashMap;

public class HennaDownload {

    private String folder;                                      //下载的默认文件夹
    private DownloadThreadPool threadPool;                      //下载的线程池
    private ConcurrentHashMap<String, DownloadTask> taskMap;    //所有任务


}
