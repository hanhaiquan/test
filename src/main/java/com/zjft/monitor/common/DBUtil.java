package com.zjft.monitor.common;

import com.sun.rowset.CachedRowSetImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.sql.rowset.CachedRowSet;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

/**
 * ʹ�������ļ�������JDBC�������ݿ� ���������������ݿ������
 */
public class DBUtil {
    // �������ݿ��·��
    public static String url;
    // �������ݿ���û���
    public static String user;
    // �������ݿ������
    public static String pwd;

    public static String driver;


    private static Log log = LogFactory.getLog(DBUtil.class);

    // ��̬��
    static {
        try {
            // ��ȡ�����ļ�
            Properties prop = new Properties();
            /*
             * ����д���ǽ��������Ƽ������·�� д����
             */
            InputStream is = DBUtil.class.getClassLoader().getResourceAsStream(
                    "com/zjft/monitor/db.properties");

            prop.load(is);
            is.close();
            // ��ȡ����
            driver = prop.getProperty("jdbc.driver");
            // ��ȡ��ַ
            url = prop.getProperty("jdbc.url");
            // ��ȡ�û���
            user = prop.getProperty("jdbc.user");
            // ��ȡ����
            pwd = prop.getProperty("jdbc.password");

            // ע������
            Class.forName(driver);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * ��ȡһ������
     *
     * @return
     * @throws Exception
     */
    public static Connection getConnection() throws Exception {
        try {
            /*
             * ͨ��DriverManager����һ�����ݿ������ ������
             */
            Connection conn = DriverManager.getConnection(url, user, pwd);
            /*
             * ThreadLocal��set���� �Ὣ��ǰ�߳���Ϊkey,����������ֵ ��Ϊvalue�����ڲ���map�б��档
             */

            return conn;
        } catch (Exception e) {
            e.printStackTrace();
            // ֪ͨ�����ߣ��������ӳ���
            throw e;
        }
    }

    /**
     * �رո���������
     */
    public static void closeConnection(Connection conn) {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static CachedRowSet executeQuery(String sql, Connection conn, Log log) throws Exception {

        CachedRowSet rowset = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            rowset = new CachedRowSetImpl();
            log.info("Query,SQL=[" + sql + "]");

            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            rowset.populate(rs);
            return rowset;
        } catch (Exception e) {
            //e.printStackTrace();
            throw e;
        } finally {
            if (ps != null) {
                ps.close();
            }
        }
    }


    public static int executeCUID(String sql, Connection conn, Log log) throws Exception {
        PreparedStatement ps = null;
        int iRetn = -99;
        try {
            log.debug("CUID's SQL=[" + sql + "]");
            ps = conn.prepareStatement(sql);
            iRetn = ps.executeUpdate();
            log.debug("CUID,record's count=[" + iRetn + "]");
        } catch (Exception e) {
            throw e;
        } finally {
            if (ps != null) {
                ps.close();
            }
        }
        return iRetn;
    }


    /**
     * �����Ƿ����ӳɹ�
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        Connection conn = getConnection();
        String sql = "SELECT PLATE_NO,STATUS FROM LOGISTICS_CAR_INFO WHERE STATUS='1' AND PLATE_NO LIKE '��%'";
        CachedRowSet rowset = DBUtil.executeQuery(sql, conn, log);
        while (rowset.next()) {
            log.info("CarNum:" + rowset.getString("PLATE_NO"));
        }
        rowset.close();
        DBUtil.closeConnection(conn);
    }

}