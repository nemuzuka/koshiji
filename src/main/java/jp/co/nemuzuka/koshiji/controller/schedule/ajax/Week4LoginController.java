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
package jp.co.nemuzuka.koshiji.controller.schedule.ajax;

import java.util.LinkedHashSet;
import java.util.Set;

import jp.co.nemuzuka.entity.UserInfo;

/**
 * 週次ログインMemberスケジュール表示用Controller
 * @author kazumune
 */
public class Week4LoginController extends WeekController {

    /**
     * 表示対象MemberKeySet作成
     * @param userInfo UserInfo
     * @return MemberKeySet
     */
    @Override
    protected Set<String> createTargetMemberKeys(UserInfo userInfo) {

        Set<String> ret = new LinkedHashSet<String>();
        ret.add(userInfo.keyToString);
        return ret;
    }
}
