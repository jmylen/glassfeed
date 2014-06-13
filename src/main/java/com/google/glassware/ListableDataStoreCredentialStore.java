/*
 * Copyright (C) 2013 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.glassware;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.CredentialStore;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A new credential store persisted in datastore.
 */
public class ListableDataStoreCredentialStore implements CredentialStore {

	/**
	 * Lock on access to the store.
	 */
	private final Lock lock = new ReentrantLock();

	/**
	 * Store of memory persisted credentials, indexed by userId.
	 */
	private final Map<String, DataStorePersistedCredential> store = new HashMap<String, DataStorePersistedCredential>();

	public void store(String userId, Credential credential) {
		lock.lock();
		try {
			DataStorePersistedCredential item = store.get(userId);
			if (item == null) {
				item = new DataStorePersistedCredential();
				store.put(userId, item);
			}
			item.store(credential);
		} finally {
			lock.unlock();
		}
	}

	public void delete(String userId, Credential credential) {
		lock.lock();
		try {
			store.remove(userId);
		} finally {
			lock.unlock();
		}
	}

	public boolean load(String userId, Credential credential) {
		lock.lock();
		try {
			DataStorePersistedCredential item = store.get(userId);
			if (item != null) {
				item.load(credential);
			}
			return item != null;
		} finally {
			lock.unlock();
		}
	}

	/**
	 * returns all users stored. List is guaranteed to have no duplicates.
	 * 
	 * @return
	 */
	public List<String> listAllUsers() {
		List<String> allUsers = new ArrayList<String>();
		for (Entry<String, DataStorePersistedCredential> credEntry : store.entrySet()) {
			allUsers.add(credEntry.getKey());
		}
		return allUsers;
	}

	class DataStorePersistedCredential {

		/**
		 * Access token or {@code null} for none.
		 */
		private String accessToken;

		/**
		 * Refresh token {@code null} for none.
		 */
		private String refreshToken;

		/**
		 * Expiration time in milliseconds {@code null} for none.
		 */
		private Long expirationTimeMillis;

		/**
		 * Store information from the credential.
		 * 
		 * @param credential
		 *            credential whose {@link Credential#getAccessToken access
		 *            token}, {@link Credential#getRefreshToken refresh token},
		 *            and {@link Credential#getExpirationTimeMilliseconds
		 *            expiration time} need to be stored
		 */
		void store(Credential credential) {
			Key guestbookKey = KeyFactory.createKey("Credential", "bob");
			Date date = new Date();
			Entity credentialEntity = new Entity("Greeting", guestbookKey);
			credentialEntity.setProperty("user", "user");
			credentialEntity.setProperty("date", date);
			credentialEntity.setProperty("token", credential.getAccessToken());
			credentialEntity.setProperty("refreshToken", credential.getRefreshToken());
			credentialEntity.setProperty("expirationTimeMillis", credential.getExpirationTimeMilliseconds());

			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			Key credentialKey = datastore.put(credentialEntity);
			System.out.println(credentialKey.getId() + " " + credentialKey.getName());
			try {
				Entity storedEntity = datastore.get(credentialKey);
				for (Entry<String, Object> e : storedEntity.getProperties().entrySet()) {
					System.out.println(e.getKey() + " " + e.getValue());
				}
			} catch (EntityNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			accessToken = credential.getAccessToken();
			refreshToken = credential.getRefreshToken();
			expirationTimeMillis = credential.getExpirationTimeMilliseconds();
		}

		/**
		 * Load information into the credential.
		 * 
		 * @param credential
		 *            credential whose {@link Credential#setAccessToken access
		 *            token}, {@link Credential#setRefreshToken refresh token},
		 *            and {@link Credential#setExpirationTimeMilliseconds
		 *            expiration time} need to be set if the credential already
		 *            exists in storage
		 */
		void load(Credential credential) {
			credential.setAccessToken(accessToken);
			credential.setRefreshToken(refreshToken);
			credential.setExpirationTimeMilliseconds(expirationTimeMillis);
		}
	}
}