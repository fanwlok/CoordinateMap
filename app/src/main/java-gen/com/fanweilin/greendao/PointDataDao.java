package com.fanweilin.greendao;

import java.util.List;
import java.util.ArrayList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.SqlUtils;
import de.greenrobot.dao.internal.DaoConfig;
import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;

import com.fanweilin.greendao.PointData;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table POINT_DATA.
*/
public class PointDataDao extends AbstractDao<PointData, Long> {

    public static final String TABLENAME = "POINT_DATA";

    /**
     * Properties of entity PointData.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Name = new Property(1, String.class, "name", false, "NAME");
        public final static Property Describe = new Property(2, String.class, "describe", false, "DESCRIBE");
        public final static Property Address = new Property(3, String.class, "address", false, "ADDRESS");
        public final static Property Wgslatitude = new Property(4, String.class, "wgslatitude", false, "WGSLATITUDE");
        public final static Property Wgslongitude = new Property(5, String.class, "wgslongitude", false, "WGSLONGITUDE");
        public final static Property Baidulatitude = new Property(6, String.class, "baidulatitude", false, "BAIDULATITUDE");
        public final static Property Baidulongitude = new Property(7, String.class, "baidulongitude", false, "BAIDULONGITUDE");
        public final static Property Altitude = new Property(8, String.class, "altitude", false, "ALTITUDE");
        public final static Property Latitude = new Property(9, String.class, "latitude", false, "LATITUDE");
        public final static Property Longitude = new Property(10, String.class, "longitude", false, "LONGITUDE");
        public final static Property FileId = new Property(11, Long.class, "fileId", false, "FILE_ID");
    };

    private DaoSession daoSession;

    private Query<PointData> files_PointItemsQuery;

    public PointDataDao(DaoConfig config) {
        super(config);
    }
    
    public PointDataDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'POINT_DATA' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'NAME' TEXT," + // 1: name
                "'DESCRIBE' TEXT," + // 2: describe
                "'ADDRESS' TEXT," + // 3: address
                "'WGSLATITUDE' TEXT," + // 4: wgslatitude
                "'WGSLONGITUDE' TEXT," + // 5: wgslongitude
                "'BAIDULATITUDE' TEXT," + // 6: baidulatitude
                "'BAIDULONGITUDE' TEXT," + // 7: baidulongitude
                "'ALTITUDE' TEXT," + // 8: altitude
                "'LATITUDE' TEXT," + // 9: latitude
                "'LONGITUDE' TEXT," + // 10: longitude
                "'FILE_ID' INTEGER);"); // 11: fileId
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'POINT_DATA'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, PointData entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(2, name);
        }
 
        String describe = entity.getDescribe();
        if (describe != null) {
            stmt.bindString(3, describe);
        }
 
        String address = entity.getAddress();
        if (address != null) {
            stmt.bindString(4, address);
        }
 
        String wgslatitude = entity.getWgslatitude();
        if (wgslatitude != null) {
            stmt.bindString(5, wgslatitude);
        }
 
        String wgslongitude = entity.getWgslongitude();
        if (wgslongitude != null) {
            stmt.bindString(6, wgslongitude);
        }
 
        String baidulatitude = entity.getBaidulatitude();
        if (baidulatitude != null) {
            stmt.bindString(7, baidulatitude);
        }
 
        String baidulongitude = entity.getBaidulongitude();
        if (baidulongitude != null) {
            stmt.bindString(8, baidulongitude);
        }
 
        String altitude = entity.getAltitude();
        if (altitude != null) {
            stmt.bindString(9, altitude);
        }
 
        String latitude = entity.getLatitude();
        if (latitude != null) {
            stmt.bindString(10, latitude);
        }
 
        String longitude = entity.getLongitude();
        if (longitude != null) {
            stmt.bindString(11, longitude);
        }
 
        Long fileId = entity.getFileId();
        if (fileId != null) {
            stmt.bindLong(12, fileId);
        }
    }

    @Override
    protected void attachEntity(PointData entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public PointData readEntity(Cursor cursor, int offset) {
        PointData entity = new PointData( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // name
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // describe
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // address
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // wgslatitude
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // wgslongitude
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // baidulatitude
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // baidulongitude
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // altitude
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // latitude
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // longitude
            cursor.isNull(offset + 11) ? null : cursor.getLong(offset + 11) // fileId
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, PointData entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setName(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setDescribe(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setAddress(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setWgslatitude(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setWgslongitude(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setBaidulatitude(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setBaidulongitude(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setAltitude(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setLatitude(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setLongitude(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setFileId(cursor.isNull(offset + 11) ? null : cursor.getLong(offset + 11));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(PointData entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(PointData entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
    /** Internal query to resolve the "pointItems" to-many relationship of Files. */
    public List<PointData> _queryFiles_PointItems(Long fileId) {
        synchronized (this) {
            if (files_PointItemsQuery == null) {
                QueryBuilder<PointData> queryBuilder = queryBuilder();
                queryBuilder.where(Properties.FileId.eq(null));
                files_PointItemsQuery = queryBuilder.build();
            }
        }
        Query<PointData> query = files_PointItemsQuery.forCurrentThread();
        query.setParameter(0, fileId);
        return query.list();
    }

