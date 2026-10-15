package com.chinatower.bdm.modules.grid.config;

import com.alibaba.csp.sentinel.util.StringUtil;
import com.chinatower.bdm.modules.grid.dao.LayerStyleFiledParamsDTO;
import com.chinatower.bdm.modules.grid.dao.LayerStyleRangeDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @desription: geoserver 样式模板配置
 * @data 创建时间：2024/10/16 11:02
 * @author chuan hao
 * @version 1.0
 *
 */
@SuppressWarnings("all")
public class GeoserverStyleTemplate {

    private static String MULTIPOLYGON_TEMPLATE = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n" +
            "<StyledLayerDescriptor version=\"1.0.0\"\n" +
            "                       xsi:schemaLocation=\"http://www.opengis.net/sld StyledLayerDescriptor.xsd\"\n" +
            "                       xmlns=\"http://www.opengis.net/sld\"\n" +
            "                       xmlns:ogc=\"http://www.opengis.net/ogc\"\n" +
            "                       xmlns:xlink=\"http://www.w3.org/1999/xlink\"\n" +
            "                       xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
            "  <NamedLayer>\n" +
            "    <Name>default_polygon</Name>\n" +
            "    <UserStyle>\n" +
            "      <Title>Default Polygon</Title>\n" +
            "      <Abstract>A sample style that draws a polygon</Abstract>\n" +
            "      <FeatureTypeStyle>\n" +
            "        <Rule>\n" +
            "          <Name>rule1</Name>\n" +
            "          <Title>Gray Polygon with Black Outline</Title>\n" +
            "          <Abstract>A polygon with a gray fill and a 1 pixel black outline</Abstract>\n" +
            "          <PolygonSymbolizer>\n" +
            "            <Fill>\n" +
            "              <CssParameter name=\"fill\">fill_color</CssParameter>\n" +
            "              <CssParameter name=\"fill-opacity\">\n" +
            "                <ogc:Function name=\"env\">\n" +
            "                  <ogc:Literal>opacity1</ogc:Literal>\n" +
            "                  <ogc:Literal>fill_opacity</ogc:Literal> \n" +
            "                </ogc:Function>\n" +
            "              </CssParameter>\n" +
            "            </Fill>\n" +
            "            <Stroke>\n" +
            "              <CssParameter name=\"stroke\">stroke_color</CssParameter>\n" +
            "              <CssParameter name=\"stroke-width\">stroke_width</CssParameter>\n" +
            "              <CssParameter name=\"stroke-opacity\">\n" +
            "                  <ogc:Function name=\"env\">\n" +
            "                    <ogc:Literal>opacity1</ogc:Literal>\n" +
            "                    <ogc:Literal>stroke_opacity</ogc:Literal> \n" +
            "                  </ogc:Function>\n" +
            "              </CssParameter>\n" +
            "            </Stroke>\n" +
            "          </PolygonSymbolizer>\n" +
            "        </Rule>\n" +
            "      </FeatureTypeStyle>\n" +
            "    </UserStyle>\n" +
            "  </NamedLayer>\n" +
            "</StyledLayerDescriptor>\n";

