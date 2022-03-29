package personalwebsite.personalweb.service;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import personalwebsite.personalweb.config.auth.dto.SessionUser;
import personalwebsite.personalweb.domain.uploadFile.UploadFile;
import personalwebsite.personalweb.domain.uploadFile.UploadFileRepository;
import personalwebsite.personalweb.domain.user.Role;
import personalwebsite.personalweb.domain.user.User;
import personalwebsite.personalweb.domain.user.UserRepository;
import personalwebsite.personalweb.web.dto.FileResponseDto;
import personalwebsite.personalweb.web.dto.posts.PostForm;
import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.Assertions;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotEquals;


@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class FileServiceTest {

    @Autowired
    FileService fileService;

    @Autowired
    PostService postService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UploadFileRepository fileRepository;

    private User user;
    private Long postId;
    MockHttpSession session;
    MockHttpServletRequest request;

    MockMultipartFile imageFile = new MockMultipartFile("testImage", "testImage.png", "image/png", "<<png data>>".getBytes());
    String rootLocation_image = "D:\\springboot_project_test\\summernote_image\\";
    String rootLocation_temp_image = "D:\\springboot_project_test\\temp_summernote_image\\";
    String rootLocation_other = "D:\\springboot_project_test\\attachments\\";

    @Before()
    public void setUp() throws Exception {
        user = User.builder()
                .name("kim")
                .email("example@gmail.com")
                .role(Role.USER)
                .build();
        userRepository.save(user);

        session = new MockHttpSession();
        session.setAttribute("user", new SessionUser(user));

        request = new MockHttpServletRequest();
        request.setSession(session);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        PostForm form = new PostForm();
        form.setTitle("title");
        form.setSummary("summary");
        form.setContent("content");
        postId = postService.savePost(form);
    }

    @After
    public void cleanup() {
        session.clearAttributes();
        session = null;
        fileRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void 파일저장_불러오기() throws Exception {
        //given

        //when
        UploadFile uploadFile_image = fileService.storeFile(rootLocation_image, imageFile);
        UploadFile uploadFile_other = fileService.storeFile(rootLocation_other, imageFile);

        //then
        List<UploadFile> files = fileRepository.findAll();
        UploadFile file1 = fileService.loadFile(files.get(0).getId());
        UploadFile file2 = fileService.loadFile(files.get(1).getId());

        assertEquals(2, files.size());
        assertEquals("testImage.png", file1.getFileName());
        assertEquals("testImage.png", file2.getFileName());
        assertThat(file1.getFilePath(), containsString("summernote_image"));
        assertThat(file2.getFilePath(), containsString("attachments"));
        assertEquals(null, file1.getReference());
        assertEquals("yes", file2.getReference());
        assertEquals("yes", file1.getTemp());
    }

    @Test
    public void 파일이동() throws Exception {
        //given
        UploadFile uploadFile_image1 = fileService.storeFile(rootLocation_temp_image, imageFile);
        String beforePath = uploadFile_image1.getFilePath();

        String content = "<img src=\"/summernoteImage/"+uploadFile_image1.getId()+"\">";

        //when
        fileService.moveFile(rootLocation_image, content);

        //then
        assertNotEquals(beforePath, uploadFile_image1.getFilePath());
        assertEquals(null, uploadFile_image1.getTemp());
    }

    @Test
    public void 파일삭제() throws Exception {
        //given
        UploadFile uploadFile_image1 = fileService.storeFile(rootLocation_temp_image, imageFile);
        UploadFile uploadFile_image2 = fileService.storeFile(rootLocation_temp_image, imageFile);

        String content = "<img src=\"/summernoteImage/"+uploadFile_image2.getId()+"\">";

        fileService.moveFile(rootLocation_image, content);

        //when
        fileService.deleteFile();

        //then
        List<UploadFile> all = fileRepository.findAll(); // 1은 tmpdir에서 삭제, 2는 원래dir로, 1은 repo에서 삭제
        assertEquals(1, all.size());
        assertEquals(all.get(0).getTemp(), null);
    }


    @Test
    public void 파일번호설정() {
        //given
        UploadFile uploadFile_image = null;
        try {
            uploadFile_image = fileService.storeFile(rootLocation_image, imageFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String content = "<p><img src=\"/summernoteImage/"+
                uploadFile_image.getId()+"\" style=\"width: 25%;\"><br></p>";

        //when
        fileService.setPostIdForImage(postId, content);

        //then
        UploadFile file = fileService.loadFile(uploadFile_image.getId());
        assertEquals(file.getPostId(), postId);
    }

    @Test
    public void 첨부파일확인_불러오기() {
        //given
        UploadFile uploadFile_other = null;
        UploadFile uploadFile_image = null;
        try {
            uploadFile_image = fileService.storeFile(rootLocation_image, imageFile);
            uploadFile_other = fileService.storeFile(rootLocation_other, imageFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        uploadFile_image.setPostId(postId);
        uploadFile_other.setPostId(postId);

        //when
        FileResponseDto responseDto = fileService.findReferenceByPostId(postId);
        boolean hasAttachment = fileService.checkPostHasAttachment(postId);

        //then
        assertEquals(responseDto.getId(), uploadFile_other.getId());
        assertEquals(hasAttachment, true);
    }

    @Test
    public void 게시글에포함된파일전체삭제() {
        //given
        UploadFile uploadFile1 = null;
        UploadFile uploadFile2 = null;
        try {
            uploadFile1 = fileService.storeFile(rootLocation_image, imageFile);
            uploadFile2 = fileService.storeFile(rootLocation_other, imageFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        uploadFile1.setPostId(postId);
        uploadFile2.setPostId(postId);

        //when
        fileService.deleteAllFileByPostId(postId);

        //then
        assertEquals(0, fileRepository.findAllByPostId(postId).size());
    }

    @Test
    public void 게시글이미지정보수정() {
        //given
        UploadFile file1 = null;
        UploadFile file2 = null;
        try {
            file1 = fileService.storeFile(rootLocation_temp_image, imageFile);
            file2 = fileService.storeFile(rootLocation_temp_image, imageFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String content = "<p><img src=\"/summernoteImage/"+
                file1.getId()+"\" style=\"width: 25%;\"><img src=\"/summernoteImage/"+
                file2.getId()+"\" style=\"width: 25%;\"><br></p>";

        fileService.transferFile(rootLocation_image, content);
        fileService.setPostIdForImage(postId, content); // 파일 번호 설정

        //when
        UploadFile file3 = null;
        UploadFile file4 = null;

        try {
            file3 = fileService.storeFile(rootLocation_temp_image, imageFile);
            file4 = fileService.storeFile(rootLocation_temp_image, imageFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        content = "<p><img src=\"/summernoteImage/"+
                file2.getId()+"\" style=\"width: 25%;\"><img src=\"/summernoteImage/"+
                file3.getId()+"\" style=\"width: 25%;\"><br></p>"; // 게시글 내용 수정

        fileService.updateFile(rootLocation_image, postId, content);
        fileService.setPostIdForImage(postId, content);

        //then
        List<UploadFile> files = fileRepository.findAllByPostId(postId);
        List<Long> idList = new ArrayList<>();
        for (UploadFile file : files) {
            idList.add(file.getId());
        }
        assertEquals(files.size(), 2);
        Assertions.assertThat(idList).containsOnly(file2.getId(), file3.getId());
        assertEquals(files.get(0).getTemp(), null);
        assertEquals(files.get(1).getTemp(), null);
        assertEquals(file3.getPostId(), file2.getPostId());
    }


}