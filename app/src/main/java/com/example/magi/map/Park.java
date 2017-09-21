package com.example.magi.map;

import com.baidu.mapapi.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Park {
    private static String ak = "K3rIDlaBoyjTPiC5iw02uyFUYDnDOwZP";
    private static String geotable_id = "176724";
    private static String radius = "5000";
    private static String page_size = "50";
    private static String sn;


    public static PoiList getPark(final LatLng latLng, int currPage){
        HttpURLConnection connection;

        Map map = new LinkedHashMap<String, String>();
        map.put("location", latLng.longitude + "," + latLng.latitude);
        map.put("page_index", String.valueOf(currPage));
        map.put("ak", ak);
        map.put("geotable_id", geotable_id);
        map.put("radius", radius);
        map.put("page_size", page_size);
        sn = SNCal.getSn(map);

        try{
            URL url = new URL("http://api.map.baidu.com/geosearch/v3/nearby?location=" + latLng.longitude + "," + latLng.latitude + "&page_index=" + currPage +
                    "&ak=" + ak + "&geotable_id=" + geotable_id + "&radius=" + radius + "&page_size=" + page_size + "&sn=" + sn);
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            InputStream in = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            PoiList list = ParseJsonObjectToJsonArray(response.toString());
            return list;
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    private static PoiList ParseJsonObjectToJsonArray(String jsonData) {
        PoiList poiList = new PoiList();
        try{
            JSONObject jsonObject = new JSONObject(jsonData);
            poiList.setTotalNum(jsonObject.getInt("total"));
            poiList.setCurrSize(jsonObject.getInt("size"));
            JSONArray contens = jsonObject.getJSONArray("contents");
            List<Storage> lst = new ArrayList<>();
            for(int i = 0; i < contens.length(); i++){
                JSONObject item = contens.getJSONObject(i);
                Storage storage = new Storage();
                storage.setName(item.getString("title"));
                storage.setAddress(item.getString("address"));
                storage.setTotal(item.getInt("total"));
                storage.setEmpty(item.getInt("empty"));
                storage.setTime1(item.getInt("time1"));
                storage.setTime2(item.getInt("time2"));
                storage.setTime3(item.getInt("time3"));
                storage.setStyle(item.getString("style"));
                storage.setUid("1");
                JSONArray location = item.getJSONArray("location");
                storage.setLocation(new LatLng(location.getDouble(1), location.getDouble(0)));
                lst.add(storage);
            }
            poiList.setStorageList(lst);
            return poiList;
        }
        catch (JSONException e){
            e.printStackTrace();
            return null;
        }
    }
}