    private static String POINT_TEMPLATE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<StyledLayerDescriptor xmlns=\"http://www.opengis.net/sld\" version=\"1.1.0\" xsi:schemaLocation=\"http://www.opengis.net/sld http://schemas.opengis.net/sld/1.1.0/StyledLayerDescriptor.xsd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:ogc=\"http://www.opengis.net/ogc\" xmlns:se=\"http://www.opengis.net/se\">\n" +
            "  <NamedLayer>\n" +
            "    <se:Name>default_point</se:Name>\n" +
            "    <UserStyle>\n" +
            "      <se:Name>Default point</se:Name>\n" +
            "      <se:FeatureTypeStyle>\n" +
            "        <se:Rule>\n" +
            "          <se:Name>Single symbol</se:Name>\n" +
            "          <se:PointSymbolizer>\n" +
            "            <se:Graphic>\n" +
            "              <se:Mark>\n" +
            "                <se:WellKnownName>circle</se:WellKnownName>\n" +
            "                <se:Fill>\n" +
            "                  <se:SvgParameter name=\"fill\">fill_color</se:SvgParameter>\n" +
            "                  <se:SvgParameter name=\"fill-opacity\">\n" +
            "                     <ogc:Function name=\"env\">\n" +
            "                        <ogc:Literal>opacity1</ogc:Literal>\n" +
            "                        <ogc:Literal>fill_opacity</ogc:Literal> \n" +
            "                     </ogc:Function>\n" +
            "                   </se:SvgParameter>\n" +
            "                </se:Fill>\n" +
            "                <se:Stroke>\n" +
            "                  <se:SvgParameter name=\"stroke\">stroke_color</se:SvgParameter>\n" +
            "                  <se:SvgParameter name=\"stroke-width\">stroke_width</se:SvgParameter>\n" +
            "                  <se:SvgParameter name=\"stroke-opacity\">\n" +
            "                       <ogc:Function name=\"env\">\n" +
            "                           <ogc:Literal>opacity1</ogc:Literal>\n" +
            "                           <ogc:Literal>stroke_opacity</ogc:Literal> \n" +
            "                       </ogc:Function>\n" +
            "                   </se:SvgParameter>\n" +
            "                </se:Stroke>\n" +
            "              </se:Mark>\n" +
            "              <se:Size>7</se:Size>\n" +
            "            </se:Graphic>\n" +
            "          </se:PointSymbolizer>\n" +
            "        </se:Rule>\n" +
            "      </se:FeatureTypeStyle>\n" +
            "    </UserStyle>\n" +
            "  </NamedLayer>\n" +
            "</StyledLayerDescriptor>\n";

    public static String LINE_TEMPLATE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<StyledLayerDescriptor xmlns=\"http://www.opengis.net/sld\" xmlns:se=\"http://www.opengis.net/se\" version=\"1.1.0\" xmlns:ogc=\"http://www.opengis.net/ogc\" xsi:schemaLocation=\"http://www.opengis.net/sld http://schemas.opengis.net/sld/1.1.0/StyledLayerDescriptor.xsd\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
            "  <NamedLayer>\n" +
            "    <se:Name>default_line</se:Name>\n" +
            "    <UserStyle>\n" +
            "      <se:Name>default_line</se:Name>\n" +
            "      <se:FeatureTypeStyle>\n" +
            "        <se:Rule>\n" +
            "          <se:Name>Single symbol</se:Name>\n" +
            "          <se:LineSymbolizer>\n" +
            "            <se:Stroke>\n" +
            "              <se:SvgParameter name=\"stroke\">stroke_color</se:SvgParameter>\n" +
            "              <se:SvgParameter name=\"stroke-opacity\">\n" +
            "                  <ogc:Function name=\"env\">\n" +
            "                    <ogc:Literal>opacity1</ogc:Literal>\n" +
            "                    <ogc:Literal>stroke_opacity</ogc:Literal> \n" +
            "                  </ogc:Function>\n" +
            "              </se:SvgParameter>\n" +
            "              <se:SvgParameter name=\"stroke-width\">stroke_width</se:SvgParameter>\n" +
            "              <se:SvgParameter name=\"stroke-linejoin\">bevel</se:SvgParameter>\n" +
            "              <se:SvgParameter name=\"stroke-linecap\">square</se:SvgParameter>\n" +
            "            </se:Stroke>\n" +
            "          </se:LineSymbolizer>\n" +
            "        </se:Rule>\n" +
            "      </se:FeatureTypeStyle>\n" +
            "    </UserStyle>\n" +
            "  </NamedLayer>\n" +
            "</StyledLayerDescriptor>\n";

