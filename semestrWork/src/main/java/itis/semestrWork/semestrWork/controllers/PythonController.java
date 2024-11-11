package itis.semestrWork.semestrWork.controllers;

import itis.semestrWork.semestrWork.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@Controller
public class PythonController {

    @Autowired
    private AuthenticationService authenticationService;

    // Карта для хранения истории скриптов для каждого пользователя
    private final Map<Long, List<Map<String, String>>> userScriptHistory = new HashMap<>();

    // Отображение страницы запуска скрипта
    @GetMapping()
    public String showScriptPage(Model model) {
        Long userId = authenticationService.getCurrentUserId();

        if (userId != null) {
            model.addAttribute("userId", userId);
            return "python"; // Имя вашего шаблона .ftlh, например "python_execution.ftlh"
        } else {
            return "redirect:/login"; // Перенаправление на страницу входа, если пользователь не аутентифицирован
        }
    }

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
                return "Ошибка выполнения: " + output.toString();
            }

            return "Результат выполнения: \n" + output.toString();
        } catch (IOException | InterruptedException e) {
            return "Ошибка: " + e.getMessage();
        }
    }


    // Сохранение скрипта
    @PostMapping("/save")
    @ResponseBody
    public Map<String, Object> saveScript(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();

        Long userId = authenticationService.getCurrentUserId();
        if (userId != null) {
            try {
                String code = request.get("code");
                Map<String, String> scriptEntry = new HashMap<>();
                scriptEntry.put("code", code);
                scriptEntry.put("date", new Date().toString());

                // Получаем или создаём историю скриптов для текущего пользователя
                userScriptHistory.computeIfAbsent(userId, k -> new ArrayList<>()).add(scriptEntry);

                response.put("success", true);
            } catch (Exception e) {
                response.put("success", false);
            }
        } else {
            response.put("success", false);
            response.put("error", "Пользователь не аутентифицирован");
        }
        return response;
    }

    // Получение истории скриптов
    @GetMapping("/history")
    @ResponseBody
    public List<Map<String, String>> getHistory() {
        Long userId = authenticationService.getCurrentUserId();
        if (userId != null) {
            // Возвращаем историю для текущего пользователя
            return userScriptHistory.getOrDefault(userId, Collections.emptyList());
        } else {
            return Collections.emptyList(); // Если пользователь не аутентифицирован, возвращаем пустой список
        }
    }
}
