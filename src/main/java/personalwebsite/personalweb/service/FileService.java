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
import personalwebsite.personalweb.domain.posts.Post;
import personalwebsite.personalweb.domain.posts.PostRepository;
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
    private final PostRepository postRepository;

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
            String temp = "yes";
            if (fileRoot.contains("attachments")) {
                reference = "yes";
                temp = null;
            }
            UploadFile saveFile = UploadFile.builder()
                    .fileName(file.getOriginalFilename())
                    .filePath(fileRoot+saveFileName)
                    .saveFileName(saveFileName)
                    .size(file.getResource().contentLength())
                    .registerDate(LocalDateTime.now())
                    .reference(reference)
                    .temp(temp)
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
     * @return 저장된 파일명
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
     * 게시글 작성이 완료되면 임시 폴더에 저장된 이미지를 지정폴더로 이동시키고 임시폴더 내용을 삭제한다.
     * @param saveFileRoot 지정폴더
     * @param content 게시글 내용
     */
    @Transactional
    public void transferFile(String saveFileRoot, String content) {
        moveFile(saveFileRoot, content);
        deleteFile();
    }

    /**
     * 내용에 포함된 이미지는 지정폴더로 이동시키고 temp와 filePath를 업데이트한다.
     * @param newDir 지정폴더
     * @param content 게시글 내용
     */
    @Transactional
    public void moveFile(String newDir, String content) {

        Document doc = Jsoup.parse(content);
        Elements imgTags = doc.select("img");

        for (Element el : imgTags) { // 내용에 포함된 이미지
            String imgSrc = el.attr("src");
            Long imgId = Long.parseLong(imgSrc.substring(17)); // 17 ~
            UploadFile uploadFile = fileRepository.findById(imgId)
                    .orElseThrow(() -> new IllegalArgumentException("해당 이미지가 없습니다. id = " + imgId));

            File dirPath = new File(newDir); // 지정 폴더
            if (!dirPath.exists()) { // 폴더가 없으면 폴더 생성
                dirPath.mkdirs();
            }
            try {
                File oldFile = new File(uploadFile.getFilePath()); // 이동시킬 파일
                String newFilePath = newDir + File.separator + uploadFile.getSaveFileName();
                oldFile.renameTo(new File(newFilePath));
                uploadFile.setTempAndFilePath(null, newFilePath);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 내용에 포함되지 않은 이미지는 임시폴더에서 삭제하고 db 정보를 삭제한다.
     */
    @Transactional
    public void deleteFile() {
        List<UploadFile> files = fileRepository.findAllByTempIsNotNull();
        for (UploadFile uploadFile : files) {
            String filePath = uploadFile.getFilePath();
            File file = new File(filePath);
            if(file.exists()) {
                file.delete();  // 저장된 파일 삭제
                fileRepository.delete(uploadFile);
            }
        }
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
    public void setPostForImage(Long postId, String content) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("해당 포스트가 없습니다. id = " + postId));
        Document doc = Jsoup.parse(content);
        Elements imgTags = doc.select("img");
        for (Element el : imgTags) {
            String imgSrc = el.attr("src");
            // upload file PK
            Long imgId = Long.parseLong(imgSrc.substring(17)); // 17 ~
            UploadFile uploadFile = fileRepository.findById(imgId)
                    .orElseThrow(() -> new IllegalArgumentException("해당 이미지가 없습니다. id = " + imgId));
            uploadFile.setPost(post);
        }
    }

    /**
     * 이미지 파일에 post를 설정해준다.
     * @param postId post id
     * @param file 이미지 파일
     */
    public void setPostForFile(Long postId, UploadFile file) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("해당 포스트가 없습니다. id = " + postId));
        file.setPost(post);
    }

    /**
     * 수정 전 <-> 수정 후 내용을 비교하여 삭제된 이미지와 새로 추가된 이미지를 처리한다.
     * @param postId post id
     * @param content 수정된 게시글 내용
     */
    @Transactional
    public void updateFile(String saveRoot, Long postId, String content) {

        List<UploadFile> allFile = fileRepository.findAllByPostIdAndReferenceIsNull(postId); // 기존 저장되어있던 모든 파일
        List<Long> newContentImageId = new ArrayList<>(); // 수정된 게시글에 포함된 모든 파일의 id

        Document doc = Jsoup.parse(content);
        Elements imgTags = doc.select("img");
        for (Element el : imgTags) {
            String imgSrc = el.attr("src");
            Long imgId = Long.parseLong(imgSrc.substring(17)); // 17 ~
            newContentImageId.add(imgId);
        }

        // 삭제된 이미지 처리
        for (UploadFile file : allFile) {
            if (!newContentImageId.contains(file.getId())) { // 수정 전 존재했던 이미지가 삭제됐다면
                File fi = new File(file.getFilePath());
                if(fi.exists()) {
                    fi.delete();  // 저장된 파일 삭제
                    fileRepository.deleteById(file.getId()); // DB 정보 삭제
                }
            }
            else {
                newContentImageId.remove(file.getId());
            }
        }

        // 새로 추가된 이미지 처리
        for (Long imgId : newContentImageId) {
            UploadFile uploadFile = fileRepository.findById(imgId)
                    .orElseThrow(() -> new IllegalArgumentException("해당 이미지가 없습니다. id = " + imgId));

            File dirPath = new File(saveRoot);
            if (!dirPath.exists()) {
                dirPath.mkdirs();
            }
            try {   // 지정 폴더로 이동
                File oldFile = new File(uploadFile.getFilePath()); // 이동시킬 파일
                String newFilePath = saveRoot + File.separator + uploadFile.getSaveFileName();
                oldFile.renameTo(new File(newFilePath));
                uploadFile.setTempAndFilePath(null, newFilePath);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 임시 폴더 비우기
        deleteFile();
    }
}