    /**
     * 图例样式父集
     */
    public static String LAYER_FILED_STYLE_TEMPLATE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<StyledLayerDescriptor xmlns=\"http://www.opengis.net/sld\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" version=\"1.1.0\" xsi:schemaLocation=\"http://www.opengis.net/sld http://schemas.opengis.net/sld/1.1.0/StyledLayerDescriptor.xsd\" xmlns:ogc=\"http://www.opengis.net/ogc\" xmlns:se=\"http://www.opengis.net/se\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
            "  <NamedLayer>\n" +
            "    <se:Name>lindi</se:Name>\n" +
            "    <UserStyle>\n" +
            "      <se:Name>lindi</se:Name>\n" +
            "      <se:FeatureTypeStyle>\n" +
                   "rule_list" +
            "      </se:FeatureTypeStyle>\n" +
            "    </UserStyle>\n" +
            "  </NamedLayer>\n" +
            "</StyledLayerDescriptor>\n";

    public static String RULE_TEMPLATE =
                    "        <se:Rule>\n" +
                    "          <se:Name>rule_key</se:Name>\n" +
                    "          <se:Description>\n" +
                    "            <se:Title>rule_key</se:Title>\n" +
                    "          </se:Description>\n" +
                    "          <ogc:Filter xmlns:ogc=\"http://www.opengis.net/ogc\">\n" +
                    "            <ogc:PropertyIsEqualTo>\n" +
                    "              <ogc:PropertyName>rule_code</ogc:PropertyName>\n" +
                    "              <ogc:Literal>rule_key</ogc:Literal>\n" +
                    "            </ogc:PropertyIsEqualTo>\n" +
                    "          </ogc:Filter>\n";
    /**
     * 范围
     */
    public static String RULE_RANGE_TEMPLATE =
                    "        <se:Rule>\n" +
                    "          <se:Name>rule_key</se:Name>\n" +
                    "          <se:Description>\n" +
                    "            <se:Title>rule_key</se:Title>\n" +
                    "          </se:Description>\n" +
                    "          <ogc:Filter xmlns:ogc=\"http://www.opengis.net/ogc\">\n" +
                    "            <ogc:And>\n" +
                    "               rule_range\n" +
                    "            </ogc:And>\n" +
                    "          </ogc:Filter>\n";
    /**
     * 范围比较单词
     */
    private static Map<String,String> RANGE_MAP = new HashMap(){{
        put("lt","PropertyIsLessThan");
        put("lte","PropertyIsLessThanOrEqualTo");
        put("gt","PropertyIsGreaterThan");
        put("gte","PropertyIsGreaterThanOrEqualTo");
    }};

    /**
     * 范围比较标签
     */
    public static String RULE_RANGE_TEMPLATE_CHILD =
                    "            <ogc:rule_compare>\n" +
                    "              <ogc:PropertyName>rule_code</ogc:PropertyName>\n" +
                    "              <ogc:Literal>rule_key</ogc:Literal>\n" +
                    "            </ogc:rule_compare>\n";

