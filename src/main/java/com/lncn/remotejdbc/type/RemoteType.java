package com.lncn.remotejdbc.type;

import java.util.HashMap;

public enum RemoteType {
    CHAR(java.sql.Types.CHAR,String.class),
    VARCHAR(java.sql.Types.VARCHAR,String.class),
    LONGVARCHAR(java.sql.Types.LONGVARCHAR,String.class),
    NUMERIC(java.sql.Types.NUMERIC,java.math.BigDecimal.class),
    DECIMAL(java.sql.Types.DECIMAL,java.math.BigDecimal.class),
    BIT(java.sql.Types.BIT,Boolean.class),
    BOOLEAN(java.sql.Types.BOOLEAN,Boolean.class),
    TINYINT(java.sql.Types.TINYINT,Integer.class),
    SMALLINT(java.sql.Types.SMALLINT,Integer.class),
    INTEGER(java.sql.Types.INTEGER,Integer.class),
    BIGINT(java.sql.Types.BIGINT,Long.class),
    REAL(java.sql.Types.REAL,Float.class),
    FLOAT(java.sql.Types.FLOAT,Double.class),
    DOUBLE(java.sql.Types.DOUBLE,Double.class),
    BINARY(java.sql.Types.BINARY,byte[].class),
    VARBINARY(java.sql.Types.VARBINARY,byte[].class),
    LONGVARBINARY(java.sql.Types.LONGVARBINARY,byte[].class),
    DATE(java.sql.Types.DATE,java.sql.Date.class),
    TIME(java.sql.Types.TIME,java.sql.Time.class),
    TIMESTAMP(java.sql.Types.TIMESTAMP,java.sql.Timestamp.class)
    ;
    public  int jdbcType;
    public final Class<?> javaClass;
    private static final HashMap<Integer,RemoteType> jdbcTypeMap = new HashMap<>(27);

    static {
        for( RemoteType remoteType : RemoteType.values()){
            jdbcTypeMap.put(remoteType.jdbcType,remoteType);
        }
    }

    RemoteType(int jdbcType, Class<?> javaClass) {
        this.jdbcType = jdbcType;
        this.javaClass = javaClass;
    }

    static public RemoteType getType(int i){
        return jdbcTypeMap.get(i);
    }
}
