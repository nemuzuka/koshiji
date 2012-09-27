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
import jp.co.nemuzuka.koshiji.form.MessageCreateForm;
import jp.co.nemuzuka.koshiji.service.MessageEditService;
import jp.co.nemuzuka.koshiji.service.impl.MessageEditServiceImpl;

import org.slim3.controller.validator.Validators;
import org.slim3.datastore.Datastore;
import org.slim3.util.ApplicationMessage;

/**
 * Messageを登録します。
 * @author kazumune
 */
public class CreateController extends JsonController {
    
    /** ActionForm. */
    @ActionForm
    protected MessageCreateForm form;

    private MessageEditService messageEditService = MessageEditServiceImpl.getInstance();
    
	/* (非 Javadoc)
	 * @see jp.co.nemuzuka.core.controller.JsonController#execute()
	 */
	@Override
    @TokenCheck
    @Validation(method="validate", input="jsonError")
	protected Object execute() throws Exception {
	    
	    form.memberKeyStrings = paramValues("memberKeyStrings[]");
	    
	    MessageEditService.CreateParam param = new MessageEditService.CreateParam();
	    param.groupKey = Datastore.stringToKey(getUserInfo().selectedGroupKeyString);
        param.createMemberKey = Datastore.stringToKey(getUserInfo().keyToString);
	    param.body = form.body;
	    param.memberKeyStrings = form.memberKeyStrings;
        
        messageEditService.createMessage(param);
	    
        JsonResult jsonResult = new JsonResult();
        jsonResult.getInfoMsg().add(ApplicationMessage.get("info.success"));
        return jsonResult;
	}

    /**
     * validate設定.
     * @return validate
     */
    protected Validators validate() {
        Validators v = new Validators(request);
        v.add("body", v.required(), v.maxlength(2048));
        return v;
    }
}
