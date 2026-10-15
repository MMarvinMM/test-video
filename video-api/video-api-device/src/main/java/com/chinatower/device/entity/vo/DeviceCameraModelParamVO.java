package com.chinatower.device.entity.vo;

import lombok.Data;

@Data
public class DeviceCameraModelParamVO {

   /**
    * 相机型号编码
    */
   private String cameraModelCode;

   /**
    * 镜头名称
    */
   private String lensName;

   /**
    * 光谱类型
    */
   private String spectralType;

   /**
    * 光谱类型名称
    */
   private String spectralTypeName;

   /**
    * 传感器尺寸(mm)
    */
   private String sensorSize;

   /**
    * 像元大小(mm)
    */
   private String pixelSize;

   /**
    * 焦距类型
    */
   private String focalLengthType;

   /**
    * 变焦转化系数
    */
   private String zoomConversionCoefficient;

   /**
    * 实际焦距范围(mm)
    */
   private String actualFocalLengthRange;

   /**
    * 等效焦距范围(mm)
    */
   private String equivalentFocalLengthRange;

   /**
    * 光学变焦范围
    */
   private String opticalZoomRange;

   /**
    * 数码变焦范围
    */
   private String digitalZoomRange;

   /**
    * 调色盘样式
    */
   private String colorPaletteStyle;

   /**
    * 支持功能
    */
   private String supportingFeature;

   /**
    * 传感器尺寸宽(mm)
    */
   private String sensorSizeWidth;

   /**
    * 传感器尺寸高(mm)
    */
   private String sensorSizeHeight;

   /**
    * 像元大小宽(mm)
    */
   private String pixelSizeWidth;
}
