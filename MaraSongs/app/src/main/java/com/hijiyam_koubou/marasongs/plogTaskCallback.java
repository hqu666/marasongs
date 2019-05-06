package com.hijiyam_koubou.marasongs;

public interface plogTaskCallback {
//	void onSuccessplogTask(Object... myResult);	//成功した時に呼ばれるメソッド	AsyncTaskResult<Object> 	Object myResult でもここは通る
//	void onSuccessplogTask(int nextStep , String pdMes , ArrayList<String> sList);	//成功した時に呼ばれるメソッド	
	void onSuccessplogTask(int reqCode);	//成功した時に呼ばれるメソッド	
//	void onFailedDownloadImage(int resId);	//失敗した時に呼ばれるメソッド  @param resId;エラーメッセージのリソースID
}
//http://android.keicode.com/basics/async-asynctask.php