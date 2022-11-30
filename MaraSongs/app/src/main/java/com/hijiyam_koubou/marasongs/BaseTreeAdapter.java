package com.hijiyam_koubou.marasongs;

import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseTreeAdapter extends BaseAdapter {			//abstract class BaseTreeAdapter extends BaseAdapter
	TreeEntry rootEntry = null;

	/**
	 * クラス変数 TreeEntry rootEntryに、他のクラスから渡されたObject entryを追加する
	 *
	 * class TreeEntryを生成する/MyTreeAdapter treeAdapter.add で呼び出さる(album_artistの書き込み)
	 * */
	public TreeEntry add(Object entry) {
		final String TAG = "add[BaseTreeAdapter]";
		String dbMsg= "開始";/////////////////////////////////////
		try{
			dbMsg= "entry=" + entry;
			if(rootEntry == null) rootEntry = new TreeEntry();
			dbMsg= dbMsg +",rootEntry=" + rootEntry.getCount() + "件";
	//		myLog(TAG, dbMsg);
		} catch (Exception e) {		//汎用
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
		return rootEntry.add(entry);
	}

	/**
	 * クラス変数 TreeEntry rootEntryに設定されている要素の数を返す
	 * */
	@Override
	public int getCount() {
		final String TAG = "getCount[BaseTreeAdapter]";
		String dbMsg= "開始";/////////////////////////////////////
		try{
			dbMsg= "rootEntry=" + rootEntry;
	//		myLog(TAG, dbMsg);
		} catch (Exception e) {		//汎用
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
		return rootEntry == null ? 0 : rootEntry.getCount();
	}

	/**
	 * positionで指定されたtreeEntryを返す
	 * nullの場合はnullを返す
	 * @param position リスト中の位置
	 * */
	@Override
	public Object getItem(int position) {
		final String TAG = "getItem[BaseTreeAdapter]";
		String dbMsg= "開始";/////////////////////////////////////
		try{
			dbMsg= "position=" + position;
	//		myLog(TAG, dbMsg);
		} catch (Exception e) {		//汎用
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
		return rootEntry == null ? null : rootEntry.getItem(position + 1) ;		// position=0はrootEntryとなってしまうためインクリメントする
	}

	/**
	 * positionで指定されたtreeEntryを返すが、このクラスでは0を返す
	 * @param position リスト中の位置
	 * */
	@Override
	public long getItemId(int position) {
		final String TAG = "getItemId[BaseTreeAdapter]";
		String dbMsg= "開始";/////////////////////////////////////
		try{
			//TreeEntry treeEntry = (TreeEntry)getItem(position);
			//return treeEntry.getId();
		} catch (Exception e) {		//汎用
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
		return 0;
	}

	public class TreeEntry{
	//	private List<List<Map<String, String>>> treeEntries = null;
		private List<TreeEntry> treeEntries = null;					//オリジナルはList treeEntries
		private int depth = -1; // マイナスはルートオブジェクトのみ
		private boolean isExpanded = false;
		private Object data = null;
		private int listType = -1;
		private int layerName = -1;
//アーティストごとの情報
		private int artistID = -1;
//アルバムごとの情報
		private int albumID = -1;
		private String albumYear = null;
		private String date_modified = null;
//曲ごとの情報
		private int playListID = -1;
		private String playlistNAME = null;
		private int playOrder = -1;
		private int audioID = -1;
		private String track = null;
		private String duration = null;
		private String dataURL = null;
		private String albumArtistName = null;
		private String artistName = null;

		/**
		 * クラス変数 isExpandedをtrueに設定する
		 * ルートオブジェクト専用
		 * */
		public TreeEntry() {												//privateから可視性に変更
			final String TAG = "TreeEntry[TreeEntry.BaseTreeAdapter]";
			String dbMsg= "開始";/////////////////////////////////////
			try{
				isExpanded = true;
				dbMsg= "isExpanded=" + isExpanded;
	//			myLog(TAG, dbMsg);
			} catch (Exception e) {		//汎用
				myErrorLog(TAG ,  dbMsg + "で" + e);
			}
		}

		/**
		 * 他のクラスから渡されたdataをクラス変数 Object dataに代入する
		 * このクラスのaddを経て全データーが書き込まれる*/
		private TreeEntry(TreeEntry parentEntry, Object data) {
			final String TAG = "TreeEntry[TreeEntry.BaseTreeAdapter]";
			String dbMsg= "開始";/////////////////////////////////////
			try{
				dbMsg= "parentEntry=" + parentEntry;
				if(parentEntry != null){
					depth = parentEntry.depth + 1;
					dbMsg= "depth=" + depth;
				}
//				this.key = key;
//				dbMsg += key;
				this.data = data;
				dbMsg +="=" + data;
		//		myLog(TAG, dbMsg);
			} catch (Exception e) {		//汎用
				myErrorLog(TAG ,  dbMsg + "で" + e);
			}
		}

		/**
		 * クラス変数 List treeEntriesに要素treeEntryを追加する */
		public TreeEntry add(Object entry) {
			TreeEntry tEntry = null;
			final String TAG = "add[TreeEntry.BaseTreeAdapter]";
			String dbMsg= "開始";/////////////////////////////////////
			try{
				dbMsg= "entry=" + entry;
				if(treeEntries == null){
					treeEntries = new ArrayList();
				}
				TreeEntry treeEntry = new TreeEntry(this,entry);
				dbMsg= "treeEntry=" + treeEntry.data;
				treeEntries.add(treeEntry);
				tEntry = treeEntry;
				dbMsg +=",戻り値=" + tEntry.data;
				dbMsg +=",treeEntries=" + treeEntries.size() + "件";
		//		myLog(TAG, dbMsg);
			} catch (Exception e) {		//汎用
				myErrorLog(TAG ,  dbMsg + "で" + e);
			}
			return tEntry;
		}

		/**
		 * アーティストごとの情報を付与		どのアーティストかを特定するMediaStore.Audio.Artists._ID
		 * */
		public void addArtistOther( int artistID , int layerName , int listType) {
			final String TAG = "addArtistOther[TreeEntry.BaseTreeAdapter]";
			String dbMsg= "開始";/////////////////////////////////////
			try{
				dbMsg= "artistID=" + artistID;
				this.artistID = artistID;
				dbMsg= "layerName=" + layerName;
				this.layerName = layerName;
				dbMsg= "listType=" + listType;
				this.listType = listType;
	//			myLog(TAG, dbMsg);
			} catch (Exception e) {		//汎用
				myErrorLog(TAG ,  dbMsg + "で" + e);
			}
		}

		/**
		 * アルバムごとの情報を付与 addに続いて使用
		 * */
		public void addAlbumOther( int albumID , String albumYear , String date_modified , int layerName , int listType, String playlistNAME) {
			final String TAG = "addAlbumOther[TreeEntry.BaseTreeAdapter]";
			String dbMsg= "開始";/////////////////////////////////////
			try{
				dbMsg +=",albumID=" + albumID;
				this.albumID = albumID;
				dbMsg +=",albumYear=" + albumYear;
				this.albumYear = albumYear;
				dbMsg +=",date_modified=" + date_modified;
				this.date_modified = date_modified;
				dbMsg= ",layerName=" + layerName;
				this.layerName = layerName;
				dbMsg= ",listType=" + listType;
				this.listType = listType;
				dbMsg= ",playlistNAME=" + playlistNAME;
				this.playlistNAME = playlistNAME;
	//			myLog(TAG, dbMsg);
			} catch (Exception e) {		//汎用
				myErrorLog(TAG ,  dbMsg + "で" + e);
			}
		}

		/**
		 * 曲ごとの情報を付与 addに続いて使用
		 * */
		public void addOther( int playListID , int playOrder , int artistID, int albumID , int audioID, String track , String duration ,
								String dataURL , String playlistNAME  , String albumArtistName , String artistName, int layerName , int listType) {
			final String TAG = "addOther[TreeEntry.BaseTreeAdapter]";
			String dbMsg= "開始";/////////////////////////////////////
			try{
				dbMsg= "[playListID=" + playListID;
				this.playListID = playListID;
				dbMsg= "]playlistNAME=" + playlistNAME;
				this.playlistNAME = playlistNAME;
				dbMsg +=",playOrder=" + playOrder;
				this.playOrder = playOrder;
				dbMsg= "artistID=" + artistID;
				this.artistID = artistID;
				dbMsg +=",albumID=" + albumID;
				this.albumID = albumID;
				dbMsg +=",audioID=" + audioID;
				this.audioID = audioID;
				dbMsg +=",duration=" + duration;
				this.duration = duration;
				dbMsg +=",track=" + track;
				this.track = track;
				dbMsg +=",dataUR=" + dataURL;
				this.dataURL = dataURL;
				dbMsg +=",albumArtistName=" + albumArtistName;
				this.albumArtistName = albumArtistName;
				dbMsg +=",artistName=" + artistName;
				this.artistName = artistName;
				dbMsg= "layerName=" + layerName;
				this.layerName = layerName;
				dbMsg= "listType=" + listType;
				this.listType = listType;
			} catch (Exception e) {		//汎用
				myErrorLog(TAG ,  dbMsg + "で" + e);
			}
		}

		public int getLayerName() {
			int retInt = -1;
			final String TAG = "getLayerName[TreeEntry.BaseTreeAdapter]";
			String dbMsg= "開始";/////////////////////////////////////
			try{
				retInt = this.layerName;
			} catch (Exception e) {		//汎用
				myErrorLog(TAG ,  dbMsg + "で" + e);
			}
			return retInt;
		}

		public int getListType() {
			int retInt = -1;
			final String TAG = "getListType[TreeEntry.BaseTreeAdapter]";
			String dbMsg= "開始";/////////////////////////////////////
			try{
				retInt = this.listType;
			} catch (Exception e) {		//汎用
				myErrorLog(TAG ,  dbMsg + "で" + e);
			}
			return retInt;
		}

		public int getArtistID() {
			int retInt = -1;
			final String TAG = "getArtistID[TreeEntry.BaseTreeAdapter]";
			String dbMsg= "開始";/////////////////////////////////////
			try{
				retInt = this.artistID;
			} catch (Exception e) {		//汎用
				myErrorLog(TAG ,  dbMsg + "で" + e);
			}
			return retInt;
		}

		public int getPlayListID() {
			int retInt = -1;
			final String TAG = "getPlayListID[TreeEntry.BaseTreeAdapter]";
			String dbMsg= "開始";/////////////////////////////////////
			try{
				retInt = this.playListID;
			} catch (Exception e) {		//汎用
				myErrorLog(TAG ,  dbMsg + "で" + e);
			}
			return retInt;
		}

		public String getAlbumYear() {
			String retStr = "";
			final String TAG = "getAlbumYear[TreeEntry.BaseTreeAdapter]";
			String dbMsg= "開始";/////////////////////////////////////
			try{
				retStr = this.albumYear;
			} catch (Exception e) {		//汎用
				myErrorLog(TAG ,  dbMsg + "で" + e);
			}
			return retStr;
		}

		public String getModified() {
			String retStr = "";
			final String TAG = "getAlbumYear[TreeEntry.BaseTreeAdapter]";
			String dbMsg= "開始";/////////////////////////////////////
			try{
				retStr = this.date_modified;
			} catch (Exception e) {		//汎用
				myErrorLog(TAG ,  dbMsg + "で" + e);
			}
			return retStr;
		}

		public int getPlayOrder() {
			int retInt = -1;
			final String TAG = "getPlayOrder[TreeEntry.BaseTreeAdapter]";
			String dbMsg= "開始";/////////////////////////////////////
			try{
				retInt = this.playOrder;
			} catch (Exception e) {		//汎用
				myErrorLog(TAG ,  dbMsg + "で" + e);
			}
			return retInt;
		}

		public int getAlbumID() {
			int retInt = -1;
			final String TAG = "getAlbumID[TreeEntry.BaseTreeAdapter]";
			String dbMsg= "開始";/////////////////////////////////////
			try{
				retInt = this.albumID;
			} catch (Exception e) {		//汎用
				myErrorLog(TAG ,  dbMsg + "で" + e);
			}
			return retInt;
		}

		public int getAudioID() {
			int retInt = -1;
			final String TAG = "getAudioID[TreeEntry.BaseTreeAdapter]";
			String dbMsg= "開始";/////////////////////////////////////
			try{
				retInt = this.audioID;
			} catch (Exception e) {		//汎用
				myErrorLog(TAG ,  dbMsg + "で" + e);
			}
			return retInt;
		}

		public String getTrack() {
			String retStr = "";
			final String TAG = "getTrack[TreeEntry.BaseTreeAdapter]";
			String dbMsg= "開始";/////////////////////////////////////
			try{
				retStr = this.track;
			} catch (Exception e) {		//汎用
				myErrorLog(TAG ,  dbMsg + "で" + e);
			}
			return retStr;
		}

		public String getDuration() {
			String retStr = "";
			final String TAG = "getAlbumYear[TreeEntry.BaseTreeAdapter]";
			String dbMsg= "開始";/////////////////////////////////////
			try{
				retStr = this.duration;
			} catch (Exception e) {		//汎用
				myErrorLog(TAG ,  dbMsg + "で" + e);
			}
			return retStr;
		}

		public String getDataURL() {
			String retStr = "";
			final String TAG = "getDataURL[TreeEntry.BaseTreeAdapter]";
			String dbMsg= "開始";/////////////////////////////////////
			try{
				retStr = this.dataURL;
			} catch (Exception e) {		//汎用
				myErrorLog(TAG ,  dbMsg + "で" + e);
			}
			return retStr;
		}

		public String getPlaylistNAME() {
			String retStr = "";
			final String TAG = "getPlaylistNAME[TreeEntry.BaseTreeAdapter]";
			String dbMsg= "開始";/////////////////////////////////////
			try{
				retStr = this.playlistNAME;
			} catch (Exception e) {		//汎用
				myErrorLog(TAG ,  dbMsg + "で" + e);
			}
			return retStr;
		}

		public String getAlbumArtistName() {
			String retStr = "";
			final String TAG = "getAlbumArtistName[TreeEntry.BaseTreeAdapter]";
			String dbMsg= "開始";/////////////////////////////////////
			try{
				retStr = this.albumArtistName;
			} catch (Exception e) {		//汎用
				myErrorLog(TAG ,  dbMsg + "で" + e);
			}
			return retStr;
		}

		public String getArtistName() {
			String retStr = "";
			final String TAG = "getArtistName[TreeEntry.BaseTreeAdapter]";
			String dbMsg= "開始";/////////////////////////////////////
			try{
				retStr = this.artistName;
			} catch (Exception e) {		//汎用
				myErrorLog(TAG ,  dbMsg + "で" + e);
			}
			return retStr;
		}

		/**
		 * クラス変数 List treeEntries の登録データの合計を返す。
		 * １レコードづつ読み出しす
		 * オリジナル
		 * List treeEntriesではfor(TreeEntry entry : treeEntries)で
		 * 「型の不一致: 要素タイプ Object から BaseTreeAdapter.TreeEntry には変換できません」が発生
		 * */
		private int getCount() {
			int count = 0;
			final String TAG = "getCount[TreeEntry.BaseTreeAdapter]";
			String dbMsg= "開始";/////////////////////////////////////
			try{
				dbMsg= "treeEntries=" + treeEntries;
				dbMsg +=",isExpanded=" + isExpanded;
				if(treeEntries == null || !isExpanded) return 0;
				// 自分の持っているエントリの内、開いている数を返す
				count = treeEntries.size();
				for(TreeEntry entry : treeEntries){
					count += entry.getCount();
					dbMsg +=">>" + count;
				}
				dbMsg +=",count=" + count;
		//		myLog(TAG, dbMsg);
			} catch (Exception e) {		//汎用
				myErrorLog(TAG ,  dbMsg + "で" + e);
			}
			return count;
		}

		/**
		 * クラス変数 List treeEntries からpositionで指定されたTreeEntryを返す
		 * 該当が無ければnuu\llを返す
		 * @param position レコードを指定する０始まりのインデックス
		 * */
		private TreeEntry getItem(int position) {
			final String TAG = "getItem[TreeEntry.BaseTreeAdapter]";
			String dbMsg= "開始";/////////////////////////////////////
			try{
				dbMsg= "position=" + position;
				if(position == 0) return this; // position=0は自分を返す
				if(treeEntries == null || !isExpanded) return null;
				for(int i = 0; position >= 0 && i < treeEntries.size(); ++ i) {
					TreeEntry entry = treeEntries.get(i);
					-- position;							// 子について、エントリが取得できない場合、孫をpositionから引く
					int count = entry.getCount();
					if(count >= position) {		// 子以降で必ずアイテムが見つかる
						return entry.getItem(position);
					}
					position -= count;
				}
				dbMsg +=",position=" + position;
				myLog(TAG, dbMsg);
			} catch (Exception e) {		//汎用
				myErrorLog(TAG ,  dbMsg + "で" + e);
			}

			return null;			// ここは、アイテム数より大きなpositionを指定された場合のみ
		}

		/**
		 * isExpanded を trueに設定して
		 * notifyDataSetChanged();でデータが変更された時にViewに対してリフレッシュを通知する
		 * 子を持っていない場合、開けない
		 * */
		public void expand() {
			final String TAG = "expand[TreeEntry.BaseTreeAdapter]";
			String dbMsg= "開始";/////////////////////////////////////
			try{
				boolean hc = hasChild();
				dbMsg= "hasChild=" + hc;
				if(! hc) return; // 子を持っていない場合、開けない
				if(isExpanded) return;
				isExpanded = true;
	//			dbMsg= "isExpanded=" + isExpanded;
	//			myLog(TAG, dbMsg);
				notifyDataSetChanged();		//http://unia-uniblo529.blogspot.jp/2014/10/androidarrayadapternotifydatasetchanged.html
			} catch (Exception e) {		//汎用
				myErrorLog(TAG ,  dbMsg + "で" + e);
			}
		}

		/**
		 * クラス変数 boolean isExpanded をfalseにセットして
		 * notifyDataSetChanged();でデータが変更された時にViewに対してリフレッシュを通知する
		 * */
		public void collapse() {
			final String TAG = "collapse[TreeEntry.BaseTreeAdapter]";
			String dbMsg= "開始";/////////////////////////////////////
			try{
				dbMsg= "isExpanded=" + isExpanded;
				if(!isExpanded) return;
				isExpanded = false;
				notifyDataSetChanged();
		//		myLog(TAG, dbMsg);
			} catch (Exception e) {		//汎用
				myErrorLog(TAG ,  dbMsg + "で" + e);
			}
		}

		/**
		 * クラス変数 boolean isExpanded を返す
		 * */
		public boolean isExpanded() {
			final String TAG = "isExpanded[TreeEntry.BaseTreeAdapter]";
			String dbMsg= "開始";/////////////////////////////////////
			try{
				dbMsg= "isExpanded=" + isExpanded;
	//			myLog(TAG, dbMsg);
			} catch (Exception e) {		//汎用
				myErrorLog(TAG ,  dbMsg + "で" + e);
			}
			return isExpanded;
		}
//
		/**
		 * クラス変数 int depth を返す
		 * */
		public int getDepth() {
			final String TAG = "getDepth[TreeEntry.BaseTreeAdapter]";
			String dbMsg= "開始";/////////////////////////////////////
			try{
				dbMsg= "depth=" + depth;
	//			myLog(TAG, dbMsg);
			} catch (Exception e) {		//汎用
				myErrorLog(TAG ,  dbMsg + "で" + e);
			}
			return depth;
		}

		/**
		 * クラス変数 Object data を返す
		 * */
		public Object getData() {
			final String TAG = "getData[TreeEntry.BaseTreeAdapter]";
			String dbMsg= "開始";/////////////////////////////////////
			try{
				dbMsg= "data=" + data;
	//			myLog(TAG, dbMsg);
			} catch (Exception e) {		//汎用
				myErrorLog(TAG ,  dbMsg + "で" + e);
			}
			return data;
		}

		/**
		 * クラス変数 List treeEntriesがnullでなくisEmptyでもなければ...
		 * */
		public boolean hasChild() {
			final String TAG = "hasChild[TreeEntry.BaseTreeAdapter]";
			String dbMsg= "開始";/////////////////////////////////////
			try{
				if(treeEntries != null  && !treeEntries.isEmpty()){
					dbMsg= "treeEntries=" + treeEntries.size() + "件";
				}
	//			myLog(TAG, dbMsg);
			} catch (Exception e) {		//汎用
				myErrorLog(TAG ,  dbMsg + "で" + e);
			}
			return treeEntries != null && !treeEntries.isEmpty();
		}
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
}

//http://aquacast-tech.blogspot.jp/2011/06/blog-post.html