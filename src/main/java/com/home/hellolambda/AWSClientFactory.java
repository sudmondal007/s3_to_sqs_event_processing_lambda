package com.home.hellolambda;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

public class AWSClientFactory {
	private static S3Client s3Client = S3Client.builder()
			.region(Region.of(System.getenv("aws_region")))
			.build();
	
	public static S3Client getS3Client() {
		return s3Client;
	}
}
