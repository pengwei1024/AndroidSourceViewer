package com.apkfuns.androidsourceviewer.entity;

public class SearchEntity {
    private String showName;
    private String downloadUrl;

    public String getShowName() {
        return showName;
    }

    public void setShowName(String showName) {
        this.showName = showName;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SearchEntity that = (SearchEntity) o;

        if (showName != null ? !showName.equals(that.showName) : that.showName != null) return false;
        return downloadUrl != null ? downloadUrl.equals(that.downloadUrl) : that.downloadUrl == null;
    }

    @Override
    public int hashCode() {
        int result = showName != null ? showName.hashCode() : 0;
        result = 31 * result + (downloadUrl != null ? downloadUrl.hashCode() : 0);
        return result;
    }
}
