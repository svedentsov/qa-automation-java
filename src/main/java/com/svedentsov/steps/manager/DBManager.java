package com.svedentsov.steps.manager;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import com.svedentsov.steps.common.DbSteps;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DBManager {

    private static DBManager dbManager;
    private DbSteps dbSteps;

    public synchronized static DBManager getManager() {
        return Optional.ofNullable(dbManager).orElseGet(() -> dbManager = new DBManager());
    }

    public DbSteps steps() {
        return Optional.ofNullable(dbSteps).orElseGet(() -> dbSteps = new DbSteps());
    }
}
