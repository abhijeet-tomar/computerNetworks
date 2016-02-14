
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

class Database {

        public Connection conn = null;

        public Database() {
                try {
                        Class.forName("com.mysql.jdbc.Driver");
                        String url = "jdbc:mysql://localhost:3306/anwesha_16";
                        conn = DriverManager.getConnection(url, "root", "");
                        System.out.println("conn built");
                } catch (SQLException e) {
                    System.out.println(e);
                } catch (ClassNotFoundException e) {
                        System.out.println(e);
                }
        }
        public static void main(String[] args) {
            new Database();
    }

        public ResultSet runSql(String sql) throws SQLException {
                Statement sta = conn.createStatement();
                return sta.executeQuery(sql);
        }

        public void runSql2(String sql) throws SQLException {
                Statement sta = conn.createStatement();
                sta.executeUpdate(sql);
        }

        @Override
        protected void finalize() throws Throwable {
                if (conn != null || !conn.isClosed()) {
                        conn.close();
                }
        }
}
