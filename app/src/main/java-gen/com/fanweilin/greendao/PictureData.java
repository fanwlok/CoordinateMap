package com.fanweilin.greendao;

import com.fanweilin.greendao.DaoSession;
import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table PICTURE_DATA.
 */
public class PictureData implements java.io.Serializable {

    private Long id;
    private String path;
    private Long pointId;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient PictureDataDao myDao;

    private PointData pointData;
    private Long pointData__resolvedKey;


    public PictureData() {
    }

    public PictureData(Long id) {
        this.id = id;
    }

    public PictureData(Long id, String path, Long pointId) {
        this.id = id;
        this.path = path;
        this.pointId = pointId;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getPictureDataDao() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Long getPointId() {
        return pointId;
    }

    public void setPointId(Long pointId) {
        this.pointId = pointId;
    }

    /** To-one relationship, resolved on first access. */
    public PointData getPointData() {
        Long __key = this.pointId;
        if (pointData__resolvedKey == null || !pointData__resolvedKey.equals(__key)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            PointDataDao targetDao = daoSession.getPointDataDao();
            PointData pointDataNew = targetDao.load(__key);
            synchronized (this) {
                pointData = pointDataNew;
            	pointData__resolvedKey = __key;
            }
        }
        return pointData;
    }

    public void setPointData(PointData pointData) {
        synchronized (this) {
            this.pointData = pointData;
            pointId = pointData == null ? null : pointData.getId();
            pointData__resolvedKey = pointId;
        }
    }

    /** Convenient call for {@link AbstractDao#delete(Object)}. Entity must attached to an entity context. */
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.delete(this);
    }

    /** Convenient call for {@link AbstractDao#update(Object)}. Entity must attached to an entity context. */
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.update(this);
    }

    /** Convenient call for {@link AbstractDao#refresh(Object)}. Entity must attached to an entity context. */
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.refresh(this);
    }

}
