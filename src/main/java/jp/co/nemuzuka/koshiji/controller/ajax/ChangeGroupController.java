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

import javax.servlet.http.HttpSession;

import jp.co.nemuzuka.controller.JsonController;
import jp.co.nemuzuka.entity.JsonResult;
import jp.co.nemuzuka.entity.UserInfo;
import jp.co.nemuzuka.koshiji.service.UserInfoService;
import jp.co.nemuzuka.koshiji.service.impl.UserInfoServiceImpl;

import org.slim3.datastore.Datastore;

import com.google.appengine.api.datastore.Key;

/**
 * 表示対象のグループを変更します。
 * @author kazumune
 */
public class ChangeGroupController extends JsonController {
    
    private UserInfoService userInfoService = UserInfoServiceImpl.getInstance();
    
	/* (非 Javadoc)
	 * @see jp.co.nemuzuka.core.controller.JsonController#execute()
	 */
	@Override
	protected Object execute() throws Exception {
	    
	    String groupKeyString = asString("selectedGroupKeyString");
	    Key memberKey = Datastore.stringToKey(getUserInfo().keyToString);
	    UserInfo userInfo = getUserInfo();
	    userInfoService.changeGroup(memberKey, groupKeyString, userInfo);
        HttpSession session = request.getSession(false);
        if(session != null) {
            session.invalidate();
        }
        sessionScope(USER_INFO_KEY, userInfo);
	    
        JsonResult jsonResult = new JsonResult();
        return jsonResult;
	}
}
