package itis.semestrWork.semestrWork.repository;

import itis.semestrWork.semestrWork.model.UserFiles;
import itis.semestrWork.semestrWork.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FilesRepository extends JpaRepository<UserFiles, Long> {

   List<UserFiles> findAllByUser(User user);
}