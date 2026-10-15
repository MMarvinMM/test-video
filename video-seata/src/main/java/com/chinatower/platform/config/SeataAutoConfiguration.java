package com.chinatower.platform.config;

import com.chinatower.common.factory.YamlPropertySourceFactory;
import com.chinatower.platform.props.SeataProperties;
import io.seata.spring.annotation.datasource.EnableAutoDataSourceProxy;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * Seata配置
 *
 * @author chinatower
 * @EnableAutoDataSourceProxy 自动开启代理数据源
 */
@Configuration
@PropertySource(factory = YamlPropertySourceFactory.class, value = "classpath:tower-seata.yml")
@EnableConfigurationProperties(SeataProperties.class)
@ConditionalOnProperty(value = SeataProperties.SEATA_PREFIX + ".enable", havingValue = "true", matchIfMissing = true)
@AllArgsConstructor
@ConditionalOnBean(DataSource.class)
@EnableAutoDataSourceProxy
@Slf4j
public class SeataAutoConfiguration {

  public DataSource dataSource;

  public static final String undoLogSql = "CREATE TABLE IF NOT EXISTS undo_log(" +
      "`id` bigint(20) NOT NULL AUTO_INCREMENT," +
      "`branch_id` bigint(20) NOT NULL COMMENT 'branch transaction id'," +
      "`xid` varchar(100) NOT NULL COMMENT 'global transaction id'," +
      "`context` varchar(128) NOT NULL COMMENT 'undo_log context,such as serialization'," +
      "`rollback_info` longblob NOT NULL COMMENT 'rollback info'," +
      "`log_status` int(11) NOT NULL COMMENT '0:normal status,1:defense status'," +
      "`log_created` datetime NOT NULL COMMENT 'create datetime'," +
      "`log_modified` datetime NOT NULL COMMENT 'modify datetime'," +
      "`ext` varchar(100) DEFAULT NULL," +
      "PRIMARY KEY (`id`)," +
      "UNIQUE KEY `ux_undo_log` (`xid`,`branch_id`)" +
      ")ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;";

  public final SeataProperties seataProperties;

  /**
   * 判断当前数据库是否有undo_log 该表，如果没有，
   * 创建该表 undo_log 为seata 记录事务sql执行的记录表 第二阶段时，如果confirm会清除记录，如果是cancel 会根据记录补偿原数据
   */
  @PostConstruct
  public void detectTable() {
    try {
      dataSource.getConnection().prepareStatement(undoLogSql).execute();
    } catch (SQLException e) {
      log.error("创建[seata] undo_log表错误。", e);
    }
  }

}
