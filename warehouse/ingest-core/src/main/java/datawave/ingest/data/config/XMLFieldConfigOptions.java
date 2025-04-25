package datawave.ingest.data.config;

import org.apache.hadoop.conf.Configuration;

import datawave.ingest.data.Type;

public class XMLFieldConfigOptions {
    private final static String FIELD_CONFIG_CACHE_TYPE = ".data.category.field.config.cache.type";
    private final static String FIELD_CONFIG_CACHE_CAPACITY = ".data.category.field.config.cache.capacity";

    public enum FieldCacheType {
        LRU, MAP
    }

    private FieldCacheType fieldCacheType;
    private int capacity;

    public static XMLFieldConfigOptions parseFrom(Configuration conf, Type dataType) {
        XMLFieldConfigOptions options = new XMLFieldConfigOptions();
        String typeName = dataType.typeName();
        options.fieldCacheType = Enum.valueOf(FieldCacheType.class, conf.get(typeName + "." + FIELD_CONFIG_CACHE_TYPE, FieldCacheType.LRU.name()));
        options.capacity = conf.getInt(typeName + "." + FIELD_CONFIG_CACHE_CAPACITY, 100_000);
        return options;
    }

    public FieldCacheType getFieldCacheType() {
        return fieldCacheType;
    }

    public int getCapacity() {
        return capacity;
    }
}
