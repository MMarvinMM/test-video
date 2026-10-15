package com.chinatower.platform.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Seata配置
 *
 * @author chinatower
 */
@Data
@ConfigurationProperties(prefix = SeataProperties.SEATA_PREFIX)
public class SeataProperties {

  public static final String SEATA_PREFIX = "seata";

  private boolean enable;

  private String applicationId;

  private String txServiceGroup;

}
