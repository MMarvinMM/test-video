package com.chinatower.file.feign.client;

import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.common.core.web.domain.ApiResult;
import com.chinatower.common.entity.Result;
import com.chinatower.file.feign.fallback.FileFallbackFactory;
import com.chinatower.file.request.FileObsEncryptRequest;
import com.chinatower.file.request.SysFileRequest;
import com.chinatower.file.request.SysFileUrlRequest;
import com.chinatower.file.response.FileObsEncryptResponse;
import com.chinatower.system.api.resp.SysFileInfoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@FeignClient(contextId = "FileFeignClient", value = ServiceNameConstants.VIDEO_FILE_SERVICE, fallbackFactory = FileFallbackFactory.class)
public interface FileFeignClient {

    /**
     * 新增文件信息,视联使用
     * @apiNote 文件上传
     * @apiVersion 9.1
     * @tag 九、文件能力
     * @requestDemo
     * file: 文件
     *
     * @responseDemo
     * code枚举
     * 200  操作成功
     * 400	业务异常
     * 401	未认证
     * 404	服务未找到
     * 500	服务异常
     *  fileId: 文件id
     *  fileSize: 文件大小
     *  fileName: 文件名称
     *  fileUrl: 文件url
     *  fileSuffix: 文件后缀
     *  originUrl: 文件原始url
     *  proxyAddr: 文件代理url
     * {
     *     "msg": "操作成功",
     *     "code": 200,
     *     "data": {
     *         "creator": null,
     *         "createTime": null,
     *         "createBy": null,
     *         "updateBy": null,
     *         "modifier": null,
     *         "updateTime": null,
     *         "deleteFlag": null,
     *         "tenantId": null,
     *         "userId": null,
     *         "appVerCode": null,
     *         "industryCode": null,
     *         "platformCode": null,
     *         "dataScope": null,
     *         "criteriaList": null,
     *         "screenId": null,
     *         "screenType": null,
     *         "dataPermissions": null,
     *         "pageNum": null,
     *         "pageSize": null,
     *         "resourceProperty": null,
     *         "orderByColumn": null,
     *         "isAsc": null,
     *         "fileId": "12fc6b7a492a4c95ac666bbab56730ea",
     *         "fileClassify": null,
     *         "fileSize": "276486",
     *         "fileName": "ae1c-fyscsmu5399362.jpg",
     *         "fileUrl": "https://video-platform.obs.cn-north-4.myhuaweicloud.com/slyy2/dev/1.0/dab159cadbea9bb247a4a4dca032de3c/2eff5446551221d9bb6e3d5c49068696/858c19108d7b4d92ace09b1fee96dd29.jpg?AccessKeyId=CRFYWADDOJHXLKROOIXX&Expires=1747377049&Signature=rCpcexow527R9hTpNeaeX1FWQEE%3D",
     *         "fileSuffix": ".jpg",
     *         "delFlag": null,
     *         "deptId": null,
     *         "beginTime": null,
     *         "endTime": null,
     *         "originUrl": "https://video-platform.obs.cn-north-4.myhuaweicloud.com:443/slyy2/dev/1.0/dab159cadbea9bb247a4a4dca032de3c/2eff5446551221d9bb6e3d5c49068696/858c19108d7b4d92ace09b1fee96dd29.jpg",
     *         "proxyAddr": "https://video-platform.obs.cn-north-4.myhuaweicloud.com/slyy2/dev/1.0/dab159cadbea9bb247a4a4dca032de3c/2eff5446551221d9bb6e3d5c49068696/858c19108d7b4d92ace09b1fee96dd29.jpg?AccessKeyId=CRFYWADDOJHXLKROOIXX&Expires=1747294249&Signature=dt1DvrMGNxOEiwZ5YRFdF/SEsw8%3D",
     *         "orderBy": ""
     *     }
     * }
     *
     *
     * @return
     */
    @PostMapping(path ="/base/common/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AjaxResult uploadFile(@RequestPart("file")MultipartFile file);

    /**
     * 上传文件至ofs
     * @param file
     * @return
     */
    @PostMapping(path = "/base/common/uploadToOFS", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AjaxResult uploadFileToOFS(@RequestPart("file") MultipartFile file);

    /**
     * 通用上传请求
     * @apiNote 通用上传请求
     * @apiVersion 9.2
     * @tag 九、文件能力
     * @requestDemo
     * file: 文件
     *
     * @responseDemo
     *
     * data 成功后的url
     *
     *{
     *     "msg": [
     *         "上传成功"
     *     ],
     *     "errno": 0,
     *     "data": [
     *         "https://video-platform.obs.cn-north-4.myhuaweicloud.com/slyy2/dev/baseplatform/00609eb34240add94fd70d98485b5933/403017a7e91aeb49c2a90fb8273936d5/ce7606ba41e147978e3232bfbc8e29c2.jpg?AccessKeyId=CRFYWADDOJHXLKROOIXX&Expires=1736820039&Signature=BqjWViiWC//Eh9v2oBNRCr3%2B4LE%3D"
     *     ]
     * }
     * @return
     */
    @PostMapping(path="/base/common/editorUpload",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String,Object> uploadEditorFile(@RequestPart("file")MultipartFile file);

    /**
     * 新增文件信息,视联使用
     * @apiNote 新增文件信息,视联使用
     * @apiVersion 9.3
     * @tag 九、文件能力
     * @requestDemo
     * uploadFile: 文件
     *
     * @responseDemo
     * code枚举
     * 200  操作成功
     * 400	业务异常
     * 401	未认证
     * 404	服务未找到
     * 500	服务异常
     *  fileId: 文件id
     *  fileSize: 文件大小
     *  fileName: 文件名称
     *  fileUrl: 文件url
     *  fileSuffix: 文件后缀
     *  originUrl: 文件原始url
     *{
     *     "code": 200,
     *     "msg": "上传成功",
     *     "time": 1736819625835,
     *     "data": {
     *         "fileId": "8f29019be7ae4dfb9cbf03d3784e444c",
     *         "fileSize": "245529",
     *         "fileName": "xxxxx.jpg",
     *         "fileUrl": "https://video-platform.obs.cn-north-4.myhuaweicloud.com/slyy2/dev/baseplatform/00609eb34240add94fd70d98485b5933/403017a7e91aeb49c2a90fb8273936d5/65a83399c99b49e3af5c17a7dede4f6e.jpg?AccessKeyId=CRFYWADDOJHXLKROOIXX&Expires=1736820225&Signature=bCfEM73QY6/eNvCASUti7rzAxco%3D",
     *         "fileSuffix": ".jpg",
     *         "originUrl": "https://video-platform.obs.cn-north-4.myhuaweicloud.com:443/slyy2/dev/baseplatform/00609eb34240add94fd70d98485b5933/403017a7e91aeb49c2a90fb8273936d5/65a83399c99b49e3af5c17a7dede4f6e.jpg",
     *     }
     * }
     * @return
     */
    @PostMapping(path="/base/common/uploadFileVideo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result uploadFile2Video(@RequestPart("uploadFile") MultipartFile file);
    /**
     * 通用下载请求
     * @apiNote 通用下载请求
     * @apiVersion 9.4
     * @tag 九、文件能力
     * @requestDemo
     * fileName: 文件名称
     * delete: 是否删除 true 删除 false 不删除
     * @responseDemo
     * 文件流
     */
    @GetMapping("/base/common/download/name")
    public void fileDownload(@RequestParam("fileName")String fileName, @RequestParam("delete")Boolean delete, @RequestParam("response")HttpServletResponse response,  @RequestParam("request")HttpServletRequest request);

    /**
     * 根据文件url下载文件
     * @apiNote 根据文件url下载文件
     * @apiVersion 9.5
     * @tag 九、文件能力
     * @requestDemo
     * key: 字典配置值
     *
     * @responseDemo
     * 流文件
     */
    @GetMapping("/base/common/download/configkey/{key}")
    public void downloadByUrl(@PathVariable("key") String key, HttpServletResponse response);

    @GetMapping("/common/temp/syncNoticeData")
    public AjaxResult syncNoticeData();


    /**
     * 下载文件接口，林业使用
     * @param url
     * @requestDemo
     * url: 文件url
     *
     * @responseDemo
     * code枚举
     * 200  操作成功
     * 400	业务异常
     * 401	未认证
     * 404	服务未找到
     * 500	服务异常
     * data byte数组
     *{
     *     "code": 200,
     *     "msg": "上传成功",
     *     "time": 1736819625835,
     *     "data": {
     *         [asda21231123144k534krp3mk,f3rr34324423412]
     *     }
     * }
     */
    @GetMapping("/common/downloadByUrl")
    public Result<Object> downloadByUrl(@RequestParam("url") String url);

    /**
     * 获取序列值
     * @apiNote 获取序列值
     * @apiVersion 9.6
     * @tag 九、文件能力
     * @requestDemo
     * name 名称
     * number 数量
     * @responseDemo
     * code枚举
     * 200  操作成功
     * 400	业务异常
     * 401	未认证
     * 404	服务未找到
     * 500	服务异常
     *
     * data 所有序列号
     *{
     *     "code": 200,
     *     "msg": "请求成功",
     *     "time": 1736822857731,
     *     "data": "0,0,0,0,0"
     * }
     */
    @GetMapping("/common/getSeqByName/{name}/{number}")
    public Result<String> getSeqByName(@PathVariable("name") String name,@PathVariable("number") String number);

    /**
     * 对入参fileUrl进行obs加密返回
     * @apiNote 对入参fileUrl进行obs加密返回
     * @apiVersion 9.7
     * @tag 九、文件能力
     * @requestDemo
     * fileUrl: 文件url  必填
     * @responseDemo
     *
     * data 加密后的url
     * {
     *     "code": 200,
     *     "msg": "请求成功",
     *     "time": 1736922381232,
     *     "data": "https://slw-base-video-test.obs.cn-north-4.myhuaweicloud.com:443/test/videoBase/snapshotByNode/20250114/21060200001311124870_0/testzxzxzxzxzxz%20333333.jpg?AccessKeyId=6RTWXLU6OWVQWSKHXWTC&Expires=1737008781&Signature=Dwcl4duDNPjNYsQdhSEbYupRPuQ%3D"
     * }
     */
    @GetMapping("/common/getObsFileUrlEncrypt")
    public Result<String> getObsFileUrlEncrypt(@RequestParam("fileUrl") String fileUrl);

    /**
     * 获取加密后的OBS文件地址
     * @apiNote 获取加密后的OBS文件地址
     * @apiVersion 9.8
     * @tag 九、文件能力
     * @requestDemo
     * fileUrls 文件url  必填
     * useOfs 是否使用ofs  true  false
     * colonyCode 节点编码
     * @responseDemo
     *
     * fileUrl 原始文件地址
     * encryptUrl 加密后的地址
     *
     * {
     *     "returnCode": "0000",
     *     "returnMsg": "请求成功",
     *     "errorCode": null,
     *     "errorMsg": null,
     *     "data": [
     *         {
     *             "fileUrl": "/test/videoBase/snapshotByNode/20250114/21060200001311124870_0/testzxzxzxzxzxz+333333.jpg",
     *             "encryptUrl": "https://slw-base-video-test.obs.cn-north-4.myhuaweicloud.com:443/test/videoBase/snapshotByNode/20250114/21060200001311124870_0/testzxzxzxzxzxz%2B333333.jpg?AccessKeyId=6RTWXLU6OWVQWSKHXWTC&Expires=1737009048&Signature=rrQ%2BsZc0Le0Kw%2BiB3eCMyT901K0%3D"
     *         }
     *     ]
     * }
     */
    @PostMapping(value = "/file/obsFiles/getObsEncryptUrls/v2")
    public ApiResult<List<FileObsEncryptResponse>> getObsEncryptUrls(@RequestBody FileObsEncryptRequest fileObsEncryptRequest);

    /**
     * 批量获取加密后的OBS文件地址
     * @apiNote 批量获取加密后的OBS文件地址
     * @apiVersion 9.9
     * @tag 九、文件能力
     * @requestDemo
     * urlList 文件集合 必填
     * @responseDemo
     * map 形式
     * key: 原始文件地址
     * value: 加密后的地址
     *{
     *     "/test/videoBase/snapshotByNode/20250114/21060200001311124870_0/testzxzxzxzxzxz+333333.jpg": "https://slw-base-video-test.obs.cn-north-4.myhuaweicloud.com:443/test/videoBase/snapshotByNode/20250114/21060200001311124870_0/testzxzxzxzxzxz%2B333333.jpg?AccessKeyId=6RTWXLU6OWVQWSKHXWTC&Expires=1737009597&Signature=xBAAPq5fhxDVLPrdtS4qyQITfyk%3D"
     * }
     */
    @PostMapping("/file/obsFiles/encryptBatch/v2")
    public Map<String, String> encryptBatch(@RequestBody List<String> urlList);

    @PostMapping("/file/obsFiles/genericEncryptBatch/v2")
    public Object genericEncryptBatch(@RequestBody Object obj);

    /**
     * 根据文件名称获取文件地址【微创内部使用】
     * @param request 文件名称
     * @return 文件地址
     */
    @PostMapping("/base/common/getFileUrlByFileName")
    Result<String> getFileUrlByFileName(@RequestBody SysFileRequest request);


    /**
     * 根据文件路径获取文件地址【微创内部使用】
     * @param request 文件路径
     * @return 文件地址
     */
    @PostMapping("/base/common/getFileUrlByOldUrl")
    Result<String> getFileUrlByOldUrl(@RequestBody SysFileUrlRequest request);

    /**
     * 新增文件信息,视联使用
     * @apiNote 文件上传
     * @apiVersion 9.12
     * @tag 九、文件能力
     * @requestDemo
     * file: 文件
     *
     * @responseDemo
     * code枚举
     * 200  操作成功
     * 400	业务异常
     * 401	未认证
     * 404	服务未找到
     * 500	服务异常
     *  fileId: 文件id
     *  fileSize: 文件大小
     *  fileName: 文件名称
     *  fileUrl: 文件url
     *  fileSuffix: 文件后缀
     *  originUrl: 文件原始url
     *  proxyAddr: 文件代理url
     * {
     *     "msg": "操作成功",
     *     "code": 200,
     *     "data": {
     *         "creator": null,
     *         "createTime": null,
     *         "createBy": null,
     *         "updateBy": null,
     *         "modifier": null,
     *         "updateTime": null,
     *         "deleteFlag": null,
     *         "tenantId": null,
     *         "userId": null,
     *         "appVerCode": null,
     *         "industryCode": null,
     *         "platformCode": null,
     *         "dataScope": null,
     *         "criteriaList": null,
     *         "screenId": null,
     *         "screenType": null,
     *         "dataPermissions": null,
     *         "pageNum": null,
     *         "pageSize": null,
     *         "resourceProperty": null,
     *         "orderByColumn": null,
     *         "isAsc": null,
     *         "fileId": "12fc6b7a492a4c95ac666bbab56730ea",
     *         "fileClassify": null,
     *         "fileSize": "276486",
     *         "fileName": "ae1c-fyscsmu5399362.jpg",
     *         "fileUrl": "https://video-platform.obs.cn-north-4.myhuaweicloud.com/slyy2/dev/1.0/dab159cadbea9bb247a4a4dca032de3c/2eff5446551221d9bb6e3d5c49068696/858c19108d7b4d92ace09b1fee96dd29.jpg?AccessKeyId=CRFYWADDOJHXLKROOIXX&Expires=1747377049&Signature=rCpcexow527R9hTpNeaeX1FWQEE%3D",
     *         "fileSuffix": ".jpg",
     *         "delFlag": null,
     *         "deptId": null,
     *         "beginTime": null,
     *         "endTime": null,
     *         "originUrl": "https://video-platform.obs.cn-north-4.myhuaweicloud.com:443/slyy2/dev/1.0/dab159cadbea9bb247a4a4dca032de3c/2eff5446551221d9bb6e3d5c49068696/858c19108d7b4d92ace09b1fee96dd29.jpg",
     *         "proxyAddr": "https://video-platform.obs.cn-north-4.myhuaweicloud.com/slyy2/dev/1.0/dab159cadbea9bb247a4a4dca032de3c/2eff5446551221d9bb6e3d5c49068696/858c19108d7b4d92ace09b1fee96dd29.jpg?AccessKeyId=CRFYWADDOJHXLKROOIXX&Expires=1747294249&Signature=dt1DvrMGNxOEiwZ5YRFdF/SEsw8%3D",
     *         "orderBy": ""
     *     }
     * }
     *
     *
     * @return
     */
    @PostMapping(path ="/base/common/uploadNoLogin", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    AjaxResult uploadNoLogin(@RequestPart("file")MultipartFile file);
}
