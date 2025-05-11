package org.example.final_project.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author nawaz
 */
public interface FileService {
	String uploadVideo(String path, MultipartFile video);
	String uploadImage(String path, MultipartFile image);

	boolean checkFile(String fileName) throws IOException;

	InputStream getResource(String path, String fileName) throws FileNotFoundException;

	void deleteProfilePicture(String imageName,String path);

}