    /**
     * 图例其他颜色
     */
    public static String RULE_ELSE_TEMPLATE =
                    "        <se:Rule>\n" +
                    "          <se:Name>qt</se:Name>\n" +
                    "          <se:Description>\n" +
                    "            <se:Title>qt</se:Title>\n" +
                    "          </se:Description>\n" +
                    "          <se:ElseFilter xmlns:se=\"http://www.opengis.net/se\"/>";
    /**
     * MULTIPOLYGON
     */
    public static String MULTIPOLYGON_FILED_RULE_TEMPLATE =
                    "          <se:PolygonSymbolizer>\n" +
                    "            <se:Fill>\n" +
                    "              <se:SvgParameter name=\"fill\">fill_color</se:SvgParameter>\n" +
                    "              <se:SvgParameter name=\"fill-opacity\">\n" +
                    "                  <ogc:Function name=\"env\">\n" +
                    "                      <ogc:Literal>opacity1</ogc:Literal>\n" +
                    "                      <ogc:Literal>fill_opacity</ogc:Literal> \n" +
                    "                  </ogc:Function>\n" +
                    "              </se:SvgParameter>\n" +
                    "            </se:Fill>\n" +
                    "            <se:Stroke>\n" +
                    "              <se:SvgParameter name=\"stroke\">stroke_color</se:SvgParameter>\n" +
                    "              <se:SvgParameter name=\"stroke-width\">stroke_width</se:SvgParameter>\n" +
                    "              <se:SvgParameter name=\"stroke-opacity\">\n" +
                    "                  <ogc:Function name=\"env\">\n" +
                    "                    <ogc:Literal>opacity1</ogc:Literal>\n" +
                    "                    <ogc:Literal>stroke_opacity</ogc:Literal> \n" +
                    "                  </ogc:Function>\n" +
                    "              </se:SvgParameter>\n" +
                    "              <se:SvgParameter name=\"stroke-linejoin\">bevel</se:SvgParameter>\n" +
                    "            </se:Stroke>\n" +
                    "          </se:PolygonSymbolizer>\n" +
                    "        </se:Rule>\n";

    /**
     * LINE 图例
     */
    public static String LINE_FILED_RULE_TEMPLATE =
                    "          <se:LineSymbolizer>\n" +
                    "            <se:Stroke>\n" +
                    "              <se:SvgParameter name=\"stroke\">stroke_color</se:SvgParameter>\n" +
                    "              <se:SvgParameter name=\"stroke-opacity\">\n" +
                    "                  <ogc:Function name=\"env\">\n" +
                    "                    <ogc:Literal>opacity1</ogc:Literal>\n" +
                    "                    <ogc:Literal>stroke_opacity</ogc:Literal> \n" +
                    "                  </ogc:Function>\n" +
                    "              </se:SvgParameter>\n" +
                    "              <se:SvgParameter name=\"stroke-width\">stroke_width</se:SvgParameter>\n" +
                    "              <se:SvgParameter name=\"stroke-linejoin\">bevel</se:SvgParameter>\n" +
                    "              <se:SvgParameter name=\"stroke-linecap\">square</se:SvgParameter>\n" +
                    "            </se:Stroke>\n" +
                    "          </se:LineSymbolizer>\n" +
                    "        </se:Rule>";

    /**
     * POINT 图例
     */
    public static String POINt_FILED_RULE_TEMPLATE =
                    "          <se:PointSymbolizer>\n" +
                    "            <se:Graphic>\n" +
                    "              <se:Mark>\n" +
                    "                <se:WellKnownName>circle</se:WellKnownName>\n" +
                    "                <se:Fill>\n" +
                    "                  <se:SvgParameter name=\"fill\">fill_color</se:SvgParameter>\n" +
                    "                  <se:SvgParameter name=\"fill-opacity\">\n" +
                    "                    <ogc:Function name=\"env\">\n" +
                    "                      <ogc:Literal>opacity1</ogc:Literal>\n" +
                    "                      <ogc:Literal>fill_opacity</ogc:Literal> \n" +
                    "                    </ogc:Function>\n" +
                    "                  </se:SvgParameter>\n" +
                    "                </se:Fill>\n" +
                    "                <se:Stroke>\n" +
                    "                  <se:SvgParameter name=\"stroke\">stroke_color</se:SvgParameter>\n" +
                    "                  <se:SvgParameter name=\"stroke-opacity\">\n" +
                    "                       <ogc:Function name=\"env\">\n" +
                    "                           <ogc:Literal>opacity1</ogc:Literal>\n" +
                    "                           <ogc:Literal>stroke_opacity</ogc:Literal> \n" +
                    "                       </ogc:Function>\n" +
                    "                  </se:SvgParameter>\n" +
                    "                  <se:SvgParameter name=\"stroke-width\">stroke_width</se:SvgParameter>\n" +
                    "                </se:Stroke>\n" +
                    "              </se:Mark>\n" +
                    "              <se:Size>7</se:Size>\n" +
                    "            </se:Graphic>\n" +
                    "          </se:PointSymbolizer>\n" +
                    "        </se:Rule>";

