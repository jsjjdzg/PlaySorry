package com.dzg.PlaySorry.service;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import com.dzg.PlaySorry.entity.Subtitles;
import com.google.common.base.Splitter;

import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * 生成GIf的实现类
 * 
 * @author DZG
 * @since V1.0 2018年3月23日
 */
@Service
@Getter
@Setter
@ConfigurationProperties(prefix = "cache.template")
public class GifService {

	private static final Logger logger = LoggerFactory.getLogger(GifService.class);

	private String tempPath;

	public String getTempPath() {
		return tempPath;
	}

	public void setTempPath(String tempPath) {
		this.tempPath = tempPath;
	}

	/**
	 * 生成Gif图片，并贴上字幕
	 * 
	 * @since 2018年3月23日
	 */
	public String renderGif(Subtitles subtitles) {
		String assPath = renderAss(subtitles);
		String gifPath = Paths.get(tempPath).resolve(UUID.randomUUID() + ".gif").toString();
		String videoPath = Paths.get(tempPath).resolve(subtitles.getTemplateName() + "/template.mp4").toString();
		String cmd = String.format("ffmpeg -i %s -r 6 -vf ass=%s,scale=300:-1 -y %s", videoPath, assPath, gifPath);
		if ("simple".equals(subtitles.getMode())) {
			cmd = String.format("ffmpeg -i %s -r 5 -vf ass=%s,scale=180:-1 -y %s ", videoPath, assPath, gifPath);
		}
		logger.info("cmd: {}", cmd);
		try {
			Process exec = Runtime.getRuntime().exec(cmd);
			exec.waitFor();
		} catch (Exception e) {
			logger.error("生成gif报错：{}", e);
		}
		return gifPath;
	}

	/**
	 * 生成字幕ass文件
	 * 
	 * @since 2018年3月23日
	 */
	private String renderAss(Subtitles subtitles) {
		Path path = Paths.get(tempPath).resolve(UUID.randomUUID().toString().replace("-", "") + ".ass");
		Configuration cfg = new Configuration(Configuration.VERSION_2_3_27);
		cfg.setDefaultEncoding("UTF-8");
		try {
			cfg.setDirectoryForTemplateLoading(Paths.get(tempPath).resolve(subtitles.getTemplateName()).toFile());
		} catch (IOException e1) {
			logger.error("设置模版载入文件报错", e1);
		}
		Map<String, Object> root = new HashMap<>();
		Map<String, String> mx = new HashMap<>();
		List<String> list = Splitter.on(",").splitToList(subtitles.getSentence());
		for (int i = 0; i < list.size(); i++) {
			mx.put("sentences" + i, list.get(i));
		}
		root.put("mx", mx);
		Template temp = null;
		try {
			temp = cfg.getTemplate("template.ftl");
		} catch (IOException e1) {
			logger.error("获取模版保存", e1);
		}
		try (FileWriter writer = new FileWriter(path.toFile())) {
			temp.process(root, writer);
		} catch (Exception e) {
			logger.error("生成ass文件报错", e);
		}
		return path.toString();
	}

}
