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
package jp.co.nemuzuka.koshiji.controller.message.ajax;

import jp.co.nemuzuka.annotation.ActionForm;
import jp.co.nemuzuka.annotation.TokenCheck;
import jp.co.nemuzuka.annotation.Validation;
import jp.co.nemuzuka.controller.JsonController;
import jp.co.nemuzuka.entity.JsonResult;
import jp.co.nemuzuka.koshiji.form.GroupForm;
import jp.co.nemuzuka.koshiji.service.GroupService;
import jp.co.nemuzuka.koshiji.service.MemberGroupConnService;
import jp.co.nemuzuka.koshiji.service.impl.GroupServiceImpl;
import jp.co.nemuzuka.koshiji.service.impl.MemberGroupConnServiceImpl;

import org.slim3.controller.validator.Validators;
import org.slim3.datastore.Datastore;
import org.slim3.util.ApplicationMessage;

import com.google.appengine.api.datastore.Key;

/**
 * 新規グループを登録し、ログインユーザを管理者にします。
 * @author kazumune
 */
public class CreateGroupController extends JsonController {
    
    /** ActionForm. */
    @ActionForm
    protected GroupForm form;

    private MemberGroupConnService memberGroupConnService = MemberGroupConnServiceImpl.getInstance();
    private GroupService groupService = GroupServiceImpl.getInstance();
    
	/* (非 Javadoc)
	 * @see jp.co.nemuzuka.core.controller.JsonController#execute()
	 */
	@Override
    @TokenCheck
    @Validation(method="validate", input="jsonError")
	protected Object execute() throws Exception {
	    
	    Key groupKey = groupService.put(form.groupName);
	    Key memberKey = Datastore.stringToKey(getUserInfo().keyToString);
	    memberGroupConnService.put(memberKey, groupKey, true);
	    
        JsonResult jsonResult = new JsonResult();
        jsonResult.getInfoMsg().add(ApplicationMessage.get("info.success"));
        jsonResult.setResult(Datastore.keyToString(groupKey));
        return jsonResult;
	}

    /**
     * validate設定.
     * @return validate
     */
    protected Validators validate() {
        Validators v = new Validators(request);
        v.add("groupName", v.required(), v.maxlength(128));
        return v;
    }
}
