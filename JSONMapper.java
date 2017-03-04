package com.technologies.future;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Orgie on 05.02.2017.
 */
 
public class JSONMapper {

    private Map<String, Object> map  = new HashMap<String, Object>();
    private String type;
    private JSONObject json;

    public List<Object> JSONArrayLoop(JSONArray json){

        List<Object> list  = new ArrayList<Object>();

        Object value = null;

        for(int i = 0; i < json.length(); i++){

            value = null;
            try {
                value = json.get(i);
            } catch (JSONException e) {
                // Something went wrong!
            }

            if(value.getClass() == JSONArray.class) {
                value = JSONArrayLoop((JSONArray) value);
            }else if(value.getClass() == JSONObject.class){
                value = JSONObjectLoop((JSONObject) value);
            }

            list.add(value);

        }

        return list;

    }

    public Map<String, Object> JSONObjectLoop(JSONObject json){

        Map<String, Object> localMap  = new HashMap<String, Object>();

        Iterator<String> iter = json.keys();
        while (iter.hasNext()) {

            String key = iter.next();
            Object value = null;
            try {
                value = json.get(key);
            } catch (JSONException e) {
                // Something went wrong!
            }

            if(value.getClass() == JSONArray.class) {
                value = JSONArrayLoop((JSONArray) value);
            }else if(value.getClass() == JSONObject.class){
                value = JSONObjectLoop((JSONObject) value);
            }

            localMap.put(key, value);

        }

        return localMap;

    }

    public JSONArray ListLoop(List _list){

        JSONArray localJson = new JSONArray();

        for(int i = 0; i < _list.size(); i++){

            Object value = null;
            value = _list.get(i);

            if(value.getClass() == ArrayList.class) {
                value = ListLoop((List) value);
            }else if(value.getClass() == HashMap.class){
                value = MapLoop((Map) value);
            }


            localJson.put(value);


        }

        return localJson;

    }

    public JSONObject MapLoop(Map _map){

        JSONObject localJson = new JSONObject();

        Iterator<Map.Entry> iter = _map.entrySet().iterator();
        while (iter.hasNext()) {

            Map.Entry<String, Object> key = iter.next();
            Object value = null;

            value = key.getValue();

            if(value.getClass() == ArrayList.class) {
                value = ListLoop((List) value);
            }else if(value.getClass() == HashMap.class){
                value = MapLoop((Map) value);
            }

            try {
                localJson.put(key.getKey(), value);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        return localJson;

    }

    public Object ioStream(String _path, String _option, Object _value){

        String[] array = _path.split("\\.");
        int number = 0;
        Object target = map;
        Class cls;

        if(array.length < 1){

            if(_option.equals("write")) {
                map.put(_path, _value);
            }

            target = map.get(_path);

            //Log.e("!!", _path);

        }

        for(int i = 0; i < array.length; i++){

            cls = target.getClass();
            //Log.e(array[i], cls.getName() + " " + target.toString());

            if(cls == HashMap.class){

                if( _option.equals("write") && i == (array.length - 1) ) {
                    ((Map)target).put(array[i], _value);
                }

                target = ((Map)target).get(array[i]);

            }else if(cls == ArrayList.class){

                try {

                    number = Integer.parseInt(array[i]);

                    if( _option.equals("write") && i == (array.length - 1) ) {
                        ((List) target).set(number, _value);
                    }

                    target = ((List) target).get(number);

                }catch(NumberFormatException e){

                    target = null;

                }

            }

        }

        return target;

    }

    public Object get(String _path){

        return ioStream(_path, "read", "");

    }

    public String getString(String _path) {

        Object target = get(_path);

        if(target.getClass() == String.class){
            return (String) target;
        }

        return null;

    }

    public Integer getInt(String _path) {

        try {

            return new Integer( getString(_path) );

        }catch(NumberFormatException e){

            return null;

        }

    }

    public Map getMap(String _path) {

        Object target = get(_path);

        if(target.getClass() == HashMap.class){
            return (Map) target;
        }

        return null;

    }

    public List getList(String _path) {

        Object target = get(_path);

        if(target.getClass() == ArrayList.class){
            return (List) target;
        }

        return null;

    }

    public String getJSON(){

        json = MapLoop(map);
        return json.toString();

    }

    public Object set(String _path, Object _value){

        return ioStream(_path, "write", _value);

    }

    public void setJSON(String _stringJSON){

        if(_stringJSON.charAt(0) == '{'){
            type = "object";
        }
        if(_stringJSON.charAt(0) == '['){
            type = "array";
        }

        json = new JSONObject();

        if(type == "array"){

            JSONArray array = new JSONArray();

            try {
                array = new JSONArray(_stringJSON);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                json.put("array", array);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }else if (type == "object"){

            try {
                json = new JSONObject(_stringJSON);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        map = JSONObjectLoop(json);

    }

    public JSONMapper(String _stringJSON){

        setJSON(_stringJSON);

    }

}
