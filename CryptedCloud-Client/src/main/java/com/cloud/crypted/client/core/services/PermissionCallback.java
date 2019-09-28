package com.cloud.crypted.client.core.services;

import java.io.IOException;

import com.google.api.client.googleapis.batch.json.JsonBatchCallback;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.http.HttpHeaders;
import com.google.api.services.drive.model.Permission;

public class PermissionCallback extends JsonBatchCallback<Permission> {

	@Override
	public void onSuccess(Permission permission, HttpHeaders httpHeaders) throws IOException { }

	@Override
	public void onFailure(GoogleJsonError googleJsonError, HttpHeaders httpHeaders) throws IOException { }
	
}