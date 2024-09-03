/**
 * 
 */
package com.frontier.shishya.distributed.testing;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.FilenameUtils;

/**
 * @author mlcs05
 *
 */
public class FileUpload implements Serializable {

	private static final long serialVersionUID = 5406980946784523655L;

	private final byte[] fileData;
	
	private final String extension;
	
	private final String name;
	
	public FileUpload(Path file) throws IOException {
		fileData = Files.readAllBytes(file);
		extension = FilenameUtils.getExtension(file.toString());
		name = FilenameUtils.getName(file.toString());
	}
	
	public String getExtension() {
		return extension;
	}
	
	public String getName() {
		return name;
	}
	
	public byte[] getFileData() {
		return fileData;
	}
}
