package com.akabazan.api.reponse;

import java.io.Serializable;

public class MediaResponse implements Serializable {

    private MediaData data;

    public static class MediaData implements Serializable {
        private String id;
        private String filename;
        private String originalName;
        private String url;
        private String mimeType;
        private long size;

        public MediaData() {
        }

        public MediaData(String id, String filename, String originalName, String url, String mimeType, long size) {
            this.id = id;
            this.filename = filename;
            this.originalName = originalName;
            this.url = url;
            this.mimeType = mimeType;
            this.size = size;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getFilename() {
            return filename;
        }

        public void setFilename(String filename) {
            this.filename = filename;
        }

        public String getOriginalName() {
            return originalName;
        }

        public void setOriginalName(String originalName) {
            this.originalName = originalName;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getMimeType() {
            return mimeType;
        }

        public void setMimeType(String mimeType) {
            this.mimeType = mimeType;
        }

        public long getSize() {
            return size;
        }

        public void setSize(long size) {
            this.size = size;
        }
    }

    public MediaResponse() {
    }

    public MediaResponse(MediaData data) {
        this.data = data;
    }

    public MediaData getData() {
        return data;
    }

    public void setData(MediaData data) {
        this.data = data;
    }
}
