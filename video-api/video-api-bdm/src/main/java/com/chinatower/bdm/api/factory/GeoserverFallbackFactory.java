package com.chinatower.bdm.api.factory;

import com.chinatower.bdm.api.GeoserverAPI;
import com.chinatower.bdm.api.GridAPI;
import com.chinatower.bdm.api.entity.GeoGroupVO;
import com.chinatower.bdm.api.entity.GeoVO;
import com.chinatower.common.entity.Result;
import com.chinatower.platform.model.dto.GeoserverWfsDTO;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class GeoserverFallbackFactory implements FallbackFactory<GeoserverAPI> {

    @Override
    public GeoserverAPI create(Throwable throwable) {

        return new GeoserverAPI() {
            @Override
            public Result<Object> wfs(GeoserverWfsDTO dto) {
                return Result.fail("查询Geoserver WFS 失败" + throwable.getMessage());
            }

            @Override
            public Result<Void> uploadGeoStyle(MultipartFile file) {
                return Result.fail("上传样式失败" + throwable.getMessage());
            }

            @Override
            public Result<Void> deleteGeoStyle(GeoVO vo) {
                return Result.fail("删除样式失败" + throwable.getMessage());
            }

            @Override
            public Result<Void> modifyLayerStyle(GeoVO vo) {
                return Result.fail("修改图层样式失败" + throwable.getMessage());
            }

            @Override
            public Result<Void> publishShp(MultipartFile file, String styleName) {
                return Result.fail("发布SHP图层失败" + throwable.getMessage());
            }

            @Override
            public Result<Void> publishTif(MultipartFile file, String styleName) {
                return Result.fail("发布tiff失败" + throwable.getMessage());
            }

            @Override
            public Result<Void> publishPostGis(GeoVO vo) {
                return Result.fail("发布postgis图层失败" + throwable.getMessage());
            }

            @Override
            public Result<Void> publishLayerGroup(GeoGroupVO vo) {
                return Result.fail("发布图层组失败" + throwable.getMessage());
            }

            @Override
            public Result<Void> modifyLayerGroup(GeoGroupVO vo) {
                return Result.fail("修改图层组失败" + throwable.getMessage());
            }
        };
    }
}
