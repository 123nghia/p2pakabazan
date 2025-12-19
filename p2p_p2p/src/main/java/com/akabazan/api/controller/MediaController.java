package com.akabazan.api.controller;

import com.akabazan.api.reponse.MediaUploadResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("/v1/media")
@Tag(name = "Media Management", description = "APIs for uploading and managing media files (images, documents, etc.)")
public class MediaController extends BaseController {

        private static final String UPLOAD_DIR = "/app/uploads"; // Absolute path in container

        @PostMapping(value = "/upload/public-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        @Operation(summary = "Upload a public image (No authentication required)", description = "Upload an image file to the server. The image will be stored and accessible via a public URL. "
                        +
                        "Supports common image formats like JPG, PNG, GIF, etc. This endpoint is publicly accessible.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Image uploaded successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MediaUploadResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid file or file is empty"),
                        @ApiResponse(responseCode = "500", description = "Failed to store file on server")
        })
        public ResponseEntity<MediaUploadResponse> uploadImage(
                        @Parameter(description = "Image file to upload", required = true, content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)) @RequestParam("file") MultipartFile file,

                @Parameter(description = "Folder type for organizing uploads (e.g., 'avatar', 'trade', 'chat', 'default')", example = "default") @RequestHeader(value = "folder-type", defaultValue = "default") String folderType) {
                if (file.isEmpty()) {
                        return ResponseEntity.badRequest()
                                        .body(new MediaUploadResponse(false, null, "File must not be empty"));
                }

                try {
                        // Determine sub-folder
                        String type = "image"; // fixed for this endpoint
                        String pathStr = UPLOAD_DIR + File.separator + type + File.separator + folderType;
                        Path uploadPath = Paths.get(pathStr);

                        if (!Files.exists(uploadPath)) {
                                Files.createDirectories(uploadPath);
                        }

                        // Generate filename
                        String originalFilename = file.getOriginalFilename();
                        String extension = "";
                        if (originalFilename != null && originalFilename.contains(".")) {
                                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                        }
                        String filename = UUID.randomUUID().toString() + extension;
                        Path filePath = uploadPath.resolve(filename);

                        // Save file
                        Files.copy(file.getInputStream(), filePath);

                        // Create response URL
                        String fileUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                                        .path("/image/")
                                        .path(folderType + "/")
                                        .path(filename)
                                        .toUriString();

                        MediaUploadResponse.MediaData mediaData = new MediaUploadResponse.MediaData();
                        mediaData.setId(UUID.randomUUID().toString());
                        mediaData.setFilename(filename);
                        mediaData.setOriginalName(originalFilename);
                        mediaData.setUrl(fileUrl);
                        mediaData.setMimeType(file.getContentType());
                        mediaData.setSize(file.getSize());

                        MediaUploadResponse response = new MediaUploadResponse(true, mediaData, "Image uploaded successfully");

                        return ResponseEntity.ok(response);

                } catch (IOException e) {
                        e.printStackTrace();
                        return ResponseEntity.internalServerError()
                                        .body(new MediaUploadResponse(false, null, "Failed to store file: " + file.getOriginalFilename()));
                }
        }
}
