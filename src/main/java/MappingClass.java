import java.util.HashMap;

public class MappingClass extends HashMap<String, Integer> {


    public Integer addToMap(String key){
        if (containsKey(key)) {
            return get(key);
        }else{
            return put(key, this.size()+1);
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
