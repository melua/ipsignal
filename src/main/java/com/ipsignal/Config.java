package com.ipsignal;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.logging.Level;

import javax.inject.Singleton;

import org.apache.commons.io.IOUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import lombok.Getter;
import lombok.extern.java.Log;

@Log
@Getter
@Singleton
public final class Config {
	
	@ConfigProperty(name="version")
	String version;
	
	@ConfigProperty(name="serve.url", defaultValue="http://localhost")
	URI serviceUrl;
	
	@ConfigProperty(name="read.ids", defaultValue="")
	String readOnlyIds;
	
	@ConfigProperty(name="smtp.host", defaultValue="localhost")
	String smtpHost;
	
	@ConfigProperty(name="smtp.port", defaultValue="25")
	Integer smtpPort;
	
	@ConfigProperty(name="memc.host", defaultValue="localhost")
	String memcHost;
	
	@ConfigProperty(name="memc.port", defaultValue="11211")
	Integer memcPort;
	
	@ConfigProperty(name="memc.time", defaultValue="0")
	Integer memcTime;
	
	@ConfigProperty(name="filer.path")
	String filerPath;
	
	@ConfigProperty(name="jobs.skip", defaultValue="false")
	boolean disableJobs;
	
	@ConfigProperty(name="dele.skip", defaultValue="false")
	boolean disablePurges;
	
	@ConfigProperty(name="serve.brand")	
	String brandName;
	
	@ConfigProperty(name="mail.noreply", defaultValue="noreply@localhost.lan")	
	String noReplyMail;
	
	@ConfigProperty(name="mail.alert", defaultValue="alert@localhost.lan")
	String alertMail;
	
	@ConfigProperty(name="mail.premium", defaultValue="premium@localhost.lan")
	String premiumMail;
	
	private String crudCreateContent = getContent("/crud/create.txt");
	private String crudUpdateContent = getContent("/crud/update.txt");
	private String crudeDeleteContent = getContent("/crud/delete.txt");
	
	private String alertNotificationContent = getContent("/alert/notification.txt");
	private String alertCertificateContent = getContent("/alert/certificate.txt");
	
	private String premiumExpiredContent = getContent("/premium/expired.txt");	
	private String premiumSoonContent = getContent("/premium/soon.txt");
	
	private String unsubscribeNotificationHtml = getContent("/unsubscribe/notification.html");	
	private String unsubscribeCertificateHtml = getContent("/unsubscribe/certificate.html");
	
	/**
	 * Load content from the given path
	 * @param path where content are stored
	 * @return the content
	 */
	private static String getContent(String path) {
		String value = null;
		try {		
			value = IOUtils.toString(Config.class.getResourceAsStream(path), Charset.defaultCharset());
		} catch (IOException ioex) {
			LOGGER.log(Level.SEVERE, "Invalid content \"{0}\"", path);
		}
		return value;
	}
	
}