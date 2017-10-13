/*
* WebTemplate 1.0
* Luca Vercelli 2017
* Released under MIT license 
*/
package com.example.myapp.login.helpers;

//taken from https://stackoverflow.com/questions/390150/authenticating-against-active-directory-with-java-on-linux

import com.sun.jndi.ldap.LdapCtxFactory;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Iterator;
import javax.naming.Context;
import javax.naming.AuthenticationException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import static javax.naming.directory.SearchControls.SUBTREE_SCOPE;

import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.GrantedAuthorityImpl;

public class LdapTest {

	public static void main(String[] args) {

		if (args.length != 4 && args.length != 2) {
			System.out.println("Purpose: authenticate user against Active Directory and list group membership.");
			System.out.println("Usage: App2 <username> <password> <domain> <server>");
			System.out.println("Short usage: App2 <username> <password>");
			System.out.println("(short usage assumes 'xyz.tld' as domain and 'abc' as server)");
			System.exit(1);
		}

		String domainName;
		String serverName;

		if (args.length == 4) {
			domainName = args[2];
			serverName = args[3];
		} else {
			domainName = "xyz.tld";
			serverName = "abc";
		}

		String username = args[0];
		String password = args[1];

		System.out
				.println("Authenticating " + username + "@" + domainName + " through " + serverName + "." + domainName);

		// bind by using the specified username/password
		Hashtable<String, String> props = new Hashtable<String, String>();
		String principalName = username + "@" + domainName;
		props.put(Context.SECURITY_PRINCIPAL, principalName);
		props.put(Context.SECURITY_CREDENTIALS, password);
		DirContext context;

		try {
			context = LdapCtxFactory.getLdapCtxInstance("ldap://" + serverName + "." + domainName + '/', props);
			System.out.println("Authentication succeeded!");

			// locate this user's record
			SearchControls controls = new SearchControls();
			controls.setSearchScope(SUBTREE_SCOPE);
			NamingEnumeration<SearchResult> renum = context.search(toDC(domainName),
					"(& (userPrincipalName=" + principalName + ")(objectClass=user))", controls);
			if (!renum.hasMore()) {
				System.out.println("Cannot locate user information for " + username);
				System.exit(1);
			}
			SearchResult result = renum.next();

			List<GrantedAuthority> groups = new ArrayList<GrantedAuthority>();
			Attribute memberOf = result.getAttributes().get("memberOf");
			if (memberOf != null) {// null if this user belongs to no group at
									// all
				for (int i = 0; i < memberOf.size(); i++) {
					Attributes atts = context.getAttributes(memberOf.get(i).toString(), new String[] { "CN" });
					Attribute att = atts.get("CN");
					groups.add(new GrantedAuthorityImpl(att.get().toString()));
				}
			}

			context.close();

			System.out.println();
			System.out.println("User belongs to: ");
			Iterator<GrantedAuthority> ig = groups.iterator();
			while (ig.hasNext()) {
				System.out.println("   " + ig.next().toString());
			}

		} catch (AuthenticationException a) {
			System.out.println("Authentication failed: " + a);
			System.exit(1);
		} catch (NamingException e) {
			System.out.println("Failed to bind to LDAP / get account information: " + e);
			System.exit(1);
		}
	}

	private static String toDC(String domainName) {
		StringBuilder buf = new StringBuilder();
		for (String token : domainName.split("\\.")) {
			if (token.length() == 0)
				continue; // defensive check
			if (buf.length() > 0)
				buf.append(",");
			buf.append("DC=").append(token);
		}
		return buf.toString();
	}

}