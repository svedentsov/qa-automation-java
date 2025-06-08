package com.svedentsov.steps.common;

import com.svedentsov.steps.manager.DBManager;
import com.svedentsov.steps.manager.KafkaManager;
import com.svedentsov.steps.manager.RestManager;
import com.svedentsov.steps.manager.UiManager;

public class BaseSteps {
    public final DBManager db = DBManager.getManager();
    public final KafkaManager kafka = KafkaManager.getManager();
    public final RestManager rest = RestManager.getManager();
    public final UiManager ui = UiManager.getManager();
}
