package com.example;


import java.awt.Point;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;
import de.greenrobot.daogenerator.ToMany;

public class daogenerator {
    public static void main(String[] args) throws Exception {
        // 正如你所见的，你创建了一个用于添加实体（Entity）的模式（Schema）对象。
        // 两个参数分别代表：数据库版本号与自动生成代码的包路径。
        Schema schema = new Schema(2, "com.fanweilin.greendao");
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
        new DaoGenerator().generateAll(schema, "G:/gongzuo/ANDROID/CoordinateMap/app/src/main/java-gen");
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
        Entity note = schema.addEntity("ShowData");
        note.addIdProperty();
        note.addStringProperty("title").notNull();
        note.addStringProperty("latitude");
        note.addStringProperty("longitude");
        note.addIntProperty("cdstyle");
        note.addLongProperty("fileid");
        note.addLongProperty("pointid");
        note.addIntProperty("datastyle");
    }

    private static void init(Schema schema) {
        Entity pictureItems = schema.addEntity("PictureData");
        pictureItems.implementsSerializable();
        pictureItems.addIdProperty();
        pictureItems.addStringProperty("path");
        Property pointId = pictureItems.addLongProperty("pointId").getProperty();

        Entity pointItems = schema.addEntity("PointData");
        pointItems.implementsInterface();
        pointItems.addIdProperty();
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


        Property fileID = pointItems.addLongProperty("fileId").getProperty();
        pictureItems.addToOne(pointItems, pointId);
        ToMany toMany = pointItems.addToMany(pictureItems, pointId);
        toMany.setName("pictureItems");

        Entity files = schema.addEntity("Files");
        files.addIdProperty();
        files.addStringProperty("title").notNull();
        files.addStringProperty("path");
        files.addStringProperty("date");
        files.addIntProperty("cdstyle");
        files.addIntProperty("datastyle");
        pointItems.addToOne(files, fileID);
        ToMany fileTomany = files.addToMany(pointItems, fileID);
        fileTomany.setName("pointItems");
    }
}
