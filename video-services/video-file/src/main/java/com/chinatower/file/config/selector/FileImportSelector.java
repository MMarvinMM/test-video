package com.chinatower.file.config.selector;

import cn.hutool.extra.spring.SpringUtil;
import com.chinatower.file.config.runner.FileConfigRunner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

@Slf4j
public class FileImportSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        return new String[]{
                FileConfigRunner.class.getName(),
                SpringUtil.class.getName(),
                com.chinatower.common.utils.SpringUtil.class.getName()
        };
    }
}
