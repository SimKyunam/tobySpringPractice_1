package user.sqlservice;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mileNote on 2021-05-15
 * Blog : https://milenote.tistory.com
 * Github : https://github.com/SimKyunam
 */
public class HashMapSqlRegistry implements SqlRegistry{
    private Map<String, String> sqlMap = new HashMap<String, String>();

    @Override
    public String findSql(String key) throws SqlNotFoundException {
        String sql = sqlMap.get(key);
        if (sql == null){
            throw new SqlNotFoundException(key +
                    "에 대한 SQL을 찾을 수 없습니다.");
        }else{
            return sql;
        }
    }

    @Override
    public void registerSql(String key, String sql) {
        sqlMap.put(key, sql);
    }
}
