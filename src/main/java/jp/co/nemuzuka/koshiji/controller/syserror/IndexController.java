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
package jp.co.nemuzuka.koshiji.controller.syserror;

import jp.co.nemuzuka.annotation.NoRegistCheck;
import jp.co.nemuzuka.annotation.NoSessionCheck;
import jp.co.nemuzuka.controller.HtmlController;

import org.slim3.controller.Navigation;

/**
 * システムエラーController.
 * システムエラー画面を表示します。
 * @author kazumune
 */
public class IndexController extends HtmlController {
	/* (非 Javadoc)
	 * @see jp.co.nemuzuka.core.controller.HtmlController#execute()
	 */
	@NoSessionCheck
	@NoRegistCheck
	@Override
	protected Navigation execute() throws Exception {
		return forward("/syserror/index.jsp");
	}

}
