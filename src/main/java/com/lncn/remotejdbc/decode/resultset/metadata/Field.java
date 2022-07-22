package com.lncn.remotejdbc.decode.resultset.metadata;

/**
 * @Classname Field
 * @Description TODO
 * @Date 2022/7/8 12:41
 * @Created by byco
 */
public class Field {
    private final String label;
    private final String name;
    private final int scale;
    private final int jdbcType;

    @Override
    public String toString() {
        return "Field{" +
            "label='" + label + '\'' +
            ", name='" + name + '\'' +
            ", scale=" + scale +
            ", jdbcType=" + jdbcType +
            '}';
    }

    public Field(String label, String name, int scale, int jdbcType) {
        this.label = label;
        this.name = name;
        this.scale = scale;
        this.jdbcType = jdbcType;
    }

    public String getLabel() {
        return label;
    }

    public String getName() {
        return name;
    }

    public int getScale() {
        return scale;
    }

    public int getJdbcType() {
        return jdbcType;
    }
}
