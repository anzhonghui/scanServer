package utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

/**
 * Created by vaf71 on 2017/5/9.
 */
public class DBUtils {
    final static Logger logger = LoggerFactory.getLogger("SCAN");
    /**
     * 释放资源
     *
     * @param resultSet
     * @param statement
     * @param connection
     */
    public static void releaseDB(ResultSet resultSet, Statement statement,
                                 Connection connection) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                logger.error(ExceptionUtils.getTrace(e));
            }
        }

        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                logger.error(ExceptionUtils.getTrace(e));
            }
        }

        if (connection != null) {
            try {
                //数据库连接池的 Connection 对象进行 close 时
                //并不是真的进行关闭, 而是把该数据库连接会归还到数据库连接池中.
                connection.close();
            } catch (SQLException e) {
                logger.error(ExceptionUtils.getTrace(e));
            }
        }
    }

    /**
     * 获取数据库连接
     *
     * @return
     */
    public static Connection getConnection() {
        Connection connection = null;
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:db/data.db");
            return connection;
        } catch (Exception e) {
            logger.error(ExceptionUtils.getTrace(e));
        }
        return null;
    }
}

