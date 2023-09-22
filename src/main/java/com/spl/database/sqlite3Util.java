package com.spl.database;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.*;

public class sqlite3Util {

    private static final String sqliteFilePath = "jdbc:sqlite:/Users/spl/Library/Application Support/Google/Chrome/Default/Cookies";

    @Test
    public void test1() throws Exception {
        readSqlite3File();
    }

    public static void readSqlite3File() throws Exception {

        Connection connection = null;
        try {
            connection = createConnection();

            Statement stmt = connection.createStatement();
            stmt.setQueryTimeout(3);

            String sql = "select * from cookies where host_key like \"%km.sankuai.com%\" and name=\"logan_session_token\"";
            ResultSet resultSet = stmt.executeQuery(sql);
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                InputStream inputStream = resultSet.getBinaryStream("encrypted_value");
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                int ch;
                while ((ch = inputStream.read()) != -1) {
                    byteArrayOutputStream.write(ch);
                }
                byte[] b = byteArrayOutputStream.toByteArray();
                byteArrayOutputStream.close();

//                System.out.println(String.format("name=%s value=%s encrypted_value=%s",
//                        name, value,testCoolies.decrypt(b)));
                System.out.println(name);

                resultSet.close();
                connection.close();
            }


            System.out.println("success");
        } catch (Exception e) {
            System.out.println("error!");
            throw e;
        } finally {
            if(connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    System.out.println("关闭链接异常");
                }
            }
        }


    }

//    private final DataProtector protector;
//    public TestCookies() {
//        this.protector = new DataProtector();
//    }
//
//    private String decrypt(byte[] data) {
//        return protector.unprotect(data);
//    }

    private static Connection createConnection() throws SQLException, ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        return DriverManager.getConnection(sqliteFilePath);
    }

}