    /**
     * leble 最后的规则
     */
    public static String LINE_LABLE_TEMPLATE =
            "          <se:TextSymbolizer>\n" +
            "           <se:Geometry>\n" +
            "              <ogc:Function name=\"centroid\">\n" +
            "                <ogc:PropertyName>geom</ogc:PropertyName>\n" +
            "              </ogc:Function>\n" +
            "            </se:Geometry>\n" +
            "              rule_label" +
            "            <se:Font>\n" +
            "              <se:SvgParameter name=\"font-family\">黑体</se:SvgParameter>\n" +
            "              <se:SvgParameter name=\"font-size\">20</se:SvgParameter>\n" +
            "            </se:Font>\n" +
            "            <se:LabelPlacement>\n" +
            "              <se:LinePlacement>\n" +
            "                <se:GeneralizeLine>true</se:GeneralizeLine>\n" +
            "              </se:LinePlacement>\n" +
            "            </se:LabelPlacement>\n" +
            "            <se:Fill>\n" +
            "              <se:SvgParameter name=\"fill\">label_color</se:SvgParameter>\n" +
            "              <se:SvgParameter name=\"fill-opacity\">1</se:SvgParameter>\n" +
            "            </se:Fill>\n" +
            "            <VendorOption name=\"partials\">true</VendorOption>\n" +
            "            <VendorOption name=\"conflictResolution\">false</VendorOption>\n" +
            "          </se:TextSymbolizer>\n";

    public static String POINT_LABLE_TEMPLATE =
                    "          <se:TextSymbolizer>\n" +
                    "           <se:Geometry>\n" +
                    "              <ogc:Function name=\"centroid\">\n" +
                    "                <ogc:PropertyName>geom</ogc:PropertyName>\n" +
                    "              </ogc:Function>\n" +
                    "            </se:Geometry>\n" +
                    "              rule_label" +
                    "            <se:Font>\n" +
                    "              <se:SvgParameter name=\"font-family\">黑体</se:SvgParameter>\n" +
                    "              <se:SvgParameter name=\"font-size\">20</se:SvgParameter>\n" +
                    "            </se:Font>\n" +
                    "            <se:LabelPlacement>\n" +
                    "               <se:PointPlacement>\n" +
                    "                  <AnchorPoint>\n" +
                    "                    <AnchorPointX>0.5</AnchorPointX>\n" +
                    "                    <AnchorPointY>0.5</AnchorPointY>\n" +
                    "                   </AnchorPoint>\n" +
                    "               </se:PointPlacement>\n" +
                    "            </se:LabelPlacement>\n" +
                    "            <se:Fill>\n" +
                    "              <se:SvgParameter name=\"fill\">label_color</se:SvgParameter>\n" +
                    "              <se:SvgParameter name=\"fill-opacity\">1</se:SvgParameter>\n" +
                    "            </se:Fill>\n" +
                    "            <VendorOption name=\"partials\">true</VendorOption>\n" +
                    "            <VendorOption name=\"conflictResolution\">false</VendorOption>\n" +
                    "          </se:TextSymbolizer>\n";

    /**
     * 标签名称
     */
    public static String LEBLE_TEXT =
            "            <se:Label>\n" +
            "              rule_value\n" +
            "            </se:Label>\n";

    // 填充颜色
    private static final String QT_FILL_COLOR = "#e1cf20";
    // 填充透明度
    private static final String QT_FILL_OPACITY = "0.4";
    // 线框颜色
    private static final String QT_STROKE_COLOR = "#e1cf20";
    // 线框宽度
    private static final String QT_STROKE_WIDTH = "1";
    // 线框透明度
    private static final String QT_STROKE_OPACITY = "0.8";
    //标签默认颜色
    private static final String LABEL_COLOR = "#323232";

