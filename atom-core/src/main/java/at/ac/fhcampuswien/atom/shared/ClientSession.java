/* ATOM - Advanced Transparent Object Manager
 * Copyright © Vienna, Austria 2014 by FH Campus Wien 
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.ac.fhcampuswien.atom.shared;

import java.util.Date;
import java.util.HashMap;

import at.ac.fhcampuswien.atom.shared.domain.StoreableUser;

public class ClientSession implements java.io.Serializable {

	private static final long serialVersionUID = -3144447134145060164L;
	private long sessionId;
	private String sessionToken;
	private String validForRemoteAdress;
	private String cookieValue;

	private int userId;
	private String username;
	private String userFirstName;
	private String userLastName;
	private String userSex;
	private String userType;

	private String userEmail;
	private Date userDateOfBirth;
	private Date userLastLoginDate;
	private String userCity;
	private String userCountry;

	private HashMap<Integer, String> userRoles;

	private StoreableUser user;
	
	private transient Date cacheDate;

	/** required for GWT Serializability */
	private ClientSession() {
		cacheDate = new Date();
	}

	public ClientSession(long sessionId, String sessionToken, String cookieValue, String validForRemoteAdress, int userId,
			String username, String userFirstName, String userLastName, String userSex, String userType, String userEmail,
			Date userDateOfBirth, Date userLastLoginDate, String userCity, String userCountry) {
		this();
		this.sessionId = sessionId;
		this.sessionToken = sessionToken;
		this.cookieValue = cookieValue;
		this.validForRemoteAdress = validForRemoteAdress;
		this.userId = userId;
		this.username = username;
		this.userFirstName = userFirstName;
		this.userLastName = userLastName;
		this.userSex = userSex;
		this.userType = userType;
		this.userEmail = userEmail;
		this.userDateOfBirth = userDateOfBirth;
		this.userLastLoginDate = userLastLoginDate;
		this.userCity = userCity;
		this.userCountry = userCountry;
	}

	public ClientSession(long sessionId, String sessionToken, String validForRemoteAdress) {
		this();
		this.sessionId = sessionId;
		this.sessionToken = sessionToken;
		this.validForRemoteAdress = validForRemoteAdress;
	}

	public ClientSession(long sessionId, String sessionToken, String validForRemoteAdress, String username) {
		this(sessionId, sessionToken, validForRemoteAdress);
		this.username = username;
	}

	public long getSessionId() {
		return sessionId;
	}

	public String getSessionToken() {
		return sessionToken;
	}

	public String getValidForRemoteAdress() {
		return validForRemoteAdress;
	}

	public void setValidForRemoteAdress(String validForRemoteAdress) {
		this.validForRemoteAdress = validForRemoteAdress;
	}

	public String getCookieValue() {
		return cookieValue;
	}

	public int getUserId() {
		return userId;
	}

	public String getUsername() {
		return username;
	}

	public String getUserFirstName() {
		return userFirstName;
	}

	public String getUserLastName() {
		return userLastName;
	}

	public String getUserSex() {
		return userSex;
	}

	public String getUserType() {
		return userType;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public Date getUserDateOfBirth() {
		return userDateOfBirth;
	}

	public Date getUserLastLoginDate() {
		return userLastLoginDate;
	}

	public String getUserCity() {
		return userCity;
	}

	public String getUserCountry() {
		return userCountry;
	}

	public void addRole(int id, String name) {
		if (userRoles == null)
			userRoles = new HashMap<Integer, String>();
		userRoles.put(id, name);
	}

	public HashMap<Integer, String> getRoles() {
		return userRoles;
	}

//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see java.lang.Object#equals(java.lang.Object)
//	 */
//	@Override
//	public boolean equals(Object obj) {
//
//		if (sessionIdsCleared)
//			return super.equals(obj);
//
//		try {
//			ClientSession otherClientSession = (ClientSession) obj;
//			return (this.sessionId == otherClientSession.sessionId
//					&& ((this.sessionToken == null && otherClientSession.sessionToken == null) || (this.sessionToken
//							.equals(otherClientSession.sessionToken))) && ((this.validForRemoteAdress == null && otherClientSession.validForRemoteAdress == null) || (this.validForRemoteAdress
//					.equals(otherClientSession.validForRemoteAdress))));
//		} catch (Throwable t) {
//			return false;
//		}
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see java.lang.Object#hashCode()
//	 */
//	@Override
//	public int hashCode() {
//		if (sessionIdsCleared)
//			return super.hashCode();
//		String combinedValue = (Long.toString(sessionId) + sessionToken + validForRemoteAdress);
//		return combinedValue.hashCode();
//	}

	public StoreableUser getUser() {
		return user;
	}

	public void setUser(StoreableUser user) {
		this.user = user;
	}

	public void clearSessionIds() {
		this.cookieValue = null;
		this.sessionId = -1;
		this.sessionToken = null;
	}

	public Date getCacheDate() {
		return cacheDate;
	}
	
	public boolean isStillValid() {
		// 1000 ms = 1 sec *60 = 1 minute *60 = 1 hour
		Date oldestAllowed = new Date((new Date()).getTime() - (1000 * 60 * 60));
		if (cacheDate.before(oldestAllowed)) {
			return false;
		}
		return true;
	}
}
