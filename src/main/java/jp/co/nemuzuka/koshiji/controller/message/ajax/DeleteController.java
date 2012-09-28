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
import jp.co.nemuzuka.controller.JsonController;
import jp.co.nemuzuka.entity.JsonResult;
import jp.co.nemuzuka.koshiji.service.MessageEditService;
import jp.co.nemuzuka.koshiji.service.impl.MessageEditServiceImpl;

import org.slim3.datastore.Datastore;
import org.slim3.util.ApplicationMessage;

import com.google.appengine.api.datastore.Key;

/**
 * Messageを削除します。
 * @author kazumune
 */
public class DeleteController extends JsonController {
    
    private MessageEditService messageEditService = MessageEditServiceImpl.getInstance();
    
	/* (非 Javadoc)
	 * @see jp.co.nemuzuka.core.controller.JsonController#execute()
	 */
	@Override
    @TokenCheck
	protected Object execute() throws Exception {
	    
        Key memberKey = Datastore.stringToKey(getUserInfo().keyToString);
	    Key messageKey = Datastore.stringToKey(asString("messageKeyString"));
	    Key groupKey = Datastore.stringToKey(getUserInfo().selectedGroupKeyString);
        messageEditService.deleteAddress(messageKey, memberKey, groupKey);
	    
        JsonResult jsonResult = new JsonResult();
        jsonResult.setToken(setToken());
        jsonResult.getInfoMsg().add(ApplicationMessage.get("info.success"));
        return jsonResult;
	}
}
