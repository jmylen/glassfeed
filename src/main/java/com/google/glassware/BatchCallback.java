package com.google.glassware;

import java.io.IOException;
import java.util.logging.Logger;

import com.google.api.client.googleapis.batch.json.JsonBatchCallback;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.http.HttpHeaders;
import com.google.api.services.mirror.model.TimelineItem;

/**
 * Private class to process batch request results.
 * <p/>
 * For more information, see
 * https://code.google.com/p/google-api-java-client/wiki/Batch.
 */
public final class BatchCallback extends JsonBatchCallback<TimelineItem> {
	private static final Logger LOG = Logger.getLogger(BatchCallback.class
			.getSimpleName());
	int success = 0;
	int failure = 0;

	@Override
	public void onSuccess(TimelineItem item, HttpHeaders headers)
			throws IOException {
		++success;
	}

	@Override
	public void onFailure(GoogleJsonError error, HttpHeaders headers)
			throws IOException {
		++failure;
		LOG.info("Failed to insert item: " + error.getMessage());
	}
}