package hudson.plugins.performance.reports;

import hudson.plugins.performance.data.HttpSample;
import hudson.plugins.performance.data.TaurusFinalStats;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 * @author Artem Stasiuk (artem.stasuk@gmail.com)
 */
public class ThroughputReportTest {

    private final static double DELTA = 0.000001;
    private PerformanceReport performanceReport = new PerformanceReport();

    private ThroughputReport throughputReport = new ThroughputReport(performanceReport);

    @Test
    public void shouldReturnZeroIfNoUri() {
        assertEquals(0.0, throughputReport.get(), DELTA);
    }

    @Test
    public void shouldSummarizeThroughputByDifferentUri() {
        HttpSample httpSample1 = new HttpSample();
        Date date = new Date();
        httpSample1.setDate(date);

        UriReport uriReport1 = new UriReport(performanceReport, "f", "url1");
        uriReport1.addHttpSample(httpSample1);

        HttpSample httpSample2 = new HttpSample();
        httpSample2.setDate(date);

        UriReport uriReport2 = new UriReport(performanceReport, "f", "url2");
        uriReport2.addHttpSample(httpSample2);

        performanceReport.getUriReportMap().clear();
        performanceReport.getUriReportMap().put(uriReport1.getUri(), uriReport1);
        performanceReport.getUriReportMap().put(uriReport2.getUri(), uriReport2);

        assertEquals(2.0, throughputReport.get(), DELTA);
    }

    @Test
    public void shouldSummarizeThroughputUnder1ByDifferentUri() {
        HttpSample httpSample1 = new HttpSample();
        Date date = new Date();
        httpSample1.setDate(date);
        httpSample1.setDuration(1100);

        UriReport uriReport1 = new UriReport(null, "f", "url1");
        uriReport1.addHttpSample(httpSample1);

        HttpSample httpSample2 = new HttpSample();
        httpSample2.setDate(date);
        httpSample2.setDuration(1100);

        UriReport uriReport2 = new UriReport(null, "f", "url2");
        uriReport2.addHttpSample(httpSample2);

        performanceReport.getUriReportMap().clear();
        performanceReport.getUriReportMap().put(uriReport1.getUri(), uriReport1);
        performanceReport.getUriReportMap().put(uriReport2.getUri(), uriReport2);

        assertEquals(2.0 / 1100 * 1000, throughputReport.get(), DELTA);
    }

    @Test
    public void testThroughputJMeterReport() throws Exception {
        long time = System.currentTimeMillis();

        HttpSample firstSample = new HttpSample();
        firstSample.setDate(new Date(time));
        firstSample.setDuration(1000);

        HttpSample secondSample = new HttpSample();
        secondSample.setDate(new Date(time + 2000));
        secondSample.setDuration(1000);

        HttpSample thirdSample = new HttpSample();
        thirdSample.setDate(new Date(time + 3000));
        thirdSample.setDuration(1000);

        HttpSample lastSample = new HttpSample();
        lastSample.setDate(new Date(time + 8000));
        lastSample.setDuration(1000);



        UriReport uriReport1 = new UriReport(null, "f", "url1");
        uriReport1.addHttpSample(firstSample);
        uriReport1.addHttpSample(thirdSample);


        UriReport uriReport2 = new UriReport(null, "f", "url2");
        uriReport2.addHttpSample(secondSample);
        uriReport2.addHttpSample(lastSample);

        performanceReport.getUriReportMap().clear();
        performanceReport.getUriReportMap().put(uriReport1.getUri(), uriReport1);
        performanceReport.getUriReportMap().put(uriReport2.getUri(), uriReport2);

        assertEquals((4 / (9000.0 / 1000)), throughputReport.get(), DELTA);
    }

    @Test
    public void testThroughputTaurusReport() throws Exception {
        performanceReport.getUriReportMap().clear();

        TaurusFinalStats stats = new TaurusFinalStats();
        stats.setThroughput(777);
        stats.setLabel("777");
        performanceReport.addSample(stats, true);

        assertEquals(777, throughputReport.get(), DELTA);
    }
}
