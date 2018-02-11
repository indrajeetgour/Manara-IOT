package com.slb.manara;

import java.security.Key;
import java.util.LinkedHashMap;

public class MappingClass extends LinkedHashMap<String, WellAndStationMapping> {


    public WellAndStationMapping addToMap(String key,String wellOrTimeZone) {

        if (containsKey(key)) {
            return get(key);
        } else {
            return put(key, this.size() + 1, wellOrTimeZone);
        }
    }


 public WellAndStationMapping put(String key, Integer value, String wellOrTimeZone) {
//    public Integer put(String key, Integer value) {
        if (containsKey(key)) {
            return get(key);
        } else {
            WellAndStationMapping wellAndStationMapping = new WellAndStationMapping(key,value,wellOrTimeZone);
             return put(key, wellAndStationMapping);
        }
    }

    @Override
    public WellAndStationMapping put(String key, WellAndStationMapping value) {
        return super.put(key, value);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
