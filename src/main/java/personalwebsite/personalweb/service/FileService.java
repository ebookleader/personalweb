package personalwebsite.personalweb.service;

import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import personalwebsite.personalweb.domain.uploadFile.UploadFile;
import personalwebsite.personalweb.domain.uploadFile.UploadFileRepository;
import personalwebsite.personalweb.web.dto.FileResponseDto;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {

    private final UploadFileRepository fileRepository;

    @Transactional
    public UploadFile storeFile(String fileRoot, MultipartFile file) throws Exception {
        try {
            if(file.isEmpty()) {
                throw new Exception("Failed to store empty file " + file.getOriginalFilename());
            }
            String saveFileName = fileSave(fileRoot, file);
            String reference = null;
            if (fileRoot.contains("attachments")) {
                reference = "yes";
            }
            UploadFile saveFile = UploadFile.builder()
                    .fileName(file.getOriginalFilename())
                    .filePath(fileRoot+saveFileName)
                    .saveFileName(saveFileName)
                    .size(file.getResource().contentLength())
                    .registerDate(LocalDateTime.now())
                    .reference(reference)
                    .build();
            fileRepository.save(saveFile);
            return saveFile;
        } catch(IOException e) {
            throw new Exception("Failed to store file " + file.getOriginalFilename(), e);
        }
    }

    /**
     * uuid 생성해서 파일 이름 생성하고 파일 저장한뒤 파일명 반환
     */
    private String fileSave(String rootLocation, MultipartFile file) throws IOException {
        File uploadDir = new File(rootLocation); // 상위 주소
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        String saveFileName = UUID.randomUUID() + file.getOriginalFilename();
        File saveFile = new File(rootLocation, saveFileName); // 상위 주소, 파일 이름
        FileCopyUtils.copy(file.getBytes(), saveFile); // 입력파일내용을 출력파일에 복사

        return saveFileName;
    }

    public UploadFile loadFile(Long fileId) {
        return fileRepository.findById(fileId).get();
    }

    public FileResponseDto findReferenceByPostId(Long postId) {
        UploadFile file = fileRepository.findByPostIdAndReference(postId, "yes");
        System.out.println("reference file is: "+file);
        System.out.println("reference file id is: "+file.getId());
        return new FileResponseDto(file);
    }

    /** 이미지에 postId 설정 */
    @Transactional
    public void setPostIdForImage(Long postId, String content) {
        System.out.println("postid is: "+postId);
        Document doc = Jsoup.parse(content);
        Elements imgTags = doc.select("img");
        for (Element el : imgTags) {
            String imgSrc = el.attr("src");
            // uploadFile의 PK
            Long imgId = Long.parseLong(imgSrc.substring(17)); // 17 ~
            System.out.println("imgId is: "+imgId);
            UploadFile uploadFile = fileRepository.findById(imgId)
                    .orElseThrow(() -> new IllegalArgumentException("해당 이미지가 없습니다. id = " + imgId));
            System.out.println("find postid is: "+uploadFile.getPostId());
            uploadFile.setPostId(postId);
        }
    }

    @Transactional
    public void deleteAllFileByPostId(Long postId) {
        Long file_cnt = fileRepository.countByPostId(postId);
        if (file_cnt > 0) {
            System.out.println("file delete start");
            fileRepository.deleteByPostId(postId);
        }
    }

    /**
     * content(수정된 새로운 content)에서 img태그로 img pk 가져옴
     * -> pk가
     */
    @Transactional
    public void deleteRemovedFile(Long postId, String content) {
        List<UploadFile> allFile = fileRepository.findAllByPostId(postId);
        List<Long> newContentImageId = new ArrayList<>();
        Document doc = Jsoup.parse(content);
        Elements imgTags = doc.select("img");
        for (Element el : imgTags) {
            String imgSrc = el.attr("src");
            Long imgId = Long.parseLong(imgSrc.substring(17)); // 17 ~
            newContentImageId.add(imgId);
        }
        System.out.println("image Id>>>>>>> "+newContentImageId);

        for (UploadFile file : allFile) {
            if (!newContentImageId.contains(file.getId())) { // 기존 이미지가 삭제되면
                System.out.println("delete id is: "+file.getId());
                fileRepository.deleteById(file.getId());
            }
        }
    }
}