    private String selectDeep;

    protected String getSelectDeep() {
        if (selectDeep == null) {
            StringBuilder builder = new StringBuilder("SELECT ");
            SqlUtils.appendColumns(builder, "T", getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T0", daoSession.getFilesDao().getAllColumns());
            builder.append(" FROM POINT_DATA T");
            builder.append(" LEFT JOIN FILES T0 ON T.'FILE_ID'=T0.'_id'");
            builder.append(' ');
            selectDeep = builder.toString();
        }
        return selectDeep;
    }
    
    protected PointData loadCurrentDeep(Cursor cursor, boolean lock) {
        PointData entity = loadCurrent(cursor, 0, lock);
        int offset = getAllColumns().length;

        Files files = loadCurrentOther(daoSession.getFilesDao(), cursor, offset);
        entity.setFiles(files);

        return entity;    
    }

    public PointData loadDeep(Long key) {
        assertSinglePk();
        if (key == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder(getSelectDeep());
        builder.append("WHERE ");
        SqlUtils.appendColumnsEqValue(builder, "T", getPkColumns());
        String sql = builder.toString();
        
        String[] keyArray = new String[] { key.toString() };
        Cursor cursor = db.rawQuery(sql, keyArray);
        
        try {
            boolean available = cursor.moveToFirst();
            if (!available) {
                return null;
            } else if (!cursor.isLast()) {
                throw new IllegalStateException("Expected unique result, but count was " + cursor.getCount());
            }
            return loadCurrentDeep(cursor, true);
        } finally {
            cursor.close();
        }
    }
    
    /** Reads all available rows from the given cursor and returns a list of new ImageTO objects. */
    public List<PointData> loadAllDeepFromCursor(Cursor cursor) {
        int count = cursor.getCount();
        List<PointData> list = new ArrayList<PointData>(count);
        
        if (cursor.moveToFirst()) {
            if (identityScope != null) {
                identityScope.lock();
                identityScope.reserveRoom(count);
            }
            try {
                do {
                    list.add(loadCurrentDeep(cursor, false));
                } while (cursor.moveToNext());
            } finally {
                if (identityScope != null) {
                    identityScope.unlock();
                }
            }
        }
        return list;
    }
    
    protected List<PointData> loadDeepAllAndCloseCursor(Cursor cursor) {
        try {
            return loadAllDeepFromCursor(cursor);
        } finally {
            cursor.close();
        }
    }
    

    /** A raw-style query where you can pass any WHERE clause and arguments. */
    public List<PointData> queryDeep(String where, String... selectionArg) {
        Cursor cursor = db.rawQuery(getSelectDeep() + where, selectionArg);
        return loadDeepAllAndCloseCursor(cursor);
    }
 
}