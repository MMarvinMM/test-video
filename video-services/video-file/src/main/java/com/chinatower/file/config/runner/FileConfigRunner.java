package com.chinatower.file.config.runner;

import cn.hutool.extra.spring.SpringUtil;
import com.chinatower.file.FileOperation;
import com.chinatower.file.config.filestategy.ObsStrategy;
import com.chinatower.file.config.filestategy.OfsStrategy;
import com.chinatower.file.config.filestategy.OfsStrategyV2;
import com.chinatower.file.domain.ByteArrayMultipartFile;
import com.chinatower.file.domain.FileUrlOfsColonyCode;
import com.chinatower.file.domain.SysConfig;
import com.chinatower.file.request.ToEncryptUrl;
import com.chinatower.file.service.SysConfigService;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.props.HwObsProperties;
import com.chinatower.platform.utils.SecurityUtils;
import com.obs.services.ObsClient;
import com.obs.services.ObsConfiguration;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionJavaScript;
import org.apache.pdfbox.pdmodel.interactive.action.PDPageAdditionalActions;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationUnknown;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionJavaScript;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@SuppressWarnings("all")
public class FileConfigRunner
        implements ApplicationRunner, Ordered {
    @Resource
    ConfigurableListableBeanFactory beanFactory;

    public static final String DEPENDENCE_MANAGE = "dependence_manage";

    @Component
    public static class FileStrategeManager implements FileOperation {
        @Resource
        ConfigurableListableBeanFactory beanFactory;
        @Value("${obsBucketList}")
        String obsBucketList;
        List<String> convertBucketList;

        @Autowired
        SysConfigService sysConfigService;

        public OfsStrategy getOfsStrategy() {
            return SpringUtil.getBean(OfsStrategy.class);
        }

        public ObsStrategy getObsStrategy() {
            return SpringUtil.getBean(ObsStrategy.class);
        }

        /**
         * 初始化文件策略
         * <ul>
         *     <li>1.初始化OBS策略</li>
         *     <li>2.初始化OFS策略</li>
         *     <li>3.初始化OBS桶列表</li>
         * </ul>
         * 以下给出两个不同的桶示例、<br>
         *  https://video-platform.obs.cn-north-4.myhuaweicloud.com/slyy2/pre/baseplatform/ec7c3b0bc5f9b10ff3b4839d88332a78/aeafc529db954e46a25d10a4530f3613/b2e76b37d5444a8c81a15a88a72cdad5.png
         *  https://ykyj-obs.obs.cn-north-4.myhuaweicloud.com/oil-server/2023/06/01/205b25086e944c5b81c899dd833c2911.png
         *  <br>
         *  可以看到不同的桶的域名不同，所以需要根据域名来判断是哪个桶
         */
        public void init() {
            initObsStrategy();
            initOfsStrategy();
            initOfsStrategyV2();
            convertBucketList = Stream.of(obsBucketList.split(",")).collect(Collectors.toList());
        }

        /*@Override
        public String encrypt(String url, boolean useOfs, String colonyCode) {
            if (useOfs) {
                return beanFactory.getBean(OfsStrategyV2.class).encrypt(url, colonyCode);
            } else if (isObs(url)) {
                return beanFactory.getBean(ObsStrategy.class).encrypt(url);
            } else {
                return beanFactory.getBean(OfsStrategy.class).encrypt(url);
            }
        }*/

        @Override
        public String encrypt(String url, boolean useOfs, String colonyCode) {
            if (isObs(url)) {
                return beanFactory.getBean(ObsStrategy.class).encrypt(url);
            }else if (useOfs) {
                return beanFactory.getBean(OfsStrategyV2.class).encrypt(url, colonyCode);
            } else {
                return beanFactory.getBean(OfsStrategy.class).encrypt(url);
            }
        }

        /**
         * 不同方式验签
         *
         * @param url 文件地址
         * @param strategy 0-obs 1-ofs 2- ofsV2
         * @return
         */
        public String encryptStrategy(String url, Integer strategy) {
            if (0 == strategy) {
                return beanFactory.getBean(ObsStrategy.class).encrypt(url);
            }else if (1 == strategy) {
                return beanFactory.getBean(OfsStrategy.class).encrypt(url);
            } else if (2 == strategy) {
                return beanFactory.getBean(OfsStrategyV2.class).encrypt(url);
            } else {
                return beanFactory.getBean(OfsStrategyV2.class).refreshOfsTempUrlAndProxyUrlV2(url);
            }
        }

        /**
         * 判断是否是OBS桶
         * 如果url中的域名在obsBucketList中，则认为是OBS桶
         *
         * @param url 文件url
         * @return 是否是OBS桶
         */
        public boolean isObs(String url) {
            for (String s : this.convertBucketList) {
                if (url.contains(s)) {
                    return true;
                }
            }
            return false;
        }

        /**
         * 根据url列表获取文件策略
         *
         * @param urls 文件url列表
         * @return 文件策略
         */
        public FileOperation fileOperation(List<String> urls) {
            String url = urls.get(0);
            if (isObs(url)) {
                return beanFactory.getBean(ObsStrategy.class);
            } else {
                return beanFactory.getBean(OfsStrategy.class);
            }
        }

        /**
         * 初始化OFS策略
         */
        private void initOfsStrategy() {
            String appId = SpringBeanFactoryFileUtil.resolve("${ofs.appId}");
            String filePath = SpringBeanFactoryFileUtil.resolve("${ofs.filePath}");
            String privateKey = SpringBeanFactoryFileUtil.resolve("${ofs.privateKey}");
            String serverPrefix = SpringBeanFactoryFileUtil.resolve("${ofs.serverPrefix}");
            String regexPattern = SpringBeanFactoryFileUtil.resolve("${ofs.regexPattern}");
            String storeType = SpringBeanFactoryFileUtil.resolve("${ofs.storeType}");

            OfsStrategy ofsStrategy = new OfsStrategy();
            ofsStrategy.setAppId(appId);
            ofsStrategy.setFilePath(filePath);
            ofsStrategy.setPrivateKey(privateKey);
            ofsStrategy.setServerPrefix(serverPrefix);
            ofsStrategy.setRegexPattern(regexPattern);
            ofsStrategy.setStoreType(storeType);
            ofsStrategy.setSysConfigService(sysConfigService);
            beanFactory.registerSingleton(OfsStrategy.class.getSimpleName(), ofsStrategy);
        }

        /**
         * 初始化OFS v2策略
         */
        private void initOfsStrategyV2() {
            String appId = SpringBeanFactoryFileUtil.resolve("${ofs.appId}");
            String filePath = SpringBeanFactoryFileUtil.resolve("${ofs.filePath}");
            String privateKey = SpringBeanFactoryFileUtil.resolve("${ofs.privateKey}");
            String serverPrefix = SpringBeanFactoryFileUtil.resolve("${ofs.serverPrefix}");
            String regexPattern = SpringBeanFactoryFileUtil.resolve("${ofs.regexPattern}");
            String storeType = SpringBeanFactoryFileUtil.resolve("${ofs.storeType}");

            OfsStrategyV2 ofsStrategyV2 = new OfsStrategyV2();
            ofsStrategyV2.setAppId(appId);
            ofsStrategyV2.setFilePath(filePath);
            ofsStrategyV2.setPrivateKey(privateKey);
            ofsStrategyV2.setServerPrefix(serverPrefix);
            ofsStrategyV2.setStoreType(storeType);
            ofsStrategyV2.setSysConfigService(sysConfigService);
            beanFactory.registerSingleton(OfsStrategyV2.class.getSimpleName(), ofsStrategyV2);
        }

        /**
         * 初始化OBS策略
         */
        private void initObsStrategy() {
            String bucketName = SpringBeanFactoryFileUtil.resolve("${obs.bucketName}");
            String suffixRules = SpringBeanFactoryFileUtil.resolve("${obs.suffixRules}");
            String rootDir = SpringBeanFactoryFileUtil.resolve("${obs.customPath}");
            String ak = SpringBeanFactoryFileUtil.resolve("${obs.ak}");
            String sk = SpringBeanFactoryFileUtil.resolve("${obs.sk}");
            String endPoint = SpringBeanFactoryFileUtil.resolve("${obs.endPoint}");

            ObsConfiguration config = new ObsConfiguration();
            config.setSocketTimeout(30000);
            config.setConnectionTimeout(10000);
            config.setEndPoint(endPoint);
            ObsClient obsClient = new ObsClient(ak, sk, config);

            ObsStrategy.ObsClientUtil obsClientUtil = new ObsStrategy.ObsClientUtil(obsClient);
            obsClientUtil.setBucketName(bucketName);
            obsClientUtil.setSuffixRules(suffixRules);
            obsClientUtil.setRootDir(rootDir);
            obsClientUtil.setAk(ak);
            obsClientUtil.setSk(sk);
            obsClientUtil.setEndPoint(endPoint);
            obsClientUtil.setHwObsProperties(SpringUtil.getBean(HwObsProperties.class));
            ObsStrategy obsStrategy = new ObsStrategy(obsClientUtil);
            beanFactory.registerSingleton(ObsStrategy.class.getSimpleName(), obsStrategy);
        }

        @Override
        public Pattern pattern() {
            return null;
        }

        @Override
        public FileInfo fileUpload(MultipartFile file) throws Exception {
            return fileUpload(file, null, false, SecurityUtils.getLoginUser());
        }

        @Override
        public FileInfo fileUpload(MultipartFile file, String colonyCode, Boolean useOfs, LoginUser loginUser) throws Exception {
            String suffixRules = SpringBeanFactoryFileUtil.resolve("${obs.suffixRules}");
            String fileExt = FilenameUtils.getExtension(file.getOriginalFilename());
            List<String> list = Arrays.asList(suffixRules.split(","));
            if (!list.contains(fileExt)) {
                throw new Exception(fileExt + "格式文件不能上传");
            }
            //增加pdf-xss、svg-xss防护
            // 2. 增加 PDF-XSS 和 SVG-XSS 防护
            byte[] fileBytes = file.getBytes();
            String filename = file.getOriginalFilename();

            if ("svg".equals(fileExt) || "SVG".equals(fileExt)) {
                // SVG 文件校验 & 净化
                String svgContent = new String(fileBytes, StandardCharsets.UTF_8);
                if (!isSafeSvg(svgContent)) {
                    throw new Exception("SVG 文件包含恶意代码，上传被拒绝");
                }
            } else if ("pdf".equals(fileExt) || "PDF".equals(fileExt)) {
                boolean pdfSafe = isPdfSafe(file.getInputStream());
                if (!pdfSafe) {
                    throw new Exception("PDF 文件包含恶意代码，上传被拒绝");
                }
            }


            if (BooleanUtils.isTrue(useOfs)) {
                return SpringUtil.getBean(OfsStrategyV2.class.getSimpleName(), OfsStrategyV2.class).fileUpload(file, colonyCode, useOfs, loginUser);
            } else if ("ter".equals(SpringUtil.getActiveProfile())) {
                return SpringUtil.getBean(OfsStrategy.class.getSimpleName(), OfsStrategy.class).fileUpload(file);
            } else {
                return SpringUtil.getBean(ObsStrategy.class.getSimpleName(), ObsStrategy.class).fileUploadWithLoginInfo(file, loginUser);
            }
        }

        @Override
        public String getFileUrlByFileName(String fileName, String colonyCode, Boolean useOfs) {
            if (BooleanUtils.isTrue(useOfs)) {
                return SpringUtil.getBean(OfsStrategyV2.class.getSimpleName(), OfsStrategyV2.class).getFileUrlByFileName(fileName, colonyCode, useOfs);
            } else {
                return null;
            }
        }

        @Override
        public String getFileUrlByOldUrl(String oldUrl, String colonyCode, Boolean useOfs) {
            if (BooleanUtils.isTrue(useOfs)) {
                return SpringUtil.getBean(OfsStrategyV2.class.getSimpleName(), OfsStrategyV2.class).getFileUrlByOldUrl(oldUrl, colonyCode, useOfs);
            } else {
                return SpringUtil.getBean(ObsStrategy.class.getSimpleName(), ObsStrategy.class).encrypt(oldUrl);
            }
        }

        @Override
        public FileInfo fileUploadV3(MultipartFile file, String colonyCode, Boolean useOfs) {
            return SpringUtil.getBean(OfsStrategyV2.class.getSimpleName(), OfsStrategyV2.class).fileUploadV3(file, colonyCode, useOfs);
        }

        @Override
        public String encrypt(String url) {
            return encrypt(url, false, null);
        }

        @Override
        public Map<String, String> encryptBatch(List<String> urlList) {
            List<String> obsUrlList = urlList.stream().filter(it -> isObs(it)).collect(Collectors.toList());
            Map<String, String> map = new HashMap<>();
            if (CollectionUtils.isNotEmpty(obsUrlList)) {
                Map<String, String> obsMap = getObsStrategy().encryptBatch(obsUrlList);
                map.putAll(obsMap);
            }
            List<String> ofsUrlList = urlList.stream().filter(it -> !isObs(it)).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(ofsUrlList)) {
                // 纯属地化节点不能使用ofsV2调用级联网关，需要用原始ofsV1的方式
                Map<String, String> ofsMap = new HashMap<>();
                SysConfig sysConfig = sysConfigService.selectConfigByKey(DEPENDENCE_MANAGE);
                if (sysConfig != null && Constants.ZERO.equals(sysConfig.getConfigValue())) {
                    ofsMap = getOfsStrategy().encryptBatch(ofsUrlList);
                } else {
                    ofsMap = SpringUtil.getBean(OfsStrategyV2.class.getSimpleName(), OfsStrategyV2.class).encryptBatch(ofsUrlList);
                }
                map.putAll(ofsMap);
            }
            return map;
        }

        @Override
        public <T extends ToEncryptUrl> List<T> genericEncryptBatch(List<T> urlList) {
            List<T> obsUrlList = urlList.stream().filter(it -> isObs(it.getOriginalUrl())).collect(Collectors.toList());
            List<T> ofsUrlList = urlList.stream().filter(it -> !isObs(it.getOriginalUrl())).collect(Collectors.toList());
            List<T> totalList = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(obsUrlList)) {
                List<T> obsResult = SpringUtil.getBean(ObsStrategy.class).genericEncryptBatch(obsUrlList);
                if (CollectionUtils.isEmpty(ofsUrlList)) {
                    totalList.addAll(obsResult);
                }
            }
            if (CollectionUtils.isNotEmpty(ofsUrlList)) {
                List<T> ofsResult = SpringUtil.getBean(OfsStrategy.class).genericEncryptBatch(ofsUrlList);
                if (CollectionUtils.isEmpty(ofsResult)) {
                    totalList.addAll(ofsResult);
                }
            }
            return totalList;
        }

        @Override
        public void fileDownload(String url, OutputStream out, boolean isCloseOut) {
            String encryptURL = encrypt(url, false, null);
            FileOperation.super.fileDownload(encryptURL, out, isCloseOut);
        }

        /**
         * ofs v2 专用方法
         * @param urlList
         */
        public String encryptStrategyOfsV2(String url) {
            return SpringUtil.getBean(OfsStrategyV2.class.getSimpleName(), OfsStrategyV2.class).refreshOfsTempUrlAndProxyUrlV2(url);
        }
    }

    @Resource
    FileStrategeManager fileStrategeManager;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        fileStrategeManager.init();
        log.info("config file strategy finish");
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE;
    }

    @Component
    static class SpringBeanFactoryFileUtil implements BeanFactoryAware {
        private static BeanFactory beanFactory;

        @Override
        public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
            SpringBeanFactoryFileUtil.beanFactory = beanFactory;
        }

        public static String resolve(String value) {
            try {
                if (beanFactory instanceof ConfigurableBeanFactory) {
                    return ((ConfigurableBeanFactory) beanFactory).resolveEmbeddedValue(value);
                }
            } catch (Exception e) {
                throw new FileOperation.FileOperationException("未找到 " + value);
            }
            return null;
        }
    }

    // 检查 SVG 是否安全（无 <script> 或 onload 事件）
    private static boolean isSafeSvg(String svgContent) {
        Document doc = Jsoup.parse(svgContent, "", Parser.xmlParser());
        return doc.select("script").isEmpty() && doc.select("[onload]").isEmpty();
    }

    /**
     * 检查PDF是否包含潜在恶意内容
     */
    public static boolean isPdfSafe(InputStream pdfStream) throws IOException {
        try (PDDocument document = PDDocument.load(pdfStream)) {
            // 1. 检查JavaScript动作
            if (containsJavaScript(document)) {
                return false;
            }

           /* // 2. 检查可疑的链接注释
            if (containsSuspiciousAnnotations(document)) {
                return false;
            }

            // 3. 检查嵌入的文件（可能包含恶意内容）
            if (containsEmbeddedFiles(document)) {
                return false;
            }*/

            return true;
        }
    }

    private static boolean containsJavaScript(PDDocument document) throws IOException {
        // 检查文档级别的JavaScript
        if (document.getDocumentCatalog().getOpenAction() instanceof PDActionJavaScript) {
            return true;
        }

        // 页面级检查
        for (PDPage page : document.getPages()) {
            PDPageAdditionalActions pageActions = page.getActions();
            if (pageActions != null) {
                // 检查各种可能包含JS的动作
                if (pageActions.getC() instanceof PDActionJavaScript ||  // 页面打开时// 页面关闭时
                        pageActions.getO() instanceof PDActionJavaScript) {  // 页面可见时
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean containsSuspiciousAnnotations(PDDocument document) throws IOException {
        for (var page : document.getPages()) {
            List<PDAnnotation> annotations = page.getAnnotations();
            for (PDAnnotation annotation : annotations) {
                // 检查链接注释是否指向外部URL
                if (annotation instanceof PDAnnotationLink) {
                    PDAnnotationLink link = (PDAnnotationLink) annotation;
                    if (link.getAction() != null) {
                        // 这里可以添加对可疑URL的检查
                        return true;
                    }
                }

                // 检查未知类型的注释（可能包含恶意内容）
                if (annotation instanceof PDAnnotationUnknown) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean containsEmbeddedFiles(PDDocument document) {
        return document.getDocumentCatalog().getNames() != null &&
                document.getDocumentCatalog().getNames().getEmbeddedFiles() != null;
    }

}
