import java.sql.*;

public class MigrateDifyEngineType {

    private static final String DB_URL = "jdbc:mysql://106.12.33.232:3306/zqgl?useSSL=false&serverTimezone=Asia/Shanghai";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "Bjzk60166200";

    public static void main(String[] args) {
        Connection conn = null;
        Statement stmt = null;

        try {
            // 1. 加载MySQL驱动
            Class.forName("com.mysql.cj.jdbc.Driver");

            // 2. 连接数据库
            System.out.println("正在连接数据库...");
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            System.out.println("✓ 数据库连接成功");

            stmt = conn.createStatement();

            // 3. 检查字段是否已存在
            System.out.println("\n检查engine_type字段...");
            ResultSet rs = stmt.executeQuery(
                    "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS " +
                            "WHERE TABLE_SCHEMA = 'zqgl' AND TABLE_NAME = 'ai_chat' " +
                            "AND COLUMN_NAME = 'engine_type'");

            if (rs.next()) {
                System.out.println("⚠️  engine_type字段已存在，跳过创建");
            } else {
                // 4. 添加engine_type字段
                System.out.println("添加engine_type字段...");
                stmt.executeUpdate(
                        "ALTER TABLE ai_chat " +
                                "ADD COLUMN engine_type VARCHAR(20) DEFAULT 'legacy' " +
                                "COMMENT 'AI引擎类型: legacy(现有方案)/dify(Dify平台)'");
                System.out.println("✓ engine_type字段添加成功");
            }
            rs.close();

            // 5. 更新历史数据
            System.out.println("\n更新历史数据...");
            int updated = stmt.executeUpdate(
                    "UPDATE ai_chat SET engine_type = 'legacy' WHERE engine_type IS NULL");
            System.out.println("✓ 更新了 " + updated + " 条历史记录");

            // 6. 创建索引
            System.out.println("\n创建索引...");
            try {
                stmt.executeUpdate(
                        "CREATE INDEX idx_session_engine ON ai_chat(session_id, engine_type)");
                System.out.println("✓ 索引创建成功");
            } catch (SQLException e) {
                if (e.getMessage().contains("Duplicate key name")) {
                    System.out.println("⚠️  索引已存在，跳过创建");
                } else {
                    throw e;
                }
            }

            // 7. 验证结果
            System.out.println("\n验证结果...");
            rs = stmt.executeQuery(
                    "SELECT " +
                            "  COUNT(*) as total, " +
                            "  SUM(CASE WHEN engine_type = 'legacy' THEN 1 ELSE 0 END) as legacy_count, " +
                            "  SUM(CASE WHEN engine_type = 'dify' THEN 1 ELSE 0 END) as dify_count " +
                            "FROM ai_chat");

            if (rs.next()) {
                System.out.println("✓ 数据统计:");
                System.out.println("  总记录数: " + rs.getInt("total"));
                System.out.println("  Legacy引擎: " + rs.getInt("legacy_count"));
                System.out.println("  Dify引擎: " + rs.getInt("dify_count"));
            }
            rs.close();

            System.out.println("\n====================================");
            System.out.println("🎉 数据库迁移完成！");
            System.out.println("====================================");

        } catch (ClassNotFoundException e) {
            System.err.println("✗ MySQL驱动未找到: " + e.getMessage());
            System.err.println("  请确保MySQL Connector/J已添加到classpath");
        } catch (SQLException e) {
            System.err.println("✗ 数据库操作失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // 8. 关闭连接
            try {
                if (stmt != null)
                    stmt.close();
                if (conn != null)
                    conn.close();
                System.out.println("\n✓ 数据库连接已关闭");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
