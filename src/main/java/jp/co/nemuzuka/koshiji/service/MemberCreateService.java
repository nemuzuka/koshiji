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

/**
 * メンバー自動生成に関するService
 * @author kazumune
 */
public interface MemberCreateService {

    /**
     * 管理者作成.
     * 指定したメールアドレスと名称でMemberと、新規グループを作成し
     * Memberをグループの管理者として関連付けます
     * @param email メールアドレス
     * @param name 名称
     */
    void createAdminMember(String email, String name);
    
    /**
     * グループメンバー作成.
     * 指定したメールアドレスでMemberを作成し、
     * Memberをグループのメンバーとして関連付けます。
     * メールアドレスが既に登録されている場合、新しくMemberを作成することはしません。
     * @param email メールアドレス
     * @param groupKeyString グループKey文字列
     */
    void createNormalMember(String email, String groupKeyString);
    
}
