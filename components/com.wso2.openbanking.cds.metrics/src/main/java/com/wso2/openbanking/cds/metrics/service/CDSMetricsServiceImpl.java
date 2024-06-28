/*
 * Copyright (c) 2021-2024, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package com.wso2.openbanking.cds.metrics.service;

import com.google.gson.Gson;
import com.wso2.openbanking.accelerator.common.exception.OpenBankingException;
import com.wso2.openbanking.cds.common.config.OpenBankingCDSConfigParser;
import com.wso2.openbanking.cds.metrics.cache.MetricsCache;
import com.wso2.openbanking.cds.metrics.constants.MetricsConstants;
import com.wso2.openbanking.cds.metrics.data.MetricsDataProvider;
import com.wso2.openbanking.cds.metrics.data.MetricsV5DataProvider;
import com.wso2.openbanking.cds.metrics.model.MetricsResponseModel;
import com.wso2.openbanking.cds.metrics.util.MetricsServiceUtil;
import com.wso2.openbanking.cds.metrics.util.PeriodEnum;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Date;

/**
 * Implementation of CDS Admin metrics service.
 */
public class CDSMetricsServiceImpl implements CDSMetricsService {

    private static final Log log = LogFactory.getLog(CDSMetricsServiceImpl.class);
    private static final ZoneId TIME_ZONE = ZoneId.of(OpenBankingCDSConfigParser.getInstance().getMetricsTimeZone());

    /**
     * {@inheritDoc}
     */
    @Override
    public MetricsResponseModel getMetrics(String xV, PeriodEnum period) throws OpenBankingException {

        MetricsResponseModel metricsResponseModel;
        String requestTime = new SimpleDateFormat(MetricsConstants.REQUEST_TIMESTAMP_PATTERN).format(new Date());

        switch (period) {
            case CURRENT:
                metricsResponseModel = getCurrentDayMetrics(requestTime);
                break;
            case HISTORIC:
                metricsResponseModel = getHistoricMetrics(requestTime);
                break;
            default:
                metricsResponseModel = getAllMetrics(requestTime);
        }
        return metricsResponseModel;
    }

    /**
     * Get current day metrics.
     *
     * @param requestTime Request time
     * @return ResponseMetricsListModel
     * @throws OpenBankingException OpenBankingException
     */
    public MetricsResponseModel getCurrentDayMetrics(String requestTime) throws OpenBankingException {

        MetricsQueryCreator metricsV5QueryCreator = new MetricsV5QueryCreatorImpl(PeriodEnum.CURRENT);
        MetricsDataProvider metricsV5DataProvider = new MetricsV5DataProvider(metricsV5QueryCreator);
        MetricsProcessor metricsV5Processor = new MetricsV5ProcessorImpl(PeriodEnum.CURRENT,
                metricsV5DataProvider, TIME_ZONE);
        MetricsFetcher metricsFetcherV5Current = new MetricsV5FetcherImpl(metricsV5Processor);
        return metricsFetcherV5Current.getResponseMetricsListModel(requestTime);
    }

    /**
     * Get historic metrics.
     * Uses cache if available, otherwise fetches from analytics server.
     *
     * @param requestTime Request time
     * @return ResponseMetricsListModel
     * @throws OpenBankingException OpenBankingException
     */
    public MetricsResponseModel getHistoricMetrics(String requestTime) throws OpenBankingException {

        MetricsResponseModel metricsResponseModelHistoric;
        metricsResponseModelHistoric = getCachedHistoricMetrics();
        if (metricsResponseModelHistoric == null) {
            log.debug("Getting historic metrics from analytics server since cached model is not found.");
            metricsResponseModelHistoric = getRealtimeHistoricMetrics(requestTime);
            log.debug("Historic metrics retrieval completed.");
        } else {
            metricsResponseModelHistoric.setRequestTime(requestTime);
        }
        return metricsResponseModelHistoric;
    }

    /**
     * Get historic metrics from analytics server.
     *
     * @param requestTime Request time
     * @return ResponseMetricsListModel
     * @throws OpenBankingException OpenBankingException
     */
    private MetricsResponseModel getRealtimeHistoricMetrics(String requestTime) throws OpenBankingException {

        MetricsQueryCreator metricsV5QueryCreator = new MetricsV5QueryCreatorImpl(PeriodEnum.HISTORIC);
        MetricsDataProvider metricsDataProvider = new MetricsV5DataProvider(metricsV5QueryCreator);
        MetricsProcessor metricsProcessor = new MetricsV5ProcessorImpl(
                PeriodEnum.HISTORIC, metricsDataProvider, TIME_ZONE);
        MetricsFetcher metricsFetcherV5Historic = new MetricsV5FetcherImpl(metricsProcessor);
        return metricsFetcherV5Historic.getResponseMetricsListModel(requestTime);
    }

    /**
     * Get historic metrics from cache.
     *
     * @return ResponseMetricsListModel, or null if cache is not available or model is expired.
     */
    private MetricsResponseModel getCachedHistoricMetrics() {

        MetricsCache metricsCache = MetricsCache.getInstance();
        Object cachedResponseMetricsJson = metricsCache.getFromCache(MetricsCache.getHistoricMetricsCacheKey());
        if (cachedResponseMetricsJson != null) {
            log.debug("Historic metrics model found in cache.");
            MetricsResponseModel metricsResponseModel = new Gson().fromJson(
                    (String) cachedResponseMetricsJson, MetricsResponseModel.class);
            return MetricsServiceUtil.isResponseModelExpired(metricsResponseModel) ? null : metricsResponseModel;
        }
        log.error("Historic metrics model not found in cache.");
        return null;
    }

    /**
     * Get all metrics.
     * Uses cache for historic metrics if available, otherwise fetches all metrics from analytics server.
     *
     * @param requestTime Request time
     * @return ResponseMetricsListModel
     * @throws OpenBankingException OpenBankingException
     */
    public MetricsResponseModel getAllMetrics(String requestTime) throws OpenBankingException {

        MetricsResponseModel metricsResponseModelHistoric = getCachedHistoricMetrics();
        MetricsResponseModel metricsResponseModel;

        if (metricsResponseModelHistoric != null) {
            metricsResponseModel = getCurrentDayMetrics(requestTime);
            MetricsServiceUtil.appendHistoricMetricsToCurrentDayMetrics(metricsResponseModel,
                    metricsResponseModelHistoric);
        } else {
            log.debug("Getting all metrics from analytics server since cached model is not found.");
            MetricsQueryCreator metricsV5QueryCreator = new MetricsV5QueryCreatorImpl(PeriodEnum.ALL);
            MetricsDataProvider metricsDataProvider = new MetricsV5DataProvider(metricsV5QueryCreator);
            MetricsProcessor metricsProcessor = new MetricsV5ProcessorImpl(
                    PeriodEnum.ALL, metricsDataProvider, TIME_ZONE);
            MetricsFetcher metricsFetcherV5Current = new MetricsV5FetcherImpl(metricsProcessor);
            metricsResponseModel = metricsFetcherV5Current.getResponseMetricsListModel(requestTime);
            log.debug("All metrics retrieval completed.");
        }
        return metricsResponseModel;
    }
}