    public static final String LT = "lt";
    public static final String LTE = "lte";
    public static final String LT_LTE = LT + "," + LTE;
    /**
     * 获得 通用 Multipolygon 样式
     */
    public static String getCommonMultipolygonTemplate(String fill_color,String fill_opacity,String stroke_color,String stroke_opacity,String stroke_width) {

        return MULTIPOLYGON_TEMPLATE.replace("fill_color",fill_color).replace("fill_opacity",fill_opacity).replace("stroke_color",stroke_color).replace("stroke_opacity",stroke_opacity).replace("stroke_width",stroke_width);
    }

    /**
     * 获得 通用 LINE 样式
     */
    public static String getCommonLineTemplate(String stroke_color,String stroke_opacity,String stroke_width) {

        return LINE_TEMPLATE.replace("stroke_color",stroke_color).replace("stroke_opacity",stroke_opacity).replace("stroke_width",stroke_width);
    }

    /**
     * 获得 通用 POINT 样式
     */
    public static String getCommonPointTemplate(String fill_color,String fill_opacity,String stroke_color,String stroke_opacity,String stroke_width) {

        return POINT_TEMPLATE.replace("fill_color",fill_color).replace("fill_opacity",fill_opacity).replace("stroke_color",stroke_color).replace("stroke_opacity",stroke_opacity).replace("stroke_width",stroke_width);
    }

    /**
     * 获得 通用 Multipolygon 图例 样式
     */
    public static String getFiledMultipolygonTemplate(List<LayerStyleFiledParamsDTO> filedList){
        StringBuffer sb = new StringBuffer();
        for(LayerStyleFiledParamsDTO filed : filedList){
            String template = getTemplate(filed);
            String rule = (template + MULTIPOLYGON_FILED_RULE_TEMPLATE).replace("fill_color",filed.getFillColor()).replace("fill_opacity",filed.getFillOpacity()).replace("stroke_color",filed.getStrokeColor()).replace("stroke_opacity",filed.getStrokeOpacity()).replace("stroke_width",filed.getStrokeWidth());
            sb.append(rule);
        }
        // 图例其他颜色
        String rule = (RULE_ELSE_TEMPLATE + MULTIPOLYGON_FILED_RULE_TEMPLATE).replace("fill_color",QT_FILL_COLOR).replace("fill_opacity",QT_FILL_OPACITY).replace("stroke_color",QT_STROKE_COLOR).replace("stroke_opacity",QT_STROKE_OPACITY).replace("stroke_width",QT_STROKE_WIDTH);
        sb.append(rule);
        return LAYER_FILED_STYLE_TEMPLATE.replace("rule_list",sb.toString());
    }

    /**
     * 获得 通用 Line 图例 样式
     */
    public static String getFiledLineTemplate(List<LayerStyleFiledParamsDTO> filedList){
        StringBuffer sb = new StringBuffer();
        for(LayerStyleFiledParamsDTO filed : filedList){
            String template = getTemplate(filed);
            String rule = (template + LINE_FILED_RULE_TEMPLATE).replace("stroke_color",filed.getStrokeColor()).replace("stroke_opacity",filed.getStrokeOpacity()).replace("stroke_width",filed.getStrokeWidth());
            sb.append(rule);
        }
        // 图例其他颜色
        String rule = (RULE_ELSE_TEMPLATE + LINE_FILED_RULE_TEMPLATE).replace("stroke_color",QT_STROKE_COLOR).replace("stroke_opacity",QT_STROKE_OPACITY).replace("stroke_width",QT_STROKE_WIDTH);
        sb.append(rule);
        return LAYER_FILED_STYLE_TEMPLATE.replace("rule_list",sb.toString());
    }

