package com.example;


import org.greenrobot.greendao.generator.DaoGenerator;
import org.greenrobot.greendao.generator.Entity;
import org.greenrobot.greendao.generator.Property;
import org.greenrobot.greendao.generator.Schema;
import org.greenrobot.greendao.generator.ToMany;

public class daogenerator {
    public static void main(String[] args) throws Exception {
        // 正如你所见的，你创建了一个用于添加实体（Entity）的模式（Schema）对象。
        // 两个参数分别代表：数据库版本号与自动生成代码的包路径。
        Schema schema = new Schema(11, "com.fanweilin.greendao");
//      当然，如果你愿意，你也可以分别指定生成的 Bean 与 DAO 类所在的目录，只要如下所示：
//      Schema schema = new Schema(1, "me.itangqi.bean");
//      schema.setDefaultJavaPackageDao("me.itangqi.dao");

        // 模式（Schema）同时也拥有两个默认的 flags，分别用来标示 entity 是否是 activie 以及是否使用 keep sections。
        // schema2.enableActiveEntitiesByDefault();
        // schema2.enableKeepSectionsByDefault();

        // 一旦你拥有了一个 Schema 对象后，你便可以使用它添加实体（Entities）了。
        addData(schema);
        init(schema);

        // 最后我们将使用 DAOGenerator 类的 generateAll() 方法自动生成代码，此处你需要根据自己的情况更改输出目录（既之前创建的 java-gen)。
        // 其实，输出目录的路径可以在 build.gradle 中设置，有兴趣的朋友可以自行搜索，这里就不再详解。
        new DaoGenerator().generateAll(schema, "E:/AndroidStudioProjects/CoordinateMap/app/src/main/java-gen");

    }

    /**
     * @param schema
     */
//    private static void addNote(Schema schema) {
//        // 一个实体（类）就关联到数据库中的一张表，此处表名为「Note」（既类名）
//        Entity note = schema.addEntity("Files");
//               note.addIdProperty();
//        note.addStringProperty("title").notNull();
//        note.addStringProperty("path");
//        note.addStringProperty("date");
//        note.addIntProperty("cdstyle");
//        note.addIntProperty("datastyle");
//    }
    private static void addData(Schema schema) {
        //ShowData
        Entity note = schema.addEntity("ShowData");
        note.addIdProperty().primaryKey().autoincrement();
        note.addStringProperty("title");
        note.addStringProperty("latitude");
        note.addStringProperty("longitude");
        note.addStringProperty("wgslatitude");
        note.addStringProperty("wgslongitude");
        note.addStringProperty("baidulatitude");
        note.addStringProperty("baidulongitude");
        note.addIntProperty("cdstyle");
        note.addLongProperty("fileid");
        note.addLongProperty("pointid");
        note.addIntProperty("datastyle");
        note.addIntProperty("style");
    }

