package com.springboot.smartcampusoperationshub.controller;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.springboot.smartcampusoperationshub.model.Resource;
import com.springboot.smartcampusoperationshub.repository.ResourceRepository;
import com.springboot.smartcampusoperationshub.service.ResourceQrService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/rooms")
public class ResourceQrController {

    private final ResourceRepository resourceRepository;
    private final ResourceQrService resourceQrService;

    @Value("${app.base-url}")
    private String baseUrl;

    public ResourceQrController(ResourceRepository resourceRepository, ResourceQrService resourceQrService) {
        this.resourceRepository = resourceRepository;
        this.resourceQrService = resourceQrService;
    }

    @GetMapping("/{roomId}/qr")
    public ResponseEntity<byte[]> getRoomQr(@PathVariable UUID resourceId) throws WriterException, IOException {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        resourceQrService.ensureSecret(resource);

        String url = String.format("%s/checkin/%d?k=%s", baseUrl, resource.getId(), resource.getQrSecret());

        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.MARGIN, 2);

        BitMatrix matrix = new QRCodeWriter().encode(url, BarcodeFormat.QR_CODE, 600, 600, hints);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(matrix, "PNG", out);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        headers.setContentDispositionFormData("inline",
                "room-" + resource.getId() + "-qr.png");

        return new ResponseEntity<>(out.toByteArray(), headers, 200);
    }
}