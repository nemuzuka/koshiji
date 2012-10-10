package jp.co.nemuzuka.koshiji.controller;

import org.slim3.controller.router.RouterImpl;

/**
 * Router追加定義クラス.
 * @author kazumune
 */
public class AppRouter extends RouterImpl {
    /**
     * デフォルトコンストラクタ.
     */
    public AppRouter() {
        addRouting("/_ah/start", "/admin/deleteMessage");
    }
}
