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

import java.util.List;

import jp.co.nemuzuka.controller.JsonController;
import jp.co.nemuzuka.entity.JsonResult;
import jp.co.nemuzuka.entity.UserInfo;
import jp.co.nemuzuka.koshiji.model.UnreadMessageModel;
import jp.co.nemuzuka.koshiji.service.MessageSearchService;
import jp.co.nemuzuka.koshiji.service.impl.MessageSearchServiceImpl;

import org.slim3.datastore.Datastore;

import com.google.appengine.api.datastore.Key;

/**
 * 未読Message件数を検索します。
 * @author kazumune
 */
public class SearchUnreadMessageController extends JsonController {

    private MessageSearchService messageSearchService = MessageSearchServiceImpl.getInstance();
    
	/* (非 Javadoc)
	 * @see jp.co.nemuzuka.core.controller.JsonController#execute()
	 */
	@Override
	protected Object execute() throws Exception {
	    UserInfo userInfo = getUserInfo();
	    Key memberKey = Datastore.stringToKey(userInfo.keyToString);
	    Key groupKey = Datastore.stringToKey(userInfo.selectedGroupKeyString);
	    List<UnreadMessageModel> list = messageSearchService.getUnreadMessage(memberKey, groupKey);
	    
        JsonResult jsonResult = new JsonResult();
        jsonResult.setResult(list.size());
        return jsonResult;
	}    
}
