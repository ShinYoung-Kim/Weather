package com.ksy.mpl;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class WeatherData extends Thread {
    public String getWeatherState() {
        return weatherState;
    }

    public Double getWeatherTemp() {
        return weatherTemp;
    }

    private String nx = "55";
    private String ny = "127";
    private String baseDate = "20221012";
    private String baseTime = "0630";
    private String type = "json";
    private String weatherState = "";
    private Double weatherTemp = 0.0;
    private String pageNo = "1";
    private String numOfRows = "1000";

    public String[] lookUpWeather() throws IOException, JSONException {
        String apiUrl = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtFcst";
        String serviceKey = "bblped1fj1rwoLuY06oOCNnw%2B%2FnW97U1cZXfubIkK1YpznRPiOsi7dHb%2F%2FarMS1Buk7nLZ917PG%2Bc8bFdz%2F%2F%2Fw%3D%3D";
        StringBuilder urlBuilder = new StringBuilder(apiUrl);
        urlBuilder.append("?" + URLEncoder.encode("ServiceKey","UTF-8") + "="+serviceKey);
        urlBuilder.append("&" + URLEncoder.encode("nx","UTF-8") + "=" + URLEncoder.encode(nx, "UTF-8")); //경도
        urlBuilder.append("&" + URLEncoder.encode("ny","UTF-8") + "=" + URLEncoder.encode(ny, "UTF-8")); //위도
        urlBuilder.append("&" + URLEncoder.encode("base_date","UTF-8") + "=" + URLEncoder.encode(baseDate, "UTF-8")); /* 조회하고싶은 날짜*/
        urlBuilder.append("&" + URLEncoder.encode("base_time","UTF-8") + "=" + URLEncoder.encode(baseTime, "UTF-8")); /* 조회하고싶은 시간 AM 02시부터 3시간 단위 */
        urlBuilder.append("&" + URLEncoder.encode("dataType","UTF-8") + "=" + URLEncoder.encode(type, "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode(numOfRows, "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode(pageNo, "UTF-8"));	/* 타입 *//* 타입 */
        /* 타입 */

        /*
         * GET방식으로 전송해서 파라미터 받아오기
         */
        URL url = new URL(urlBuilder.toString());
        Log.d("Url", url.toString());

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");
        //System.out.println("Response code: " + conn.getResponseCode());

        BufferedReader rd;
        if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }

        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }

        rd.close();
        conn.disconnect();
        String result= sb.toString();
        Log.d("error catching", result);

        //=======이 밑에 부터는 json에서 데이터 파싱해 오는 부분이다=====//

        // response 키를 가지고 데이터를 파싱
        JSONObject jsonObj_1 = new JSONObject(result);
        String response = jsonObj_1.getString("response");

        Log.d("response", response);
        // response 로 부터 body 찾기
        JSONObject jsonObj_2 = new JSONObject(response);
        String body = jsonObj_2.getString("body");
        Log.d("body", body);

        // body 로 부터 items 찾기
        JSONObject jsonObj_3 = new JSONObject(body);
        String items = jsonObj_3.getString("items");
        Log.i("ITEMS",items);
        Log.d("items", items);

        // items로 부터 itemlist 를 받기
        JSONObject jsonObj_4 = new JSONObject(items);
        JSONArray jsonArray = jsonObj_4.getJSONArray("item");

        for(int i=0;i<jsonArray.length();i++){
            jsonObj_4 = jsonArray.getJSONObject(i);
            String fcstValue = jsonObj_4.getString("fcstValue");
            String category = jsonObj_4.getString("category");
            String fcstTime = jsonObj_4.getString("fcstTime");

            if (fcstTime.equals("0700")) {
                Log.d("category", category);
                Log.d("fcstValue", fcstValue);
                if(category.equals("PTY")){
                    if (Integer.parseInt(fcstValue) > 0) {
                        weatherState = "비";
                    }
                }

                if(category.equals("T3H") || category.equals("T1H")){
                    weatherTemp = Double.valueOf(fcstValue);
                }

                if(category.equals("SKY")){
                    if (fcstValue.equals("1")) {
                        weatherState = "맑음";
                    } else if (fcstValue.equals("3")) {
                        weatherState = "구름많음";
                    } else if (fcstValue.equals("4")) {
                        weatherState = "흐림";
                    } else {
                        weatherState = fcstValue;
                    }
                }
            }
        }
        Log.d("weatherState", weatherState);
        Log.d("weatherTemp", weatherTemp.toString());
        return new String[] {weatherState, weatherTemp.toString()};
    }
}
