package com.zzammo.calendar.holiday;

import android.content.Context;
import android.util.Log;
import android.util.Xml;
import android.widget.Toast;

import com.zzammo.calendar.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

public class ApiExplorer {
    private Context context;
    ArrayList<String> namelist;
    ArrayList<String> datelist;
    public ApiExplorer(){namelist = new ArrayList<>(); datelist = new ArrayList<>();}
    public ApiExplorer(Context c) {context = c; namelist = new ArrayList<>(); datelist = new ArrayList<>();}
    public void getHolidays(int Year, int Month) throws IOException, XmlPullParserException {
        Log.d("WeGlonD", "11");
        String key = "Iw10j3Acrg3WrDmYl0%2FXMM08AJStZ0qjmSPPaS2Lb7oazhsvcADHN2v%2BkkSotAJWWHqErRHUUK9no0Z8o6hLrA%3D%3D";
        String year = Integer.valueOf(Year).toString(); String month = Integer.valueOf(Month).toString();
        if(Month < 10) month = "0" + month;
        String urlBuilder = "http://apis.data.go.kr/B090041/openapi/service/SpcdeInfoService/getHoliDeInfo" +
                "?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + key + /*Service Key*/
                "&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8") + /*페이지번호*/
                "&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("10", "UTF-8") + /*한 페이지 결과 수*/
                "&" + URLEncoder.encode("solYear", "UTF-8") + "=" + URLEncoder.encode(year, "UTF-8") + /*연*/
                "&" + URLEncoder.encode("solMonth", "UTF-8") + "=" + URLEncoder.encode(month, "UTF-8"); /*월*/
        URL url = new URL(urlBuilder);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");
        Log.d("WeGlonD", "Response code: " + conn.getResponseCode());
        BufferedReader rd;
        if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(rd);
        int eventType = parser.getEventType();

        while(eventType!=XmlPullParser.END_DOCUMENT){
            String name = null;
            if(eventType == XmlPullParser.START_TAG){
                name = parser.getName();
                if(name.equals("dateName")){
                    eventType = parser.next();
                    if(eventType==XmlPullParser.TEXT){
                        namelist.add(parser.getText());
                    }
                }else if(name.equals("locdate")){
                    eventType = parser.next();
                    if(eventType==XmlPullParser.TEXT) {
                        datelist.add(parser.getText());
                    }
                }
            }
            eventType = parser.next();
        }
        for(int i = 0; i < datelist.size(); i++){
            Log.d("WeGlonD", datelist.get(i) + ' ' + namelist.get(i));
        }

        rd.close();
        conn.disconnect();
    }
}