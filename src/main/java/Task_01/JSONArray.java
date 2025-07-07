package Task_01;


import java.util.ArrayList;
import java.util.List;

public class JSONArray {
    private List<Object> list = new ArrayList<>();

    public JSONArray(String[] topics) {
    }

    public void put(Object value) {
        list.add(value);
    }

    public String getString(int index) {
        return (String) list.get(index);
    }

    public int length() {
        return list.size();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (Object item : list) {
            if (sb.length() > 1) sb.append(",");
            if (item instanceof String) {
                sb.append("\"").append(item).append("\"");
            } else {
                sb.append(item);
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
