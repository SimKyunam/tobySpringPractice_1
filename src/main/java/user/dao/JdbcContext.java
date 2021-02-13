package user.dao;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by mileNote on 2021-02-09
 * Blog : https://milenote.tistory.com
 * Github : https://github.com/SimKyunam
 */
public class JdbcContext {
    private javax.sql.DataSource dataSource;

    public void setDataSource(DataSource dataSource){
        this.dataSource = dataSource;
    }

    public void workWithStatementStrategy(StatementStrategy stmt) throws SQLException {
        Connection c = null;
        PreparedStatement ps = null;

        try{
            c = dataSource.getConnection();

            ps = stmt.makePreparedStatement(c);

            ps.executeUpdate();
        }catch(SQLException e) {
            throw e;
        }finally{
            if(ps != null){try {ps.close();} catch(SQLException e){}}
            if(c != null){try {c.close();} catch(SQLException e){}}
        }
    }

    public void executeSql(final String query) throws SQLException{
        workWithStatementStrategy(new StatementStrategy() {
            @Override
            public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
                return c.prepareStatement(query);
            }
        });
    }
}
