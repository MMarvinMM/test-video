package com.chinatower.bdm.modules.grid.config;

import it.geosolutions.geoserver.rest.GeoServerRESTPublisher;
import it.geosolutions.geoserver.rest.GeoServerRESTReader;
import it.geosolutions.geoserver.rest.manager.GeoServerRESTAbstractManager;
import it.geosolutions.geoserver.rest.manager.GeoServerRESTStoreManager;
import it.geosolutions.geoserver.rest.manager.GeoServerRESTStructuredGridCoverageReaderManager;

import java.net.URL;
@SuppressWarnings("all")
public class MyGeoServerRESTManager extends GeoServerRESTAbstractManager {

    private final MyGeoServerRESTPublisher publisher;
    private final GeoServerRESTReader reader;
    private final GeoServerRESTStoreManager storeManager;
    private final MyGeoServerRESTStyleManager styleManager;
    private final GeoServerRESTStructuredGridCoverageReaderManager structuredGridCoverageReader;

    public MyGeoServerRESTManager(URL restURL, String username, String password) throws IllegalArgumentException {
        super(restURL, username, password);
        this.publisher = new MyGeoServerRESTPublisher(restURL.toString(), username, password);
        this.reader = new GeoServerRESTReader(restURL, username, password);
        this.structuredGridCoverageReader = new GeoServerRESTStructuredGridCoverageReaderManager(restURL, username, password);
        this.storeManager = new GeoServerRESTStoreManager(restURL, this.gsuser, this.gspass);
        this.styleManager = new MyGeoServerRESTStyleManager(restURL, this.gsuser, this.gspass);
    }

    public MyGeoServerRESTPublisher getPublisher() {
        return this.publisher;
    }

    public GeoServerRESTReader getReader() {
        return this.reader;
    }

    public GeoServerRESTStoreManager getStoreManager() {
        return this.storeManager;
    }

    public MyGeoServerRESTStyleManager getStyleManager() {
        return this.styleManager;
    }

    public GeoServerRESTStructuredGridCoverageReaderManager getStructuredGridCoverageReader() {
        return this.structuredGridCoverageReader;
    }
}
