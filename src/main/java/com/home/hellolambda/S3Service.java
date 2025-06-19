package com.home.hellolambda;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.logging.LogLevel;

import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

public class S3Service {
	
	public static ResponseBytes<GetObjectResponse> getS3FileObject(String s3ObjectKey, String s3BucketName, LambdaLogger logger) {
		ResponseBytes<GetObjectResponse> objectResponse = null;
		try {
			GetObjectRequest objectRequest = GetObjectRequest.builder().key(s3ObjectKey).bucket(s3BucketName).build();
			objectResponse = AWSClientFactory.getS3Client().getObjectAsBytes(objectRequest);
		} catch(Exception ex) {
			logger.log("S3Service.getS3FileObject:: error: " + ex, LogLevel.ERROR);
		}
		return objectResponse;
	}
}
