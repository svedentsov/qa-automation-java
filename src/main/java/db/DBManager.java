package db;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DBManager {

    private static DBManager dbManager;

    public synchronized static DBManager getDbManager() {
        return Optional.ofNullable(dbManager).orElseGet(() -> dbManager = new DBManager());
    }
}