    /**
     * 获得 通用 Point 图例 样式
     */
    public static String getFiledPointTemplate(List<LayerStyleFiledParamsDTO> filedList){
        StringBuffer sb = new StringBuffer();
        for(LayerStyleFiledParamsDTO filed : filedList){
            String template = getTemplate(filed);

            String rule = (template + POINt_FILED_RULE_TEMPLATE).replace("fill_color",filed.getFillColor()).replace("fill_opacity",filed.getFillColor()).replace("stroke_color",filed.getStrokeColor()).replace("stroke_opacity",filed.getStrokeOpacity()).replace("stroke_width",filed.getStrokeWidth());
            sb.append(rule);
        }
        // 图例其他颜色
        String rule = (RULE_ELSE_TEMPLATE + POINt_FILED_RULE_TEMPLATE).replace("fill_color",QT_FILL_COLOR).replace("fill_opacity",QT_FILL_OPACITY).replace("stroke_color",QT_STROKE_COLOR).replace("stroke_opacity",QT_STROKE_OPACITY).replace("stroke_width",QT_STROKE_WIDTH);
        sb.append(rule);
        return LAYER_FILED_STYLE_TEMPLATE.replace("rule_list",sb.toString());
    }


    /**
     * 获得 通用 Multipolygon 图例 样式
     */
    public static String getLableMultipolygonTemplate(List<LayerStyleFiledParamsDTO> filedList){
        StringBuffer sb = new StringBuffer();
        for(LayerStyleFiledParamsDTO filed : filedList){
            String template = getTemplate(filed);
            String roleLable = "";
            if (StringUtil.isNotBlank(filed.getRuleValue())){
                roleLable = LEBLE_TEXT.replace("rule_value",filed.getRuleValue());
            }
            String rule = (template + POINT_LABLE_TEMPLATE + MULTIPOLYGON_FILED_RULE_TEMPLATE).replace("label_color",filed.getLabelColor()).replace("fill_color",filed.getFillColor()).replace("fill_opacity",filed.getFillOpacity()).replace("stroke_color",filed.getStrokeColor()).replace("stroke_opacity",filed.getStrokeOpacity()).replace("stroke_width",filed.getStrokeWidth()).replace("rule_label",roleLable);
            sb.append(rule);
        }
        // 图例其他颜色
        String rule = (RULE_ELSE_TEMPLATE + POINT_LABLE_TEMPLATE + MULTIPOLYGON_FILED_RULE_TEMPLATE).replace("label_color",LABEL_COLOR).replace("fill_color",QT_FILL_COLOR).replace("fill_opacity",QT_FILL_OPACITY).replace("stroke_color",QT_STROKE_COLOR).replace("stroke_opacity",QT_STROKE_OPACITY).replace("stroke_width",QT_STROKE_WIDTH).replace("rule_label","");
        sb.append(rule);
        return LAYER_FILED_STYLE_TEMPLATE.replace("rule_list",sb.toString());
    }

    /**
     * 获得 通用 Line 图例 样式
     */
    public static String getLableLineTemplate(List<LayerStyleFiledParamsDTO> filedList){
        StringBuffer sb = new StringBuffer();
        for(LayerStyleFiledParamsDTO filed : filedList){
            String template = getTemplate(filed);
            String roleLable = "";
            if (StringUtil.isNotBlank(filed.getRuleValue())){
                roleLable = LEBLE_TEXT.replace("rule_value",filed.getRuleValue());
            }
            String rule = (template + LINE_LABLE_TEMPLATE + LINE_FILED_RULE_TEMPLATE).replace("label_color",filed.getLabelColor()).replace("stroke_color",filed.getStrokeColor()).replace("stroke_opacity",filed.getStrokeOpacity()).replace("stroke_width",filed.getStrokeWidth()).replace("rule_label",roleLable);
            sb.append(rule);
        }
        // 图例其他颜色
        String rule = (RULE_ELSE_TEMPLATE + LINE_LABLE_TEMPLATE + LINE_FILED_RULE_TEMPLATE).replace("label_color",LABEL_COLOR).replace("stroke_color",QT_STROKE_COLOR).replace("stroke_opacity",QT_STROKE_OPACITY).replace("stroke_width",QT_STROKE_WIDTH).replace("rule_label","");
        sb.append(rule);
        return LAYER_FILED_STYLE_TEMPLATE.replace("rule_list",sb.toString());
    }

