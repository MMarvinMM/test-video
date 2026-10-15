package com.chinatower.platform.entity.device;

import lombok.Data;

import java.io.Serializable;

@Data
public class PageIotMonitorSubVO implements Serializable {

      /**
       * 因子编码
       * @required true
       */
      private String monitorCode;

      /**
       * 因子名称
       * @required true
       */
      private String monitorName;

      /**
       * 因子单位
       * @required true
       */
      private String monitorUnits;

      /**
       * 告警类型编码
       * @required true
       */
      private String alarmType;

      /**
       * 告警类型名称
       * @required true
       */
      private String alarmTypeName;
}
