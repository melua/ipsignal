package com.ipsignal.dto.impl;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.ipsignal.annotation.BrowserName;
import com.ipsignal.annotation.EmailAddress;
import com.ipsignal.annotation.Premium;
import com.ipsignal.annotation.Regexp;
import com.ipsignal.annotation.Reserved;
import com.ipsignal.annotation.UrlAddress;
import com.ipsignal.annotation.Xpath;
import com.ipsignal.dto.DTO;
import com.ipsignal.dto.Restrictive;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class SignalDTO extends Restrictive implements DTO {
	
	@NotNull
	@UrlAddress
	@Size(max=1000)
	private String url;
	
	@Reserved(numbers={7,15,30,60,90})
	@Premium(numbers={15,30,60,90})
	private Integer certificate;
	
	@Reserved(numbers={150,120,90,60,30,15})
	private Integer latency;
	
	@Xpath
	@Size(max=1000)
	private String path = "//*";
	
	@NotNull
	@Regexp
	@Size(max=255)
	private String expected;
	
	@NotNull
	@EmailAddress
	@Size(max=255)
	private String email;
	
	@BrowserName
	private String browser;
	
	@UrlAddress
	@Size(max=1000)
	private String notify;
	
	@Reserved(numbers={1440,720,360,120,60,30,20,10})
	@Premium(numbers={30,20,10})
	private Integer interval = 1440;
	
	@Reserved(numbers={3,25,50,75,100})
	@Premium(numbers={25,50,75,100})
	private Integer retention = 3;
	
	private String premium;

	private List<LogDTO> logs;
	
	public SignalDTO() {
		// for the marshaller
	}
	
	public SignalDTO(String url, Integer certificate, Integer latency, String path, String expected, String email, String browser, String premium, String notify,
			Integer interval, Integer retention) {
		this();
		this.url = url;
		this.certificate = certificate;
		this.latency = latency;
		this.path = path;
		this.expected = expected;
		this.email = email;
		this.browser = browser;
		this.premium = premium;
		this.notify = notify;
		this.interval = interval;
		this.retention = retention;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	public Integer getCertificate() {
		return certificate;
	}

	public void setCertificate(Integer certificate) {
		this.certificate = certificate;
	}
	
	public Integer getLatency() {
		return latency;
	}

	public void setLatency(Integer latency) {
		this.latency = latency;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getExpected() {
		return expected;
	}

	public void setExpected(String expected) {
		this.expected = expected;
	}
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getBrowser() {
		return browser;
	}

	public void setBrowser(String browser) {
		this.browser = browser;
	}

	public String getPremium() {
		return premium;
	}

	public void setPremium(String premium) {
		this.premium = premium;
	}

	public String getNotify() {
		return notify;
	}

	public void setNotify(String notify) {
		this.notify = notify;
	}

	public Integer getInterval() {
		return interval;
	}

	public void setInterval(Integer interval) {
		this.interval = interval;
	}

	public Integer getRetention() {
		return retention;
	}

	public void setRetention(Integer retention) {
		this.retention = retention;
	}

	public List<LogDTO> getLogs() {
		return logs;
	}

	public void setLogs(List<LogDTO> logs) {
		this.logs = logs;
	}

}
