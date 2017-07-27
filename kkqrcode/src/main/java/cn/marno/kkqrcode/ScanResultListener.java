package cn.marno.kkqrcode;

/**
 * Created by Marno on 2016/12/8/16:20
 * Function：扫描结果回掉
 * Desc：
 */
public interface ScanResultListener {
    void onSuccess(String result);

    void onFailure();

    void onCancel();
}
