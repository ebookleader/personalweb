package personalwebsite.personalweb.web;

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
     * 게시글 작성 버튼 클릭시 게시글과 첨부파일을 저장하고 저장한 게시글의 상세페이지로 이동한다.
     * @param postForm 저장할 게시글 정보를 담은 dto
     * @param file 첨부파일
     * @return 게시글 저장 후 게시글 상세 페이지로 이동
     */
    @PostMapping("/admin/api/posts")
    public String writePost(@ModelAttribute PostForm postForm, @RequestParam("uploadFile") MultipartFile file) throws Exception {

        Long postId = postService.savePost(postForm);

        if (!file.isEmpty()) { // 첨부파일이 게시글에 포함된 경우 첨부파일을 저장해준다.
            UploadFile uploadFile = fileService.storeFile("D:\\springboot_project\\attachments\\", file);
            fileService.setPostForFile(postId, uploadFile);
        }

        fileService.transferFile("D:\\springboot_project\\summernote_image\\", postForm.getContent()); // 임시 저장된 이미지 처리
        fileService.setPostForImage(postId, postForm.getContent()); // content에 포함된 <img>태그의 이미지 id에 postId set

        return "redirect:/posts/" + postId;
    }

    /**
     * summernote 에디터에 업로드 된 파일을 전달받아 저장하고 파일 정보를 DB에 저장한다.
     * @param file summernote 에디터에 들어가는 파일
     * @return '/summernoteImage/파일번호'
     */
    @PostMapping(value = "/uploadSummernoteImage")
    public ResponseEntity<?> uploadSummernoteImage(@RequestParam("file") MultipartFile file) {
        try {
            UploadFile uploadFile = fileService.storeFile("D:\\springboot_project\\temp_summernote_image\\", file);
            return ResponseEntity.ok().body("/summernoteImage/" + uploadFile.getId());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 파일 번호로부터 파일의 전체 경로를 얻어와 resource로 파일을 읽어 전달한다.
     * @param fileId 파일번호
     * @return 파일을 ResourceLoader로 읽어 전달
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

    /** 첨부파일을 다운로드 한다. */
    @GetMapping(value = "/download/{fileId}")
    public ResponseEntity<Resource> fileDownload(@PathVariable Long fileId) throws IOException {

        UploadFile uploadFile = fileService.loadFile(fileId);
        Path path = Paths.get(uploadFile.getFilePath());
        String contentType = Files.probeContentType(path); // 파일의 확장자를 이용해 마임타입 확인

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename(uploadFile.getFileName(), StandardCharsets.UTF_8)
                .build());
        headers.add(HttpHeaders.CONTENT_TYPE, contentType);

        Resource resource = new InputStreamResource(Files.newInputStream(path));

        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }


    /**
     * 게시글 & 게시글과 함께 저장된 파일들을 전부 삭제하고
     *  삭제 성공 메시지와 이동할 주소를 넣은 ModelAndView 객체를 리턴한다.
     */
    @DeleteMapping(value = "/admin/api/posts/{postId}")
    public ModelAndView deletePost(@PathVariable Long postId, ModelAndView mav) {

        postService.deletePost(postId); // 게시글 삭제

        mav.addObject("data", new Message("Your post has been deleted.", "/"));
        mav.setViewName("message");

        return mav;
    }

    /**
     * 수정된 정보로 게시글을 수정하고 수정된 게시글의 상세 페이지로 이동한다.
     * @param postForm 수정된 게시글 정보를 담은 dto
     * @return 게시글 상세페이지로 이동
     */
    @PutMapping(value = "/admin/api/posts")
    public String updatePost(@ModelAttribute PostForm postForm) {
        Long updateId = postService.updatePost(postForm.getId(), postForm);

        fileService.updateFile("D:\\springboot_project\\summernote_image\\", updateId, postForm.getContent()); // 수정전에 저장된 이미지들 DB에서 삭제
        fileService.setPostForImage(updateId, postForm.getContent()); // 이미지에 postId set

        return "redirect:/posts/" + updateId;
    }

}
