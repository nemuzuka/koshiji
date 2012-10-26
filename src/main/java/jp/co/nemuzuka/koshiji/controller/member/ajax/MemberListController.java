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
package jp.co.nemuzuka.koshiji.controller.member.ajax;

import jp.co.nemuzuka.controller.JsonController;
import jp.co.nemuzuka.entity.JsonResult;
import jp.co.nemuzuka.koshiji.service.MemberGroupConnService;
import jp.co.nemuzuka.koshiji.service.impl.MemberGroupConnServiceImpl;

import org.slim3.datastore.Datastore;

import com.google.appengine.api.datastore.Key;

/**
 * 指定グループのMember情報取得Controller
 * @author kazumune
 */
public class MemberListController extends JsonController {

    private MemberGroupConnService memberGroupConnService = MemberGroupConnServiceImpl.getInstance();
    
    /* (非 Javadoc)
     * @see jp.co.nemuzuka.controller.JsonController#execute()
     */
    @Override
    protected Object execute() throws Exception {
        String groupKeyString = asString("groupKeyString");
        Key groupKey = Datastore.stringToKey(groupKeyString);
        
        JsonResult jsonResult = new JsonResult();
        jsonResult.setResult(memberGroupConnService.getMemberLabelValueList(groupKey));
        return jsonResult;
    }
}
