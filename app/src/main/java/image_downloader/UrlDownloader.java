package image_downloader;

import android.content.Context;

import java.io.InputStream;

public interface UrlDownloader {
    public static interface UrlDownloaderCallback {
        public void onDownloadComplete(UrlDownloader downloader, InputStream in, String filename);
    }
    
    public void download(Context context, String url, String filename, UrlDownloaderCallback callback, Runnable completion);
    public boolean allowCache();
    public boolean canDownloadUrl(String url);
}