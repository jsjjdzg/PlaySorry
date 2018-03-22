package com.dzg.PlaySorry.controller;

import java.nio.file.Paths;
import java.util.concurrent.ConcurrentMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.dzg.PlaySorry.entity.Subtitles;
import com.dzg.PlaySorry.service.GifService;
import com.dzg.PlaySorry.service.QcloudService;
import com.google.common.collect.Maps;

import io.swagger.annotations.ApiOperation;

/**
 * 
 * GIF生成入口Controller
 * 
 * @author DZG
 * @since V1.0 2018年3月22日
 */
@RestController
@RequestMapping(path = "/", consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = {
		MediaType.APPLICATION_JSON_VALUE }, method = RequestMethod.POST)
public class GifController {

	@Autowired
	GifService gifService;

	@Autowired
	QcloudService qcloudService;

	@ApiOperation(value = "获取gif", notes = "")
	@RequestMapping(path = "/gif/filePath", method = RequestMethod.POST)
	public ConcurrentMap<String, String> renderGifPath(@RequestBody Subtitles subtitles) {
		ConcurrentMap<String, String> map = Maps.newConcurrentMap();
		try {
			String file = gifService.renderGif(subtitles);
			String filePath = Paths.get(file).getFileName().toString();
			if ("true".equals(qcloudService.getEnable())) {
				filePath = qcloudService.upload(file);
			}
			map.put("code", "0");
			map.put("result", filePath);
		} catch (Exception e) {
			map.put("code", "1");
			map.put("result", e.getMessage());
		}
		return map;
	}

	@ApiOperation(value = "获取gif", notes = "")
	@RequestMapping(path = "/gif/file", consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = {
			MediaType.MULTIPART_FORM_DATA_VALUE }, method = RequestMethod.POST)
	public ResponseEntity<Resource> renderGif(@RequestBody Subtitles subtitles) throws Exception {
		String file = gifService.renderGif(subtitles);
		Resource resource = new FileSystemResource(file);
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=txtx.gif")
				.body(resource);
	}
}
