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
package jp.co.nemuzuka.koshiji.service;

import jp.co.nemuzuka.entity.UserInfo;
import jp.co.nemuzuka.koshiji.model.ScheduleModel;

import com.google.appengine.api.datastore.Key;

/**
 * スケジュール詳細に関するService
 * @author kazumune
 */
public interface ScheduleDetailService {

    /**
     * スケジュール詳細.
     * 指定されたスケジュールの詳細データを取得します。
     * 非公開スケジュールで、自分が参加者でなく、登録者でもない場合、該当なしとします。
     * 
     * 参加者名文字列において、自身が（作成した or 紐付いている）スケジュールの場合、
     * 全ての参加者名を表示します。
     * そうでない場合、現在表示しているグループに紐付くMemberの名前だけ表示します。
     * グループに紐付かないMemberに関しては、「他 X名」と表示します。
     * 
     * @param scheduleKey ScheduleKey
     * @param userInfo User情報
     * @return 詳細データ(該当なしの場合、null)
     */
    Detail get(Key scheduleKey, UserInfo userInfo);
    
    /**
     * スケジュール詳細.
     * @author kazumune
     */
    class Detail {
        /** スケジュールModel. */
        public ScheduleModel model;

        /** 表示日付. */
        public String viewDate;
        /** 参加者名文字列. */
        public String connMemberNames;
        /** 作成者名文字列. */
        public String createMemberName;
        /** 最終更新日. */
        public String lastUpdate;
        /** 自分が作成したものか. */
        public boolean created;
    }
}
