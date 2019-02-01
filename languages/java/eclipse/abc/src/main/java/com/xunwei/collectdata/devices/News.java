package com.xunwei.collectdata.devices;
//import javax.persistence.*;

//@Entity
//@Table(name="news_tbl")
public class News extends Device {
//	@Id
//	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
	private Device device;
	private String title;
//	private String content;
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public Integer getId() {
		return this.id;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public void setDevice(Device device) {
		this.device = device;
	}
	
	public Device getDevice() {
		return this.device;
	}
	
//	public void setContent(String content) {
//		this.content = content;
//	}
//	
//	public String getContent() {
//		return this.content;
//	}
}
