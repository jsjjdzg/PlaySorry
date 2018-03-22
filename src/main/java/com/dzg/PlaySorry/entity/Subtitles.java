package com.dzg.PlaySorry.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by lizhihao on 2018/3/11.
 */
@Getter
@Setter
public class Subtitles {

	private String templateName;

	private String sentence;

	private String mode;

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public String getSentence() {
		return sentence;
	}

	public void setSentence(String sentence) {
		this.sentence = sentence;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

}
