package com.akabazan.api.reponse;

import java.io.Serializable;

public class MediaUploadResponse implements Serializable {

    private boolean success;
    private MediaData data;
    private String message;

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

    public MediaUploadResponse() {
    }

    public MediaUploadResponse(boolean success, MediaData data, String message) {
        this.success = success;
        this.data = data;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public MediaData getData() {
        return data;
    }

    public void setData(MediaData data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
