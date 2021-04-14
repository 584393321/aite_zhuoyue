package com.aliyun.ayland.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.aliyun.ayland.data.ATParkNumberBean;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;


@SuppressWarnings({"unchecked", "rawtypes"})
public class ATDatabaseHelper extends OrmLiteSqliteOpenHelper {
    private static final String TABLE_NAME = "sqlite-db.db";
    // 数据库版本
    private static final int DBVERSION = 1;
    private Map<String, Dao> daos = new HashMap<String, Dao>();

    private ATDatabaseHelper(Context context) {
        super(context, TABLE_NAME, null, DBVERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            //停车场省份
            TableUtils.createTable(connectionSource, ATParkNumberBean.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 数据库升级
    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, ATParkNumberBean.class, true);
            onCreate(database, connectionSource);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static ATDatabaseHelper instance;

    public static synchronized ATDatabaseHelper getHelper(Context context) {
        if (instance == null) {
            synchronized (ATDatabaseHelper.class) {
                if (instance == null)
                    instance = new ATDatabaseHelper(context);
            }
        }

        return instance;
    }

    public synchronized Dao getDao(Class clazz) throws SQLException {
        Dao dao = null;
        String className = clazz.getSimpleName();

        if (daos.containsKey(className)) {
            dao = daos.get(className);
        }
        if (dao == null) {
            dao = super.getDao(clazz);
            daos.put(className, dao);
        }
        return dao;
    }

    /**
     * 释放资源
     */
    @Override
    public void close() {
        super.close();

        for (String key : daos.keySet()) {
            Dao dao = daos.get(key);
            dao = null;
        }
    }

}
