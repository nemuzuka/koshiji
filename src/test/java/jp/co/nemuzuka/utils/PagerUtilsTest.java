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

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;


/**
 * PagerUtilsのテストクラス.
 * @author kkatagiri
 */
public class PagerUtilsTest {

	/**
	 * createTargetListのテスト.
	 */
	@SuppressWarnings("unchecked")
    @Test
	public void testCreateTargetList() {
	    List<Integer> allList = new ArrayList<Integer>();
	    for(int i = 0; i < 7; i++) {
	        allList.add(i);
	    }
	    
	    List<Integer> actual = PagerUtils.createTargetList(allList, 1, 2);
	    assertThat(actual.size(), is(3));
        assertThat(actual.get(0), is(0));
        assertThat(actual.get(1), is(1));
        assertThat(actual.get(2), is(2));
	    
        actual = PagerUtils.createTargetList(allList, 2, 2);
        assertThat(actual.size(), is(3));
        assertThat(actual.get(0), is(2));
        assertThat(actual.get(1), is(3));
        assertThat(actual.get(2), is(4));

        actual = PagerUtils.createTargetList(allList, 3, 2);
        assertThat(actual.size(), is(3));
        assertThat(actual.get(0), is(4));
        assertThat(actual.get(1), is(5));
        assertThat(actual.get(2), is(6));

        actual = PagerUtils.createTargetList(allList, 4, 2);
        assertThat(actual.size(), is(1));
        assertThat(actual.get(0), is(6));

        actual = PagerUtils.createTargetList(allList, 5, 2);
        assertThat(actual.size(), is(0));
        
        allList = new ArrayList<Integer>();
        actual = PagerUtils.createTargetList(allList, 0, 2);
        assertThat(actual.size(), is(0));
        
	}

}
