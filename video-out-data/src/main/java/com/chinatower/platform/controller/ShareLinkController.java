package com.chinatower.platform.controller;

import com.chinatower.common.entity.Result;
import com.chinatower.platform.consumer.item.ShareLinkVO;
import com.chinatower.platform.consumer.service.ShareLinkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/link")
public class ShareLinkController {

    @Autowired
    private ShareLinkService shareLinkService;

    @PostMapping("/getShortUrl")
    public Result<String> getShortUrl(@RequestBody ShareLinkVO param) {
        String shortUrl = shareLinkService.getShortUrl(param);
        return Result.data(shortUrl);
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirectToLongUrl(@PathVariable String shortCode) {
        String longUrl = shareLinkService.getLongUrl(shortCode);

        if (longUrl == null) {
            return ResponseEntity.notFound().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.LOCATION, longUrl);
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

}
