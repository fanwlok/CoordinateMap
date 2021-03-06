package com.fanweilin.greendao;

import java.util.List;
import java.util.ArrayList;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.SqlUtils;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;
import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.query.QueryBuilder;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "POINT_DATA".
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
        public final static Property Date = new Property(11, java.util.Date.class, "date", false, "DATE");
        public final static Property Filename = new Property(12, String.class, "filename", false, "FILENAME");
        public final static Property Status = new Property(13, Integer.class, "status", false, "STATUS");
        public final static Property Guid = new Property(14, String.class, "guid", false, "GUID");
        public final static Property Markerid = new Property(15, Integer.class, "markerid", false, "MARKERID");
        public final static Property Gcjlongitude = new Property(16, String.class, "gcjlongitude", false, "GCJLONGITUDE");
        public final static Property Gcjlatitude = new Property(17, String.class, "gcjlatitude", false, "GCJLATITUDE");
        public final static Property OlfileId = new Property(18, Long.class, "olfileId", false, "OLFILE_ID");
        public final static Property FileId = new Property(19, Long.class, "fileId", false, "FILE_ID");
    }

    private DaoSession daoSession;

    private Query<PointData> olfiles_PointolItemsQuery;
    private Query<PointData> files_PointItemsQuery;

    public PointDataDao(DaoConfig config) {
        super(config);
    }
    
    public PointDataDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"POINT_DATA\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"NAME\" TEXT," + // 1: name
                "\"DESCRIBE\" TEXT," + // 2: describe
                "\"ADDRESS\" TEXT," + // 3: address
                "\"WGSLATITUDE\" TEXT," + // 4: wgslatitude
                "\"WGSLONGITUDE\" TEXT," + // 5: wgslongitude
                "\"BAIDULATITUDE\" TEXT," + // 6: baidulatitude
                "\"BAIDULONGITUDE\" TEXT," + // 7: baidulongitude
                "\"ALTITUDE\" TEXT," + // 8: altitude
                "\"LATITUDE\" TEXT," + // 9: latitude
                "\"LONGITUDE\" TEXT," + // 10: longitude
                "\"DATE\" INTEGER," + // 11: date
                "\"FILENAME\" TEXT," + // 12: filename
                "\"STATUS\" INTEGER," + // 13: status
                "\"GUID\" TEXT," + // 14: guid
                "\"MARKERID\" INTEGER," + // 15: markerid
                "\"GCJLONGITUDE\" TEXT," + // 16: gcjlongitude
                "\"GCJLATITUDE\" TEXT," + // 17: gcjlatitude
                "\"OLFILE_ID\" INTEGER," + // 18: olfileId
                "\"FILE_ID\" INTEGER);"); // 19: fileId
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"POINT_DATA\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, PointData entity) {
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
 
        java.util.Date date = entity.getDate();
        if (date != null) {
            stmt.bindLong(12, date.getTime());
        }
 
        String filename = entity.getFilename();
        if (filename != null) {
            stmt.bindString(13, filename);
        }
 
        Integer status = entity.getStatus();
        if (status != null) {
            stmt.bindLong(14, status);
        }
 
        String guid = entity.getGuid();
        if (guid != null) {
            stmt.bindString(15, guid);
        }
 
        Integer markerid = entity.getMarkerid();
        if (markerid != null) {
            stmt.bindLong(16, markerid);
        }
 
        String gcjlongitude = entity.getGcjlongitude();
        if (gcjlongitude != null) {
            stmt.bindString(17, gcjlongitude);
        }
 
        String gcjlatitude = entity.getGcjlatitude();
        if (gcjlatitude != null) {
            stmt.bindString(18, gcjlatitude);
        }
 
        Long olfileId = entity.getOlfileId();
        if (olfileId != null) {
            stmt.bindLong(19, olfileId);
        }
 
        Long fileId = entity.getFileId();
        if (fileId != null) {
            stmt.bindLong(20, fileId);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, PointData entity) {
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
 
        java.util.Date date = entity.getDate();
        if (date != null) {
            stmt.bindLong(12, date.getTime());
        }
 
        String filename = entity.getFilename();
        if (filename != null) {
            stmt.bindString(13, filename);
        }
 
        Integer status = entity.getStatus();
        if (status != null) {
            stmt.bindLong(14, status);
        }
 
        String guid = entity.getGuid();
        if (guid != null) {
            stmt.bindString(15, guid);
        }
 
        Integer markerid = entity.getMarkerid();
        if (markerid != null) {
            stmt.bindLong(16, markerid);
        }
 
        String gcjlongitude = entity.getGcjlongitude();
        if (gcjlongitude != null) {
            stmt.bindString(17, gcjlongitude);
        }
 
        String gcjlatitude = entity.getGcjlatitude();
        if (gcjlatitude != null) {
            stmt.bindString(18, gcjlatitude);
        }
 
        Long olfileId = entity.getOlfileId();
        if (olfileId != null) {
            stmt.bindLong(19, olfileId);
        }
 
        Long fileId = entity.getFileId();
        if (fileId != null) {
            stmt.bindLong(20, fileId);
        }
    }

    @Override
    protected final void attachEntity(PointData entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

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
            cursor.isNull(offset + 11) ? null : new java.util.Date(cursor.getLong(offset + 11)), // date
            cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12), // filename
            cursor.isNull(offset + 13) ? null : cursor.getInt(offset + 13), // status
            cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14), // guid
            cursor.isNull(offset + 15) ? null : cursor.getInt(offset + 15), // markerid
            cursor.isNull(offset + 16) ? null : cursor.getString(offset + 16), // gcjlongitude
            cursor.isNull(offset + 17) ? null : cursor.getString(offset + 17), // gcjlatitude
            cursor.isNull(offset + 18) ? null : cursor.getLong(offset + 18), // olfileId
            cursor.isNull(offset + 19) ? null : cursor.getLong(offset + 19) // fileId
        );
        return entity;
    }
     
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
        entity.setDate(cursor.isNull(offset + 11) ? null : new java.util.Date(cursor.getLong(offset + 11)));
        entity.setFilename(cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12));
        entity.setStatus(cursor.isNull(offset + 13) ? null : cursor.getInt(offset + 13));
        entity.setGuid(cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14));
        entity.setMarkerid(cursor.isNull(offset + 15) ? null : cursor.getInt(offset + 15));
        entity.setGcjlongitude(cursor.isNull(offset + 16) ? null : cursor.getString(offset + 16));
        entity.setGcjlatitude(cursor.isNull(offset + 17) ? null : cursor.getString(offset + 17));
        entity.setOlfileId(cursor.isNull(offset + 18) ? null : cursor.getLong(offset + 18));
        entity.setFileId(cursor.isNull(offset + 19) ? null : cursor.getLong(offset + 19));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(PointData entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(PointData entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(PointData entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
    /** Internal query to resolve the "pointolItems" to-many relationship of Olfiles. */
    public List<PointData> _queryOlfiles_PointolItems(Long olfileId) {
        synchronized (this) {
            if (olfiles_PointolItemsQuery == null) {
                QueryBuilder<PointData> queryBuilder = queryBuilder();
                queryBuilder.where(Properties.OlfileId.eq(null));
                olfiles_PointolItemsQuery = queryBuilder.build();
            }
        }
        Query<PointData> query = olfiles_PointolItemsQuery.forCurrentThread();
        query.setParameter(0, olfileId);
        return query.list();
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
            SqlUtils.appendColumns(builder, "T0", daoSession.getOlfilesDao().getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T1", daoSession.getFilesDao().getAllColumns());
            builder.append(" FROM POINT_DATA T");
            builder.append(" LEFT JOIN OLFILES T0 ON T.\"OLFILE_ID\"=T0.\"_id\"");
            builder.append(" LEFT JOIN FILES T1 ON T.\"FILE_ID\"=T1.\"_id\"");
            builder.append(' ');
            selectDeep = builder.toString();
        }
        return selectDeep;
    }
    
    protected PointData loadCurrentDeep(Cursor cursor, boolean lock) {
        PointData entity = loadCurrent(cursor, 0, lock);
        int offset = getAllColumns().length;

        Olfiles olfiles = loadCurrentOther(daoSession.getOlfilesDao(), cursor, offset);
        entity.setOlfiles(olfiles);
        offset += daoSession.getOlfilesDao().getAllColumns().length;

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