    private static void init(Schema schema) {
        //Coordinate
        Entity coordinate = schema.addEntity("CoordinateData");
        coordinate.implementsSerializable();
        coordinate.addIdProperty().primaryKey().autoincrement();
        coordinate.addStringProperty("name");
        coordinate.addDoubleProperty("midlat");
        coordinate.addDoubleProperty("lat");
        coordinate.addDoubleProperty("lon");
        coordinate.addDoubleProperty("x");
        coordinate.addDoubleProperty("y");
        coordinate.addDoubleProperty("difx");
        coordinate.addDoubleProperty("dify");
        //PictureData
        Entity pictureItems = schema.addEntity("PictureData");
        pictureItems.implementsSerializable();
        pictureItems.addIdProperty().primaryKey().autoincrement();
        pictureItems.addStringProperty("path");

        //PointData
        Entity pointItems = schema.addEntity("PointData");
        pointItems.implementsInterface();
        pointItems.addIdProperty().primaryKey().autoincrement();
        pointItems.addStringProperty("name");
        pointItems.addStringProperty("describe");
        pointItems.addStringProperty("address");
        pointItems.addStringProperty("wgslatitude");
        pointItems.addStringProperty("wgslongitude");
        pointItems.addStringProperty("baidulatitude");
        pointItems.addStringProperty("baidulongitude");

        pointItems.addStringProperty("altitude");
        pointItems.addStringProperty("latitude");
        pointItems.addStringProperty("longitude");
        pointItems.addDateProperty("date");
        //版本4添加
        pointItems.addStringProperty("filename");
        pointItems.addIntProperty("status");
        pointItems.addStringProperty("guid");
        //
        pointItems.addIntProperty("markerid");
        //版本7添加
        pointItems.addStringProperty("gcjlongitude");
        pointItems.addStringProperty("gcjlatitude");
        Property pointId = pictureItems.addLongProperty("pointId").getProperty();
        pictureItems.addToOne(pointItems, pointId);
        ToMany toMany = pointItems.addToMany(pictureItems, pointId);
        toMany.setName("pictureItems");
        //Files
        Entity files = schema.addEntity("Files");
        files.addIdProperty().primaryKey().autoincrement();
        files.addStringProperty("title").notNull();
        files.addStringProperty("path");
        files.addStringProperty("date");
        files.addIntProperty("cdstyle");
        files.addIntProperty("datastyle");
        //版本4添加
        files.addIntProperty("status");
        files.addDateProperty("anchor");
        files.addIntProperty("markerid");
        //
        Entity olfiles = schema.addEntity("Olfiles");
        olfiles .addIdProperty().primaryKey().autoincrement();
        olfiles .addStringProperty("title").notNull();
        olfiles .addStringProperty("mapname");
        olfiles .addIntProperty("num");
        olfiles .addStringProperty("date");
        olfiles .addDateProperty("anchor");
        olfiles.addIntProperty("markerid");
        //10
        Property olfileID = pointItems.addLongProperty("olfileId").getProperty();
        pointItems.addToOne(olfiles, olfileID);
        Property fileID = pointItems.addLongProperty("fileId").getProperty();
        pointItems.addToOne(files, fileID);
        ToMany olfileTomany = olfiles.addToMany(pointItems, olfileID);
        olfileTomany.setName("pointolItems");
        ToMany fileTomany = files.addToMany(pointItems, fileID);
        fileTomany.setName("pointItems");
        //版本6
        Entity sqlpolyline = schema.addEntity("SqlPolyline");
        sqlpolyline.addIdProperty().primaryKey().autoincrement();
        sqlpolyline.addStringProperty("name");
        sqlpolyline.addStringProperty("describe");
        sqlpolyline.addDoubleProperty("distance");
        sqlpolyline.addIntProperty("type");
        sqlpolyline.addIntProperty("color");
        sqlpolyline.addIntProperty("width");
        sqlpolyline.addStringProperty("points");
        /*
        polylineToFile
        * */
        Property polyToFileID = sqlpolyline .addLongProperty("polyToFileID").getProperty();
        sqlpolyline .addToOne(files, polyToFileID );
        ToMany fileTopoly = files.addToMany(sqlpolyline, polyToFileID  );
        fileTopoly.setName("polyItems");
       /*
        polylineToFile
        * */
        //版本7
     /*
     * polylineToOlFile
    * */
        Property polyToOlFileID = sqlpolyline .addLongProperty("polyToOlFileID").getProperty();
        sqlpolyline .addToOne(olfiles, polyToOlFileID );
        ToMany olfileTopoly = olfiles.addToMany(sqlpolyline, polyToOlFileID );
        olfileTopoly.setName("polyOlItems");

        //版本6
        Entity sqlpolygon = schema.addEntity("SqlPolygon");
        sqlpolygon.addIdProperty().primaryKey().autoincrement();
        sqlpolygon.addStringProperty("name");
        sqlpolygon.addStringProperty("describe");
        sqlpolygon.addDoubleProperty("distance");
        sqlpolygon.addDoubleProperty("area");
        sqlpolygon.addIntProperty("type");
        sqlpolygon.addIntProperty("color");
        sqlpolygon.addIntProperty("width");
        sqlpolygon.addIntProperty("innercolor");
        sqlpolygon.addStringProperty("points");
        /*
       *
       *  polygonToFile
       * */
        Property polyGonToFileID = sqlpolygon.addLongProperty("polyGonToFileID").getProperty();
        sqlpolygon .addToOne(files, polyGonToFileID );
        ToMany fileTopolyGon = files.addToMany(sqlpolygon, polyGonToFileID );
        fileTopolyGon.setName("polygonItems");
         /*
       *
       *  polygonToOlFile
       * */
        Property polyGonToOlFileID = sqlpolygon.addLongProperty("polyGonToOlFileID").getProperty();
        sqlpolygon .addToOne(olfiles, polyGonToOlFileID );
        ToMany OlfileTopolyGon = olfiles.addToMany(sqlpolygon, polyGonToOlFileID);
        OlfileTopolyGon.setName("polygonOlItems");
        //版本6
        Entity sqlpoint = schema.addEntity("Sqlpoint");
        sqlpoint .addIdProperty().primaryKey().autoincrement();
        sqlpoint .addDoubleProperty("latitude");
        sqlpoint .addDoubleProperty("longitude");
        Property ptTopolyGonID = sqlpoint.addLongProperty("ptTopolyGonID").getProperty();
        sqlpoint.addToOne(sqlpolygon, ptTopolyGonID);
        Property ptTopolyID = sqlpoint.addLongProperty("ptTopolyID").getProperty();
        sqlpoint .addToOne(sqlpolyline ,ptTopolyID);
        //
        ToMany polyGonToPoint = sqlpolygon.addToMany(sqlpoint, ptTopolyGonID) ;
        polyGonToPoint.setName("pointGonItems");
        //;
        ToMany polyToPoint = sqlpolyline.addToMany(sqlpoint , ptTopolyID);
        polyToPoint.setName("pointpolyItems");
    }
}

