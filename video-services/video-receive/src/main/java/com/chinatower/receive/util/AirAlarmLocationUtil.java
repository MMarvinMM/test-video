package com.chinatower.receive.util;
import com.alibaba.fastjson.JSON;
import com.chinatower.receive.vo.dto.AirAlarmLocationReq;
import lombok.extern.slf4j.Slf4j;

/**
 * 根据无人机实时遥测数据计算事发地点经纬度工具类
 * @Author: songxinfeng
 * @Date: 2024/12/9 10:05
 */
@Slf4j
public class AirAlarmLocationUtil {

    public static class DroneCalculations {
        // 内参矩阵K
        double[][] K = new double[3][3];

        // 计算内参矩阵K
        void calculateK(double f, double k, double cx, double cy) {
            K[0][0] = f * k;
            K[0][2] = cx;
            K[1][1] = f * k;
            K[1][2] = cy;
            K[2][2] = 1;
        }


        // 旋转矩阵R
        double[][] calculateRotationMatrix(double yaw, double pitch, double roll) {
            double cosYaw = Math.cos(yaw);
            double sinYaw = Math.sin(yaw);
            double cosPitch = Math.cos(pitch);
            double sinPitch = Math.sin(pitch);
            double cosRoll = Math.cos(roll);
            double sinRoll = Math.sin(roll);

            double[][] R = new double[3][3];
            R[0][0] = cosYaw * cosPitch;
            R[0][1] = cosYaw * sinPitch * sinRoll - sinYaw * cosRoll;
            R[0][2] = cosYaw * sinPitch * cosRoll + sinYaw * sinRoll;
            R[1][0] = sinYaw * cosPitch;
            R[1][1] = sinYaw * sinPitch * sinRoll + cosYaw * cosRoll;
            R[1][2] = sinYaw * sinPitch * cosRoll - cosYaw * sinRoll;
            R[2][0] = -sinPitch;
            R[2][1] = cosPitch * sinRoll;
            R[2][2] = cosPitch * cosRoll;

            return R;
        }

        // 像素坐标转相机坐标
        double[][] pixelToCamera(double u, double v, double[][] K) {
            double[][] X_c = new double[3][1];
            X_c[0][0] = (u - K[0][2]) / K[0][0];
            X_c[1][0] = (v - K[1][2]) / K[1][1];
            X_c[2][0] = 1;
            return X_c;
        }

        // 相机坐标转世界坐标
        double[][] cameraToWorld(double[][] X_c, double[][] R, double[] T) {
            double[][] X_w = new double[3][1];
            // 计算R的转置
            double[][] R_transpose = new double[3][3];
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    R_transpose[i][j] = R[j][i];
                }
            }
            // 矩阵乘法 R^T * X_c
            for (int i = 0; i < 3; i++) {
                double sum = 0;
                for (int j = 0; j < 3; j++) {
                    sum += R_transpose[i][j] * X_c[j][0];
                }
                X_w[i][0] = sum;
            }
            // 矩阵加法 R^T * X_c + T
            for (int i = 0; i < 3; i++) {
                X_w[i][0] += T[i];
            }
            return X_w;
        }

        // 地面坐标转经纬度
        double[] groundToLatLon(double X_w, double Y_w, double Z_w,double Lat_d,double Lon_d) {
            double R_e = 6371000; // 地球半径
            double x = X_w;
            double y = Y_w;
            double deltaLon = x / (R_e * Math.cos(Math.toRadians(Lat_d)));
            double deltaLat = y / R_e;
            double[] latLon = new double[2];
            latLon[0] = Lat_d + deltaLat;
            latLon[1] = Lon_d + deltaLon;
            return latLon;
        }


        double[][] matrixMultiply(double[][] matrix1, double[][] matrix2){
            int rows1 = matrix1.length;
            int cols1 = matrix1[0].length;
            int cols2 = matrix2[0].length;

            // 检查矩阵维度是否满足乘法规则（第一个矩阵的列数要等于第二个矩阵的行数）
            if (cols1!= matrix2.length) {
                throw new IllegalArgumentException("矩阵维度不满足乘法规则");
            }

            double[][] result = new double[rows1][cols2];

            // 进行矩阵乘法运算
            for (int i = 0; i < rows1; i++) {
                for (int j = 0; j < cols2; j++) {
                    double sum = 0;
                    for (int k = 0; k < cols1; k++) {
                        sum += matrix1[i][k] * matrix2[k][j];
                    }
                    result[i][j] = sum;
                }
            }

            return result;
        }
    }

    /**
     * 根据无人机实时遥测数据计算事发地点经纬度工具类
     * @param req
     * @return
     */
    public static double[] getAirAlarmLocation(AirAlarmLocationReq req){
        log.info("AirAlarmLocationUtil-getAirAlarmLocation-req:{}", JSON.toJSONString(req));
        DroneCalculations calculations = new DroneCalculations();
        // 设置参数
        double f = req.getF(); // 焦距
        double k = req.getK(); // 相机放大倍数
        double cx = req.getCx(); // 主点x偏移
        double cy = req.getCy(); // 主点y偏移
        double yaw = req.getYaw(); // 无人机航向角
        double pitch = req.getPitch(); // 无人机俯仰角
        double roll = req.getRoll(); // 无人机横滚角
        double yaw1 = req.getYaw1(); // 云台航向角
        double pitch1 = req.getPitch1(); // 云台俯仰角
        double roll1 = req.getRoll1(); // 云台横滚角
        double Lat_d = req.getLat_d(); // 无人机纬度
        double Lon_d = req.getLon_d(); // 无人机经度
        double u = req.getU(); // 像素坐标u
        double v = req.getV(); // 像素坐标v

        // 计算内参矩阵K
        calculations.calculateK(f, k, cx, cy);

        // 计算旋转矩阵R
        double[][] R = calculations.matrixMultiply(calculations.calculateRotationMatrix(yaw, pitch, roll),calculations.calculateRotationMatrix(yaw1, pitch1, roll1));

        // 像素坐标转相机坐标
        double[][] X_c = calculations.pixelToCamera(u, v, calculations.K);

        // 相机坐标转世界坐标
        double[] T = new double[]{0, 0, 0}; // 平移向量
        double[][] X_w = calculations.cameraToWorld(X_c, R, T);

        // 地面坐标转经纬度
        double[] latLon = calculations.groundToLatLon(X_w[0][0], X_w[1][0], X_w[2][0],Lat_d,Lon_d);
        log.info("Latitude: " + latLon[0] + ", Longitude: " + latLon[1]);
        // 检测是否为 NaN
        if (Double.isNaN(latLon[0]) || Double.isNaN(latLon[1])) {
            log.error("计算结果为NaN，纬度：{}，经度：{}，返回null", latLon[0], latLon[1]);
            return null;
        }
        return latLon;
    }
}
