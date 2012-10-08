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

import jp.co.nemuzuka.annotation.TokenCheck;
import jp.co.nemuzuka.annotation.Validation;
import jp.co.nemuzuka.controller.JsonController;
import jp.co.nemuzuka.entity.JsonResult;
import jp.co.nemuzuka.koshiji.form.MemberForm;
import jp.co.nemuzuka.koshiji.service.MemberService;
import jp.co.nemuzuka.koshiji.service.impl.MemberServiceImpl;

import org.slim3.controller.validator.Validators;
import org.slim3.util.ApplicationMessage;

/**
 * ログインユーザ情報を更新します。
 * @author kazumune
 */
public class UpdateMemberController extends JsonController {
    
    private MemberService memberService = MemberServiceImpl.getInstance();
    
	/* (非 Javadoc)
	 * @see jp.co.nemuzuka.core.controller.JsonController#execute()
	 */
	@Override
    @TokenCheck
    @Validation(method="validate", input="jsonError")
	protected Object execute() throws Exception {
	    
	    MemberForm form = new MemberForm();
        form.keyToString = getUserInfo().keyToString;
        form.mail = userService.getCurrentUser().getEmail();
        form.name = asString("name");
        form.timeZone = asString("timeZone");
	    form.defaultGroup = asString("defaultGroup");
	    form.memo = asString("memo");
	    form.versionNo = asString("versionNo");
	    
	    memberService.put(form);
	    
        JsonResult jsonResult = new JsonResult();
        jsonResult.getInfoMsg().add(ApplicationMessage.get("info.success"));
        jsonResult.setToken(setToken());
        return jsonResult;
	}

    /**
     * validate設定.
     * @return validate
     */
    protected Validators validate() {
        Validators v = new Validators(request);
        v.add("name", v.required(), v.maxlength(128));
        v.add("memo", v.maxlength(1024));
        return v;
    }
}
