package personalwebsite.personalweb.service;

import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
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

    /**
     * 파일 저장: 파일을 저장하고 DB에 파일 정보를 저장한 뒤 저장한 파일을 리턴한다.
     * @param fileRoot 상위 주소
     * @param file 저장할 파일
     * @return 저장한 파일
     * @throws Exception
     */
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
     * UUID를 생성해 파일 이름을 새로 생성하고 파일을 저장한 뒤 저장된 파일 명을 리턴한다.
     * @param rootLocation 상위 주소
     * @param file 저장할 파일
     * @return
     * @throws IOException
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

    /**
     * 파일 불러오기: id로 파일을 찾아 찾은 파일을 리턴한다.
     * @param fileId file id
     * @return id에 해당하는 파일
     */
    public UploadFile loadFile(Long fileId) {
        return fileRepository.findById(fileId).get();
    }

    /**
     * 저장된 파일이 첨부파일이면 true, 게시글 내용에 포함된 이미지 파일이면 false를 리턴한다.
     * @param postId post id
     * @return 첨부파일이면 true,아니면 false 리턴
     */
    public boolean checkPostHasAttachment(Long postId) {
        UploadFile file = fileRepository.findByPostIdAndReference(postId, "yes");
        if (file == null) {
            return false;
        }
        else {
            return true;
        }
    }

    /**
     * 게시글에 첨부된 첨부파일의 정보를 담아 리턴한다.
     * @param postId post id
     * @return 첨부파일의 파일정보를 담은 dto
     */
    public FileResponseDto findReferenceByPostId(Long postId) {
        UploadFile file = fileRepository.findByPostIdAndReference(postId, "yes");
        return new FileResponseDto(file);
    }

    /**
     * 내용에 있는 img 태그를 확인하여 이미지 파일의 id를 찾아 어떤 포스트의 이미지 파일인지 구분하는 post id를 설정해준다.
     * @param postId post id
     * @param content 게시글 내용
     */
    @Transactional
    public void setPostIdForImage(Long postId, String content) {
        Document doc = Jsoup.parse(content);
        Elements imgTags = doc.select("img");
        for (Element el : imgTags) {
            String imgSrc = el.attr("src");
            // upload file PK
            Long imgId = Long.parseLong(imgSrc.substring(17)); // 17 ~
            UploadFile uploadFile = fileRepository.findById(imgId)
                    .orElseThrow(() -> new IllegalArgumentException("해당 이미지가 없습니다. id = " + imgId));
            uploadFile.setPostId(postId);
        }
    }

    /**
     * 게시글에 포함된 파일을 전부 삭제한다.
     * @param postId
     */
    @Transactional
    public void deleteAllFileByPostId(Long postId) {
        Long file_cnt = fileRepository.countByPostId(postId);
        if (file_cnt > 0) {
            System.out.println("file delete start");
            fileRepository.deleteByPostId(postId);
        }
    }

    /**
     * 수정 전 <-> 수정 후 내용을 비교하여 삭제된 이미지 정보를 DB에서 제거한다.
     * @param postId post id
     * @param content 수정된 게시글 내용
     */
    @Transactional
    public void deleteRemovedFile(Long postId, String content) {
        List<UploadFile> allFile = fileRepository.findAllByPostIdAndReferenceIsNull(postId);
        List<Long> newContentImageId = new ArrayList<>();

        Document doc = Jsoup.parse(content);
        Elements imgTags = doc.select("img");
        for (Element el : imgTags) {
            String imgSrc = el.attr("src");
            Long imgId = Long.parseLong(imgSrc.substring(17)); // 17 ~
            newContentImageId.add(imgId);
        }

        for (UploadFile file : allFile) {
            if (!newContentImageId.contains(file.getId())) { // 수정 전 존재했던 이미지가 삭제됐다면
                fileRepository.deleteById(file.getId()); // DB에서 해당 파일 정보를 삭제
            }
        }
    }
}
