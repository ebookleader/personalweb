package personalwebsite.personalweb.web;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import personalwebsite.personalweb.domain.uploadFile.UploadFile;
import personalwebsite.personalweb.service.FileService;
import personalwebsite.personalweb.service.PostService;
import personalwebsite.personalweb.web.dto.Message;
import personalwebsite.personalweb.web.dto.posts.PostForm;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@Slf4j
@RequiredArgsConstructor
public class PostApiController {

    private final PostService postService;
    private final FileService fileService;
    private final ResourceLoader resourceLoader;

    /**
     * 게시글 작성 후 submit 누르면 게시글을 저장하고 상세페이지로 리다이렉트
     * 여기서 이미지 업로드는 처리하지 않음
     */
    @PostMapping("/admin/api/posts")
    public String writePost(@ModelAttribute PostForm postForm, @RequestParam("uploadFile") MultipartFile file) throws Exception {
        Long postId = postService.savePost(postForm);
        if (!file.isEmpty()) {
            UploadFile uploadFile = fileService.storeFile("D:\\springboot_project\\attachments\\", file);
            uploadFile.setPostId(postId);
        }
        // 이미지에 postId set
        fileService.setPostIdForImage(postId, postForm.getContent());
        return "redirect:/posts/" + postId;
    }

    /**
     * 파일을 전달받아 FileService 를 이용해 파일을 디스크에 저장하고 파일 정보 DB에 저장
     * 생성된 파일 번호를 클라이언트에 전달
     */
    @PostMapping(value = "/uploadSummernoteImage")
    public ResponseEntity<?> uploadSummernoteImage(@RequestParam("file") MultipartFile file) {
        try {
            UploadFile uploadFile = fileService.storeFile("D:\\springboot_project\\summernote_image\\", file);
            return ResponseEntity.ok().body("/summernoteImage/" + uploadFile.getId());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 서버에 "/image/파일번호" 요청시 이미지를 찾아 제공
     * 파일번호로부터 전체경로를 얻어와 파일 resource로 읽어들여 전달
     */
    @GetMapping(value = "/summernoteImage/{fileId}")
    public ResponseEntity<?> serveFile(@PathVariable Long fileId) {
        try {
            UploadFile uploadFile = fileService.loadFile(fileId);
            Resource resource = resourceLoader.getResource("file:" + uploadFile.getFilePath());
            return ResponseEntity.ok().body(resource);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping(value = "/download/{fileId}")
    public ResponseEntity<Resource> fileDownload(@PathVariable Long fileId) throws IOException {
        UploadFile uploadFile = fileService.loadFile(fileId);
        Path path = Paths.get(uploadFile.getFilePath());
        String contentType = Files.probeContentType(path);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename(uploadFile.getFileName(), StandardCharsets.UTF_8)
                .build());
        headers.add(HttpHeaders.CONTENT_TYPE, contentType);
        Resource resource = new InputStreamResource(Files.newInputStream(path));
        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }


    /**
     * 게시글 삭제
     */
    @DeleteMapping(value = "/admin/api/posts/{postId}")
    public ModelAndView deletePost(@PathVariable Long postId, ModelAndView mav) {
        postService.deletePost(postId);
        fileService.deleteAllFileByPostId(postId);
        mav.addObject("data", new Message("delete post success", "/admin/home"));
        mav.setViewName("message");
        return mav;
    }

    /**
     * 게시글 수정
     */
    @PutMapping(value = "/admin/api/posts")
    public String updatePost(@ModelAttribute PostForm postForm) {
        Long updateId = postService.updatePost(postForm.getId(), postForm);

        fileService.deleteRemovedFile(updateId, postForm.getContent()); // 수정전에 저장된 이미지들 DB에서 삭제

        fileService.setPostIdForImage(updateId, postForm.getContent()); // 이미지에 postId set
        return "redirect:/posts/" + updateId;
    }

}
