package com.hijiyam_koubou.marasongs;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.transition.Visibility;

import java.io.File;

public class ItemLayout extends LinearLayout {							//Custom View
	public OrgUtil ORGUT;						//自作関数集
	public ImageView mIconView;			// アイコン
	public Button playBt;
	public Button pouseBt;
	public TextView mTitleView;			// タイトル
	public TextView mDescriptionView;		// 概要
	public TextView noView;							//番号表示
	public int imHight = 48;					//イメージビューの高さ
	public int imWidth = 48;					//イメージビューの幅

	Context context;
	AttributeSet attrs;
	String sucssesPass;

	public ItemLayout(Context context, AttributeSet attrs) {					// , int reqCode
		super(context, attrs);													// , reqCode
		final String TAG = "ItemLayout";
		String dbMsg= "開始";/////////////////////////////////////
		try{
	//		ORGUT = new OrgUtil();						//自作関数集
	//	myLog(TAG,dbMsg);
		} catch (Exception e) {		//汎用
			myErrorLog(TAG,dbMsg+"で"+e.toString());
		}
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		final String TAG = "onFinishInflate";
		String dbMsg= "開始";/////////////////////////////////////
		try{
			mIconView = findViewById(R.id.iconView);						//mIconView = (ImageView) findViewById(R.id.lrow_image);
			pouseBt = findViewById(R.id.pouseBt);
			playBt = findViewById(R.id.playBt);
			mIconView = findViewById(R.id.iconView);						//mIconView = (ImageView) findViewById(R.id.lrow_image);
			dbMsg= "mIconView=" + mIconView;
			mTitleView = findViewById(R.id.titleView);					//mTitleView = (TextView) findViewById(R.id.row_main_tv);
			mDescriptionView = findViewById(R.id.descriptionView);		//mDescriptionView = (TextView) findViewById(R.id.row_sub_tv);
			noView = findViewById(R.id.noView);							//番号表示
	//		myLog(TAG,dbMsg);
		} catch (Exception e) {		//汎用
			myErrorLog(TAG,dbMsg+"で"+e.toString());
		}
	}

