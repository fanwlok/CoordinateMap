package com.fanweilin.greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.fanweilin.greendao.ShowData;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table SHOW_DATA.
*/
public class ShowDataDao extends AbstractDao<ShowData, Long> {

    public static final String TABLENAME = "SHOW_DATA";

    /**
     * Properties of entity ShowData.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Title = new Property(1, String.class, "title", false, "TITLE");
        public final static Property Latitude = new Property(2, String.class, "latitude", false, "LATITUDE");
        public final static Property Longitude = new Property(3, String.class, "longitude", false, "LONGITUDE");
        public final static Property Cdstyle = new Property(4, Integer.class, "cdstyle", false, "CDSTYLE");
        public final static Property Datastyle = new Property(5, Integer.class, "datastyle", false, "DATASTYLE");
    };


    public ShowDataDao(DaoConfig config) {
        super(config);
    }
    
    public ShowDataDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'SHOW_DATA' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'TITLE' TEXT NOT NULL ," + // 1: title
                "'LATITUDE' TEXT," + // 2: latitude
                "'LONGITUDE' TEXT," + // 3: longitude
                "'CDSTYLE' INTEGER," + // 4: cdstyle
                "'DATASTYLE' INTEGER);"); // 5: datastyle
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'SHOW_DATA'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, ShowData entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.getTitle());
 
        String latitude = entity.getLatitude();
        if (latitude != null) {
            stmt.bindString(3, latitude);
        }
 
        String longitude = entity.getLongitude();
        if (longitude != null) {
            stmt.bindString(4, longitude);
        }
 
        Integer cdstyle = entity.getCdstyle();
        if (cdstyle != null) {
            stmt.bindLong(5, cdstyle);
        }
 
        Integer datastyle = entity.getDatastyle();
        if (datastyle != null) {
            stmt.bindLong(6, datastyle);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public ShowData readEntity(Cursor cursor, int offset) {
        ShowData entity = new ShowData( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getString(offset + 1), // title
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // latitude
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // longitude
            cursor.isNull(offset + 4) ? null : cursor.getInt(offset + 4), // cdstyle
            cursor.isNull(offset + 5) ? null : cursor.getInt(offset + 5) // datastyle
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, ShowData entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setTitle(cursor.getString(offset + 1));
        entity.setLatitude(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setLongitude(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setCdstyle(cursor.isNull(offset + 4) ? null : cursor.getInt(offset + 4));
        entity.setDatastyle(cursor.isNull(offset + 5) ? null : cursor.getInt(offset + 5));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(ShowData entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(ShowData entity) {
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
    
}