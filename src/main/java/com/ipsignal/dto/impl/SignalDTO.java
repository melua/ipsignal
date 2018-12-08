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

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class SignalDTO extends Restrictive implements DTO {
	
	public static final byte T_URL = 0x01;
	public static final byte T_CERTIFICATE = 0x02;
	public static final byte T_LATENCY = 0x03;
	public static final byte T_PATH = 0x04;
	public static final byte T_EXPECTED = 0x05;
	public static final byte T_EMAIL = 0x06;
	public static final byte T_BROWSER = 0x07;
	public static final byte T_NOTIFY = 0x08;
	public static final byte T_INTERVAL = 0x09;
	public static final byte T_RETENTION = 0x0f;

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
	
	private WhoisDTO whois;

	private List<LogDTO> logs;

}