	public void bindView(CustomData item , int reqCode,Context context) {							//Item item
		final String TAG = "bindView";
		String dbMsg= "開始";/////////////////////////////////////
		try{
			mIconView.setImageResource(R.drawable.no_image);					//eListItem.icon		android.R.drawable.ic_dialog_alert
			pouseBt.setVisibility(View.GONE);
			playBt.setVisibility(View.GONE);

			dbMsg +=",title =" + item.textData_;
			mTitleView.setText(item.textData_);
			dbMsg +=",description =" + item.subData_;
			mDescriptionView.setText(item.subData_);
			dbMsg +=",reqCode =" + reqCode;
			switch(reqCode) {
			case MaraSonActivity.v_titol:
				mIconView.setVisibility(View.GONE);
				noView.setVisibility(View.VISIBLE);
				noView.setText(item.no_);
				break;
			default:
//			case MaraSonActivity.v_artist:													//...334
//			case MaraSonActivity.v_alubum:												//...340
				noView.setVisibility(View.GONE);
				mIconView.setVisibility(View.VISIBLE);
				if( item.imageUrl_ != null ){
					final Handler bindViewHandler = new Handler(Looper.getMainLooper());
					bindViewHandler.post(() -> {
						final String TAG1 = "post";
						String dbMsg1 = "[bindView]";
						try{
							//					//		Uri uri = MediaStore.Images.Media.getContentUri( item.imageUrl_);
//					//		String wholeId = DocumentsContract.getDocumentId(Uri.parse(item.imageUrl_));
							Uri albumArtUri = Uri.parse("content://media/external/audio/albumart");
							long albumId = Long.parseLong(item.getAlbum_id());
							dbMsg1 += ",albumId= " + albumId;
							Uri imageUrl = ContentUris.withAppendedId(albumArtUri, albumId);
					//		String imageUrl = item.imageUrl_;
							dbMsg1 += ",imageUrl_= " + imageUrl;
							String albumKey = item.getAlbum_key();
							dbMsg1 += ",albumKey= " + albumKey;

							Cursor cursor = context.getContentResolver().query(
									MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
									null,
									MediaStore.Audio.Albums.ALBUM_KEY + "=?",
									new String[]{ albumKey },
									null);
			//				MediaStore.Audio.AlbumColumns.
							dbMsg1 += ",cursor= " + cursor.getCount() +"件";
							if (cursor.moveToFirst()){

								// アルバム画像ファイル
								int albumArtIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM_ART);			//nullだった
								String albumArtPass = cursor.getString(albumArtIndex);
								dbMsg1 += ",albumArtPass= " + albumArtPass;
								if(albumArtPass != null ){
									Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(albumArtPass));
									dbMsg1 += ",bitmap= " + bitmap.getWidth() + " × " + bitmap.getHeight();
									mIconView.setImageBitmap(bitmap);
								}else{
									//  https://pisuke-code.com/android-10-or-later-file-save/
									String albumName = item.getAlbum_name();
									dbMsg1 +=  ",albumName= " + albumName + "　を再検索";
									File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
									dbMsg1 +=  ",PublicDirectory= " + path.getName() ;
//									File folder = new File(path, "subDirectoryName");
//									String passName = folder.getPath();
//									dbMsg1 +=  ",passName= " + folder.getPath();
//									String[] files = folder.list();
//									dbMsg1 += ",passName= " + folder.getPath()+",file= " + files[0] ;
								}
							}
							cursor.close();

//							Cursor cursor = context.getContentResolver().query(
//									MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
//									null,
//									MediaStore.Audio.Albums.ALBUM_ID + "=?",
//									new String[]{ item.getAlbum_id() },
//									null);
//							dbMsg1 += ",cursor= " + cursor.getCount() +"件";
//							if (cursor.moveToFirst()){
//
//								// アルバム画像ファイル
//								int albumArtIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM_ART);
//								String albumArt = cursor.getString(albumArtIndex);
//								Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(albumArt));
//								dbMsg1 += ",bitmap= " + bitmap.getWidth() + " × " + bitmap.getHeight();
//								mIconView.setImageBitmap(bitmap);
//							}

//							ContentResolver cr = context.getContentResolver();
//							InputStream inputStream = cr.openInputStream(imageUrl);
//							Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
//							inputStream.close();


//							File file = new File(imageUrl.getPath());			//パス抜きのファイル名
//							dbMsg1 += ",file= " + file.getName();
//							Bitmap bitmap= context.getContentResolver().loadThumbnail (imageUrl,new Size(mIconView.getWidth(),mIconView.getHeight()),null);
			//				Bitmap bitmap= context.getContentResolver().loadThumbnail (imageUrl,new Size(96,96),null);

//							ParcelFileDescriptor parcelFileDescriptor =context.getContentResolver().openFileDescriptor(imageUrl, "r");
//							FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
//							Bitmap bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);
//							parcelFileDescriptor.close();


//								ContentResolver cr = context.getContentResolver();
//								String[] columns = {
//										MediaStore.Images.Media.ALBUM
////										MediaStore.Images.Media.DATA
//								};
//							String selection = MediaStore.Images.Media.ALBUM + "=?";
////							String selection = MediaStore.Images.Media._ID + "=?";
//								Cursor cursor = cr.query(
//										MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//										columns, selection, new String[]{item.getAlbum_id()}, null);
//								dbMsg1 += ",cursor= " + cursor.getCount() +"件";
//								if(cursor.moveToFirst()) {
//									String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
//									dbMsg1 += ",path= " + path;
//									Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(path));
//	//							InputStream stream = context.getContentResolver().openInputStream(uri);
//	//							dbMsg1 += ",stream= " + stream.available();
//									dbMsg1 += ",bitmap= " + bitmap.getWidth() + " × " + bitmap.getHeight();
//									mIconView.setImageBitmap(bitmap);
//								}
//								cursor.close();

////							InputStream is = cr.openInputStream(imageUrl);
////								Bitmap bitmap = BitmapFactory.decodeStream(is);
//								dbMsg1 += ",bitmap= " + bitmap.getWidth() + " × " + bitmap.getHeight();
//								mIconView.setImageBitmap(bitmap);
////							try{
////							}catch(FileNotFoundException err){
////								err.printStackTrace();
////							}




////							ContentResolver resolver = context.getContentResolver();
////							try (InputStream stream = resolver.openInputStream(Uri.parse(item.imageUrl_))) {
////								BitmapFactory.Options options = new BitmapFactory.Options();
////								options.inJustDecodeBounds = true;											//最終的に縮小して読み込む
////								Bitmap bitmap = BitmapFactory.decodeStream(new BufferedInputStream(stream), null, options);
////							}
//					//		Bitmap bitmap = context.getContentResolver().loadThumbnail(uri, new Size(mIconView.getWidth(), mIconView.getHeight()), null);
////							Drawable drawable = Drawable.createFromPath(item.imageUrl_);
////							mIconView.setImageDrawable(drawable);
							myLog(TAG1, dbMsg1);
						} catch (Exception e) {
							myErrorLog(TAG1 ,  dbMsg1 + "で" + e);
						}
					});
				}
				break;
			}
			myLog(TAG,dbMsg);
		} catch (Exception e) {		//汎用
			myErrorLog(TAG,"[ItemLayout]"+ dbMsg+"で"+e.toString());
		}
	}

	///////////////////////////////////////////////////////////////////////////////////
	public static void myLog(String TAG , String dbMsg) {
		MyUtil MyUtil = new MyUtil();
		MyUtil.myLog(TAG , "[ItemLayout]"+ dbMsg);
	}

	public static void myErrorLog(String TAG , String dbMsg) {
		MyUtil MyUtil = new MyUtil();
		MyUtil.myErrorLog(TAG , "[ItemLayout]"+ dbMsg);
	}

}


/**
 * ViewHolderを使わないでListViewを高速化する		http://qiita.com/mofumofu3n/items/28f8be64d39b20e69552
 * **/
