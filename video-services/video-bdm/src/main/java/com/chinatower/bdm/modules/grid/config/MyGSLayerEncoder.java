package com.chinatower.bdm.modules.grid.config;

import it.geosolutions.geoserver.rest.encoder.GSLayerEncoder;

public class MyGSLayerEncoder extends GSLayerEncoder {

    public MyGSLayerEncoder() {
        super();
    }

    public void setDefaultRenderingBuffer(String buffer) throws IllegalArgumentException {
        if (buffer != null && !buffer.isEmpty()) {
            this.set("metadata/buffer", buffer);
        } else {
            throw new IllegalArgumentException("Unable to set an empty or null parameter");
        }
    }
}
