package unsw.file;

public class File {
    private String title;
    private String fullContent;
    private int fullSize;

    private String sourceId;
    private String targetId;

    private String downloadedContent = "";
    private int downloadedSize = 0;
    private boolean complete = false;

    public File(String title, String fullContent, String sourceId, String targetId, boolean hasComplete) {
        this.title = title;
        this.fullContent = fullContent;
        this.fullSize = fullContent.length();

        this.sourceId = sourceId;
        this.targetId = targetId;
        this.complete = hasComplete;
        if (this.complete) {
            this.downloadedContent = this.fullContent;
            this.downloadedSize = this.fullSize;
        }
    }

    public void download(int bytes) {
        int downloadedBytes = this.downloadedContent.length();
        downloadedBytes += bytes;
        if (downloadedBytes >= this.fullSize) {
            this.complete = true;
            this.downloadedContent = this.fullContent;
            this.downloadedSize = this.fullSize;
        } else {
            this.downloadedContent = this.fullContent.substring(0, downloadedBytes);
            this.downloadedSize = this.downloadedContent.length();
        }

    }

    public String getSourceId() {
        return this.sourceId;
    }
    public String getTargetId() {
        return this.targetId;
    }

    public String getTitle() {
        return this.title;
    }
    public String getFullContent() {
        return this.fullContent;
    }
    // This is for the teleporting satellite. No other class should be resetting the full content.
    public void setFullContent(String fullContent) {
        this.fullContent = fullContent;
    }
    public int getFullSize() {
        return this.fullSize;
    }
    // This is for the teleporting satellite. No other class should be resetting the size of the full content.
    public void setFullSize(int fullSize) {
        this.fullSize = fullSize;
    }

    public int getDownloadedSize() {
        return this.downloadedSize;
    }

    public String getDownloadedContent() {
        return this.downloadedContent;
    }
    public void setDownloadedContent(String downloadedContent) {
        this.downloadedContent = downloadedContent;
        this.downloadedSize = this.downloadedContent.length();
    }
    
    public boolean isComplete() {
        return this.complete;
    }
    public void setComplete(boolean complete) {
        this.complete = complete;
    }
}
