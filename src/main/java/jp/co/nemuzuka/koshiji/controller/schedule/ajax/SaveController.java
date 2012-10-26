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

import jp.co.nemuzuka.annotation.ActionForm;
import jp.co.nemuzuka.annotation.TokenCheck;
import jp.co.nemuzuka.annotation.Validation;
import jp.co.nemuzuka.controller.JsonController;
import jp.co.nemuzuka.entity.JsonResult;
import jp.co.nemuzuka.entity.UserInfo;
import jp.co.nemuzuka.koshiji.form.ScheduleForm;
import jp.co.nemuzuka.koshiji.service.ScheduleEditService;
import jp.co.nemuzuka.koshiji.service.impl.ScheduleEditServiceImpl;
import jp.co.nemuzuka.koshiji.validator.DateTimeRangeValidator;

import org.slim3.controller.validator.Validators;
import org.slim3.datastore.Datastore;
import org.slim3.util.ApplicationMessage;

import com.google.appengine.api.datastore.Key;

/**
 * スケジュール登録・更新Controller
 * @author kazumune
 */
public class SaveController extends JsonController {

    private ScheduleEditService scheduleEditService = ScheduleEditServiceImpl.getInstance();

    /** ActionForm. */
    @ActionForm
    protected ScheduleForm form;
    
    /* (非 Javadoc)
     * @see jp.co.nemuzuka.controller.JsonController#execute()
     */
    @TokenCheck
    @Override
    @Validation(method="validate", input="jsonError")
    protected Object execute() throws Exception {
        
        JsonResult jsonResult = null;
        jsonResult = customValidate();
        if(jsonResult != null) {
            return jsonResult;
        }
        
        UserInfo userInfo = getUserInfo();
        Key loginMemberKey = Datastore.stringToKey(userInfo.keyToString);
        scheduleEditService.put(form, loginMemberKey);
        
        jsonResult = new JsonResult();
        jsonResult.getInfoMsg().add(ApplicationMessage.get("info.success"));
        return jsonResult;
    }

    /**
     * validate設定.
     * @return validate
     */
    protected Validators validate() {
        Validators v = new Validators(request);
        v.add("title", v.required(), v.maxlength(64));
        v.add("memo", v.maxlength(1024));
        v.add("startDate,startTime,endDate,endTime", new DateTimeRangeValidator());
        return v;
    }

    /**
     * 自前validate.
     * ActionFormにデータを設定して、入力チェックを行います。
     * @return エラーが存在する場合、JsonResultのインスタンス。エラーが存在しない場合、null
     */
    private JsonResult customValidate() {
        
        form.setConnMemberKeyString(paramValues("connMemberKeyString[]"));
        
        //メンバーは1つ以上選択すること
        if(form.getConnMemberKeyString() == null || form.getConnMemberKeyString().length == 0) {
            return createErrorMsg("validator.check.required", "メンバー");
        }
        return null;
    }

}
