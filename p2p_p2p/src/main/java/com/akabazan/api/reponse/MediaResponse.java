package com.akabazan.api.reponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaResponse implements Serializable {

    private MediaData data;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MediaData implements Serializable {
        private String id;
        private String filename;
        private String originalName;
        private String url;
        private String mimeType;
        private long size;
    }
}
