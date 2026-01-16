package com.example.aicosbackendspringboot.JWT.controller;

import com.example.aicosbackendspringboot.JWT.dtos.request.AddFieldRequest;
import com.example.aicosbackendspringboot.JWT.dtos.response.CommonResponse;
import com.example.aicosbackendspringboot.JWT.service.FieldService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class FieldController {
    private FieldService fieldService;

    @PreAuthorize("hasRole('admin')")
    @PostMapping("/add/field")
    public ResponseEntity<CommonResponse> addField(@RequestBody AddFieldRequest addFieldRequest) {
        try {
            return ResponseEntity.ok().body(fieldService.addField(addFieldRequest));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new CommonResponse(false, e.getMessage(), null));
        }
    }

    @GetMapping("/get/field")
    public ResponseEntity<CommonResponse> getField() {
        try {
            return ResponseEntity.ok().body(fieldService.getField());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new CommonResponse(false, e.getMessage(), null));
        }
    }
}