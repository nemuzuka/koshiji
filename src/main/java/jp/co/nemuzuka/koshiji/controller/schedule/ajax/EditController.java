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
import jp.co.nemuzuka.koshiji.form.ScheduleForm;
import jp.co.nemuzuka.koshiji.service.ScheduleEditService;
import jp.co.nemuzuka.koshiji.service.impl.ScheduleEditServiceImpl;

import org.slim3.util.ApplicationMessage;

/**
 * スケジュール登録・更新画面表示用Controller
 * @author kazumune
 */
public class EditController extends JsonController {

    private ScheduleEditService scheduleEditService = ScheduleEditServiceImpl.getInstance();
    
    /* (非 Javadoc)
     * @see jp.co.nemuzuka.controller.JsonController#execute()
     */
    @Override
    protected Object execute() throws Exception {
        UserInfo userInfo = getUserInfo();
        String targetDate = asString("targetDate");
        String scheduleKeyString = asString("scheduleKeyString");
        String memberKeyString = asString("memberKeyString");
        
        ScheduleForm form = scheduleEditService.getForm(scheduleKeyString, memberKeyString, targetDate, userInfo);
        
        JsonResult jsonResult = new JsonResult();
        jsonResult.setResult(form);
        if(form == null) {
            jsonResult.setStatus(JsonResult.NO_DATA);
            jsonResult.getErrorMsg().add(ApplicationMessage.get("errors.not.exist"));
        }
        return jsonResult;
    }
}
