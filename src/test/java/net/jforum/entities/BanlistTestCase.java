package net.jforum.entities;

import junit.framework.TestCase;

/**
 * @author Rafael Steil
 * @version $Id$
 */
public class BanlistTestCase extends TestCase {
	public void testMatchIpUsingOnlyStarsExpectsFalse() {
		Banlist b = this.newBanlist(0, null, "*.*.*.*");
		assertFalse(b.matches(this.newBanlist(0, null, "172.55.7.2")));
	}
	
	public void testMatchIpUsingStarExpectsTrue2() {
		Banlist b = this.newBanlist(0, null, "172.*.7.2");
		assertTrue(b.matches(this.newBanlist(0, null, "172.55.7.2")));
	}
	
	public void testMatchIpUsingStarExpectsTrue() {
		Banlist b = this.newBanlist(0, null, "*.168.7.*");
		assertTrue(b.matches(this.newBanlist(0, null, "172.168.7.2")));
	}
	
	public void testMatchIpUsingStarExpectsFalse2() {
		Banlist b = this.newBanlist(0, null, "*.168.7.*");
		assertFalse(b.matches(this.newBanlist(0, null, "172.168.1.2")));
	}
	
	public void testMatchIpUsingStarExpectsFalse() {
		Banlist b = this.newBanlist(0, null, "192.168.7.*");
		assertFalse(b.matches(this.newBanlist(0, null, "192.168.1.2")));
	}
	
	public void testMatchIpUsingDifferentLengthExpectsFalse() {
		Banlist b = this.newBanlist(0, null, "192.168.7");
		assertFalse(b.matches(this.newBanlist(0, null, "192.168.1.2")));
	}
	
	public void testMatchIpExpectsTrue() {
		Banlist b = this.newBanlist(0, "email@3", "192.168.1.1");
		assertTrue(b.matches(this.newBanlist(0, "email@2", "192.168.1.1")));
	}
	
	public void testMatchIpExpectsFalse() {
		Banlist b = this.newBanlist(0, null, "192.168.1.1");
		assertFalse(b.matches(this.newBanlist(0, null, "192.168.1.2")));
	}
	
	public void testMatchEmailExpectsTrue() {
		Banlist b = this.newBanlist(0, "email@2", null);
		assertTrue(b.matches(this.newBanlist(0, "email@2", null)));
	}
	
	public void testMatchEmailExpectsFalse() {
		Banlist b = this.newBanlist(0, "email@1", null);
		assertFalse(b.matches(this.newBanlist(0, "email@2", null)));
	}
	
	public void testMatchUserIdExpectsTrue() {
		Banlist b = this.newBanlist(2, null, null);
		assertTrue(b.matches(this.newBanlist(2, null, null)));
	}
	
	public void testMatchUserIdExpectsFalse() {
		Banlist b = this.newBanlist(1, null, null);
		assertFalse(b.matches(this.newBanlist(2, null, null)));
	}
	
	private Banlist newBanlist(int userId, String email, String ip) {
		Banlist b = new Banlist();
		
		b.setUserId(userId);
		b.setEmail(email);
		b.setIp(ip);
		
		return b;
	}
}
