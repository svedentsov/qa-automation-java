package com.svedentsov.steps.common;

import com.svedentsov.manager.DBManager;
import com.svedentsov.manager.KafkaManager;
import com.svedentsov.manager.RestManager;
import com.svedentsov.manager.UiManager;

public class BaseSteps {
    public final DBManager db = DBManager.getManager();
    public final KafkaManager kafka = KafkaManager.getManager();
    public final RestManager rest = RestManager.getManager();
    public final UiManager ui = UiManager.getManager();
}
