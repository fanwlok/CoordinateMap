package com.fanweilin.greendao;

import org.greenrobot.greendao.annotation.*;

import java.util.List;
import com.fanweilin.greendao.DaoSession;
import org.greenrobot.greendao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit.

/**
 * Entity mapped to table "SQL_POLYLINE".
 */
@Entity(active = true)
public class SqlPolyline {

    @Id(autoincrement = true)
    private Long id;
    private String name;
    private String describe;
    private Double distance;
    private Integer type;
    private Integer color;
    private Integer width;
    private String points;
    private Long polyToFileID;
    private Long polyToOlFileID;

    /** Used to resolve relations */
    @Generated
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated
    private transient SqlPolylineDao myDao;

    @ToOne(joinProperty = "polyToFileID")
    private Files files;

    @Generated
    private transient Long files__resolvedKey;

    @ToOne(joinProperty = "polyToOlFileID")
    private Olfiles olfiles;

    @Generated
    private transient Long olfiles__resolvedKey;

    @ToMany(joinProperties = {
        @JoinProperty(name = "id", referencedName = "ptTopolyID")
    })
    private List<Sqlpoint> pointpolyItems;

    @Generated
    public SqlPolyline() {
    }

    public SqlPolyline(Long id) {
        this.id = id;
    }

    @Generated
    public SqlPolyline(Long id, String name, String describe, Double distance, Integer type, Integer color, Integer width, String points, Long polyToFileID, Long polyToOlFileID) {
        this.id = id;
        this.name = name;
        this.describe = describe;
        this.distance = distance;
        this.type = type;
        this.color = color;
        this.width = width;
        this.points = points;
        this.polyToFileID = polyToFileID;
        this.polyToOlFileID = polyToOlFileID;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getSqlPolylineDao() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getColor() {
        return color;
    }

    public void setColor(Integer color) {
        this.color = color;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public Long getPolyToFileID() {
        return polyToFileID;
    }

    public void setPolyToFileID(Long polyToFileID) {
        this.polyToFileID = polyToFileID;
    }

    public Long getPolyToOlFileID() {
        return polyToOlFileID;
    }

    public void setPolyToOlFileID(Long polyToOlFileID) {
        this.polyToOlFileID = polyToOlFileID;
    }

    /** To-one relationship, resolved on first access. */
    @Generated
    public Files getFiles() {
        Long __key = this.polyToFileID;
        if (files__resolvedKey == null || !files__resolvedKey.equals(__key)) {
            __throwIfDetached();
            FilesDao targetDao = daoSession.getFilesDao();
            Files filesNew = targetDao.load(__key);
            synchronized (this) {
                files = filesNew;
            	files__resolvedKey = __key;
            }
        }
        return files;
    }

    @Generated
    public void setFiles(Files files) {
        synchronized (this) {
            this.files = files;
            polyToFileID = files == null ? null : files.getId();
            files__resolvedKey = polyToFileID;
        }
    }

    /** To-one relationship, resolved on first access. */
    @Generated
    public Olfiles getOlfiles() {
        Long __key = this.polyToOlFileID;
        if (olfiles__resolvedKey == null || !olfiles__resolvedKey.equals(__key)) {
            __throwIfDetached();
            OlfilesDao targetDao = daoSession.getOlfilesDao();
            Olfiles olfilesNew = targetDao.load(__key);
            synchronized (this) {
                olfiles = olfilesNew;
            	olfiles__resolvedKey = __key;
            }
        }
        return olfiles;
    }

    @Generated
    public void setOlfiles(Olfiles olfiles) {
        synchronized (this) {
            this.olfiles = olfiles;
            polyToOlFileID = olfiles == null ? null : olfiles.getId();
            olfiles__resolvedKey = polyToOlFileID;
        }
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    @Generated
    public List<Sqlpoint> getPointpolyItems() {
        if (pointpolyItems == null) {
            __throwIfDetached();
            SqlpointDao targetDao = daoSession.getSqlpointDao();
            List<Sqlpoint> pointpolyItemsNew = targetDao._querySqlPolyline_PointpolyItems(id);
            synchronized (this) {
                if(pointpolyItems == null) {
                    pointpolyItems = pointpolyItemsNew;
                }
            }
        }
        return pointpolyItems;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated
    public synchronized void resetPointpolyItems() {
        pointpolyItems = null;
    }

    /**
    * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
    * Entity must attached to an entity context.
    */
    @Generated
    public void delete() {
        __throwIfDetached();
        myDao.delete(this);
    }

    /**
    * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
    * Entity must attached to an entity context.
    */
    @Generated
    public void update() {
        __throwIfDetached();
        myDao.update(this);
    }

    /**
    * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
    * Entity must attached to an entity context.
    */
    @Generated
    public void refresh() {
        __throwIfDetached();
        myDao.refresh(this);
    }

    @Generated
    private void __throwIfDetached() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
    }

}