    /**
     * 获得 通用 Point 图例 样式
     */
    public static String getLablePointTemplate(List<LayerStyleFiledParamsDTO> filedList){
        StringBuffer sb = new StringBuffer();
        for(LayerStyleFiledParamsDTO filed : filedList){
            String template = getTemplate(filed);
            String roleLable = "";
            if (StringUtil.isNotBlank(filed.getRuleValue())){
                roleLable = LEBLE_TEXT.replace("rule_value",filed.getRuleValue());
            }
            String rule = (template + POINT_LABLE_TEMPLATE + POINt_FILED_RULE_TEMPLATE).replace("label_color",filed.getLabelColor()).replace("fill_color",filed.getFillColor()).replace("fill_opacity",filed.getFillColor()).replace("stroke_color",filed.getStrokeColor()).replace("stroke_opacity",filed.getStrokeOpacity()).replace("stroke_width",filed.getStrokeWidth()).replace("rule_label",roleLable);
            sb.append(rule);
        }
        // 图例其他颜色
        String rule = (RULE_ELSE_TEMPLATE + POINT_LABLE_TEMPLATE + POINt_FILED_RULE_TEMPLATE).replace("label_color",LABEL_COLOR).replace("fill_color",QT_FILL_COLOR).replace("fill_opacity",QT_FILL_OPACITY).replace("stroke_color",QT_STROKE_COLOR).replace("stroke_opacity",QT_STROKE_OPACITY).replace("stroke_width",QT_STROKE_WIDTH).replace("rule_label","");
        sb.append(rule);
        return LAYER_FILED_STYLE_TEMPLATE.replace("rule_list",sb.toString());
    }

    /**
     * 样式规则
     * @param filed
     * @return
     */
    private static String getTemplate(LayerStyleFiledParamsDTO filed){
        String template = "";
        //判断是否为范围分段
        if (Boolean.TRUE.equals(filed.getRangeFlag())){
            LayerStyleRangeDTO min = filed.getMin();
            LayerStyleRangeDTO max = filed.getMax();
            String range_template = "";
            if (min != null && StringUtil.isNotBlank(min.getValue())){
                String ruleCompare = null;
                if (LT.equals(min.getSymbol())){
                    ruleCompare = "PropertyIsGreaterThan";
                }
                if (LTE.equals(min.getSymbol())){
                    ruleCompare = "PropertyIsGreaterThanOrEqualTo";
                }
                if (StringUtil.isNotBlank(ruleCompare)){
                    range_template += RULE_RANGE_TEMPLATE_CHILD.replace("rule_compare",ruleCompare).replace("rule_key", min.getValue()).replace("rule_code", filed.getRuleCode());
                }
            }
            if (max != null && StringUtil.isNotBlank(max.getValue())){
                String ruleCompare = null;

                if (LT.equals(max.getSymbol())){
                    ruleCompare = "PropertyIsLessThan";
                }
                if (LTE.equals(max.getSymbol())){
                    ruleCompare = "PropertyIsLessThanOrEqualTo";
                }

                if (StringUtil.isNotBlank(ruleCompare)){
                    range_template += RULE_RANGE_TEMPLATE_CHILD.replace("rule_compare",ruleCompare).replace("rule_key", max.getValue()).replace("rule_code", filed.getRuleCode());
                }
            }
            template = RULE_RANGE_TEMPLATE.replace("rule_key",filed.getRuleValue()).replace("rule_range",range_template);
        }else {
            template = RULE_TEMPLATE.replace("rule_key",filed.getRuleKey()).replace("rule_code",filed.getRuleCode());
        }
        return template;
    }
}
