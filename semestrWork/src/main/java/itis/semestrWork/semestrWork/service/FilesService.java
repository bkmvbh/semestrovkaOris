package itis.semestrWork.semestrWork.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import itis.semestrWork.semestrWork.dto.FileData;
import itis.semestrWork.semestrWork.model.UserFiles;
import itis.semestrWork.semestrWork.model.User;
import itis.semestrWork.semestrWork.repository.FilesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@Service
public class FilesService {

    @Autowired
    private FilesRepository filesRepository;

    public List<UserFiles> getFilesByUser(User user) {
        return filesRepository.findAllByUser(user);
    }

    @Transactional
    public void saveFile(String content, User user) {
        UserFiles userFiles = UserFiles.builder()
                .user(user)
                .content(content)
                .build();

        filesRepository.save(userFiles);
    }
}
