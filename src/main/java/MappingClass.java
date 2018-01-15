import java.util.*;

public class MappingClass extends LinkedHashMap<String, Integer> {
//public class MappingClass extends TreeMap<String, Integer> {


    public Integer addToMap(String key) {
        if (containsKey(key)) {
            return get(key);
        } else {
//            List<Entry<String, Integer>> entries = new ArrayList<Entry<String, Integer>>(this.entrySet());


//            Integer val = get(this.entrySet().toArray()) + 1;
            return put(key, this.size() + 1);
//            Map.Entry<String, Integer> lastentry = this.lastEntry();
//            return put(key, lastentry.getValue() + 1);
        }
    }

    @Override
    public Integer put(String key, Integer value) {
        if (containsKey(key)) {
            return value;
        } else {
            return super.put(key, value);
        }
    }


    @Override
    public String toString() {
        return super.toString();
    }
}
