package tj.gfit;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Converts Google's FIT TCX format to CSV
 */
public class TCX2CSV extends DefaultHandler {
    private static final String dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private static final SimpleDateFormat dateFormatter = new SimpleDateFormat(dateFormat);
    private final List<TrackPoint> trackPointList = new LinkedList<>();
    private TrackPoint currentTrackPoint = null;

    private boolean isDistance;
    private boolean isTime;
    private boolean isHR;
    private boolean isLatitude;
    private boolean isLongitude;

    private Double lastKnownDistance = null;
    private Date lastValidTime = null;

    public TCX2CSV() {
    }

    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) throws SAXException {
        if ("Trackpoint".equals(qName)) {
            currentTrackPoint = new TrackPoint();
        } else if ("DistanceMeters".equals(qName)) {
            isDistance = true;
        } else if ("Time".equals(qName)) {
            isTime = true;
        } else if ("Value".equals(qName)) {
            isHR = true;
        } else if ("LatitudeDegrees".equals(qName)) {
            isLatitude = true;
        } else if ("LongitudeDegrees".equals(qName)) {
            isLongitude = true;
        }
    }

    @lombok.SneakyThrows
    @Override
    public void characters(final char[] ch, final int start, final int length) throws SAXException {
        final String dataValue = new String(ch, start, length);

        if (isDistance) {
            currentTrackPoint.setDistanceMeters(Double.parseDouble(dataValue));
            isDistance = false;
        } else if (isTime) {
            currentTrackPoint.setDateTime(dataValue);
            currentTrackPoint.setDate(dateFormatter.parse(dataValue));
            isTime = false;
        } else if (isHR) {
            currentTrackPoint.setHeartRate(Double.parseDouble(dataValue));
            isHR = false;
        } else if (isLatitude) {
            currentTrackPoint.setLatitudeDegrees(Double.parseDouble(dataValue));
            isLatitude = false;
        } else if (isLongitude) {
            currentTrackPoint.setLongitudeDegrees(Double.parseDouble(dataValue));
            isLongitude = false;
        }
    }

    @Override
    public void endElement(final String uri, final String localName, final String qName) throws SAXException {
        if ("Trackpoint".equals(qName)) {
            trackPointList.add(currentTrackPoint);
            if (lastKnownDistance == null) {
                lastKnownDistance = currentTrackPoint.getDistanceMeters();
                lastValidTime = currentTrackPoint.getDate();
            } else {
                final Double currentDistance = currentTrackPoint.getDistanceMeters();
                final Date currentTime = currentTrackPoint.getDate();
                // Calculate speed
                final Double distance = currentDistance - lastKnownDistance;
                if (distance > 0) {
                    final long timeSpan = (currentTime.getTime() - lastValidTime.getTime()) / 1_000;
                    if (timeSpan > 0) {
                        final double speedMPerS = distance / (double)timeSpan;
                        currentTrackPoint.setSpeed(speedMPerS * 3.6);
                        lastKnownDistance = currentDistance;
                        lastValidTime = currentTime;
                    }
                }
            }
        }
    }

    private void saveCSV() throws Exception {
        final FileWriter fileWriter = new FileWriter("oc.csv");
        final PrintWriter printWriter = new PrintWriter(fileWriter);
        printWriter.println("dt, d, dm, hr, s, lat, lon");

        trackPointList.forEach(tp -> {
            final String dt = tp.getDateTime();
            final Date d = tp.getDate();
            final Double dm = tp.getDistanceMeters();
            final String hr = tp.getHeartRate() == null ? "" : "" + tp.getHeartRate();
            final String s = tp.getSpeed() == null ? "" : "" + tp.getSpeed();
            final String lat = tp.getLatitudeDegrees() == null ? "" : "" + tp.getLatitudeDegrees();
            final String lon = tp.getLongitudeDegrees() == null ? "" : "" + tp.getLongitudeDegrees();

            printWriter.println(dt + ", " + d + ", " + dm + ", " + hr + ", " + s + ", " + lat + ", " + lon);
        });

        fileWriter.flush();
        fileWriter.close();
    }

    public static void main(String[] args) throws Exception {
        log("TCX2CSV");

        final File tcxFile = new File(TCX2CSV.class.getResource("/oc.tcx").toURI());
        final SAXParserFactory factory = SAXParserFactory.newInstance();
        final SAXParser saxParser = factory.newSAXParser();
        final TCX2CSV tcxHandler = new TCX2CSV();
        saxParser.parse(tcxFile, tcxHandler);

        log("Parsed " + tcxHandler.trackPointList.size() + " track points");
//        log(tcxHandler.trackPointList);

        tcxHandler.saveCSV();

        System.exit(0);
    }

    static void log(final Object o) {
        System.out.println(o);
    }
}
