package com.timbuchalka.top10downloader;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;

/**
 * Created by Shadow on 11/16/2016.
 */

public class ParseApplications {
    private static final String TAG = "ParseApplications";

    public ArrayList<FeedEntry> getApplications() {
        return applications;
    }

    private ArrayList<FeedEntry> applications;

    public ParseApplications() {
        this.applications = new ArrayList<>();
    }

    public boolean parse(String xmlData)
    {
        boolean status = true;
        FeedEntry currentRecord = null;
        boolean inEntry = false;
        String textvalue = "";

        try{
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(xmlData));
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT)
            {
                String tagName = xpp.getName();
                switch (eventType)
                {
                    case XmlPullParser.START_TAG:
                        //Log.d(TAG, "parse: Starting tag for " + tagName);
                        if("entry".equalsIgnoreCase(tagName))
                        {
                            inEntry = true;
                            currentRecord = new FeedEntry();
                        }
                        break;
                    case XmlPullParser.TEXT:
                        //Log.d(TAG, "parse: Text for +" + tagName);
                        textvalue = xpp.getText();
                        break;
                    case XmlPullParser.END_TAG:
                        //Log.d(TAG, "parse: Ending tag for " + tagName);
                        if(inEntry)
                        {
                            if("entry".equalsIgnoreCase(tagName))
                            {
                                applications.add(currentRecord);
                                inEntry = false;
                            }
                            else if("name".equalsIgnoreCase(tagName)) currentRecord.setName(textvalue);
                            else if("artist".equalsIgnoreCase(tagName)) currentRecord.setArtist(textvalue);
                            else if("releaseDate".equalsIgnoreCase(tagName)) currentRecord.setReleaseDate(textvalue);
                            else if("summary".equalsIgnoreCase(tagName)) currentRecord.setSummary(textvalue);
                            else if("image".equalsIgnoreCase(tagName)) currentRecord.setImageURL(textvalue);
                        }
                        break;
                    default:
                        //break;
                }
                eventType = xpp.next();
            }
            for (FeedEntry app: applications)
            {
                Log.d(TAG, "parse: ***************");
                Log.d(TAG, app.toString());
            }
        }
        catch(Exception e)
        {
            status = false;
            e.printStackTrace();
        }

        return status;
    }
}
