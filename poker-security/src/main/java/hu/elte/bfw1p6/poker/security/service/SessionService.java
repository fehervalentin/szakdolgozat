package hu.elte.bfw1p6.poker.security.service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SessionService {
	
	private Map<UUID, String> authenticatedUsers;
	
	public SessionService() {
		this.authenticatedUsers = new HashMap<>();
	}
	
	public boolean isAuthenticated(UUID uuid) {
		return authenticatedUsers.containsKey(uuid);
	}
	
	public UUID authenticate(String username, String password) {
		//TODO: a service nyúlkálhat a DAO-hoz: password ellenőrzés!
		invalidate(username);
		UUID uuid = UUID.randomUUID();
		authenticatedUsers.put(uuid, username);
		return uuid;
	}
	
	public void invalidate(UUID uuid) {
		authenticatedUsers.remove(uuid);
	}
	
	private void invalidate(String username) {
		authenticatedUsers.values().remove(username);
	}
}
