package itis.semestrWork.semestrWork.dto;
import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class RegistrationFormDto {
    private String fullname;
    private String username;
    private String useremail;
    private String password;
    private String repeatPassword;
}
