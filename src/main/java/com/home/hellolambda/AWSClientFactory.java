package com.home.hellolambda;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

public class AWSClientFactory {
	private S3Client s3Client = S3Client.builder()
			.region(Region.of(System.getenv("aws_region")))
			.build();
	
	private static AWSClientFactory instance;
	private AWSClientFactory() {}
	
	public static AWSClientFactory getInstance() {
		if(instance == null) {
			instance = new AWSClientFactory();
		}
		return instance;
	}
	
	public S3Client getS3Client() {
		return s3Client;
	}
}
