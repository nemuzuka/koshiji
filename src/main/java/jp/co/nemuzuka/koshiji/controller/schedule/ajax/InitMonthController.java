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

import java.text.SimpleDateFormat;

import jp.co.nemuzuka.entity.JsonResult;
import jp.co.nemuzuka.entity.LabelValueBean;
import jp.co.nemuzuka.entity.UserInfo;
import jp.co.nemuzuka.koshiji.entity.ScheduleEntity;
import jp.co.nemuzuka.utils.DateTimeUtils;

import org.slim3.util.ApplicationMessage;

/**
 * スケジュール月次表示用初期Controller.
 * Sessionに指定Memberを設定し、表示日付を初期化します。
 * @author kazumune
 */
public class InitMonthController extends BaseController {

    /* (非 Javadoc)
     * @see jp.co.nemuzuka.controller.JsonController#execute()
     */
    @Override
    protected Object execute() throws Exception {
        ScheduleEntity entity = getScheduleEntity();
        String targetMemberKeyString = asString("memberKeyString");
        UserInfo userInfo = getUserInfo();
        boolean exist = false;
        for(LabelValueBean target : userInfo.memberList) {
            if(targetMemberKeyString.equals(target.getValue())) {
                exist = true;
                break;
            }
        }
        if(exist == false) {
            //エラー
            JsonResult jsonResult = new JsonResult();
            jsonResult.setStatus(JsonResult.STATUS_NG);
            jsonResult.getErrorMsg().add(ApplicationMessage.get("errors.request"));
            return jsonResult;
        }
        
        entity.targetMemberKeyString = targetMemberKeyString;
        SimpleDateFormat sdf = DateTimeUtils.createSdf("yyyyMM");
        entity.baseYyyyMm = sdf.format(entity.baseDate);
        sessionScope(ScheduleEntity.KEY_NAME, entity);
        
        JsonResult jsonResult = new JsonResult();
        return jsonResult;
    }
}
