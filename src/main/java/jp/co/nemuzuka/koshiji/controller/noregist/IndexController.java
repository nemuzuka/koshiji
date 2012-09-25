package jp.co.nemuzuka.koshiji.controller.noregist;

import jp.co.nemuzuka.annotation.NoRegistCheck;
import jp.co.nemuzuka.annotation.NoSessionCheck;
import jp.co.nemuzuka.controller.HtmlController;

import org.slim3.controller.Navigation;

/**
 * ユーザがシステムに未登録時に表示する画面のController.
 * @author kazumune
 */
public class IndexController extends HtmlController {

    /* (非 Javadoc)
     * @see jp.co.nemuzuka.controller.HtmlController#execute()
     */
    @NoRegistCheck
    @NoSessionCheck
    @Override
    protected Navigation execute() throws Exception {
        requestScope("token", setToken());
        return forward("/noregist/index.jsp");
    }
}
