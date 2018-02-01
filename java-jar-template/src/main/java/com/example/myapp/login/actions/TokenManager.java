/*
* WebTemplate 1.0
* Luca Vercelli 2017
* Released under MIT license 
*/
package com.example.myapp.login.actions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Key;
import java.util.Date;
import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.slf4j.Logger;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.MacProvider;

/**
 * Manager (EJB) for token-based (JWT) authentications. This class require that a
 * secret key is stored in a "server.key" file. You can use generateSecretKey().
 * Currently implemented through JJWT.
 *
 */
// TODO use keystore instead of file
@Stateless
public class TokenManager {

	/**
	 * The server private key used for signing and encoding messages.
	 */
	public static Key serverKey = null;

	/**
	 * Validita' del token in ms
	 */
	public static final long DELAY = 24 * 3600 * 1000; // 1 day

	@Inject
	Logger LOG;

	@PostConstruct
	public void postConstructEJB() {
		// either generate or load:
		// serverKey = generateSecretKey();
		serverKey = loadSecretKey();
	}

	/**
	 * Issue a JWT token associated to given userid , then record it to
	 * database.
	 * 
	 * @see https://github.com/jwtk/jjwt
	 */
	public String issueToken(String userId) {

		Date now = new Date();

		String compactJwts = Jwts.builder().setSubject(userId).setIssuedAt(now)
				.setExpiration(new Date(now.getTime() + DELAY)).signWith(SignatureAlgorithm.HS512, serverKey).compact();

		return compactJwts;
	}

	/**
	 * Check if it was issued by the server and if it's not expired.
	 * 
	 * @see https://github.com/jwtk/jjwt
	 * @param token
	 * @throws Exception
	 * @return true if token is valid
	 */
	public boolean validateToken(String token, String callerIP) {

		Jws<Claims> jws;

		try {
			jws = Jwts.parser().setSigningKey(serverKey).parseClaimsJws(token);
		} catch (Exception e) {
			return false;
		}

		// OK, we can trust this JWT (well, JWS). What about its content?

		Claims claims = jws.getBody();

		if (claims.getExpiration() != null && claims.getExpiration().before(new Date())) {
			return false;
		}

		return true;
	}

	/**
	 * The new key is a random sequence generated through
	 * MacProvider.generateKey(), to be used for SHA-256.
	 * 
	 * Use this in local to generate key, then save key into server.
	 * 
	 * @throws IOException
	 * @see https://stackoverflow.com/questions/11410770
	 */
	public Key generateSecretKey() {
		String filename = "server.key";
		Key key = MacProvider.generateKey();
		FileOutputStream fos;
		try {
			File f = new File(filename).getAbsoluteFile();
			System.out.println("Writing server key to " + f.getPath());
			fos = new FileOutputStream(f, false);

			try {
				fos.write(key.getEncoded());
			} finally {
				fos.close();
			}
			return key;

		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @throws IOException
	 * @see https://stackoverflow.com/questions/11410770
	 */
	public SecretKey loadSecretKey() {
		String filename = "server.key";
		byte[] keyBytes;
		try {
			keyBytes = Files.readAllBytes(Paths.get(filename));
			return new SecretKeySpec(keyBytes, "SHA-512");
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
