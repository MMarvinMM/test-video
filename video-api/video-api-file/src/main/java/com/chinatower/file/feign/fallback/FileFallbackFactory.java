package com.chinatower.file.feign.fallback;

import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.common.core.web.domain.ApiResult;
import com.chinatower.common.entity.Result;
import com.chinatower.file.feign.client.FileFeignClient;
import com.chinatower.file.request.FileObsEncryptRequest;
import com.chinatower.file.request.SysFileRequest;
import com.chinatower.file.request.SysFileUrlRequest;
import com.chinatower.file.response.FileObsEncryptResponse;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class FileFallbackFactory implements FallbackFactory<FileFeignClient> {

    private static final String REQ_UPLOAD_FILE_ERROR = "调用video-file服务文件上传请求失败";
    private static final String REQ_DOWN_FILE_ERROR = "调用video-file服务文件下载请求失败";

    @Override
    public FileFeignClient create(Throwable throwable) {
        log.error("文件服务调用失败:{}", throwable.getMessage());
        return new FileFeignClient() {
            @Override
            public AjaxResult uploadFile(MultipartFile file) {
                return AjaxResult.error(REQ_UPLOAD_FILE_ERROR);
            }

            @Override
            public AjaxResult uploadFileToOFS(MultipartFile file) {
                return AjaxResult.error(REQ_UPLOAD_FILE_ERROR);
            }

            @Override
            public Map<String, Object> uploadEditorFile(MultipartFile file) {
                log.info(REQ_UPLOAD_FILE_ERROR);
                return null;
            }

            @Override
            public Result uploadFile2Video(MultipartFile file) {
                return Result.fail(REQ_UPLOAD_FILE_ERROR);
            }

            @Override
            public void fileDownload(String fileName, Boolean delete, HttpServletResponse response, HttpServletRequest request) {
                log.info(REQ_DOWN_FILE_ERROR);
            }

//            @Override
//            public void downloadById(String fileId, HttpServletResponse response) {
//                log.info(REQ_DOWN_FILE_ERROR);
//            }

            @Override
            public void downloadByUrl(String key, HttpServletResponse response) {
                log.info(REQ_DOWN_FILE_ERROR);
            }

            @Override
            public AjaxResult syncNoticeData() {
                return AjaxResult.error("调用video-file服务通知管理请求失败");
            }

//            @Override
//            public Result<SysFileInfoResponse> downloadById2Video(String fileId) {
//                return Result.fail(REQ_DOWN_FILE_ERROR);
//            }

            @Override
            public Result<Object> downloadByUrl(String url) {
                return Result.fail(REQ_DOWN_FILE_ERROR);
            }

            @Override
            public Result<String> getSeqByName(String name, String number) {
                return Result.fail("调用video-file服务获取序列值失败");
            }

            @Override
            public Result<String> getObsFileUrlEncrypt(String fileUrl) {
                return Result.fail("调用video-file服务加密失败");
            }
            @Override
            public ApiResult<List<FileObsEncryptResponse>> getObsEncryptUrls(FileObsEncryptRequest fileObsEncryptRequest) {
                return null;
            }

            @Override
            public Map<String, String> encryptBatch(List<String> urlList) {
                return null;
            }

            @Override
            public Object genericEncryptBatch(Object obj) {
                return null;
            }

            @Override
            public Result<String> getFileUrlByFileName(SysFileRequest request) {
                return Result.fail("调用video-file服务根据文件名称获取文件地址失败");
            }

            @Override
            public Result<String> getFileUrlByOldUrl(SysFileUrlRequest request) {
                return Result.fail("调用video-file服务根据文件old路径获取文件地址失败");
            }
            @Override
            public AjaxResult uploadNoLogin(MultipartFile file) {
                return AjaxResult.error(REQ_UPLOAD_FILE_ERROR);
            }
        };
    }
}
