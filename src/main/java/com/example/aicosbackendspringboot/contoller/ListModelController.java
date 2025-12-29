package com.example.aicosbackendspringboot.contoller;

import com.example.aicosbackendspringboot.service.ListModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/listModels")
public class ListModelController {

    @Autowired
    private ListModelService modelService;

    @GetMapping
    public ResponseEntity<?> listModels(@RequestParam(defaultValue = "RT") String type) {
        try {
            Map<String, Object> result = modelService.listModels(type);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
