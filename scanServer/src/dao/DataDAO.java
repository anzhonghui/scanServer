package dao;

import entity.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.DBUtils;
import utils.ExceptionUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vaf71 on 2017/5/9.
 */
public class DataDAO {
    final Logger logger = LoggerFactory.getLogger(DataDAO.class);

    /**
     * 新建表
     *
     * @param tableName
     */
    public void createTable(String tableName) {
        synchronized (this) {
            Connection connection = null;
            Statement statement = null;
            String sql = "CREATE TABLE " + tableName + "(id INTEGER PRIMARY KEY,data TEXT,is_send INTEGER,is_invalid INTEGER)";
            try {
                connection = DBUtils.getConnection();
                statement = connection.createStatement();
                statement.executeUpdate(sql);
            } catch (SQLException e) {
                logger.error(ExceptionUtils.getTrace(e));
            } finally {
                DBUtils.releaseDB(null, statement, connection);
            }
        }
    }

    /**
     * 增
     *
     * @param tableName 操作的表名
     * @param data
     */
    public void insertData(int id, String tableName, String data, int isInValid) {
        synchronized (this) {
            Connection connection = null;
            PreparedStatement statement = null;
            String sql = "INSERT INTO " + tableName + " VALUES (?,?,0,?);";
            try {
                connection = DBUtils.getConnection();
                statement = connection.prepareStatement(sql);
                statement.setInt(1, id);
                statement.setString(2, data);//插入的数据
                statement.setInt(3, isInValid);
                statement.executeUpdate();
            } catch (SQLException e) {
                logger.error(ExceptionUtils.getTrace(e));
            } finally {
                DBUtils.releaseDB(null, statement, connection);
            }
        }
    }

    /**
     * 改
     *
     * @param tableName 操作的表名
     */
    public void updateData(String tableName, int id, int isSend, int isInvalid) {
        synchronized (this) {
            Connection connection = null;
            PreparedStatement statement = null;
            String sql = "UPDATE " + tableName + " SET is_send=" + isSend + ",is_invalid=" + isInvalid + " WHERE id=" + id;
            try {
                connection = DBUtils.getConnection();
                statement = connection.prepareStatement(sql);
                statement.executeUpdate();
            } catch (SQLException e) {
                logger.error(ExceptionUtils.getTrace(e));
            } finally {
                DBUtils.releaseDB(null, statement, connection);
            }
        }
    }

    /**
     * 获取第一条有效 Data
     *
     * @param tableName 操作的表名
     * @return
     */
//    public Data getFirstValidData(String tableName) {
//        synchronized (this) {
//            Connection connection = null;
//            PreparedStatement statement = null;
//            ResultSet resultSet = null;
//            String sql = "SELECT * FROM " + tableName + " WHERE is_send=0 AND is_invalid=0 LIMIT 0,1;";
//            try {
//                connection = DBUtils.getConnection();
//                statement = connection.prepareStatement(sql);
//                resultSet = statement.executeQuery();
//                Data data = null;
//                while (resultSet.next()) {
//                    data = new Data();
//                    data.setId(resultSet.getInt("id"));
//                    data.setData(resultSet.getString("data"));
//                    data.setIsSend(resultSet.getInt("is_send"));
//                    data.setIsInvalid(resultSet.getInt("is_invalid"));
//                }
//                return data;
//            } catch (SQLException e) {
//                logger.error(ExceptionUtils.getTrace(e));
//            } finally {
//                DBUtils.releaseDB(resultSet, statement, connection);
//            }
//        }
//        return null;
//    }
    /**
     * 获取第20条有效 Data
     *
     * @param tableName 操作的表名
     * @return
     */
    public List<Data> getValidDataList(String tableName) {
        synchronized (this) {
        	List<Data> dataList = new ArrayList<Data>();
            Connection connection = null;
            PreparedStatement statement = null;
            ResultSet resultSet = null;
            String sql = "SELECT * FROM " + tableName + " WHERE is_send=0 AND is_invalid=0;";
            try {
                connection = DBUtils.getConnection();
                statement = connection.prepareStatement(sql);
                resultSet = statement.executeQuery();
                Data data = null;
                while (resultSet.next()) {
                    data = new Data();
                    data.setId(resultSet.getInt("id"));
                    data.setData(resultSet.getString("data"));
                    data.setIsSend(resultSet.getInt("is_send"));
                    data.setIsInvalid(resultSet.getInt("is_invalid"));
                    dataList.add(data);
                }
   
                return dataList;
            } catch (SQLException e) {
                logger.error(ExceptionUtils.getTrace(e));
            } finally {
                DBUtils.releaseDB(resultSet, statement, connection);
            }
        }
        return null;
    }

    /**
     * 删除已发送的数据
     */
    public void deleteIsSendData(String tableName) {
        Connection connection = null;
        PreparedStatement statement = null;
        String sql = "DELETE FROM " + tableName + " WHERE is_send=1";
        try {
            connection = DBUtils.getConnection();
            statement = connection.prepareStatement(sql);
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error(ExceptionUtils.getTrace(e));
            DBUtils.releaseDB(null, statement, connection);
        }
    }
    
//    public void insert(int id) {
//        Connection connection = null;
//        PreparedStatement statement = null;
//        String sql = "insert into temp values(?)";
//        try {
//            connection = DBUtils.getConnection();
//            statement = connection.prepareStatement(sql);
//            statement.setInt(1, id);
//            statement.executeUpdate();
//        } catch (SQLException e) {
//            logger.error(ExceptionUtils.getTrace(e));
//            DBUtils.releaseDB(null, statement, connection);
//        }
//    }
    
//    public static void main(String[] args) {
////    	Map<Data,Integer> sDatas = new DataDAO().getValidDataList("data");
////    	
////    	for (Map.Entry<Data, Integer> entry : sDatas.entrySet()) {
////    	            System.out.println("key= " + entry.getKey() + " and value= "
////    	                    + entry.getValue());
////    	        }
////    	System.out.println();
//    	DataDAO dataDAO = new DataDAO();
//    	for (int i = 1; i < 1000; i++) {
//    		dataDAO.insert(i);
//		}
//	}

}
