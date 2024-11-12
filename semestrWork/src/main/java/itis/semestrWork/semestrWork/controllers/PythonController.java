package itis.semestrWork.semestrWork.controllers;

import itis.semestrWork.semestrWork.dto.FileData;
import itis.semestrWork.semestrWork.model.User;
import itis.semestrWork.semestrWork.service.AuthenticationService;
import itis.semestrWork.semestrWork.service.FilesService;
import itis.semestrWork.semestrWork.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
public class PythonController {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private UserService userService;

    @Autowired
    private FilesService filesService;

    @PostMapping("/run")
    public ResponseEntity<?> runScript(@RequestBody Map<String, String> request) {
        try {
           String response = filesService.runFile(request.get("code"));
           return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/save")
    public ResponseEntity<Map<String, Object>> saveScript(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();

        Long userId = authenticationService.getCurrentUserId();
        User user = userService.findById(userId).orElseThrow(() ->
                new ResponseStatusException(NOT_FOUND, "Пользователь не найден")
        );

        String code = request.get("code");
        String directoryName = "scripts_" + user.getUserName();

        try {
            filesService.saveFile(code, directoryName, user);
            response.put("success", true);
            return ResponseEntity
                    .ok()
                    .body(response);

        } catch (Exception e) {
            response.put("error", e.getMessage());
            response.put("success", false);
            return ResponseEntity
                    .internalServerError()
                    .body(response);
        }
    }


    @GetMapping("/history")
    public ResponseEntity<List<FileData>> getHistory() {
        try {
            Long userId = authenticationService.getCurrentUserId();
            User user = userService.findById(userId)
                    .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Пользователь не найден"));

            return ResponseEntity.ok()
                    .body(filesService.getFilesByUser(user));

        } catch (Exception e) {
            throw new ResponseStatusException(INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }


    @GetMapping("/download/{fileId}")
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable Long fileId) {
        try {
            InputStreamResource resource = filesService.downloadFile(fileId);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/octet-stream"))
                    .body(resource);
        } catch (Exception e) {
            throw new ResponseStatusException(INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
