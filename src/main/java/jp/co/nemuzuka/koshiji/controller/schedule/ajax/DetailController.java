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

import jp.co.nemuzuka.controller.JsonController;
import jp.co.nemuzuka.entity.JsonResult;
import jp.co.nemuzuka.entity.UserInfo;
import jp.co.nemuzuka.koshiji.service.ScheduleDetailService;
import jp.co.nemuzuka.koshiji.service.impl.ScheduleDetailServiceImpl;

import org.slim3.util.ApplicationMessage;

import com.google.appengine.api.datastore.Key;

/**
 * スケジュール詳細画面表示用Controller
 * @author kazumune
 */
public class DetailController extends JsonController {

    private ScheduleDetailService scheduleDetailService = ScheduleDetailServiceImpl.getInstance();
    
    /* (非 Javadoc)
     * @see jp.co.nemuzuka.controller.JsonController#execute()
     */
    @Override
    protected Object execute() throws Exception {
        UserInfo userInfo = getUserInfo();
        Key scheduleKey = asKey("scheduleKeyString");
        
        ScheduleDetailService.Detail detail = scheduleDetailService.get(scheduleKey, userInfo);
        
        JsonResult jsonResult = new JsonResult();
        jsonResult.setResult(detail);
        if(detail == null) {
            jsonResult.setStatus(JsonResult.NO_DATA);
            jsonResult.getErrorMsg().add(ApplicationMessage.get("errors.not.exist"));
        }
        return jsonResult;
    }
}
