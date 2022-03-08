package personalwebsite.personalweb.web.dto;

import lombok.Getter;
import personalwebsite.personalweb.domain.uploadFile.UploadFile;

@Getter
public class FileResponseDto {

    private Long id;
    private String fileName;

    public FileResponseDto(UploadFile file) {
        this.id = file.getId();
        this.fileName = file.getFileName();
    }

}
