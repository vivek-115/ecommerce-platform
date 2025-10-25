package com.example.ecomDemo.Service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService{

    @Override
    public String uploadImage(String path, MultipartFile image) throws IOException {
        String originalFilename=image.getOriginalFilename();
        String randomId=   UUID.randomUUID().toString();
        String encryptedFileName=  randomId.concat(originalFilename.substring(originalFilename.lastIndexOf('.')));

        String filePath=path+ File.separator+encryptedFileName;

        File folder=new File(path);
        if(!folder.exists()){
            folder.mkdir();
        }

        Files.copy(image.getInputStream(), Paths.get(filePath));

        return encryptedFileName;
    }

}
