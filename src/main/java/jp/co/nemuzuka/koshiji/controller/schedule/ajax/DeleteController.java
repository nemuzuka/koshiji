/*
 * Copyright 2012 Kazumune Katagiri. (http://d.hatena.ne.jp/nemuzuka)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package jp.co.nemuzuka.koshiji.controller.schedule.ajax;

import jp.co.nemuzuka.annotation.TokenCheck;
import jp.co.nemuzuka.controller.JsonController;
import jp.co.nemuzuka.entity.JsonResult;
import jp.co.nemuzuka.entity.UserInfo;
import jp.co.nemuzuka.koshiji.service.ScheduleEditService;
import jp.co.nemuzuka.koshiji.service.impl.ScheduleEditServiceImpl;

import org.slim3.datastore.Datastore;
import org.slim3.util.ApplicationMessage;

import com.google.appengine.api.datastore.Key;

/**
 * スケジュール削除Controller
 * @author kazumune
 */
public class DeleteController extends JsonController {

    private ScheduleEditService scheduleEditService = ScheduleEditServiceImpl.getInstance();

    /* (非 Javadoc)
     * @see jp.co.nemuzuka.controller.JsonController#execute()
     */
    @TokenCheck
    @Override
    protected Object execute() throws Exception {
        
        UserInfo userInfo = getUserInfo();
        Key loginMemberKey = Datastore.stringToKey(userInfo.keyToString);
        scheduleEditService.delete(asKey("scheduleKeyString"), asLong("version"), loginMemberKey);
        
        JsonResult jsonResult = new JsonResult();
        jsonResult.getInfoMsg().add(ApplicationMessage.get("info.success"));
        return jsonResult;
    }
}
