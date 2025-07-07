package Task_01;

import java.util.HashMap;
import java.util.Map;

//Since can't use org.json
public class JSONObject {
    private Map<String,Object> map=new HashMap<>();

    public JSONObject() {
    }

    public void put(String key,Object value){
        map.put(key, value);
    }

    public String getString(String key){
        return (String) map.get(key);
    }

    public JSONArray getJSONArray(String key){
        return (JSONArray) map.get(key);
    }

    @Override
    public String toString(){
       StringBuilder sb=new StringBuilder("{");
       for (Map.Entry<String,Object> entry : map.entrySet()){
           if (sb.length()>1) sb.append(",");
           sb.append("\"").append(entry.getKey()).append("\":");
           Object value=entry.getValue();
           if (value instanceof String){
               sb.append("\"").append(value).append("\"");
           }else{
               sb.append(value);
           }
       }
       sb.append("}");
       return sb.toString();
    }
}
