package itis.semestrWork.semestrWork.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import itis.semestrWork.semestrWork.dto.FileData;
import itis.semestrWork.semestrWork.model.UserFiles;
import itis.semestrWork.semestrWork.model.User;
import itis.semestrWork.semestrWork.repository.FilesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilesService {

    @Value("${app.files.path}")
    private String filesPath;

    @Autowired
    private FilesRepository filesRepository;

    public List<FileData> getFilesByUser(User user) {
        List<UserFiles> userFiles = filesRepository.findAllByUser(user);

        return userFiles.stream()
                .map(this::readFileContent)
                .collect(Collectors.toList());
    }


    public InputStreamResource downloadFile(Long id) {
        UserFiles fileRecord = getFileById(id);
        File file = new File(fileRecord.getPath());

        if (!file.exists()) {
            throw new RuntimeException("Файл не найден: " + fileRecord.getPath());
        }

        try {
            return new InputStreamResource(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    public String runFile(String code) {
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
               throw new RuntimeException("Ошибка чтения скрипта. Код выхода: " + exitCode);

            }
            return "Результат выполнения: \n" + output;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    private FileData readFileContent(UserFiles fileRecord) {
        File file = new File(fileRecord.getPath());
        if (!file.exists()) {
            throw new RuntimeException("Файл не найден: " + fileRecord.getPath());
        }

        ObjectMapper objectMapper = new ObjectMapper();
        FileData data = new FileData();
        List<String> sourceContent = new ArrayList<>();

        try {
            JsonNode rootNode = objectMapper.readTree(file);
            for (JsonNode cell : rootNode.path("cells")) {
                if ("code".equals(cell.path("cell_type").asText())) {
                    for (JsonNode line : cell.path("source")) {
                        sourceContent.add(line.asText());
                    }
                }
            }

            String content = String.join(System.lineSeparator(), sourceContent);
            data.setContent(content);
            data.setFileName(file.getName());
            data.setId(fileRecord.getId());

        } catch (IOException e) {
            throw new RuntimeException("Ошибка чтения файла: " + e.getMessage());
        }

        return data;
    }


    @Transactional
    public void saveFile(String content, String directoryName, User user) {
        File directory = new File(filesPath, directoryName);

        if (!directory.exists()) {
            directory.mkdirs();
        }

        LocalDateTime now = LocalDateTime.now();
        String fileName = "script_" + now + ".ipynb";

        File file = new File(directory, fileName);

        String notebookContent = buildContent(content);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(notebookContent);
            UserFiles files = UserFiles.builder()
                    .user(user)
                    .path(file.getAbsolutePath())
                    .build();
            filesRepository.save(files);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка записи файла: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Непредвиденная ошибка: " + e.getMessage());
        }
    }

    public UserFiles getFileById(Long fileId) {
        return filesRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("Файл не найден"));
    }

    private String buildContent(String content) {
        return "{\n" +
                "  \"cells\": [\n" +
                "    {\n" +
                "      \"cell_type\": \"code\",\n" +
                "      \"execution_count\": null,\n" +
                "      \"metadata\": {},\n" +
                "      \"outputs\": [],\n" +
                "      \"source\": [\n" +
                "        \"" + content.replace("\"", "\\\"").replace("\n", "\",\n        \"") + "\"\n" +
                "      ]\n" +
                "    }\n" +
                "  ],\n" +
                "  \"metadata\": {\n" +
                "    \"kernelspec\": {\n" +
                "      \"display_name\": \"Python 3\",\n" +
                "      \"language\": \"python\",\n" +
                "      \"name\": \"python3\"\n" +
                "    },\n" +
                "    \"language_info\": {\n" +
                "      \"codemirror_mode\": {\n" +
                "        \"name\": \"ipython\",\n" +
                "        \"version\": 3\n" +
                "      },\n" +
                "      \"file_extension\": \".py\",\n" +
                "      \"mimetype\": \"text/x-python\",\n" +
                "      \"name\": \"Python\",\n" +
                "      \"nbconvert_EXPORTERS\": [\n" +
                "        \"ipynb\",\n" +
                "        \"slides\"\n" +
                "      ],\n" +
                "      \"pygments_lexer\": \"ipython3\",\n" +
                "      \"version\": \"3.8.2\"\n" +
                "    }\n" +
                "  },\n" +
                "  \"nbformat\": 4,\n" +
                "  \"nbformat_minor\": 2\n" +
                "}\n";
    }
}
