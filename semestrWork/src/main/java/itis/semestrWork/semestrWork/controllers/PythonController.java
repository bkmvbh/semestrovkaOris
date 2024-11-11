package itis.semestrWork.semestrWork.controllers;

import itis.semestrWork.semestrWork.dto.FileData;
import itis.semestrWork.semestrWork.model.User;
import itis.semestrWork.semestrWork.model.UserFiles;
import itis.semestrWork.semestrWork.service.AuthenticationService;
import itis.semestrWork.semestrWork.service.FilesService;
import itis.semestrWork.semestrWork.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@RestController
public class PythonController {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private UserService userService;

    @Autowired
    private FilesService filesService;

    @PostMapping("/run")
    @ResponseBody
    public String runScript(@RequestBody Map<String, String> request) {
        String code = request.get("code");

        try {
            String filePath = "temp_script.py";
            Files.write(Paths.get(filePath), code.getBytes());

            ProcessBuilder processBuilder = new ProcessBuilder("python3", filePath);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                return "Ошибка выполнения: " + output;
            }

            return "Результат выполнения: \n" + output;
        } catch (IOException | InterruptedException e) {
            return "Ошибка: " + e.getMessage();
        }
    }


    @PostMapping("/save")
    @ResponseBody
    public Map<String, Object> saveScript(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();

        Long userId = authenticationService.getCurrentUserId();
        User user = userService.findById(userId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден")
        );

        String code = request.get("code");

        try {
            filesService.saveFile(code, user);
            response.put("success", true);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            response.put("success", false);
        }
        return response;
    }


    @GetMapping("/history")
    @ResponseBody
    public List<UserFiles> getHistory() {
        Long userId = authenticationService.getCurrentUserId();
        User user = userService.findById(userId).orElseThrow();
        return filesService.getFilesByUser(user);
    }
}
