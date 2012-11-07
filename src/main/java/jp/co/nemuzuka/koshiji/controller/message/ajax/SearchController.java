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
import jp.co.nemuzuka.entity.UserInfo;
import jp.co.nemuzuka.koshiji.service.MessageSearchService;
import jp.co.nemuzuka.koshiji.service.MessageSearchService.SearchParam;
import jp.co.nemuzuka.koshiji.service.impl.MessageSearchServiceImpl;

import org.apache.commons.lang.StringUtils;
import org.slim3.datastore.Datastore;

/**
 * Messageを検索します。
 * @author kazumune
 */
public class SearchController extends JsonController {

    /** 表示対象のMessageKeyListのSession格納Key. */
    private static final String SESSION_KEY = "all_message_keys";

    private MessageSearchService messageSearchService = MessageSearchServiceImpl.getInstance();
    
	/* (非 Javadoc)
	 * @see jp.co.nemuzuka.core.controller.JsonController#execute()
	 */
	@Override
	protected Object execute() throws Exception {
	    SearchParam param = new SearchParam();
	    MessageSearchService.Result result = null;
	    UserInfo userInfo = getUserInfo();
	    if(StringUtils.isNotEmpty(userInfo.selectedGroupKeyString)) {
	        param.groupKey = Datastore.stringToKey(userInfo.selectedGroupKeyString);
	        String limit = System.getProperty("jp.co.nemuzuka.message.limit", "10");
	        param.limit = Integer.valueOf(limit);
	        param.memberKey = Datastore.stringToKey(userInfo.keyToString);
	        param.pageNo = asInteger("pageNo");
	        param.messageKeyStrings = sessionScope(SESSION_KEY);
	        
	        result = messageSearchService.getList(param);
	        sessionScope(SESSION_KEY, result.messageKeyStrings);
	    } else {
	        result = new MessageSearchService.Result();
	    }
	    
        JsonResult jsonResult = new JsonResult();
        jsonResult.setToken(setToken());
        jsonResult.setResult(result);
        return jsonResult;
	}    
}
