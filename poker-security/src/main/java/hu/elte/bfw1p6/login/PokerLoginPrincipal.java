package hu.elte.bfw1p6.login;

import java.security.Principal;

public class PokerLoginPrincipal implements Principal{
	
	private String username;
	
	public PokerLoginPrincipal(String username) {
		this.username = username;
	}

	@Override
	public String getName() {
		return username;
	}

}
