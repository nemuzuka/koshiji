package jp.co.nemuzuka.koshiji.controller;

import org.slim3.controller.Navigation;

import jp.co.nemuzuka.annotation.NoSessionCheck;
import jp.co.nemuzuka.controller.HtmlController;
import jp.co.nemuzuka.entity.UserInfo;
import jp.co.nemuzuka.koshiji.service.UserInfoService;
import jp.co.nemuzuka.koshiji.service.impl.UserInfoServiceImpl;

/**
 * 初期表示画面のController.
 * ユーザ情報をSessionに格納し、TOP画面に遷移します。
 * @author kazumune
 */
public class IndexController extends HtmlController {

    UserInfoService userInfoService = UserInfoServiceImpl.getInstance();
    
    /* (非 Javadoc)
     * @see jp.co.nemuzuka.controller.HtmlController#execute()
     */
    @NoSessionCheck
    @Override
    protected Navigation execute() throws Exception {
        
        //メールアドレス情報を元に、ユーザ情報を作成する
        UserInfo userInfo = userInfoService.createUserInfo(userService.getCurrentUser().getEmail());
        sessionScope(USER_INFO_KEY, userInfo);
        return forward("/dashboard/");
    }
}
