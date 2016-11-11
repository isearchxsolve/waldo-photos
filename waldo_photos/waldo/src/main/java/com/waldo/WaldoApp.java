package com.waldo;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.waldo.EXIFData.EXIFDirectory;
import com.waldo.EXIFData.EXIFTag;

/**
 * This class has the main extraction feature of ECIF data from Amazon.
 * Should be moved to an interface and a factory should instead be used.
 * Because the amazon s3 store could be shifted to some other repository.
 * In that case implementation will change.
 * Also packaging of the application should be more modular and each package
 * should contain related classes
 * It will be more better to expose a rest api using libraries such as jax rs 
 * to perform the query, store and extract operations.
 * Also JUnit Tests containing some failure and success scenarios would be
 * appropriate which is missing right now.
 * @author Kunal
 *
 */
public class WaldoApp {
	//should externalise to a properties file
	private static final String WALDO_BUCKET = "waldo-recruiting";
	
	//Should have some util class for logging in order to encapsulate the 
	//logging logic and library used
	private final static Logger LOGGER = Logger.getLogger(WaldoApp.class.getName());
	
	//A factory for store would be more appropriate
	private final WaldoStoreI waldoStore = new WaldoStore();

	//Here we have used ehcache for storing the exif data taking java object as the 
	//primary element.However for portability json should be used as the stored element
	//and document store such as mongo db would be more appropriate
	public void extract() {
		AmazonS3 s3 = new AmazonS3Client();
		ObjectListing objectListing = s3.listObjects(new ListObjectsRequest().withBucketName(WALDO_BUCKET));
		for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
			LOGGER.log(Level.INFO, objectSummary.getKey());
			S3Object s3object = s3.getObject(new GetObjectRequest(WALDO_BUCKET, objectSummary.getKey()));
			createDoc(objectSummary, s3object);
		}
	}

	private void createDoc(S3ObjectSummary objectSummary, S3Object s3object) {
		try (InputStream inputStrm = s3object.getObjectContent()) {
			Metadata metadata = ImageMetadataReader.readMetadata(inputStrm);
			EXIFData exifData = new EXIFData(objectSummary.getKey(), new ArrayList<>());
			for (Directory dir : metadata.getDirectories()) {
				exifData.addDirectory(createDirectory(dir));
			}
			waldoStore.storeDoc(exifData);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	private static EXIFDirectory createDirectory(Directory dir) {
		EXIFDirectory exifDir = new EXIFDirectory(dir.getName(), new ArrayList<>());
		for (Tag tag : dir.getTags()) {
			exifDir.addTag(createTag(tag));
		}
		return exifDir;
	}

	private static EXIFTag createTag(Tag tag) {
		LOGGER.log(Level.INFO, "Directory " + tag.getDirectoryName());
		LOGGER.log(Level.INFO, "Type " + tag.getTagType());
		LOGGER.log(Level.INFO, "Name " + tag.getTagName());
		LOGGER.log(Level.INFO, "Desc " + tag.getDescription());
		EXIFTag exifTag = new EXIFTag(tag.getTagType(), tag.getTagName(), tag.getDescription());
		return exifTag;
	}

	public static void main(String[] args) {
		WaldoApp app = new WaldoApp();
		app.extract();
	}
}
