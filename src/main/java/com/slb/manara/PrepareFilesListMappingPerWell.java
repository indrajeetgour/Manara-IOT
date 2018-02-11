package com.slb.manara;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class PrepareFilesListMappingPerWell extends HashMap<String, List<File>> {
    @Override
    public List<File> put(String key, List<File> value) {
        if (containsKey(key)) {
            for (File file : value) {
                if (String.valueOf(file).contains(key)) {
                    return super.put(key, value);
                }
            }
        }
        return super.put(key, value);
    }
}
