package at.htl.test4;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DataRepoo {
    Connection conn;

    public DataRepoo() {
        try {
            this.conn = DriverManager.getConnection("jdbc:derby://localhost:1527/db;create=true", "app", "app");
            this.conn.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean insert(int gemid, String gemeinde, int jahr, int einwohner) {
        System.out.println("INSERT");
        try {
            PreparedStatement pstmt = conn.prepareStatement("insert into population(gemid,gemeinde,jahr,einwohner) values (?,?,?,?)");
            pstmt.setInt(1, gemid);
            pstmt.setString(2, gemeinde);
            pstmt.setInt(3, jahr);
            pstmt.setInt(4, einwohner);

            int res = pstmt.executeUpdate();

            System.out.println(res);
            return true;


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void commit() throws SQLException {
        this.conn.commit();
    }
}
