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
package jp.co.nemuzuka.koshiji.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 表示スケジュール情報保持Entitiy.
 * @author kazumune
 */
public class ScheduleEntity implements Serializable {

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = 1L;

    /** Session格納Key. */
    public static final String KEY_NAME = "key.schedule";
    
    //週次グループ表示用
    /** 基準日. */
    public Date baseDate;

    //月次Member表示用
    /** 表示対象MemberKey文字列. */
    public String targetMemberKeyString;
    /** 基準年月. */
    public String baseYyyyMm;
    
}
