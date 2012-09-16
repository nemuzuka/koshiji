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
package jp.co.nemuzuka.koshiji.controller.ajax;

import jp.co.nemuzuka.annotation.NoRegistCheck;
import jp.co.nemuzuka.annotation.NoSessionCheck;
import jp.co.nemuzuka.annotation.TokenCheck;
import jp.co.nemuzuka.controller.JsonController;
import jp.co.nemuzuka.entity.JsonResult;
import jp.co.nemuzuka.koshiji.service.MemberCreateService;
import jp.co.nemuzuka.koshiji.service.impl.MemberCreateServiceImpl;

import org.apache.commons.lang.StringUtils;

/**
 * Memberを強制的に登録するController.
 * GoogleにログインしているユーザをMemberに登録し、新しく作成したグループの
 * 管理者として登録します。
 * @author kazumune
 */
public class CreateAdminMemberController extends JsonController {

    private MemberCreateService memberCreateService = MemberCreateServiceImpl.getInstance();
    
	/* (非 Javadoc)
	 * @see jp.co.nemuzuka.core.controller.JsonController#execute()
	 */
    @NoSessionCheck
    @NoRegistCheck
    @TokenCheck
	@Override
	protected Object execute() throws Exception {
        JsonResult result = new JsonResult();
        String mail = userService.getCurrentUser().getEmail();
        String nickName = StringUtils.defaultString(
            userService.getCurrentUser().getNickname(), mail);
        memberCreateService.createAdminMember(mail, nickName);
        return result;
	}    
}
