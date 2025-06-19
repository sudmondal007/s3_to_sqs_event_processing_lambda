package com.home.hellolambda;

import java.util.List;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.lambda.runtime.logging.LogLevel;
import com.fasterxml.jackson.databind.ObjectMapper;

import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.eventnotifications.s3.model.S3EventNotification;
import software.amazon.awssdk.eventnotifications.s3.model.S3EventNotificationRecord;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

public class LambdaHandler implements RequestHandler<SQSEvent, String> {

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	
	private LambdaLogger logger = null;

	@Override
	public String handleRequest(SQSEvent sqsEvent, Context context) {
		// Set the logger instance for outbound logging context
		logger = context.getLogger();

		try {

			logger.log("Event recieved from SQS: " + OBJECT_MAPPER.writeValueAsString(sqsEvent), LogLevel.INFO);
			handleSQSEvent(sqsEvent);
			return "Request Processed";
		} catch (Exception e) {
			logger.log("[OutboundEventLambda] [handleRequest] Exception occured" + e.getMessage(), LogLevel.ERROR);
		}
		/* else if for AuroraDB trigger */
		return "Request Failed to Processed";
	}
	
	/**
	 * handle SQS Event
	 * @param event
	 * @throws Exception
	 */
	private void handleSQSEvent(SQSEvent event) throws Exception {
		for (SQSEvent.SQSMessage msg : event.getRecords()) {
			String sqsMessageBody = msg.getBody();
			if(sqsMessageBody != null) {
				S3EventNotification eventNotification = S3EventNotification.fromJson(sqsMessageBody);
				if(eventNotification != null) {
					List<S3EventNotificationRecord> notificationRecords = eventNotification.getRecords();
					if(notificationRecords != null && notificationRecords.size() > 0) {
						for (S3EventNotificationRecord notificationRecord : eventNotification.getRecords()) {
							processS3Files(notificationRecord);
						}
					}
				}
			} else {
				logger.log("[OutboundEventLambda] [handleSQSEvent] SQS message body is null", LogLevel.ERROR);
			}
		}
	}
	
	
	private void processS3Files(S3EventNotificationRecord notificationRecord) {
		if(notificationRecord != null && notificationRecord.getS3() != null) {
			String s3BucketName = notificationRecord.getS3().getBucket().getName();
			String s3objectKey = notificationRecord.getS3().getObject().getKey();
			
			logger.log("processS3Files :: s3BucketName= " + s3BucketName + "; s3objectKey=" + s3objectKey, LogLevel.INFO);
			
			// do further processing
			ResponseBytes<GetObjectResponse> objectResponse = S3Service.getInstance().getS3FileObject(s3objectKey, s3BucketName, logger);
			
			if(objectResponse != null) {
				byte[] objectBytes = objectResponse.asByteArray();
				if(objectBytes != null && objectBytes.length > 0) {
					logger.log("processS3Files :: filecontent= " + new String(objectBytes), LogLevel.INFO);
				}
			}
		}
	}
}
