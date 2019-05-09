package com.hijiyam_koubou.marasongs;

import android.R.color;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MyTreeAdapter extends BaseTreeAdapter {
	private LayoutInflater inflater = null;
	public int albumPosition;
	public String sucssesPass;			//実際に読み出せたアルバムアートのパス

	public MyTreeAdapter(ArrayList<MyTreeAdapter> tList) {
		final String TAG = "MyTreeAdapter[MyTreeAdapter]";
		String dbMsg= "開始";/////////////////////////////////////
		try{
			if(tList != null){
				if(0 < tList.size()){
					dbMsg= "MyTreeAdapter= " + "(" + (tList.size()-1) + ")" +tList.get(tList.size()-1) ;
				}
			}
	//		myLog(TAG,dbMsg);
		} catch (Exception e) {		//汎用
			myErrorLog(TAG,dbMsg+"で"+e.toString());
		}
	}



	/**
	 * クラス変数 TreeEntry rootEntryに、他のクラスから渡されたObject entryを追加する
	 *  class TreeEntryを生成する */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final String TAG = "getView[MyTreeAdapter]";
		String dbMsg= "開始";/////////////////////////////////////
		try{
			dbMsg= "position= " + position;
		//	dbMsg +=",convertView= " + convertView;
	//		dbMsg +=",parent= " + parent;
			if(convertView == null) {
				if(inflater == null) {
					inflater = (LayoutInflater)parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				}
				convertView = inflater.inflate(R.layout.custom_item_layout, null);			//	convertView = inflater.inflate(R.layout.row, null);
			}
			TreeEntry treeEntry = (TreeEntry)getItem(position);
			ImageView lrow_image = (ImageView) convertView.findViewById(R.id.iconView);						//mIconView = (ImageView) findViewById(R.id.lrow_image);
			dbMsg= "mIconView=" + lrow_image;
			TextView mainText = (TextView) convertView.findViewById(R.id.titleView);					//mTitleView = (TextView) findViewById(R.id.row_main_tv);
			TextView subText = (TextView) convertView.findViewById(R.id.descriptionView);		//mDescriptionView = (TextView) findViewById(R.id.row_sub_tv);
			TextView nomText = (TextView) convertView.findViewById(R.id.noView);							//番号表示
//			TextView mainText = (TextView)convertView.findViewById(R.id.row_main_tv);
//			TextView subText = (TextView)convertView.findViewById(R.id.row_sub_tv);
//			TextView nomText = (TextView)convertView.findViewById(R.id.nol_tv);
//			ImageView lrow_image = (ImageView)convertView.findViewById(R.id.lrow_image);
			Object data = treeEntry.getData();
			dbMsg +=",data= " + data;
			String str = (String)data;
			mainText.setText(str);
	//		int rowDepth = treeEntry.getDepth();
	//		dbMsg +=",getDepth= " + rowDepth + "階層";
			int leftPad = 0;
			int pOrder = position + 1;
			String pOrderStr = " " + String.valueOf(pOrder)+",";
			if(9 < pOrder ){
				pOrderStr =  String.valueOf(pOrder)+",";
			}

			int layerName = treeEntry.getLayerName();
			dbMsg +=",layerName= " + layerName;
			int listType = treeEntry.getListType();
			dbMsg +=",listType= " + listType;
			int albumID = -1;
			switch (layerName) {					//rowDepth
			case MuList.lyer_artist:			//rowDepth = 0; 最上位；アーティスト
				lrow_image.setVisibility(View.GONE);
				nomText.setVisibility(View.GONE);
				subText.setVisibility(View.GONE);
		//		mainText.setBackgroundColor(parent.getContext().getResources().getColor(R.color.blue_dark));
				switch (listType) {
				case MuList.listType_plane:					//情報無し
					break;
				case MuList.listType_info:					//sub情報付き
					break;
				default:
					break;
				}
				break;
			case MuList.lyer_album:			//rowDepth = 1; 2層目；アルバム
				lrow_image.setVisibility(View.GONE);
				nomText.setVisibility(View.GONE);
				subText.setVisibility(View.VISIBLE);
		//		mainText.setBackgroundColor(parent.getContext().getResources().getColor(R.color.red_dark));
				str = "";
				albumPosition = position;
				switch (listType) {
				case MuList.listType_plane:					//情報無し
					nomText.setVisibility(View.VISIBLE);
					nomText.setText(pOrderStr);
					leftPad = 15;					//rowDepth * 25;
					subText.setVisibility(View.GONE);
					break;
				case MuList.listType_info:					//sub情報付き
					String listMei = treeEntry.getPlaylistNAME();
					if( listMei.equals(String.valueOf(parent.getResources().getString(R.string.playlist_namae_saikintuika))) ){				//最近追加
						String date_modified = treeEntry.getModified();
						dbMsg=dbMsg+ ",date_modified="+date_modified;
						if( date_modified != null ){
							SimpleDateFormat sdffiles = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							date_modified = sdffiles.format(new Date(Long.valueOf(date_modified)*1000));
							date_modified  = parent.getContext().getResources().getString(R.string.pref_file_saisinn) + " ; "  + date_modified;
							dbMsg=dbMsg+ ">>"+date_modified;
							str = date_modified  + "\n";
						}
					}
					String albumYear = treeEntry.getAlbumYear();
					if( albumYear != null ){
						if( ! albumYear.equals("null") ){
							albumYear  = parent.getContext().getResources().getString(R.string.fie_year) + " ; "  + albumYear;
							dbMsg=dbMsg+ ",albumYear="+albumYear;
							str = str + albumYear;
						}
					}
					subText.setText(str);
					albumID = treeEntry.getAlbumID();
					dbMsg=dbMsg+ ",albumID="+albumID;
					setIMG(  parent ,  albumID , lrow_image);						//アルバムアート描画
					break;
				default:
					break;
				}
				break;
			case MuList.lyer_titol:			//rowDepth = 1; 3層目；タイトル
				lrow_image.setVisibility(View.GONE);
				subText.setVisibility(View.VISIBLE);
				nomText.setVisibility(View.VISIBLE);
				mainText.setBackgroundColor(Color.rgb(0, 0, 0));
//				mainText.setBackgroundColor(color.black);
				String albumArtistName = treeEntry.getAlbumArtistName();
				dbMsg=dbMsg+ ",albumArtistName="+albumArtistName;
				String ArtistName = treeEntry.getArtistName();
				dbMsg=dbMsg+ ",ArtistName="+ArtistName;
				
	//			int pOrder = position + 1;
				String rStr = " " + String.valueOf(pOrder)+",";
				String duration = treeEntry.getDuration();
				dbMsg=dbMsg+ ",duration="+duration;
				if(duration != null){
					SimpleDateFormat sdf_ms = new SimpleDateFormat("mm:ss");
					duration = sdf_ms.format(new Date(Long.valueOf(duration)));
					dbMsg=dbMsg+ ",duration="+duration;
				}
				String artistInfo = null;
				if(! albumArtistName.equals(ArtistName)){
					artistInfo =ArtistName;
				}
				switch (listType) {
				case MuList.listType_plane:					//情報無し
					subText.setVisibility(View.GONE);
					nomText.setVisibility(View.VISIBLE);
					if(9 < pOrder ){
						rStr =  String.valueOf(pOrder)+",";
					}
					dbMsg +="[" + rStr +"]";
					nomText.setText(rStr);
					if( artistInfo != null ){
						rStr = artistInfo ;
					}
					rStr = rStr + duration;
					subText.setText(rStr);
					break;
				case MuList.listType_info:					//sub情報付き
					subText.setVisibility(View.VISIBLE);
					nomText.setVisibility(View.GONE);
					if(9 < pOrder ){
						rStr =  String.valueOf(pOrder)+",";
					}
					dbMsg +="[" + rStr +"]";
			//		nomText.setText(rStr);
					rStr = rStr + ")"+treeEntry.getArtistName();
					dbMsg +=",Artist=" + rStr ;
					if(duration != null){
						rStr = rStr + "  [ "+duration +" ]";
					}
					subText.setText(rStr);
					albumID = treeEntry.getAlbumID();
					dbMsg=dbMsg+ ",albumID="+albumID;
					setIMG(  parent ,  albumID , lrow_image);						//アルバムアート描画
					break;
				default:
					dbMsg +=",albumPosition= " + albumPosition ;
					int rInt = position - albumPosition;
					String playlistNAME = treeEntry.getPlaylistNAME();
					dbMsg=dbMsg+ ",playlistNAME="+playlistNAME;
					if(playlistNAME.equals(String.valueOf(parent.getContext().getResources().getString(R.string.listmei_zemkyoku)))){	// 全曲リスト
						String track = treeEntry.getTrack();
						dbMsg=dbMsg+ ",track="+track;
						rInt = Integer.valueOf(track);
					}
					rStr = " " + String.valueOf(rInt)+",";
					if(9 < rInt ){
						rStr =  String.valueOf(rInt)+",";
					}
					nomText.setText(rStr);
					leftPad = 45;								//rowDepth * 45;
					int playListID = treeEntry.getPlayListID();
					dbMsg=dbMsg+ ",playListID="+playListID;
					int playOrder = treeEntry.getPlayOrder();
					dbMsg=dbMsg+ ",playOrder="+playOrder;
					str = "";
					if(! albumArtistName.equals(ArtistName)){
						str =ArtistName;
					}
					if(duration != null){
						str = str + "[ "+duration +" ]";
					}
					subText.setText(str);
					break;
				}
				break;
			default:
				break;
			}
			nomText.setPadding(leftPad, nomText.getPaddingTop(), nomText.getPaddingRight(), nomText.getPaddingBottom());
	//		myLog(TAG,dbMsg);
		} catch (Exception e) {		//汎用
			myErrorLog(TAG,dbMsg+"で"+e.toString());
		}
		return convertView;
	}

	public void setIMG( ViewGroup parent , int albumID ,ImageView lrow_image) {						//アルバムアート描画
		final String TAG = "setIMG[MyTreeAdapter]";
		String dbMsg= "開始";/////////////////////////////////////
		try{
			lrow_image.setVisibility(View.VISIBLE);
			Uri cUri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;//1.uri  The URI, using the content:// scheme, for the content to retrieve
			String[] c_columns = null;		 		//③引数columnsには、検索結果に含める列名を指定します。nullを指定すると全列の値が含まれます。
			String c_selection =  MediaStore.Audio.Albums._ID +" = ?";
			;
			String[] c_selectionArgs= { String.valueOf(albumID) };   			//⑥引数groupByには、groupBy句を指定します。
			String c_orderBy=null ; 			//⑧引数orderByには、orderBy句を指定します。	降順はDESC
			;
			Cursor cursor = parent.getContext().getContentResolver().query( cUri , c_columns , c_selection , c_selectionArgs, c_orderBy);
			dbMsg +=",該当="+ cursor.getCount() + "件";///////////////////////////////////////////////////////////////////////////////////////////
			String album_art = null;
			;
			if(cursor.moveToFirst()){
				dbMsg +=",ALBUM="+ cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM));///////////////////////////////////////////////////////////////////////////////////////////
				album_art = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));
				dbMsg +=",album_art="+ album_art;///////////////////////////////////////////////////////////////////////////////////////////
				String rPass = null;
				if(album_art != null){
					OrgUtil ORGUT = new OrgUtil();
					rPass = ORGUT.setAlbumArt( album_art ,  lrow_image ,  45 , 45 , parent.getContext() , sucssesPass);		//指定したイメージビューに指定したURiのファイルを表示させる
					dbMsg +=",rPass="+ rPass;///////////////////////////////////////////////////////////////////////////////////////////
				}
				if( rPass != null ){
					File SPF = new File(rPass);
					sucssesPass = SPF.getPath();			//実際に読み出せたアルバムアートのパス
					dbMsg += ">>sucssesPass=" + sucssesPass;
				}else {
					Bitmap mDummyAlbumArt = BitmapFactory.decodeResource( parent.getContext().getResources(), R.drawable.no_image);
					lrow_image.setImageBitmap(mDummyAlbumArt);
				}
			}
			if(album_art == null){
				Bitmap mDummyAlbumArt = BitmapFactory.decodeResource( parent.getContext().getResources(), R.drawable.no_image);
				lrow_image.setImageBitmap(mDummyAlbumArt);
			}
			cursor.close();
			//		myLog(TAG,dbMsg);
		} catch (Exception e) {		//汎用
			myErrorLog(TAG,dbMsg+"で"+e.toString());
		}
	}

	///////////////////////////////////////////////////////////////////////////////////
	public static void myLog(String TAG , String dbMsg) {
		Util UTIL = new Util();
		Util.myLog(TAG , dbMsg);
	}

	public static void myErrorLog(String TAG , String dbMsg) {
		Util UTIL = new Util();
		Util.myErrorLog(TAG , dbMsg);
	}

}