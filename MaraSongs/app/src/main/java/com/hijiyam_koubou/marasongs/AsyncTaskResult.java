package com.hijiyam_koubou.marasongs;

public class AsyncTaskResult<T> {
	/*	AsyncTask#doInBackgroundの戻り値を考える	http://d.hatena.ne.jp/tomorrowkey/20100824/1282655538
    AsyncTaskのdoInBackgroundメソッドからonPostExecuteメソッドへ渡す引数用の独自クラス
    コンストラクタを使用できなくし、インスタンス作成メソッドを用意する
    他のAsyncTaskでも使えるよう、汎用的に作ります
*/
	private T content;		//AsyncTaskで取得したデータ
	private static int reqCode ;

	public AsyncTaskResult( T content , int reqCode) {	//コンストラクタ;content=AsyncTaskで取得したデータ,isError=エラーならtrueを設定する,resId=エラーメッセージのリソースIDを指定する
	//	boolean isError, int resId , 	, String pdMes , ArrayList<String> sList
		
		final String TAG = "AsyncTaskResult[AsyncTaskResult]";
		String dbMsg="";
		try{
			this.content = content;
			dbMsg="content = " +this.content;
			dbMsg += ",reqCode = " +reqCode;
			AsyncTaskResult.reqCode = (Integer) content;
			dbMsg += ">> " + AsyncTaskResult.reqCode;
	//		myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	public T getContent() {		//AsyncTaskで取得したデータを返す@return AsyncTaskで取得したデータ
		return content;
	}


	public int getReqCode() {
		return reqCode;
	}
	
	public static <T> AsyncTaskResult<T> createNormalResult(T content) {	//AsyncTaskが正常終了した場合の結果を作る@param <T> @param content,AsyncTaskで取得したデータを指定する, @return AsyncTaskResult
		return new AsyncTaskResult<T>( content, reqCode);
	//	return new AsyncTaskResult<T>(content, false, 0 , reqCode);
	}

	///////////////////////////////////////////////////////////////////////////////////
	public static void myLog(String TAG , String dbMsg) {
		MyUtil MyUtil = new MyUtil();
		MyUtil.myLog(TAG , dbMsg);
	}

	public static void myErrorLog(String TAG , String dbMsg) {
		MyUtil MyUtil = new MyUtil();
		MyUtil.myErrorLog(TAG , dbMsg);
	}

}//http://d.hatena.ne.jp/tomorrowkey/20100824/1282655538