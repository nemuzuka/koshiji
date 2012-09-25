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
package jp.co.nemuzuka.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * ページングに対するUtils.
 * @author kazumune
 */
public class PagerUtils {

    /**
     * 表示対象List抽出.
     * 全Listの内、表示対象ページNoと1ページあたりの表示件数を鑑み、
     * 表示対象のListを抽出します。
     * 次のページが存在するかを判断する為に、最大表示件数+1分のListを返します。
     * @param allList 全List
     * @param targetPageNo 表示対象ページNo
     * @param targetLimit 1ページあたりの表示件数
     * @return 表示対象List
     */
    @SuppressWarnings("rawtypes")
    public static List createTargetList(List allList, 
            int targetPageNo, int targetLimit) {
        int pageNo = targetPageNo -1;
        if(pageNo < 0) {
            pageNo = 0;
        }
        int limit = targetLimit;
        if(limit < 1) {
            limit = 1;
        }
        int startIndex = pageNo * limit;
        int endIndex = startIndex + limit + 1;
        
        int size = allList.size();
        int lastIndex = size;
        if(size == 0 || startIndex > lastIndex) {
            return new ArrayList();
        }
        if(endIndex > lastIndex) {
            endIndex = lastIndex;
        }
        
        return allList.subList(startIndex, endIndex);
    }
}
