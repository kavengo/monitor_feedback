package ch.fhnw.cere.repository.controllers;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.print.attribute.standard.Media;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.StringJoiner;

/**
 * Created by Aydinli on 29.01.2018.
 */
@RestController
@RequestMapping(value = "${supersede.base_path.feedback}/{language}/applications/{applicationId}/feedbacks")
public class UploadController {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(FeedbackController.class);

    @Value("${supersede.profile_pic_upload_directory}")
    protected String UPLOADED_FOLDER;

//    @RequestMapping(value = "/upload", method = RequestMethod.POST)
//    public String singleFileUpload(@RequestParam("file") MultipartFile file,
//                                   @RequestHeader(value = "api_user_id") long api_user_id){
//        try {
//            byte[] bytes = file.getBytes();
////            Path path = Paths.get(UPLOADED_FOLDER + file.getOriginalFilename());
//            Path path = Paths.get(UPLOADED_FOLDER + "/profile_pic_api_user_"+api_user_id + "."
//                    + FilenameUtils.getExtension(file.getOriginalFilename()));
//            Files.write(path, bytes);
//
//            return "successfully uploaded file";
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            return "file upload did not work";
//        }
//    }
//
//    @RequestMapping(value = "/uploadMulti", method = RequestMethod.POST)
//    public String multiFileUpload(@RequestParam("files") MultipartFile[] files){
//        StringJoiner sj = new StringJoiner(" , ");
//
//        for(MultipartFile file: files){
//            if (file.isEmpty()) {
//                continue; //next pls
//            }
//
//            try {
//                byte[] bytes = file.getBytes();
//                Path path = Paths.get(UPLOADED_FOLDER + file.getOriginalFilename());
//                Files.write(path, bytes);
//
//                sj.add(file.getOriginalFilename());
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        return "successfully uploaded files";
//    }

    @RequestMapping(value = "/uploadImage/user/{api_user_id}", method = RequestMethod.POST)
    public String handleFormUpload(@RequestParam("file") MultipartFile file,
                                   @PathVariable long api_user_id,
                                   HttpServletResponse response) throws IOException{
        if (!file.isEmpty()) {
            response.setHeader("Access-Control-Allow-Headers","api_user_id");
            BufferedImage srcOriginal = ImageIO.read(new ByteArrayInputStream(file.getBytes()));

            BufferedImage src = scale(srcOriginal,64,64);

            LOGGER.info("Upload Image Size: " + "("+src.getWidth()+" , "+src.getHeight()+")");
            if(src.getWidth() < 40 || src.getHeight() < 40){
                return "The uploaded image size is too small. Please select an image with at least " +
                        "40 x 40 Pixels";
            } else {
                String format = FilenameUtils.getExtension(file.getOriginalFilename()).toLowerCase();
                try {
                    Files.deleteIfExists(Paths.get(UPLOADED_FOLDER + "/profile_pic_api_user_"+
                            api_user_id + "." + format));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                File destination = new File(UPLOADED_FOLDER + "//profile_pic_api_user_"+
                        api_user_id + ".png"); // something like C:/Users/tom/Documents/nameBasedOnSomeId.png
                ImageIO.write(src, "png", destination);
                return "Image successfully uploaded";
            }
        }
        return "Image is empty";
    }


    @RequestMapping(value = "/getImage/user/{api_user_id}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getImage(@PathVariable long api_user_id) throws IOException {
        Path path = Paths.get(UPLOADED_FOLDER + "/profile_pic_api_user_"+api_user_id + ".png");
        if(Files.exists(path)){
            byte[] bytes = Files.readAllBytes(path);

            final HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);

            return new ResponseEntity<byte[]> (bytes, headers, HttpStatus.CREATED);
        } else {
            Path pathNotExists = Paths.get(String.valueOf(this.getClass().getClassLoader().
                    getResource("no-image-available.png").getFile())
                    .replaceFirst("^/(.:/)", "$1"));
//            this.getClass().getClassLoader().getResource("no-image-available.png");
            byte[] bytesNotExists = Files.readAllBytes(pathNotExists);

            final HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);

            return new ResponseEntity<byte[]> (bytesNotExists, headers, HttpStatus.CREATED);
        }
    }

    private static BufferedImage scale(BufferedImage src, int w, int h)
    {
        BufferedImage img =
                new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        int x, y;
        int ww = src.getWidth();
        int hh = src.getHeight();
        int[] ys = new int[h];
        for (y = 0; y < h; y++)
            ys[y] = y * hh / h;
        for (x = 0; x < w; x++) {
            int newX = x * ww / w;
            for (y = 0; y < h; y++) {
                int col = src.getRGB(newX, ys[y]);
                img.setRGB(x, y, col);
            }
        }
        return img;
    }
}
