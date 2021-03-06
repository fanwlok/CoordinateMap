package com.fanweilin.greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "OLFILES".
*/
public class OlfilesDao extends AbstractDao<Olfiles, Long> {

    public static final String TABLENAME = "OLFILES";

    /**
     * Properties of entity Olfiles.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Title = new Property(1, String.class, "title", false, "TITLE");
        public final static Property Mapname = new Property(2, String.class, "mapname", false, "MAPNAME");
        public final static Property Num = new Property(3, Integer.class, "num", false, "NUM");
        public final static Property Date = new Property(4, String.class, "date", false, "DATE");
        public final static Property Anchor = new Property(5, java.util.Date.class, "anchor", false, "ANCHOR");
        public final static Property Markerid = new Property(6, Integer.class, "markerid", false, "MARKERID");
    }

    private DaoSession daoSession;


    public OlfilesDao(DaoConfig config) {
        super(config);
    }
    
    public OlfilesDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"OLFILES\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"TITLE\" TEXT NOT NULL ," + // 1: title
                "\"MAPNAME\" TEXT," + // 2: mapname
                "\"NUM\" INTEGER," + // 3: num
                "\"DATE\" TEXT," + // 4: date
                "\"ANCHOR\" INTEGER," + // 5: anchor
                "\"MARKERID\" INTEGER);"); // 6: markerid
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"OLFILES\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, Olfiles entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.getTitle());
 
        String mapname = entity.getMapname();
        if (mapname != null) {
            stmt.bindString(3, mapname);
        }
 
        Integer num = entity.getNum();
        if (num != null) {
            stmt.bindLong(4, num);
        }
 
        String date = entity.getDate();
        if (date != null) {
            stmt.bindString(5, date);
        }
 
        java.util.Date anchor = entity.getAnchor();
        if (anchor != null) {
            stmt.bindLong(6, anchor.getTime());
        }
 
        Integer markerid = entity.getMarkerid();
        if (markerid != null) {
            stmt.bindLong(7, markerid);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, Olfiles entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.getTitle());
 
        String mapname = entity.getMapname();
        if (mapname != null) {
            stmt.bindString(3, mapname);
        }
 
        Integer num = entity.getNum();
        if (num != null) {
            stmt.bindLong(4, num);
        }
 
        String date = entity.getDate();
        if (date != null) {
            stmt.bindString(5, date);
        }
 
        java.util.Date anchor = entity.getAnchor();
        if (anchor != null) {
            stmt.bindLong(6, anchor.getTime());
        }
 
        Integer markerid = entity.getMarkerid();
        if (markerid != null) {
            stmt.bindLong(7, markerid);
        }
    }

    @Override
    protected final void attachEntity(Olfiles entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public Olfiles readEntity(Cursor cursor, int offset) {
        Olfiles entity = new Olfiles( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getString(offset + 1), // title
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // mapname
            cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3), // num
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // date
            cursor.isNull(offset + 5) ? null : new java.util.Date(cursor.getLong(offset + 5)), // anchor
            cursor.isNull(offset + 6) ? null : cursor.getInt(offset + 6) // markerid
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, Olfiles entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setTitle(cursor.getString(offset + 1));
        entity.setMapname(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setNum(cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3));
        entity.setDate(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setAnchor(cursor.isNull(offset + 5) ? null : new java.util.Date(cursor.getLong(offset + 5)));
        entity.setMarkerid(cursor.isNull(offset + 6) ? null : cursor.getInt(offset + 6));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(Olfiles entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(Olfiles entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(Olfiles entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
