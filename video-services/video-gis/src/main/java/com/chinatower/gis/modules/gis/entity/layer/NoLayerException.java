package com.chinatower.gis.modules.gis.entity.layer;

/**
 * 没有图层异常
 */
public class NoLayerException extends Exception{
    public NoLayerException(String message) {
        super(message);
    }
}
