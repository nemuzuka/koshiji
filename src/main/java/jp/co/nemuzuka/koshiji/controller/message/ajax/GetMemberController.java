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

import jp.co.nemuzuka.controller.JsonController;
import jp.co.nemuzuka.entity.JsonResult;
import jp.co.nemuzuka.koshiji.form.MemberForm;
import jp.co.nemuzuka.koshiji.service.MemberService;
import jp.co.nemuzuka.koshiji.service.impl.MemberServiceImpl;

/**
 * ログインユーザの詳細を取得します。
 * @author kazumune
 */
public class GetMemberController extends JsonController {

    private MemberService memberService = MemberServiceImpl.getInstance();
    
	/* (非 Javadoc)
	 * @see jp.co.nemuzuka.core.controller.JsonController#execute()
	 */
	@Override
	protected Object execute() throws Exception {
	    
	    MemberForm form = memberService.get(getUserInfo().keyToString);
	    
        JsonResult jsonResult = new JsonResult();
        jsonResult.setToken(setToken());
        jsonResult.setResult(form);
        return jsonResult;
	}    
}
