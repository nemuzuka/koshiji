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
import jp.co.nemuzuka.koshiji.form.CommentCreateForm;
import jp.co.nemuzuka.koshiji.service.MessageEditService;
import jp.co.nemuzuka.koshiji.service.impl.MessageEditServiceImpl;

import org.slim3.controller.validator.Validators;
import org.slim3.datastore.Datastore;
import org.slim3.util.ApplicationMessage;

/**
 * Commentを登録します。
 * @author kazumune
 */
public class CreateCommentController extends JsonController {
    
    /** ActionForm. */
    @ActionForm
    protected CommentCreateForm form;

    private MessageEditService messageEditService = MessageEditServiceImpl.getInstance();
    
	/* (非 Javadoc)
	 * @see jp.co.nemuzuka.core.controller.JsonController#execute()
	 */
	@Override
    @TokenCheck
    @Validation(method="validate", input="jsonError")
	protected Object execute() throws Exception {
	    
	    MessageEditService.CreateCommentParam param = new MessageEditService.CreateCommentParam();
	    param.groupKey = Datastore.stringToKey(getUserInfo().selectedGroupKeyString);
        param.createMemberKey = Datastore.stringToKey(getUserInfo().keyToString);
	    param.body = form.body;
	    param.messageKey = Datastore.stringToKey(form.messageKeyString);
        
        messageEditService.createComment(param);
	    
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
        v.add("messageKeyString", v.required());
        v.add("body", v.required(), v.maxlength(2048));
        return v;
    }
}
