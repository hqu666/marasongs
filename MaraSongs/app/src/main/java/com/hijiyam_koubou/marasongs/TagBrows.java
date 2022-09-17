package com.hijiyam_koubou.marasongs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataInput;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 音楽ファイルのタグから歌詞を抽出します。
 * Androidに特化して
 * ①Activityにする事で処理が長引けばプログレスを表示
 * ②Android4.4以降の書き込み制限に対応してファイルの読み込み方を調整
 * また日本語などのマルチバイト文字も表示します。
* @author Hijiyama-koubou(Hiroomi.Kuwayama)
* @version $Revision: 1.0 $
* special thanks ;Eric Farng
* {@link <a href="http://javamusictag.sourceforge.net/">...</a>}
* special thanks ;Takaaki.Mizyno
* {@link <a href="http://www.takaaki.info/wp-content/uploads/2013/01/ID3v2.3.0J.html">...</a>}
*/
public class TagBrows  extends Activity{
	// implements plogTaskCallback
	public OrgUtil ORGUT;						//自作関数集
	private ploglessTask plTask;
	public Context rContext ;
	public int backCode ;								//戻りコード
	public boolean lyricAri = false;				//歌詞が取得できた
	public String motoEncrod ="ISO-8859-1";					////元の文字コード
	public String saiEncrod = motoEncrod;					////再エンコードする文字コード
	public String filePath = null;					//渡されたファイルのフルパス名
	public String fileExt = null;					//渡されたファイルの拡張子
	public String b_filePath = null;					//読み込み済みのファイル
	public String result_Tag =null;				//	タグ名
	public String result_Samary =null;				//	その他の書き込み情報
	public String result_USLT =null;				//	<UNSYNCED LYRICS>	非同期 歌詞/文書のコピー									//10cc(3)
	public String result_SYLT =null;				//同期 歌詞/文書
	public String result_TPE2 =null;				//バンド/オーケストラ/伴奏	バンド/オーケストラ		//10cc(2)TPE2������������10cc
	public String result_TCOM =null;				//	作曲者																				//10cc(2)
	public String result_TYER =null;				//	年	レコーディング年/年*15	Date*16	 Deprecated									//10cc(5)TYER������������1975
	public String result_TALB =null;				//	アルバム/映画/ショーのタイトル														//10cc(6)
	public String result_TRCK =null;				//	トラックの番号/セット中の位置	トラック #	Track Number	Track#						//10cc(7)TRCK������������2/8
	public String result_TIT2 =null;				//タイトル/曲名/内容の説明	タイトル	Track Title	Title
	public String result_TPE1 =null;				//主な演奏者/ソリスト	トラック アーティスト	Artist Name	Artist
	public String result_TDAT =null;				//日付	Date*11	Date*12	Year*13 Deprecated
	public String result_TPE3 =null;				//指揮者/演奏者詳細情報	指揮者	<CONDUCTOR>	Conductor
	public String result_TCON =null;				//ジャンル																				//10cc(8)TCON������������
	public String result_TCOP =null;				//著作権情報
	public String result_TENC =null;				//エンコーディング ソフトウェア	<ENCODED BY>	Encoder
	public String result_TEXT =null;				//作詞家/文書作成者	作詞者	<LYRICIST>	Lyricist
	public String result_TMOO =null;				//ムード	ムード	<MOOD>	Mood	ID3v2.4フレーム
	public String result_TPE4 =null;				//翻訳者, リミックス, その他の修正	<ModifiedBy>	<REMIXED BY>	Mix Artist, Artists: Remixer
	public String result_TPUB =null;				//出版社	発行元	<PUBLISHER>	Publisher												//10cc(11)TPUB������������Universal Distribu
	public String result_TXXX =null;				//ユーザー定義文字情報フレーム
	public String result_UFID =null;				//一意的なファイル識別子	タグID
	public String result_AENC =null;				//Audio encryption
	public String result_COMR =null;				//Commercial frame]
	public String result_ENCR =null;				//Encryption method registration
	public String result_EQUA =null;				//Equalization
	public String result_ETCO =null;				// Event timing codes]
	public String result_GEOB =null;				//General encapsulated object]
	public String result_GRID =null;				//Group identification registration]
	public String result_IPLS =null;				//Involved people list]
	public String result_LINK =null;				//Linked information]
	public String result_MCDI =null;				//Music CD identifier
	public String result_MLLT =null;				//MPEG location lookup table]
	public String result_OWNE =null;				//Ownership frame]
	public String result_PCNT =null;				//Play counter]
	public String result_POSS =null;				//Position synchronisation frame]
	public String result_RBUF =null;				// Recommended buffer size]
	public String result_RVAD =null;				// Relative volume adjustment]
	public String result_SYTC =null;				//Synchronized tempo codes
	public String result_TBPM =null;				//BPM (beats per minute)
	public String result_TDLY =null;				//Playlist delay
	public String result_TFLT =null;				//File type
	public String result_TIME =null;				//Time Deprecated
	public String result_TIT1 =null;				//Content group description
	public String result_TIT3 =null;				// Subtitle/Description refinement
	public String result_TKEY =null;				//Initial key
	public String result_TLAN =null;				// Language(s)]
	public String result_TLEN =null;				// Length]
	public String result_TMED =null;				// Media type]
	public String result_TOAL =null;				//Original album/movie/show title
	public String result_TOFN =null;				//Original filename]
	public String result_TOLY =null;				//  Original lyricist(s)/text writer(s)]
	public String result_TOPE =null;				//Original artist(s)/performer(s)	 Deprecated
	public String result_TORY =null;				//Original release year]
	public String result_TOWN =null;				// File owner/licensee
	public String result_TRDA =null;				// Recording dates	Deprecated
	public String result_TRSN =null;				// Internet radio station name
	public String result_TRSO =null;				// Internet radio station owner
	public String result_TSIZ =null;				//Size	 Deprecated
	public String result_TSRC =null;				//ISRC (international standard recording code)
	public String result_TSSE =null;				//Software/Hardware and settings used for encoding
	public String result_WCOM =null;				//Commercial information]
	public String result_WCOP =null;				// Copyright/Legal information
	public String result_WOAF =null;				// Official audio file webpage
	public String result_WOAR =null;				// Official artist/performer webpage
	public String result_WOAS =null;				// Official audio source webpage
	public String result_WORS =null;				//  Official internet radio station homepage
	public String result_WPAY =null;				//  Payment
	public String result_WPUB =null;				//Publishers official webpage
	public String result_USER =null;				//Terms of use
	public String result_WXXX =null;				//User defined URL link frame
	public String result_COMM =null;				//コメント																				//10cc(7')
	public String result_TPOS =null;				//セット中の位置	ディスク #	Disc Number	Disc#										//10cc(7')TPOS������������1/1
	public String result_POPM =null;				//人気メーター																				//10cc(2')Windows Media Player 9 Seri
	public String result_APIC =null;				//付属する画像																			//10cc(5')APIC��(������JPG������ÿØÿà��
	public String result_PRIV =null;				//プライベートフレーム									//☆複数出現する；10cc(4)(10)PRIV
	public String result_RVRB =null;					//Reverb
	public String result_WCAF =null;				//ファイルの所有者/ライセンシー
	public String result_CRM =null;			//ID3v2.2
	public String result_TSOA =null;	//©nam			//アルバム（読み）										ALBUMSORT						ID3ｖ2；--	ID3ｖ3；TSOA
	public String result_TSOP =null;	//soar			//アーティスト（読み）									ARTISTSORT						ID3ｖ2；--	ID3ｖ3；TSOP
	public String result_TSOC =null;	//soco			//作曲者（読み）										SortComposer					ID3ｖ2；?-	ID3ｖ3；TSOC	COMPOSERSORT

	public String result_des =null;	//©des			//説明													Description	Track				ID3ｖ2；--	ID3ｖ3；--		SUBTITLE
	public String result_nrt =null;	//©nrt			//														Narrator						ID3ｖ2；--	ID3ｖ3；--
	public String result_iTunesInfo =null;	//----			//														iTunesInfo						ID3ｖ2；--	ID3ｖ3；--
	public String result_PST =null;	//@PST			//														Parent Short Title				ID3ｖ2；--	ID3ｖ3；--
	public String result_ppi =null;	//@ppi			//														Parent ProductID				ID3ｖ2；--	ID3ｖ3；--
	public String result_sti =null;	//@sti			//														Short Title						ID3ｖ2；--	ID3ｖ3；--
	public String result_AACR =null;	//AACR");	//														Unknown_AACR?					ID3ｖ2；--	ID3ｖ3；--
	public String result_CDEK =null;	//CDEK");	//														Unknown_CDEK?					ID3ｖ2；--	ID3ｖ3；--
	public String result_CDET =null;	//CDET");	//														Unknown_CDET?					ID3ｖ2；--	ID3ｖ3；--
	public String result_GUID =null;	//GUID");	//														GUID							ID3ｖ2；--	ID3ｖ3；--
	public String result_VERS =null;	//VERS");	//														ProductVersion					ID3ｖ2；--	ID3ｖ3；--
	public String result_akID =null;	//akID");	//アカウントの種類			編集不可					Apple Store Account Type		ID3ｖ2；--	ID3ｖ3；--
	public String result_apID =null;	//apID");	//アカウント情報					ITUNESACCOUNT		Apple Store Account				ID3ｖ2；--	ID3ｖ3；--
	public String result_auth =null;	//auth");	//														Author							ID3ｖ2；--	ID3ｖ3；--
	public String result_catg =null;	//catg");	//ポッドキャストカテゴリ								Category						ID3ｖ2；--	ID3ｖ3；--		PODCASTCATEGORY
	public String result_cnID =null;	//cnID");	//コンテンツ識別子										AppleStoreCatalogID				ID3ｖ2；--	ID3ｖ3；--		ITUNESCATALOGID
	public String result_cpil =null;	//cpil");	//コンピレーションの明示								Compilation						ID3ｖ2；--	ID3ｖ3；--		COMPILATION
	public String result_egid =null;	//egid");	//ポッドキャストエピソードユニークID					Episode Global Unique ID		ID3ｖ2；--	ID3ｖ3；--		PODCASTID
	public String result_geID =null;	//geID");	//ジャンル識別子		編集不可						GenreID							ID3ｖ2；--	ID3ｖ3；--
	public String result_grup =null;	//grup");	//														Grouping						ID3ｖ2；--	ID3ｖ3；--
	public String result_gshh =null;	//gshh");	//														GoogleHostHeade					ID3ｖ2；--	ID3ｖ3；--
	public String result_gspm =null;	//gspm");	//														GooglePingMessage				ID3ｖ2；--	ID3ｖ3；--
	public String result_gspu =null;	//gspu");	//														GooglePingURL					ID3ｖ2；--	ID3ｖ3；--
	public String result_gssd =null;	//gssd");	//														GoogleSourceData				ID3ｖ2；--	ID3ｖ3；--
	public String result_gsst =null;	//gsst");	//														GoogleStartTime					ID3ｖ2；--	ID3ｖ3；--
	public String result_gstd =null;	//gstd");	//														GoogleTrackDuration				ID3ｖ2；--	ID3ｖ3；--
	public String result_hdvd =null;	//hdvd");	//	?													HDVideo	?						ID3ｖ2；--	ID3ｖ3；--
	public String result_hdtv =null;	//hdtv");	//ビデオ解像度の明示		ITUNESHDVIDEO				HDVideo							ID3ｖ2；--	ID3ｖ3；--
	public String result_itnu =null;	//itnu");	//														iTunesU							ID3ｖ2；--	ID3ｖ3；--
	public String result_keyw =null;	//keyw");	//ポッドキャストキーワード		編集不可				Keyword							ID3ｖ2；--	ID3ｖ3；--
	public String result_pcst =null;	//pcst");	//ポッドキャストであることを明示						Podcast							ID3ｖ2；--	ID3ｖ3；--
	public String result_pgap =null;	//pgap");	//ギャップレスコンテンツの明示							PlayGap							ID3ｖ2；--	ID3ｖ3；--		ITUNESGAPLESS
	public String result_plID =null;	//plID");	//プレイリスト（アルバム）識別子	編集不可			PlayListID						ID3ｖ2；--	ID3ｖ3；--
	public String result_prID =null;	//prID");	//														ProductID						ID3ｖ2；--	ID3ｖ3；--
	public String result_purd =null;	//purd");	//購入日												ITUNESPURCHASEDATE				ID3ｖ2；--	ID3ｖ3；--		ITUNESPURCHASEDATE
	public String result_purl =null;	//purl");	//ポッドキャストURL										Podcast URL						ID3ｖ2；--	ID3ｖ3；--		PODCASTURL
	public String result_rtng =null;	//rtng");	//保護者のためのレートの明示?番組?番組（読み）			Rating?	TVSHOW?					ID3ｖ2；--	ID3ｖ3；--		ITUNESADVISORY?	TVSHOW?
	public String result_sfID =null;	//sfID");	//ストアの国				編集不可					AppleStore Country				ID3ｖ2；--	ID3ｖ3；--
	public String result_sosn =null;	//sosn");	//														Sort Show						ID3ｖ2；--	ID3ｖ3；--
	public String result_tven =null;	//tven");	//エピソードID											TVEpisodeID						ID3ｖ2；--	ID3ｖ3；--
	public String result_tves =null;	//tves");	//														TVEpisode						ID3ｖ2；--	ID3ｖ3；--
	public String result_tvnn =null;	//tvnn");	//放送局												TVNetworkName					ID3ｖ2；--	ID3ｖ3；--
	public String result_tvsh =null;	//tvsh");	//														TVShow							ID3ｖ2；--	ID3ｖ3；--
	public String result_tvsn =null;	//tvsn");	//シーズン												TVSeason						ID3ｖ2；--	ID3ｖ3；--

	public String stock_acc_moov =null;		//QuickTime Movie Tagsの一時保存
	public String stock_acc_meta =null;		//QuickTime Meta Tagsの一時保存
	public String stock_acc_mdat;		//QuickTime Meta Tagsの一時保存
	public String stock_acc_frea =null;		//Kodak frea Tagsの一時保存
	public String stock_acc_free =null;		//Kodak Free Tagsの一時保存
	public String stock_acc_pnot =null;		//QuickTime Preview Tagsの一時保存
	public String stock_acc_udta =null;		//FLIR UserData Tagsの一時保存
	public String stock_acc_skip =null;		//Canon Skip Tagsの一時保存
	public String stock_acc_uuid_XMP  =null;		//XMP Tagsの一時保存
	public String stock_acc_uuid_PROF  =null;		//UUID-PROFの一時保存
	public String stock_acc_uuid_FlipF  =null;		//UUID-Flipの一時保存

	public String stock_acc_movie_meta  =null;		//										QuickTime Tags.QuickTime Movie Tags.Meta		ID3ｖ2；CRM	ID3ｖ3；？	Encrypted meta frame?
	public String stock_acc_movie_cmov  =null;		//										QuickTime Tags.QuickTime Movie Tags.cmov		ID3ｖ2；--	ID3ｖ3；--
	public String stock_acc_movie_htka  =null;		//										QuickTime Tags.QuickTime Movie Tags.htka		ID3ｖ2；--	ID3ｖ3；--
	public String stock_acc_movie_iods  =null;		//										QuickTime Tags.QuickTime Movie Tags.iods		ID3ｖ2；--	ID3ｖ3；--
	public String stock_acc_movie_mvhd  =null;		//MovieHeader							QuickTime Tags.QuickTime Movie Tags.mvhd		ID3ｖ2；--	ID3ｖ3；
	public String stock_acc_movie_trak  =null;		//										QuickTime Tags.QuickTime Movie Tags.trak		ID3ｖ2；--	ID3ｖ3；
	public String stock_acc_movie_udta  =null;		//User Data								QuickTime Tags.QuickTime Movie Tags.udta		ID3ｖ2；--	ID3ｖ3；USER	Terms of use
	public String stock_acc_movie_uuid  =null;		//XMP/UUID-PROF/UUID-Flip/UUID-Unknown?	QuickTime Tags.QuickTime Movie Tags.uuid		ID3ｖ2；--	ID3ｖ3；--

	public String stock_acc_meta_ilst  =null;		//										QuickTime Tags.QuickTime Meta Tags.ilst			ID3ｖ2；--	ID3ｖ3；--
	public String stock_acc_meta_bxml  =null;		//BinaryXML?							QuickTime Tags.QuickTime Meta Tags.bxml			ID3ｖ2；--	ID3ｖ3；--
	public String stock_acc_meta_dinf  =null;		//DataInformation?						QuickTime Tags.QuickTime Meta Tags.dinf			ID3ｖ2；--	ID3ｖ3；--
	public String stock_acc_meta_free  =null;		//Kodak Free?	/Free?					QuickTime Tags.QuickTime Meta Tags.free			ID3ｖ2；--	ID3ｖ3；--
	public String stock_acc_meta_hdlr  =null;		//Handler								QuickTime Tags.QuickTime Meta Tags.hdlr			ID3ｖ2；--	ID3ｖ3；--
	public String stock_acc_meta_iinf  =null;		//ItemInformation?						QuickTime Tags.QuickTime Meta Tags.iinf			ID3ｖ2；--	ID3ｖ3；--
	public String stock_acc_meta_iloc  =null;		//ItemLocation?q						QuickTime Tags.QuickTime Meta Tags.iloc			ID3ｖ2；--	ID3ｖ3；--
	public String stock_acc_meta_ipmc  =null;		//IPMPControl?							QuickTime Tags.QuickTime Meta Tags.ipmc			ID3ｖ2；--	ID3ｖ3；--
	public String stock_acc_meta_ipro  =null;		//ItemProtection?						QuickTime Tags.QuickTime Meta Tags.ipro			ID3ｖ2；--	ID3ｖ3；--
	public String stock_acc_meta_keys  =null;		//Keys									QuickTime Tags.QuickTime Meta Tags.keys			ID3ｖ2；--	ID3ｖ3；--
	public String stock_acc_meta_pitm  =null;		//PrimaryItemReference?					QuickTime Tags.QuickTime Meta Tags.pitm			ID3ｖ2；--	ID3ｖ3；--
	public String stock_acc_meta_xml  =null;		//XML					QuickTime Tags.QuickTime Meta Tags.xml 			ID3ｖ2；--	ID3ｖ3；--

	static final int read_FILE = 1000;					//ファイル読込
	static final int read_USLT = read_FILE + 1;		// 歌詞読み込み
	static final int SAI_ENCORD = read_USLT + 1;		//再エンコード
	static final int read_AAC_PRE = SAI_ENCORD + 1;						//最小限の設定読取り
	static final int read_AAC_LYRIC = read_AAC_PRE + 1;						//@Lyrだけを読めるか試みる
	static final int read_AAC_HEAD = read_AAC_LYRIC + 1;						//QuickTime Tagsの読取り
	static final int read_AAC_HEAD_Movie = read_AAC_HEAD + 1;			//QuickTime Tags.QuickTime Movie Tagsの読取り
	static final int read_AAC_Movie_Meta = read_AAC_HEAD_Movie + 1;		//QuickTime Tags.QuickTime Meta Tagsの読取り
	static final int read_AAC_ITEM = read_AAC_Movie_Meta + 1;				//QuickTime Tagsの読取り
	static final int read_WMA_ITEM = read_AAC_ITEM + 1;						//WMAのオブジェクト読取り
	static final int read_WMA_ID32 = read_WMA_ITEM + 1;						//WMAに埋め込まれたID3v2タグの読取り
	static final int read_WMA_ID33 = read_WMA_ID32 + 1;						//WMAに埋め込まれたID3v3タグの読取り
	static final int read_WMA_AAC = read_WMA_ID33 + 1;						//WMAに埋め込まれたAAC Atomの読取り

	private File mp3file;						//the mp3 file that this instance represents. This value can be null. This value is also used for any methods that are called without a file argument
	private boolean padding;		//value read from the MP3 Frame header
	private boolean privacy;		//value read from the MP3 Frame header
	private boolean protection;		//value read from the MP3 Frame header
	private boolean variableBitRate;		//value read from the MP3 Frame header
	private byte layer;		//value read from the MP3 Frame header
	private byte mode;		//value read from the MP3 Frame header
	private byte modeExtension;		//value read from the MP3 Frame header
	private byte mpegVersion;		//value read from the MP3 Frame header
	private double frequency;		//frequency determined from MP3 Version and frequency value read from the MP3 Frame header
	private int bitRate;		//bitrate calculated from the frame MP3 Frame header

	public static final int BIT7 = 0x80;								//defined for convenience
	public static final int BIT6 = 0x40;
	public static final int BIT5 = 0x20;
	public static final int BIT4 = 0x10;
	public static final int BIT3 = 0x08;
	public static final int BIT2 = 0x04;
	public static final int BIT1 = 0x02;
	public static final int BIT0 = 0x01;

	public static final int MASK_V22_UNSYNCHRONIZATION = BIT7;			//ID3v2.2 Header bit mask
	public static final int MASK_V22_COMPRESSION = BIT7;					//ID3v2.2 Header bit mask
	public static final int MASK_V23_UNSYNCHRONIZATION = BIT7;			//ID3v2.3 Header bit mask
	public static final int MASK_V23_EXTENDED_HEADER = BIT6;			//ID3v2.3 Header bit mask
	public static final int MASK_V23_EXPERIMENTAL = BIT5;			// ID3v2.3 Header bit mask
	public static final int MASK_V24_UNSYNCHRONIZATION = BIT7;		// ID3v2.4 Header bit mask
	public static final int MASK_V24_EXTENDED_HEADER = BIT6;					//ID3v2.4 Header bit mask
	public static final int MASK_V24_EXPERIMENTAL = BIT5;				//ID3v2.4 Header bit mask
	public static final int MASK_V24_FOOTER_PRESENT = BIT4;				//ID3v2.4 Header bit mask
	public static final int MASK_V23_CRC_DATA_PRESENT = BIT7;			//ID3v2.3 Extended Header bit mask
	public static final int MASK_V24_TAG_UPDATE = BIT6;						// ID3v2.4 Extended header bit mask
	public static final int MASK_V24_CRC_DATA_PRESENT = BIT5;				//ID3v2.4 Extended header bit mask
	public static final int MASK_V24_TAG_RESTRICTIONS = BIT4;			//ID3v2.4 Extended header bit mask
	public static final int MASK_V24_TAG_SIZE_RESTRICTIONS = (byte) BIT7 | BIT6;					//ID3v2.4 Extended header bit mask
	public static final int MASK_V24_TEXT_ENCODING_RESTRICTIONS = BIT5;				//ID3v2.4 Extended header bit mask
	public static final int MASK_V24_TEXT_FIELD_SIZE_RESTRICTIONS = BIT4 | BIT3;			//ID3v2.4 Extended header bit mask
	public static final int MASK_V24_IMAGE_ENCODING = BIT2;										//ID3v2.4 Extended header bit mask
	public static final int MASK_V24_IMAGE_SIZE_RESTRICTIONS = BIT2 | BIT1;				//ID3v2.4 Extended header bit mask

	/**
	 * Creates a new empty MP3File object that is not associated with a specific file.
	 * */
//	public TagBrows(Context rContext) {
//		super();
//		final String TAG = "TagBrows[TagBrows]";
//		String dbMsg= "開始";/////////////////////////////////////
//		try{
//			this.rContext = rContext;
//			myLog(TAG,dbMsg);
//		}catch (Exception e) {
//			myErrorLog(TAG,dbMsg + "で"+e.toString());
//		}
//	}

	/**
	 * Creates a new MP3File object.
	 */
//	public TagBrows(final TagBrows copyObject) {
//		super();
//		final String TAG = "TagBrows(copyObject)[TagBrows]";
//		String dbMsg= "2";
//		try{
//			dbMsg= "copyObject=" + copyObject.toString();
//			copyProtected = copyObject.copyProtected;
//			home = copyObject.home;
//			padding = copyObject.padding;
//			privacy = copyObject.privacy;
//			protection = copyObject.protection;
//			variableBitRate = copyObject.variableBitRate;
//			emphasis = copyObject.emphasis;
//			layer = copyObject.layer;
//			mode = copyObject.mode;
//			modeExtension = copyObject.modeExtension;
//			mpegVersion = copyObject.mpegVersion;
//			frequency = copyObject.frequency;
//			bitRate = copyObject.bitRate;
//			mp3file = new File(copyObject.mp3file.getAbsolutePath());
//			filenameTag = new FilenameTag(copyObject.filenameTag);
//			id3v2tag = (AbstractID3v2) TagUtility.copyObject(copyObject.id3v2tag);
//			dbMsg +="id3v2tag=" + id3v2tag.toString();
//			lyrics3tag = (AbstractLyrics3) TagUtility.copyObject(copyObject.lyrics3tag);
//			dbMsg +="lyrics3tag=" + lyrics3tag.toString();
//			id3v1tag = (ID3v1) TagUtility.copyObject(copyObject.id3v1tag);
//			dbMsg +="id3v1tag=" + id3v1tag.toString();
//			myLog(TAG,dbMsg);
//		}catch (Exception e) {
//			myErrorLog(TAG,dbMsg + "で"+e.toString());
//		}
//	}

	/**
	 * 渡された名称でFileオブジェクトを作成
	 * @param filename MP3 file
	 * @throws IOException  on any I/O error
	 * @throws TagException on any exception generated by this library.
	 */
//	public TagBrows(final String filename,Context rContext) throws IOException {
//		this(new File(filename));
//
//		final String TAG = "TagBrows(filename)[TagBrows]";
//		String dbMsg= "filename=" + filename;/////////////////////////////////////
//		try{
//			this.rContext = rContext;
//			if( ! filename.equals(b_filePath) ){				//読み込み済みのファイルでなければ
//				//		mp3file = new File(filename);
//						dbMsg +="(SDK=" + String.valueOf(Build.VERSION.SDK) + ":";
//						dbMsg +=",mp3file=" + mp3file  + ")";		//(SDK=19:,exists=false.WRITE_0.READ_0)
//				//		myLog(TAG,dbMsg);
//						b_filePath = filename;
//			}
//			myLog(TAG,dbMsg);
//		}catch (Exception e) {
//			myErrorLog(TAG,dbMsg + "で"+e.toString());
//		}
//	}

	/**
	 * 渡されたFileオブジェクトからこのクラスオブジェクトを作成
	 * @param file MP3 file
	 * @throws IOException  on any I/O error
	 * @throws TagException on any exception generated by this library.
	 */
//	public TagBrows(final File file) throws IOException {
//		this(file, true);
//		final String TAG = "TagBrows(file)[TagBrows]";
//		String dbMsg= "開始";/////////////////////////////////////
//		try{
//			dbMsg +="(SDK=" + String.valueOf(Build.VERSION.SDK) + ":";
//			dbMsg +=",exists=" + file.exists()  + ")";		//(SDK=19:,exists=true)
//			myLog(TAG,dbMsg);
//		}catch (Exception e) {
//			myErrorLog(TAG,dbMsg + "で"+e.toString());
//		}
//	}

	/**
	 * Creates a new MP3File object and parse the tag from the given file Object.
	 * @param file  MP3 file
	 * @param writeable open in read (false) or read-write (true) mode
	 * @throws IOException  on any I/O error
	 * @throws TagException on any exception generated by this library.
	 */
private byte majorVersion = (byte) 0;
    private byte revision = (byte) 0;
    protected boolean encryption = false;
	protected boolean fileAlterPreservation = false;
	protected boolean groupingIdentity = false;
	protected boolean readOnly = false;
	protected boolean tagAlterPreservation = false;	// these are flags for each frame them selves
    protected boolean compression = false;												//ID3v2_2
    protected boolean unsynchronization = false;											//ID3v2_2
    protected boolean crcDataFlag = false;								//IID3v2_3
    protected boolean experimental = false;								//IID3v2_3
    protected boolean extended = false;									//IID3v2_3
    protected int crcData = 0;											//IID3v2_3
    protected int paddingSize = 0;										//IID3v2_3
    protected boolean footer = false;					//IID3v2_4
    protected boolean tagRestriction = false;					//IID3v2_4
    protected boolean updateTag = false;					//IID3v2_4
    protected byte imageEncodingRestriction = 0;					//IID3v2_4
    protected byte imageSizeRestriction = 0;					//IID3v2_4
    protected byte tagSizeRestriction = 0;					//IID3v2_4
    protected byte textEncodingRestriction = 0;					//IID3v2_4
    protected byte textFieldSizeRestriction = 0;					//IID3v2_4
	private List<Object> tagData;
	private List<String> syougou;
	private List<String> kensaku;								//検索するフレーム名

	public ScrollView pdg_scroll;		//スクロール
	public TextView pgd_msg_tv ;
	public Handler handler1;
	public ProgressBar progBar1;		 //メインプログレスバー
	public TextView pgd_val_tv;
	public TextView pgd_max_tv;
	public TextView pgd_par_tv;
	public Handler handler2;
	public ProgressBar ProgBar2;		 //セカンドプログレスバー
	public TextView pgd_val2_tv;
	public TextView pgd_max2_tv;
	public TextView pgd_par2_tv;
	public Button pgd_finsh_bt;		//終了ボタン
	public LinearLayout pgdBar1_ll;		//メインプログレスバーエリア
	public LinearLayout pgdBar2_ll;		//セカンドプログレスバーエリア
	public String _numberFormat = "%d/%d";
	public  NumberFormat _percentFormat = NumberFormat.getPercentInstance();
	/**
	 * 起動時に透明化したアクティビテイを詠み込む
	 * 呼出しサンプル
	 * 	Intent intentTB = new Intent(MaraSonActivity.this,TagBrows.class);
	 * 	intentTB.putExtra("filePath",dataFN);
	 * startActivityForResult(intentTB , LyricCheck );
	 * */
		@Override
		public void onCreate(Bundle savedInstanceState) {	//WindowManagerの設定とアクティビティの読み込み
			super.onCreate(savedInstanceState);
			final String TAG = "onCreate";
			String dbMsg="[TagBrows]";
			try{
				ORGUT = new OrgUtil();		//自作関数集
				plTask  = new TagBrows.ploglessTask(this);
				dbMsg +="rContext=" + this.rContext;
				if(this.rContext == null){
					this.rContext = TagBrows.this;
					dbMsg +=">>" + this.rContext;/////////////////////////////////////
				}
				getWindow().setFormat(PixelFormat.TRANSLUCENT);	//利かず
				setContentView(R.layout.trance);					//透明設定したActivty
				Bundle extras = getIntent().getExtras();
				int reqCode = extras.getInt("reqCode");
				backCode = extras.getInt("backCode");
				dbMsg +="、reqCode=" + reqCode + "、backCode=" + backCode;
				switch (reqCode) {
				case read_USLT:					//歌詞を読み込み
					filePath = extras.getString("filePath");					//渡されたファイル
					dbMsg +=",filePath=" + filePath;/////////////////////////////////////
					File file = new File(filePath);
					if ( filePath.endsWith("m4a") ||
							filePath.endsWith("3gp") ) {
						headReadAac(file);								//AACの処理開始
					}else if ( filePath.endsWith("wma") ||
							filePath.endsWith("wav") ) {
						result_Tag = getApplicationContext().getString(R.string.yomikomi_hunou);				//この曲はタグ情報を読み込めませんでした。</string>
						back2Activty(  );			//呼び出しの戻り処理
//	断念2016/03/24		headReadWma(file);								//WMAの処理開始
					}else{
						file2Tag( file);								//ID3タグのRandomAccessFileをString変換
					}
					break;
				case SAI_ENCORD:				//歌詞の再エンコード
					String songLyric = extras.getString("songLyric");					////歌詞の再エンコード
					dbMsg +=",songLyric=" + songLyric.length() + "文字";/////////////////////////////////////
					String motoEncrod = extras.getString("motoEncrod");					////元の文字コード
					dbMsg +=",エンコード=" + motoEncrod;/////////////////////////////////////
					String saiEncrod = extras.getString("saiEncrod");					////再エンコードする文字コード
					dbMsg +=",saiEncrod=" + saiEncrod;
					saiEncord(songLyric , motoEncrod , saiEncrod);										//再エンコードして呼出し元のActivtyに返す
					break;
				}
				myLog(TAG,dbMsg);
			}catch (Exception e) {
				myErrorLog(TAG,dbMsg +"で"+e.toString());
			}
		}
		/**
		 * <code>RandomAccessFile.read</code>で読み込んだバイト配列をUTF-8エンコードした<code>String</code>で返します。
		 * EOFに達するとnullを返します。
		 * @return
		 */
		public void file2Tag(final File file){			//RandomAccessFileをString変換
			String result =null;
			final String TAG = "file2Tag";
			String dbMsg="[TagBrows]";
			try{
				dbMsg += "file=" + file;
				filePath = file.getPath();
				dbMsg +=",filename=" + filePath ;				//this.fileObj.getPath()
				initResult();								//戻り値の初期化
				lyricAri = false;				//歌詞が取得できた
				raf2Str(file, true);			//RandomAccessFileをString変換
//				}
		//		myLog(TAG,dbMsg);
			}catch (Exception e) {
				myErrorLog(TAG,dbMsg + "で"+e.toString());
			}
		}

		/**
		 * 戻り値の初期化
		 * */
		public void initResult() {								//戻り値の初期化
			String result =null;
			final String TAG = "initResult[TagBrows]";
			String dbMsg="[TagBrows]";
			try{
				result_Tag =null;				//	タグ名
				result_USLT =null;				//	<UNSYNCED LYRICS>	非同期 歌詞/文書のコピー									//10cc(3)
				result_TPE2 =null;				//バンド/オーケストラ/伴奏	バンド/オーケストラ		//10cc(2)TPE2������������10cc
				result_TCOM =null;				//	作曲者																				//10cc(2)
				result_TYER =null;				//	年	レコーディング年/年*15	Date*16	 Deprecated									//10cc(5)TYER������������1975
				result_TALB =null;				//	アルバム/映画/ショーのタイトル														//10cc(6)
				result_TRCK =null;				//	トラックの番号/セット中の位置	トラック #	Track Number	Track#						//10cc(7)TRCK������������2/8
				result_TIT2 =null;				//タイトル/曲名/内容の説明	タイトル	Track Title	Title
				result_TPE1 =null;				//主な演奏者/ソリスト	トラック アーティスト	Artist Name	Artist
				result_SYLT =null;				//同期 歌詞/文書
				result_TDAT =null;				//日付	Date*11	Date*12	Year*13 Deprecated
				result_TPE3 =null;				//指揮者/演奏者詳細情報	指揮者	<CONDUCTOR>	Conductor
				result_TCON =null;				//ジャンル																				//10cc(8)TCON������������
				result_TCOP =null;				//著作権情報
				result_TENC =null;				//エンコーディング ソフトウェア	<ENCODED BY>	Encoder
				result_TEXT =null;				//作詞家/文書作成者	作詞者	<LYRICIST>	Lyricist
				result_TMOO =null;				//ムード	ムード	<MOOD>	Mood	ID3v2.4フレーム
				result_TPE4 =null;				//翻訳者, リミックス, その他の修正	<ModifiedBy>	<REMIXED BY>	Mix Artist, Artists: Remixer
				result_TPUB =null;				//出版社	発行元	<PUBLISHER>	Publisher												//10cc(11)TPUB������������Universal Distribu
				result_TXXX =null;				//ユーザー定義文字情報フレーム
				result_UFID =null;				//一意的なファイル識別子	タグID
				result_AENC =null;				//Audio encryption
				result_COMR =null;				//Commercial frame]
				result_ENCR =null;				//Encryption method registration
				result_EQUA =null;				//Equalization
				result_ETCO =null;				// Event timing codes]
				result_GEOB =null;				//General encapsulated object]
				result_GRID =null;				//Group identification registration]
				result_IPLS =null;				//Involved people list]
				result_LINK =null;				//Linked information]
				result_MCDI =null;				//Music CD identifier
				result_MLLT =null;				//MPEG location lookup table]
				result_OWNE =null;				//Ownership frame]
				result_PCNT =null;				//Play counter]
				result_POSS =null;				//Position synchronisation frame]
				result_RBUF =null;				// Recommended buffer size]
				result_RVAD =null;				// Relative volume adjustment]
				result_SYTC =null;				//Synchronized tempo codes
				result_TBPM =null;				//BPM (beats per minute)
				result_TDLY =null;				//Playlist delay
				result_TFLT =null;				//File type
				result_TIME =null;				//Time Deprecated
				result_TIT1 =null;				//Content group description
				result_TIT3 =null;				// Subtitle/Description refinement
				result_TKEY =null;				//Initial key
				result_TLAN =null;				// Language(s)]
				result_TLEN =null;				// Length]
				result_TMED =null;				// Media type]
				result_TOAL =null;				//Original album/movie/show title
				result_TOFN =null;				//Original filename]
				result_TOLY =null;				//  Original lyricist(s)/text writer(s)]
				result_TOPE =null;				//Original artist(s)/performer(s)	 Deprecated
				result_TORY =null;				//Original release year]
				result_TOWN =null;				// File owner/licensee
				result_TRDA =null;				// Recording dates	Deprecated
				result_TRSN =null;				// Internet radio station name
				result_TRSO =null;				// Internet radio station owner
				result_TSIZ =null;				//Size	 Deprecated
				result_TSRC =null;				//ISRC (international standard recording code)
				result_TSSE =null;				//Software/Hardware and settings used for encoding
				result_WCOM =null;				//Commercial information]
				result_WCOP =null;				// Copyright/Legal information
				result_WOAF =null;				// Official audio file webpage
				result_WOAR =null;				// Official artist/performer webpage
				result_WOAS =null;				// Official audio source webpage
				result_WORS =null;				//  Official internet radio station homepage
				result_WPAY =null;				//  Payment
				result_WPUB =null;				//Publishers official webpage
				result_USER =null;				//Terms of use
				result_WXXX =null;				//User defined URL link frame
				result_COMM =null;				//コメント																				//10cc(7')
				result_TPOS =null;				//セット中の位置	ディスク #	Disc Number	Disc#										//10cc(7')TPOS������������1/1
				result_POPM =null;				//人気メーター																				//10cc(2')Windows Media Player 9 Seri
				result_APIC =null;				//付属する画像																			//10cc(5')APIC��(������JPG������ÿØÿà��
				result_PRIV =null;				//プライベートフレーム									//☆複数出現する；10cc(4)(10)PRIV
				result_WCAF =null;				//ファイルの所有者/ライセンシー
				result_CRM =null;				//ID3v2.2
				result_TSOA =null;	//©nam			//アルバム（読み）										ALBUMSORT						ID3ｖ2；--	ID3ｖ3；TSOA
				result_TSOP =null;	//soar			//アーティスト（読み）									ARTISTSORT						ID3ｖ2；--	ID3ｖ3；TSOP
				result_TSOC =null;	//soco			//作曲者（読み）										SortComposer					ID3ｖ2；?-	ID3ｖ3；TSOC	COMPOSERSORT
			}catch (Exception e) {
				myErrorLog(TAG,dbMsg + "で"+e.toString());
			}
		}

	/**
	 * フィールド名リストをList<String> syougouに作成
	 * 	@link http://wikiwiki.jp/qmp/?Plugins%2FLibraries%2FTag%20Editors%2FID3%20Tags#u7ea0e59
	 * */
	public void makeSyougouList() {	//フィールド名リストをList<String> syougouに作成
		String result =null;
		final String TAG = "makeSyougouList";
		String dbMsg="[TagBrows]";
		try{
			if(syougou == null){
				syougou = new ArrayList<String>();
				switch (this.majorVersion) {
				case 2:
					syougou.add("BUF");		//推奨バッファサイズ											Recommended buffer size									ID3ｖ3；RBUF
					syougou.add("CNT");		//演奏回数														Play counter											ID3ｖ3；PCNT
					syougou.add("COM");		//コメント														Comments												ID3ｖ3；COMM
					syougou.add("CRA");		//オーディオの暗号化											Audio encryption										ID3ｖ3；AENC
					syougou.add("CRM");		//																Encrypted meta frame									ID3ｖ3；？
					syougou.add("ETC");		//イベントタイムコード											Event timing codes										ID3ｖ3；ETCO
					syougou.add("EQU");		//音質調整														Equalization											ID3ｖ3；EQUA
					syougou.add("GEO");		//パッケージ化された一般的なオブジェクト						General encapsulated object								ID3ｖ3；GEOB
					syougou.add("IPL");		//協力者一覧													Involved people list									ID3ｖ3；IPLS
					syougou.add("LNK");		//リンク情報	 												Linked information										ID3ｖ3；LINK
					syougou.add("MCI");		//音楽ＣＤ識別子												Music CD Identifier										ID3ｖ3；MCDI
					syougou.add("MLL");		//MPEGロケーションルックアップテーブル							MPEG location lookup table								ID3ｖ3；MLLT
					syougou.add("PIC");		//付属する画像													Attached picture										ID3ｖ3；APIC
					syougou.add("POP");		//人気メーター 													Popularimeter											ID3ｖ3；POPM
					syougou.add("REV");		//リバーブ	 													Reverb													ID3ｖ3；RVRB
					syougou.add("RVA");		//相対的ボリューム調整											Relative volume adjustment								ID3ｖ3；RVAD
					syougou.add("SLT");		//同期 歌詞/文書												Synchronized lyric/text									ID3ｖ3；SYLT
					syougou.add("STC");		//																Synced tempo codes										ID3ｖ3；?
					syougou.add("TAL");		//アルバム/映画/ショーのタイトル								Album/Movie/Show title									ID3ｖ3；TALB
					syougou.add("TBP");		//一分間の拍数													BPM (Beats Per Minute)									ID3ｖ3；TBPM
					syougou.add("TCM");		//作曲者														Composer												ID3ｖ3；TCOM
					syougou.add("TCO");		//ジャンル														Content type											ID3ｖ3；TCON
					syougou.add("TCR");		//																Copyright message										ID3ｖ3；TCOP
					syougou.add("TDA");		//日付	Date*11	Date*12	Year*13 Deprecated						Date													ID3ｖ3；TDAT
					syougou.add("TDY");		//プレイリスト遅延時間											Playlist delay											ID3ｖ3；TDLY
					syougou.add("TEN");		//エンコーディング ソフトウェア									Encoded by												ID3ｖ3；TENC
					syougou.add("TFT");		//ファイルタイプ												File type												ID3ｖ3；TFLT
					syougou.add("TIM");		//時間															Time(Time Deprecated?)									ID3ｖ3；TIME
					syougou.add("TKE");		//初めの調														Initial key												ID3ｖ3；TKEY
					syougou.add("TLA");		//言語															Language(s)												ID3ｖ3；TLAN
					syougou.add("TLE");		//長さ															Length													ID3ｖ3；TLEN
					syougou.add("TMT");		//メディアタイプ												Media type												ID3ｖ3；TMED
					syougou.add("TOA");		//オリジナルアーティスト/演奏者									Original artist(s)/performer(s)							ID3ｖ3；TOPE
					syougou.add("TOF");		//オリジナルファイル名											Original filename										ID3ｖ3；TOFN
					syougou.add("TOL");		//オリジナルの作詞家/文書作成者									Original Lyricist(s)/text writer(s)						ID3ｖ3；TOLY
					syougou.add("TOR");		//オリジナルのリリース年										Original release year									ID3ｖ3；TORY
					syougou.add("TOT");		//オリジナルのアルバム/映画/ショーのタイトル					Original album/Movie/Show title							ID3ｖ3；TOAL
					syougou.add("TP1");		//主な演奏者/ソリスト/トラック アーティスト				Lead artist(s)/Lead performer(s)/Soloist(s)/Performing group	ID3ｖ3；TPE1
					syougou.add("TP2");		//バンド/オーケストラ/伴奏	バンド/オーケストラ					Band/Orchestra/Accompaniment/Album Artist				ID3ｖ3；TPE2
					syougou.add("TP3");		//指揮者/演奏者詳細情報	指揮者									Conductor/Performer refinement							ID3ｖ3；TPE3
					syougou.add("TP4");		//翻訳者, リミックス, その他の修正								Interpreted, remixed, or otherwise modified by			ID3ｖ3；TPE4
					syougou.add("TPA");		//セット中の位置/ディスク #	Disc Number	Disc#					Part of a set											ID3ｖ3；TPOS
					syougou.add("TPB");		//出版社/発行元													Publisher												ID3ｖ3；TPUB
					syougou.add("TRC");		//国際標準レコーディングコード									ISRC (International Standard Recording Code)			ID3ｖ3；TSRC
					syougou.add("TRD");		//録音日付														Recording dates											ID3ｖ3；TRDA
					syougou.add("TRK");		//トラックの番号/セット中の位置									Track number/Position in set							ID3ｖ3；TRCK
					syougou.add("TSI");		//サイズ														Size(Size	 Deprecated?)								ID3ｖ3；TSIZ;
					syougou.add("TSS");		//エンコードに使用したソフトウエア/ハードウエアとセッティング	Software/hardware and settings used for encoding		ID3ｖ3；TSSE;
					syougou.add("TT1");		//内容の属するグループの説明									Content group description								ID3ｖ3；TIT1;
					syougou.add("TT2");		//タイトル/曲名/内容の説明	タイトル							Track Title	Title/Title/Songname/Content description	ID3ｖ3；TIT2;
					syougou.add("TT3");		//サブタイトル/説明の追加情報									Subtitle/Description refinement							ID3ｖ3；TIT3;
					syougou.add("TXT");		//作詞家/文書作成者												Lyricist/text writer									ID3ｖ3；TEXT
					syougou.add("TXX");		//ユーザー定義文字情報フレーム									User defined text information frame						ID3ｖ3；TXXX
					syougou.add("TYE");		//年	レコーディング年/年*15	Date*16	Year Deprecated			Year													ID3ｖ3；TYER
					syougou.add("UFI");		//一意的なファイル識別子/タグID?								Unique file identifier									ID3ｖ3；UFID
					syougou.add("ULT");		//非同期 歌詞/文書のコピー										Unsychronized lyric/text transcription					ID3ｖ3；USLT
					syougou.add("WAF");		//オーディオファイルの公式Webページ								Official audio file webpage								ID3ｖ3；WOAF
					syougou.add("WAR");		//アーティスト/演奏者の公式Webページ							Official artist/performer webpage						ID3ｖ3；WOAR
					syougou.add("WAS");		//音源の公式Webページ											Official audio source webpage							ID3ｖ3；WOAS
					syougou.add("WCM");		//商業上の情報													Commercial information									ID3ｖ3；WCOM
					syougou.add("WCP");		//著作権/法的情報												Copyright/Legal information								ID3ｖ3；WCOP
					syougou.add("WPB");		//出版社の公式Webページ											Publishers official webpage								ID3ｖ3；WPUB
					syougou.add("WXX");		//ユーザー定義URLリンクフレーム											User defined URL link frame						ID3ｖ3；WXXX
					break;
				default:
//				case 3:
//				case 4:
					syougou.add("TPE2");		//バンド/オーケストラ/伴奏	バンド/オーケストラ	Album Artist	Album Artist			//10cc(2)TPE2������������10cc
					syougou.add("TCOM");		//作曲者																				//10cc(2)
					syougou.add("USLT");		//USLT	<UNSYNCED LYRICS>	非同期 歌詞/文書のコピー									//10cc(3)
					syougou.add("TYER"); 		//年	レコーディング年/年*15	Date*16	Year Deprecated									//10cc(5)TYER������������1975
					syougou.add("TALB");		//アルバム/映画/ショーのタイトル														//10cc(6)
					syougou.add("TRCK");		//トラックの番号/セット中の位置	トラック #	Track Number	Track#						//10cc(7)TRCK������������2/8
					syougou.add("TIT2");		//タイトル/曲名/内容の説明	タイトル	Track Title	Title
					syougou.add("TPE1");		//主な演奏者/ソリスト	トラック アーティスト	Artist Name	Artist
					syougou.add("SYLT");		//同期 歌詞/文書
					syougou.add("TDAT"); 		//日付	Date*11	Date*12	Year*13 Deprecated
					syougou.add("TPE3");		//指揮者/演奏者詳細情報	指揮者	<CONDUCTOR>	Conductor
					syougou.add("TCON");		//ジャンル																				//10cc(8)TCON������������
					syougou.add("TCOP");		//著作権情報
					syougou.add("TENC");		//エンコーディング ソフトウェア	<ENCODED BY>	Encoder
					syougou.add("TEXT");		//作詞家/文書作成者	作詞者	<LYRICIST>	Lyricist
					syougou.add("TMOO");			//ムード	ムード	<MOOD>	Mood	ID3v2.4フレーム
					syougou.add("TPE4");		//翻訳者, リミックス, その他の修正	<ModifiedBy>	<REMIXED BY>	Mix Artist, Artists: Remixer
					syougou.add("TPUB");		//出版社	発行元	<PUBLISHER>	Publisher												//10cc(11)TPUB������������Universal Distribu
					syougou.add("TXXX");		//ユーザー定義文字情報フレーム
					syougou.add("UFID");		//一意的なファイル識別子	タグID
					syougou.add("APIC");		//付属する画像																			//10cc(5')APIC��(������JPG������ÿØÿà��
					syougou.add("AENC");		//Audio encryption
					syougou.add("COMR");		//Commercial frame]
					syougou.add("ENCR");		//Encryption method registration
					syougou.add("EQUA");		//Equalization
					syougou.add("ETCO");		// Event timing codes]
					syougou.add("GEOB");		//パッケージ化された一般的なオブジェクト	General encapsulated object]
					syougou.add("GRID");		//Group identification registration]
					syougou.add("IPLS");		//協力者一覧 Involved people list]
					syougou.add("LINK");		//Linked information]
					syougou.add("MCDI");		//Music CD identifier
					syougou.add("MLLT");		//MPEG location lookup table]
					syougou.add("OWNE");		//Ownership frame]
					syougou.add("PCNT");		//Play counter]
					syougou.add("POSS");		//Position synchronisation frame]
					syougou.add("RBUF");		//    [#sec4.19 Recommended buffer size]
					syougou.add("RVAD");		// Relative volume adjustment]
					syougou.add("SYTC");		//Synchronized tempo codes
					syougou.add("TBPM");		//BPM (beats per minute)
					syougou.add("TDLY");		//Playlist delay
					syougou.add("TFLT");		//File type
					syougou.add("TIME"); 		//Time Deprecated
					syougou.add("TIT1");		//Content group description
					syougou.add("TIT3");		// Subtitle/Description refinement
					syougou.add("TKEY");		//Initial key
					syougou.add("TLAN");		// Language(s)]
					syougou.add("TLEN");		// Length]
					syougou.add("TMED");		// Media type]
					syougou.add("TOAL");		//Original album/movie/show title
					syougou.add("TOFN");		//Original filename]
					syougou.add("TOLY");		//  Original lyricist(s)/text writer(s)]
					syougou.add("TOPE"); 		//Original artist(s)/performer(s)	 Deprecated
					syougou.add("TORY");		//Original release year]
					syougou.add("TOWN");		// File owner/licensee
					syougou.add("TRDA");		// Recording dates	Deprecated
					syougou.add("TRSN");		// Internet radio station name
					syougou.add("TRSO");		// Internet radio station owner
					syougou.add("TSIZ");		 //Size	 Deprecated
					syougou.add("TSRC");		//ISRC (international standard recording code)
					syougou.add("TSSE");		//Software/Hardware and settings used for encoding
					syougou.add("WCOM");		//Commercial information]
					syougou.add("WCOP");		// Copyright/Legal information
					syougou.add("WOAF");		// Official audio file webpage
					syougou.add("WOAR");		// Official artist/performer webpage
					syougou.add("WOAS");		// Official audio source webpage
					syougou.add("WORS");		//  Official internet radio station homepage
					syougou.add("WPAY");		//  Payment
					syougou.add("WPUB"); 		//Publishers official webpage
					syougou.add("USER");		//Terms of use
					syougou.add("WXXX");		//User defined URL link frame
					syougou.add("COMM");		//コメント																				//10cc(7')
					syougou.add("TPOS");		//セット中の位置	ディスク #	Disc Number	Disc#										//10cc(7')TPOS������������1/1
					syougou.add("POPM");		//人気メーター																				//10cc(2')Windows Media Player 9 Seri
					syougou.add("PRIV");		//プライベートフレーム									//☆複数出現する；10cc(4)(10)PRIV
					syougou.add("TOLY");		//Original lyricist(s)/text writer(s)]
					syougou.add("RVRB");		//Reverb
					syougou.add("TOWN");		//ファイルの所有者/ライセンシー
					syougou.add("TSOA");		//アルバム（読み）										ALBUMSORT						ID3ｖ2；--	ID3ｖ3；
					syougou.add("TSOP");		//アーティスト（読み）									ARTISTSORT						ID3ｖ2；--	ID3ｖ3；
					syougou.add("TSOC");		//作曲者（読み）										SortComposer					ID3ｖ2；?-	ID3ｖ3；TSOC	COMPOSERSORT
					break;
				}
			}

			if( kensaku == null ){
				kensaku = new ArrayList<String>();//検索するフレーム名
				switch (this.majorVersion) {
				case 2:
					kensaku.add("ULT");		//非同期 歌詞/文書のコピー										Unsychronized lyric/text transcription					ID3ｖ3；USLT
					kensaku.add("SLT");		//同期 歌詞/文書												Synchronized lyric/text									ID3ｖ3；SYLT
					kensaku.add("TP1");		//主な演奏者/ソリスト/トラック アーティスト				Lead artist(s)/Lead performer(s)/Soloist(s)/Performing group	ID3ｖ3；TPE1
					kensaku.add("TP2");		//バンド/オーケストラ/伴奏	バンド/オーケストラ					Band/Orchestra/Accompaniment/Album Artist				ID3ｖ3；TPE2
					kensaku.add("TAL");		//アルバム/映画/ショーのタイトル								Album/Movie/Show title									ID3ｖ3；TALB
					kensaku.add("TT2");		//タイトル/曲名/内容の説明	タイトル							Track Title	Title/Title/Songname/Content description	ID3ｖ3；TIT2;
					kensaku.add("TP3");		//指揮者/演奏者詳細情報	指揮者									Conductor/Performer refinement							ID3ｖ3；TPE3
					kensaku.add("TP4");		//翻訳者, リミックス, その他の修正								Interpreted, remixed, or otherwise modified by			ID3ｖ3；TPE4
					kensaku.add("TPA");		//セット中の位置/ディスク #	Disc Number	Disc#					Part of a set											ID3ｖ3；TPOS
					kensaku.add("MCI");		//音楽ＣＤ識別子												Music CD Identifier										ID3ｖ3；MCDI
					kensaku.add("TDA");		//日付	Date*11	Date*12	Year*13 Deprecated						Date													ID3ｖ3；TDAT
					kensaku.add("TRK");		//トラックの番号/セット中の位置									Track number/Position in set							ID3ｖ3；TRCK
					kensaku.add("PIC");		//付属する画像													Attached picture										ID3ｖ3；APIC
					kensaku.add("COM");		//コメント														Comments												ID3ｖ3；COMM
					break;
				default:
//				case 3:
//				case 4:
					kensaku.add("USLT");		//USLT	<UNSYNCED LYRICS>	非同期 歌詞/文書のコピー									//10cc(3)
					kensaku.add("SYLT");		//同期 歌詞/文書
					kensaku.add("APIC");		//付属する画像																			//10cc(5')APIC��(������JPG������ÿØÿà��
					kensaku.add("TPE1");		//主な演奏者/ソリスト	トラック アーティスト	Artist Name	Artist
					kensaku.add("TPE2");		//バンド/オーケストラ/伴奏	バンド/オーケストラ	Album Artist	Album Artist			//10cc(2)TPE2������������10cc
					kensaku.add("TALB");		//アルバム/映画/ショーのタイトル														//10cc(6)
					kensaku.add("TIT2");		//タイトル/曲名/内容の説明	タイトル	Track Title	Title
					kensaku.add("TPE3");		//指揮者/演奏者詳細情報	指揮者	<CONDUCTOR>	Conductor
					kensaku.add("TPE4");		//翻訳者, リミックス, その他の修正	<ModifiedBy>	<REMIXED BY>	Mix Artist, Artists: Remixer
					kensaku.add("TCOM");		//作曲者																				//10cc(2)
					kensaku.add("TYER"); 		//年	レコーディング年/年*15	Date*16	Year Deprecated									//10cc(5)TYER������������1975
					kensaku.add("TRCK");		//トラックの番号/セット中の位置	トラック #	Track Number	Track#						//10cc(7)TRCK������������2/8
					kensaku.add("TCON");		//ジャンル																				//10cc(8)TCON������������
					kensaku.add("TCOP");		//著作権情報
					kensaku.add("TENC");		//エンコーディング ソフトウェア	<ENCODED BY>	Encoder
					kensaku.add("TEXT");		//作詞家/文書作成者	作詞者	<LYRICIST>	Lyricist
					kensaku.add("TDAT"); 		//日付	Date*11	Date*12	Year*13 Deprecated
					kensaku.add("TMOO");			//ムード	ムード	<MOOD>	Mood	ID3v2.4フレーム
					kensaku.add("TPUB");		//出版社	発行元	<PUBLISHER>	Publisher												//10cc(11)TPUB������������Universal Distribu
					kensaku.add("TPOS");		//セット中の位置	ディスク #	Disc Number	Disc#										//10cc(7')TPOS������������1/1
					kensaku.add("POPM");		//人気メーター																				//10cc(2')Windows Media Player 9 Seri
					kensaku.add("PRIV");
					kensaku.add("COMM");		//☆""//プライベートフレーム	と	"");		//コメント	は複数出現する；10cc(4)(10)PRIV
					kensaku.add("TSOA");		//アルバム（読み）										ALBUMSORT						ID3ｖ2；--	ID3ｖ3；
					kensaku.add("TSOP");		//アーティスト（読み）									ARTISTSORT						ID3ｖ2；--	ID3ｖ3；
					kensaku.add("TSOC");		//作曲者（読み）										SortComposer					ID3ｖ2；?-	ID3ｖ3；TSOC	COMPOSERSORT
					break;
				}
			}
		}catch (Exception e) {
		myErrorLog(TAG,dbMsg + "で"+e.toString());
	}
}

	/**
	 *  未使用 渡された名称のファイルからStringを読み取る
	 * 文字コードはISO-8859-1
	 * @param filename フルパスファイル名
	 * @return result 読み出した文字
	 * */
	public String file2Str(String filename) throws IOException {			//渡された名称のファイルからStringを読み取る
		String result =null;
		final String TAG = "file2Str";
		String dbMsg= "";
		try{
			String mojiCord  = "ISO-8859-1";
			dbMsg +=",filename=" + filename + ",mojiCord=" + mojiCord;				//this.fileObj.getPath()
			FileInputStream fis = new FileInputStream(filename);
			dbMsg +=",FileInputStream=" + fis.toString();						//this.fileObj.getPath()
			InputStreamReader iS_reader = new InputStreamReader(fis,mojiCord);			//☆mojiCordはnull不可
			dbMsg +=",InputStreamReader=" + iS_reader.toString();				//this.fileObj.getPath()
			BufferedReader b_reader = new BufferedReader(iS_reader);					//ファイルを 1行ずつ読み込む
			dbMsg +=",BufferedReader=" + b_reader.toString();				//this.fileObj.getPath()
			StringBuffer strBuffer = new StringBuffer(2048);
//☆Android4.4から出力側が使えない
	//		OutputStreamWriter  OS_writer = new OutputStreamWriter(new FileOutputStream(this.fileObj),mojiCord);
//			PrintWriter p_writer    = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.fileObj),mojiCord)));
//			BufferedWriter b_writer    = new BufferedWriter(OS_writer);
			String line;
			System.getProperty("line.separator");
			while((line  = b_reader.readLine()) != null){			// && rCount < readEnd
				strBuffer.append(line);
			}
			result = strBuffer.toString();
//			p_writer.close();
	//		OS_writer.close();
			b_reader.close();
			fis.close();
			iS_reader.close();
			dbMsg= dbMsg+ result.length() + "文字;;" + result;
	//		myLog(TAG,dbMsg);
		}catch(FileNotFoundException e){
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}catch(IOException e){
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}
		return result;
	}

/**
 * ID3仕様書							http://www.takaaki.info/wp-content/uploads/2013/01/ID3v2.3.0J.html
 * 非公式標準							http://momdo.s35.xrea.com/web-html-test/mirror/ID3v2.3.0J.html
 *										 https://ja.wikipedia.org/wiki/ID3%E3%82%BF%E3%82%B0
 *　MP3ファイルのタグについて (+MP4)	http://eleken.y-lab.org/report/other/mp3tags.shtml
 *		http://id3.org/id3v2-00
 *	MP3TAG	http://www.mp3tag.de/en/index.html
 **/
	/**
	 * RandomAccessFileをString変換
	 * ID3などのタグヘッダー情報からタグ部分だけを文字列にして返す。
	 * @return
	 * @throws IOException , FileNotFoundException
	 */
	public String raf2Str(final File file, final boolean writeable) throws  IOException {		//RandomAccessFileをString変換
		String result =null;
		final String TAG = "raf2Str";
		String dbMsg= "";
		try{
			RandomAccessFile newFile = null;			//任意の位置からデータの読み込み処理、書き込み処理を行うことができます。これは余計な読み込み処理、書き込み処理を行う必要がないため、効率的な処理
			if( 18< Integer.valueOf(String.valueOf(Build.VERSION.SDK))){	//Android4.4からはファイルの書き込み制限で
				newFile = new RandomAccessFile(file, "r");					//java.io.FileNotFoundException: : open failed: EACCES (Permission denied)
			}else{
				newFile = new RandomAccessFile(file, writeable ? "rw" : "r");
			}
			dbMsg +=",newFile.length=" + newFile.length() + "バイト";					//newFile.length=11897286
			int size = 0;
			byte[] buffer = new byte[10];									//タグヘッダ読込開始
			dbMsg +=",file=" + file;
			newFile.seek(0);
			newFile.read(buffer, 0, 10);							// read the tag if it exists
			//	newFile.seek(0);
			//	newFile.read(buffer, 0, 3);					// newFile.read(buffer, 0, 3);
				dbMsg += ",buffer[0]=" + buffer[0];		//.mp3は=3,	.wmaは117	,宇多田ヒカルは2
				dbMsg += ",[1]=" + buffer[1];		//.mp3は=0,	.wmaは-114	,宇多田ヒカルは0
				dbMsg += ",[2]=" + buffer[2];		//.mp3は=0,	.wmaは102	,宇多田ヒカルは0
				dbMsg += ",[3]=" + buffer[3];		//ID3v2 バージョン			はじめの１バイトは、メジャーバージョンを示し、	$03 00			Hex	2バイト
				dbMsg += ",[4]=" + buffer[4];		//							２バイト目は改訂番号
				dbMsg += ",[5]=" + buffer[5];		//ID3v2 フラグ				%abc00000		１バイトのID3v2フラグ	a - 非同期化 / b - 拡張ヘッダ / c - 実験中
				dbMsg += ",[6]=" + buffer[6];		//ID3v2 サイズ				4 * %0xxxxxxx
				dbMsg += ",[7]=" + buffer[7];		//
				dbMsg += ",[8]=" + buffer[8];		//
				dbMsg += ",[9]=" + buffer[9];		//
			final String tag = new String(buffer, 0, 9);		//参照ID3v2_3.seek
			dbMsg +=",tag=" + tag;
			if (tag.startsWith("ID3")) {
				result_Tag ="ID3v2";			//	タグ名
				this.majorVersion = buffer[3];	//AbstractID3v2.setMajorVersion(buffer[0]);
				this.revision = buffer[4];		//AbstractID3v2.setRevision(buffer[1]);
				result_Tag =result_Tag + "." + this.majorVersion + "." + this.revision ;			//	タグ名
				dbMsg +=",result_Tag=" + result_Tag;				//+ ",info_TagT=" + info_TagT;
				switch (this.majorVersion) {
				case 2:
					this.unsynchronization = (buffer[5] & MASK_V22_UNSYNCHRONIZATION) != 0;
					this.compression = (buffer[5] & MASK_V22_COMPRESSION) != 0;
					dbMsg += ",compression(ID3v2.2)=" + this.compression;													//.mp3はfalse.wmaはtrue,
					break;
				case 3:
					this.unsynchronization = (buffer[5] & MASK_V23_UNSYNCHRONIZATION) != 0;	//128 Header bit mask						 (buffer[2] & TagConstant.MASK_V23_UNSYNCHRONIZATION) != 0;
					this.extended = (buffer[5] & MASK_V23_EXTENDED_HEADER) != 0;				//64 Header bit mask						 (buffer[2] & TagConstant.MASK_V23_EXTENDED_HEADER) != 0;
					this.experimental = (buffer[5] & MASK_V23_EXPERIMENTAL) != 0;				//32 Header bit mask						(buffer[2] & TagConstant.MASK_V23_EXPERIMENTAL) != 0;
					dbMsg += ",extended(ID3v2.3)=" + this.extended;													//.mp3はfalse.wmaはtrue,
					dbMsg += ",experimental(ID3v2.3)=" + this.experimental;											//.mp3はfalse.wmaは,true,
					break;
				case 4:
					this.unsynchronization = (buffer[5] & MASK_V24_UNSYNCHRONIZATION) != 0;
					this.extended = (buffer[5] & MASK_V24_EXTENDED_HEADER) != 0;
					this.experimental = (buffer[5] & MASK_V24_EXPERIMENTAL) != 0;
					dbMsg += ",extended(ID3v2.4)=" + this.extended;													//.mp3はfalse.wmaはtrue,
					dbMsg += ",experimental(ID3v2.4)=" + this.experimental;											//.mp3はfalse.wmaは,true,
					this.footer = (buffer[5] & MASK_V24_FOOTER_PRESENT) != 0;
					dbMsg += ",footer(ID3v2.4)=" + this.footer;											//.mp3はfalse.wmaは,true,
					break;
				}
				dbMsg += ",unsynchronization(共通)=" + this.unsynchronization;								//.mp3はfalse.wmaもfalse
				size = (buffer[6] << 21) + (buffer[7] << 14) + (buffer[8] << 7) + buffer[9];		//AbstractID3v2.byteArrayToSize(buffer); (buffer[0] << 21) + (buffer[1] << 14) + (buffer[2] << 7) + buffer[3];
				dbMsg += ",size=" + size;																//.mp3はsize=82134	,宇多田ヒカルは7175
				final long filePointer = newFile.getFilePointer();			// java.io
				dbMsg += ",filePointer=" + filePointer;								//mp3はfilePointer=10.wmaも10	,宇多田ヒカルも10
				if (this.extended) {
					final int extendedHeaderSize = newFile.readInt();							// int is 4 bytes.
					dbMsg += ",extendedHeaderSize=" + extendedHeaderSize;				//.wmaはextendedHeaderSize=11141218でcom.hijiyam_koubou.tagbrows.InvalidTagException: Invalid Extended Header Size.
					if (extendedHeaderSize != 6 && extendedHeaderSize != 10) {				// the extended header is only 6 or 10 bytes.
			//			throw new InvalidTagException("Invalid Extended Header Size.");
					}
					newFile.read(buffer, 0, 2);
					dbMsg += ",extended=true;buffer[0]=" + buffer[0];
					dbMsg += ",[1]=" + buffer[1];
					dbMsg += ",[2]=" + buffer[2];
					switch (this.majorVersion) {
//					case 2:
//						break;
					case 3:
						this.crcDataFlag = (buffer[0] & MASK_V23_CRC_DATA_PRESENT) != 0;					//ID3v2.3 Frame bit mask
						dbMsg += ",crcDataFlag=" + this.crcDataFlag;
						if (((extendedHeaderSize == 10) && (this.crcDataFlag == false)) ||
								((extendedHeaderSize == 6) && (this.crcDataFlag == true))) {				// if it's 10 bytes, the CRC flag must be set and if it's 6 bytes, it must not be set
				//			throw new InvalidTagException("CRC Data flag not set correctly.");
						}
						this.paddingSize = newFile.readInt();
						if ((extendedHeaderSize == 10) && this.crcDataFlag) {
							this.crcData = newFile.readInt();
							dbMsg += ",crcData=" + this.crcData;
						}
						break;
					case 4:
						dbMsg += ",以下、ID3v2_4.read()から" ;
						if (extendedHeaderSize <= 6) {						// the extended header must be atleast 6 bytes
							throw new InvalidTagException("Invalid Extended Header Size.");
						} else {
							final byte numberOfFlagBytes = newFile.readByte();		//オリジナルはfileにキャスト無し
							newFile.read(buffer, 0, numberOfFlagBytes);							// read the flag bytes
							this.updateTag = (buffer[0] & MASK_V24_TAG_UPDATE) != 0;
							this.crcDataFlag = (buffer[0] & MASK_V24_CRC_DATA_PRESENT) != 0;
							this.tagRestriction = (buffer[0] & MASK_V24_TAG_RESTRICTIONS) != 0;
							if (this.updateTag) {							// read the length byte if the flag is set this tag should always be zero but just in case read this information.
								final int len = ((DataInput) file).readByte();							// read the length byte if the flag is set this tag should always be zero but just in case read this information.
								buffer = new byte[len];
								newFile.read(buffer, 0, len);
							}if (this.crcDataFlag) {
								final int len = newFile.readByte();								// the CRC has a variable length
								buffer = new byte[len];
								newFile.read(buffer, 0, len);
								this.crcData = 0;
								for (int i = 0; i < len; i++) {
									this.crcData <<= 8;
									this.crcData += buffer[i];
								}
							}
							if (this.tagRestriction) {
								final int len = newFile.readByte();
								buffer = new byte[len];
								newFile.read(buffer, 0, len);
								this.tagSizeRestriction = (byte) ((buffer[0] & MASK_V24_TAG_SIZE_RESTRICTIONS) >> 6);
								this.textEncodingRestriction = (byte) ((buffer[0] & MASK_V24_TEXT_ENCODING_RESTRICTIONS) >> 5);
								this.textFieldSizeRestriction = (byte) ((buffer[0] & MASK_V24_TEXT_FIELD_SIZE_RESTRICTIONS) >> 3);
								this.imageEncodingRestriction = (byte) ((buffer[0] & MASK_V24_IMAGE_ENCODING) >> 2);
								this.imageSizeRestriction = (byte) (buffer[0] & MASK_V24_IMAGE_SIZE_RESTRICTIONS);
							}
						}
					}
				}
				/////////////////////////////////////////////////////////////////////////////////////////ID3v2_3.read////
				buffer = new byte[ (int)( newFile.length() ) ];    // 読み込む量		head_pos -RT -readPos
				int readEnd = newFile.read(buffer);
				dbMsg +="," + readEnd + "文字";
				if ( -1 != readEnd ){
					result = new String( buffer, StandardCharsets.ISO_8859_1);						////×UTF-8	Shift_JIS	SJIS	EUC_JP	ISO-8859-1
					dbMsg +=">>" + result.length() + "文字";
					switch (this.majorVersion) {
					case 2:
		//				freamReadID3v2(newFile);		//RandomAccessFileを'0'で区切ってフレームを読み込む
				//		dbMsg +=">>" + result;
						break;
					case 3:
					case 4:
						break;
					}
					result = result.substring(0, size);
					dbMsg +=">ID3v2のサイズフラグでカット>" + result.length() + "文字";
					newFile.close();
					buffer = null;
					file2Tag2(result);			//文字列からフィールドを抽出				break;
				}
			}else{
				if (tag.startsWith("TAG")) {
					result_Tag ="ID3v1";
					freamReadID3v1(file);		//ID3v1の処理
				}else if (tag.startsWith("LYRICSBEGIN")) {
					result_Tag ="Lyrics3";
					headReadLyric3(file, writeable);						//Lylic3の処理開始
				}else if (tag.startsWith("LYRICSEND")) {
					result_Tag ="Lyrics3";
					headReadLyric3(file, writeable);						//Lylic3の処理開始
				}else{										//if (tag.startsWith("ID3"))
					buffer = new byte[100];									//タグヘッダ読込開始
					newFile.seek(0);
					newFile.read(buffer, 0, 99);							// read the tag if it exists
					final String tag2 = new String(buffer, 0, 99) + "......";		//参照ID3v2_3.seek
					dbMsg +=",tag2=" + tag2;
					String tag3 = null;
					if(tag2.contains("ftyp")){
						tag3 = tag2 + "AAC ?";
				//		result_Tag =String.valueOf(tag3);
						newFile.close();
						buffer = null;
						headReadAac(file);						//AACの処理開始
					}else {					// tag2.equals("null")	if( tag2 == null )
						tag3 =  "WMA ?";
						result_Tag = tag3;			//	タグ名
						headReadWma(file);						//WMAの処理開始
					}
				}
			}
			newFile.close();
			buffer = null;
	//		myLog(TAG,dbMsg);
		}catch(FileNotFoundException e){
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}catch(IOException e){
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}
		return result;
	}


	/**
	 * Lylic3の処理開始
	 * */
	public void headReadLyric3(final File file, final boolean writeable) throws  IOException {		//Lylic3の処理開始
		String result =null;
		final String TAG = "headReadLylic3";
		String dbMsg= "";
		try{
			back2Activty(  );			//呼び出しの戻り処理
			myLog(TAG,dbMsg);
//		}catch(FileNotFoundException e){
//			myErrorLog(TAG,dbMsg + "で"+e.toString());
//		}catch(IOException e){
//			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}
	}


///AAC/////////////////////////////////////////////////////////////////////////////////////////////////////////
/**
 *aac対応
 *	Jaudiotagger	http://www.jthink.net/jaudiotagger/
 *						http://www.jthink.net/jaudiotagger/tagmapping.html
 *					http://www.mp4ra.org/atoms.html
 *					http://www.sno.phy.queensu.ca/~phil/exiftool/TagNames/QuickTime.html
 *"©lyr"	Lyrics
 * */
	public List<String> qpPre;							//最小限読み込み
	public List<String> qpHead;								//QuickTime Tags
	public List<String> qtMovie;					//QuickTime Tags.QuickTime Movie Tagsの読取り
	public List<String> qtMovieMeta;				//QuickTime Tags.QuickTime Meta Tagsの読取り
	public List<String> qtItemListCoar;			//QuickTime ItemList Tagsで確実に書き込まれている部分
	public List<String> qtItemList;				//QuickTime ItemList Tags
	public List<String> qtSubBox;					//2階層より下位に出現するもの
	public String resultStock = null;
	/**
	 * AACの処理開始
	 * */
	public void headReadAac(final File file) throws  IOException {		//AACの処理開始			final RandomAccessFile newFile
		result =null;
		final String TAG = "headReadAac";
		String dbMsg= "";
		try{
			dbMsg= filePath;					//newFile.length=11897286
			RandomAccessFile newFile = null;			//任意の位置からデータの読み込み処理、書き込み処理を行うことができます。これは余計な読み込み処理、書き込み処理を行う必要がないため、効率的な処理
			if( 18< Integer.valueOf(String.valueOf(Build.VERSION.SDK))){	//Android4.4からはファイルの書き込み制限で
				newFile = new RandomAccessFile(file, "r");					//java.io.FileNotFoundException: : open failed: EACCES (Permission denied)
			}else{
				newFile = new RandomAccessFile(file,  "rw");					//(file, writeable ? "rw" : "r");
			}
			long fileLen = newFile.length();
			dbMsg +=",newFile.length=" + fileLen + "バイト";					//newFile.length=11897286
			int size = 0;
			byte[] buffer = new byte[(int) fileLen];									//タグヘッダ読込開始
			int readEnd = newFile.read(buffer);
			result = new String( buffer, StandardCharsets.ISO_8859_1);						//ISO-8859-1	×ASCII169,ASCII-169,ASCII 169	?//@lir×US_ASCII,UTF-8、S-JIS,Shift-JIS	//文字化け×UTF-16,UTF-16BE,	//unspport×S/JIS,UTF-8 sort,UTF-8-sort,UTF-16 sort
			newFile.close();
			int readInt = result.length();
			if(20 < readInt){
				dbMsg +=result.substring(0, 20) +  "～" + result.substring(readInt-20, readInt);
			}else{
				dbMsg +=result;
			}
			initAccResult();								//ACC戻り値の初期化
			dbMsg += "(" + readInt + "/" + result.length() +"文字)";		//"M4A mp42isom".length()
			dbMsg +="," + readEnd + "バイト";
			String target = "ftyp";
			target = fremeMeiSyougouBody( result , target);	//渡された文字列を先頭からindexOfで照合し、該当すればその文字を返し、無ければnullを返す
			int startInt = fleamStart + target.length();						//開始値設定		target.getBytes("ISO-8859-1").length
			dbMsg +="," + target + "は"+ startInt + "～";
			int rEnd = result.length();
			buffer = result.substring(startInt-7, startInt).getBytes();									//タグヘッダ読込開始			"ISO-8859-1"
			dbMsg += ",buffer[0]=" + buffer[0];//Atom Size
			dbMsg += ",[1]=" + buffer[1];		//
			dbMsg += ",[2]=" + buffer[2];		//
			dbMsg += ",[3]=" + buffer[3];		//
			rEnd = (buffer[0] << 21) + (buffer[1] << 14) + (buffer[2] << 7) + buffer[3];		//AbstractID3v2.byteArrayToSize(buffer); (buffer[0] << 21) + (buffer[1] << 14) + (buffer[2] << 7) + buffer[3];
			dbMsg +="," + rEnd + "は"+ rEnd ;
			String nexttarget = fremeMeiSyougouBody( result , "moov");	//渡された文字列を先頭からindexOfで照合し、該当すればその文字を返し、無ければnullを返す
			dbMsg +=",次は" + fleamStart + "～"+ nexttarget ;
			String readStr = result.substring(0,fleamStart-4);
			dbMsg +="," + rEnd + "="+ readStr ;
			headReadAacBody(readStr ,  "ftyp");		//AACのQuickTime Tags読取り
			result = result.substring(readStr.length());
			resultStock = result;
			buffer = null;
			if ( -1 != readEnd ){
				readInt = result.length();
				if(20 < readInt){
					dbMsg +=result.substring(0, 20) +  "～" + result.substring(readInt-20, readInt);
				}else{
					dbMsg +=result;
				}
				dbMsg += "(" + readInt + "/" + result.length() +"文字)";		//"M4A mp42isom".length()
				lyricReadAac();		//@lirの優先読込み
			}
			myLog(TAG,dbMsg);
		}catch(IOException e){
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}
	}

	/**
	 * ACC戻り値の初期化
	 * */
	public void initAccResult() {								//ACC戻り値の初期化
		String result =null;
		final String TAG = "initAccResult";
		String dbMsg= "";
		try{
			//
			qpPre= new ArrayList<String>();							//最小限読み込み
		//	qpPre.add("loca");	//Typeの後ろ4ビット	A metadata value may optionally be tagged with its locale so that it may be chosen based upon the user's language, 		ID3ｖ2；--	ID3ｖ3；--
//			qpPre.add("elng");			//Extended Language Tag Atom
//			qpPre.add("tagc");			//Media characteristic optionally present in Track user data—specialized text that describes something of interest about the track. For more information, see Media Characteristic Tags.
//			qpPre.add("cdsc");			//Track reference types		TThe track reference is contained in a timed metadata track (see Timed Metadata Media for more detail) and provides links to the tracks for which it contains descriptive characteristics.
			qpPre.add("mdhd");			//Media Header Atoms 	28バイト目にLanguage
			qpPre.add("hdlr");			//Handler Reference Atoms　 	Component type/Component subtype
			qpHead= new ArrayList<String>();							//QuickTime Tags
//			qpHead.add("ftyp");			//1詳細な互換性情報。ファイルの先頭に置く。						File Type			ID3ｖ2；TFT	ID3ｖ3；TFLT	File type
			qpHead.add("moov");			//2ヘッダ情報。複数のBOXの集合。								Movie				ID3ｖ2；--	ID3ｖ3；--
			qpHead.add("mdat");			//3ビデオやオーディオデータ自体を格納。複数に分割することもある。	Movie Data		ID3ｖ2；--	ID3ｖ3；--		>ItemList>©lyr
			qpHead.add("frea");			//4写真関連情報										Kodak_frea						ID3ｖ2；--	ID3ｖ3；--
			qpHead.add("skip");			//5							Canon Skip Tags			CanonSkip						ID3ｖ2；--	ID3ｖ3；--
			qpHead.add("wide");			//6													Wide?							ID3ｖ2；--	ID3ｖ3；USER	Terms of use
			qpHead.add("pnot");			//7付属する画像			QuickTime Preview Tags		Preview							ID3ｖ2；PIC	ID3ｖ3；APIC	Attached picture
			//有無不明
			qpHead.add("mdat-offset");	//8													Movie Data Offset				ID3ｖ2；--	ID3ｖ3；--
			qpHead.add("mdat-size");	//9サイズ											MovieDataSize					ID3ｖ2；TSI	ID3ｖ3；TSIZ;	Size(Size	 Deprecated?)
			qpHead.add("PICT");			//10付属する画像									Preview PICT					ID3ｖ2；PIC	ID3ｖ3；APIC	Attached picture
			qpHead.add("pict");			//11付属する画像									PreviewPICT						ID3ｖ2；PIC	ID3ｖ3；APIC	Attached picture
			qpHead.add("thum");			//12画像のサムネール								ThumbnailImage					ID3ｖ2；--	ID3ｖ3；--
			qpHead.add("_htc");			//13												HTCInfo							ID3ｖ2；--	ID3ｖ3；--
			qpHead.add("slmt");			//14												Unknown_slmt?					ID3ｖ2；--	ID3ｖ3；--
			qpHead.add("ardt");			//15												ARDrone File					ID3ｖ2；--	ID3ｖ3；--
			qpHead.add("junk");			//16												Junk?							ID3ｖ2；--	ID3ｖ3；--
			qpHead.add("prrt");			//17												ARDrone Telemetry				ID3ｖ2；--	ID3ｖ3；--

			qtMovie= new ArrayList<String>();					//QuickTime Tags.QuickTime Movie Tagsの読取り
			qtMovie.add("meta");	//	Index4	5	MediaLanguageCode														ID3ｖ2；CRM	ID3ｖ3；？	Encrypted meta frame?
			qtMovie.add("cmov");	//							QuickTime Tags.QuickTime Movie Tags.Compressed movie		ID3ｖ2；--	ID3ｖ3；--
			qtMovie.add("htka");	//										QuickTime Tags.QuickTime Movie Tags.htka		ID3ｖ2；--	ID3ｖ3；--
			qtMovie.add("iods");	//										QuickTime Tags.QuickTime Movie Tags.iods		ID3ｖ2；--	ID3ｖ3；--
			qtMovie.add("mvhd");	//作成日時などビデオ、オーディオには関係ない全体的な情報。		MovieHeader				ID3ｖ2；--	ID3ｖ3；
			qtMovie.add("trak");	//ビデオ、オーディオそれぞれのトラックのヘッダ情報。									ID3ｖ2；--	ID3ｖ3；
			qtMovie.add("trak");	//	trak> tagc	Media Characteristic Tags												ID3ｖ2；--	ID3ｖ3；
			qtMovie.add("udta");	//						User Data		QuickTime Tags.QuickTime Movie Tags.udta		ID3ｖ2；--	ID3ｖ3；USER	Terms of use
			qtMovie.add("uuid");	//エンコーダ固有の拡張情報。デコーダは無視してよい。									ID3ｖ2；--	ID3ｖ3；--

			qtMovieMeta= new ArrayList<String>();				//QuickTime Tags.QuickTime Meta Tagsの読取り
			qtMovieMeta.add("ilst");	//	ItemList > "©lyr					QuickTime Tags.QuickTime Meta Tags.ilst			ID3ｖ2；--	ID3ｖ3；--
			qtMovieMeta.add("bxml");	//						BinaryXML?		QuickTime Tags.QuickTime Meta Tags.bxml			ID3ｖ2；--	ID3ｖ3；--
			qtMovieMeta.add("dinf");	//					DataInformation?	QuickTime Tags.QuickTime Meta Tags.dinf			ID3ｖ2；--	ID3ｖ3；--
			qtMovieMeta.add("free");	//	Kodak Free?	/Free?					QuickTime Tags.QuickTime Meta Tags.free			ID3ｖ2；--	ID3ｖ3；--
			qtMovieMeta.add("iinf");	//					ItemInformation?	QuickTime Tags.QuickTime Meta Tags.iinf			ID3ｖ2；--	ID3ｖ3；--
			qtMovieMeta.add("loca");	//A metadata value may optionally be tagged with its locale so that it may be chosen based upon the user's language, 		ID3ｖ2；--	ID3ｖ3；--
			qtMovieMeta.add("iloc");	//					ItemLocation?q		QuickTime Tags.QuickTime Meta Tags.iloc			ID3ｖ2；--	ID3ｖ3；--
			qtMovieMeta.add("ipmc");	//					IPMPControl?		QuickTime Tags.QuickTime Meta Tags.ipmc			ID3ｖ2；--	ID3ｖ3；--
			qtMovieMeta.add("ipro");	//					ItemProtection?		QuickTime Tags.QuickTime Meta Tags.ipro			ID3ｖ2；--	ID3ｖ3；--
			qtMovieMeta.add("keys");	//					Keys				QuickTime Tags.QuickTime Meta Tags.keys			ID3ｖ2；--	ID3ｖ3；--
			qtMovieMeta.add("pitm");	//				PrimaryItemReference?	QuickTime Tags.QuickTime Meta Tags.pitm			ID3ｖ2；--	ID3ｖ3；--
			qtMovieMeta.add("xml ");	//					XML					QuickTime Tags.QuickTime Meta Tags.xml 			ID3ｖ2；--	ID3ｖ3；--

			qtSubBox= new ArrayList<String>();		//2階層より下位に出現するもの
			qtSubBox.add("tkhd");	//trak>トラックの基本属性。再生時間、表示解像度など。									ID3ｖ2；--	ID3ｖ3；--
			qtSubBox.add("edts");	//trak>トラック上のデータと再生の情報。													ID3ｖ2；--	ID3ｖ3；--
			qtSubBox.add("elst");	//trak>edts>データ上の再生範囲と速度のリスト。											ID3ｖ2；--	ID3ｖ3；--
			qtSubBox.add("mdia");	//trak>トラックのデータに関するさまざまな情報。											ID3ｖ2；--	ID3ｖ3；--
			qtSubBox.add("mdhd");	//trak>mdia>トラックの基本属性。該当トラックの再生時間など。							ID3ｖ2；--	ID3ｖ3；--
			qtSubBox.add("hdlr");	//trak>mdia>トラックの種別。該当トラックがビデオかオーディオかを示す。				ID3ｖ2；--	ID3ｖ3；--
			qtSubBox.add("minf");	//?>?>トラックデータの固有情報。複数のBOXの集合。										ID3ｖ2；--	ID3ｖ3；--
			qtSubBox.add("vmhd");	//?>?>ビデオトラックデータ固有の情報。													ID3ｖ2；--	ID3ｖ3；--
			qtSubBox.add("smhd");	//?>?>オーディオトラックデータ固有の情報。												ID3ｖ2；--	ID3ｖ3；--
			qtSubBox.add("dinf");	//?>?>トラックデータの存在場所の情報。													ID3ｖ2；--	ID3ｖ3；--
			qtSubBox.add("dref");	//?>?>トラックデータの存在場所を示す。別ファイルに存在することもある。					ID3ｖ2；--	ID3ｖ3；--
			qtSubBox.add("stbl");	//trak>mdia>mdhd>トラックデータの単位（ビデオの場合フレーム）ごとの位置情報。			ID3ｖ2；--	ID3ｖ3；--
			qtSubBox.add("stsd");	//trak>mdia>mdhd>トラックデータ再生のためのヘッダ情報。									ID3ｖ2；--	ID3ｖ3；--
			qtSubBox.add("stts");	//trak>mdia>mdhd>トラックデータの単位ごとの再生時間の表。								ID3ｖ2；--	ID3ｖ3；--
			qtSubBox.add("stsc");	//trak>mdia>mdhd>mdat上のトラックデータの固まりごとの長さ（ビデオはフレーム数）の表。	ID3ｖ2；--	ID3ｖ3；--
			qtSubBox.add("stco");	//trak>mdia>mdhd>ファイル上のトラックデータの固まりの先頭位置。stscと連携。				ID3ｖ2；--	ID3ｖ3；--
			qtSubBox.add("stsz");	//trak>mdia>mdhd>トラックデータ再生単位ごとのデータ長の表。								ID3ｖ2；--	ID3ｖ3；--
			qtSubBox.add("stss");	//trak>mdia>mdhd>トラックデータのランダムアクセス可能な位置（フレーム番号）の表			ID3ｖ2；--	ID3ｖ3；--

			qtItemListCoar= new ArrayList<String>();//QuickTime ItemList Tagsで確実に書き込まれている部分
			qtItemListCoar.add("covr");	//付属する画像											CoverArt						ID3ｖ2；PIC	ID3ｖ3；APIC	Attached picture
			qtItemListCoar.add("©gen");	//ユーザ定義ジャンル									GENRE							ID3ｖ2；TCO	ID3ｖ3；TCON	GENRE
			qtItemListCoar.add("©grp");	//グループ												CONTENTGROUP					ID3ｖ2；?-	ID3ｖ3；TIT1
			qtItemListCoar.add("©ART");	//アーティスト																			ID3ｖ2；TP1	ID3ｖ3；TPE1	ARTIST[1]
			qtItemListCoar.add("©alb");	//albm?アルバム/映画/ショーのタイトル					Album							ID3ｖ2；TAL	ID3ｖ3；TALB	Album/Movie/Show title
			qtItemListCoar.add("©nam");	//アルバム（読み）										Track Title						ID3ｖ2；--	ID3ｖ3；TSOA	ALBUMSORT[1][2]
			qtItemListCoar.add("©com");	//作曲者												Composer						ID3ｖ2；TCM	ID3ｖ3；TCOM	Composer?
			qtItemListCoar.add("©cpy");	//権利元												Copyright	Copyright			ID3ｖ2；TCR	ID3ｖ3；TCOP	Copyright message
			qtItemListCoar.add("©day");	//リリース日（年）										YEAR							ID3ｖ2；?	ID3ｖ3；TYER	YEAR
			qtItemListCoar.add("©trk");	//trkn?トラックの番号/セット中の位置					Track							ID3ｖ2；TRK	ID3ｖ3；TRCK	TrackNumber	Track number/Position in set
			qtItemListCoar.add("©wrt");	//作曲者												COMPOSER						ID3ｖ2；TCM	ID3ｖ3；TCOM	Composer
			qtItemListCoar.add("aART");	//アルバムアーティスト									ALBUMARTIST						ID3ｖ2；--	ID3ｖ3；TPE2	Lead artist(s)/Lead performer(s)/Soloist(s)/Performing group
			qtItemListCoar.add("@pti");	//原題													Parent Title					ID3ｖ2；--	ID3ｖ3；TOAL	//Original album/movie/show title?
			qtItemListCoar.add("©too");	//©enc?エンコーディング ソフトウェア					Encoder							ID3ｖ2；TEN	ID3ｖ3；TENC	Encoded by
			qtItemListCoar.add("©lyr");	//非同期 歌詞/文書のコピー								Lyrics							ID3ｖ2；ULT	ID3ｖ3；USLT	Unsychronized lyric/text transcription
			//						//同期 歌詞/文書																		ID3ｖ2；SLT	ID3ｖ3；SYLT	Synchronized lyric/text

			qtItemList= new ArrayList<String>();				//QuickTime ItemList Tags
			qtItemList.addAll(qtItemListCoar);
			qtItemList.add("©pub");	//出版社/発行元											Publisher						ID3ｖ2；TPB	ID3ｖ3；TPUB	Publisher
			qtItemList.add("soar");	//アーティスト（読み）									ARTISTSORT						ID3ｖ2；--	ID3ｖ3；TSOP
			qtItemList.add("©cmt");	//コメント												Comment							ID3ｖ2；COM	ID3ｖ3；COMM	Comments
			qtItemList.add("©des");	//説明													Description	Track				ID3ｖ2；--	ID3ｖ3；--		SUBTITLE
			qtItemList.add("©enc");	//エンコーディング ソフトウェア							EncodedBy						ID3ｖ2；TEN	ID3ｖ3；TENC	Encoded by
			qtItemList.add("@sti");	//														Short Title						ID3ｖ2；--	ID3ｖ3；--
			qtItemList.add("albm");	//アルバム/映画/ショーのタイトル						Album/Movie/Show title			ID3ｖ2；TAL	ID3ｖ3；TALB
			qtItemList.add("desc");	//タイトル/曲名/内容の説明	タイトル					Description						ID3ｖ2；TT2	ID3ｖ3；TIT2;	Track Title	Title/Title/Songname/Content description
			qtItemList.add("yrrc");	//年	レコーディング年/年*15	Date*16	Year Deprecated	Year		Year				ID3ｖ2；TYE	ID3ｖ3；TYER
			qtItemList.add("akID");	//アカウントの種類			編集不可					Apple Store Account Type		ID3ｖ2；--	ID3ｖ3；--
			qtItemList.add("apID");	//アカウント情報					ITUNESACCOUNT		Apple Store Account				ID3ｖ2；--	ID3ｖ3；--
			qtItemList.add("atID");	//アーティスト識別子	編集不可						Album Title ID					ID3ｖ2；MCI?ID3ｖ3；MCDI?	Music CD Identifier?
			qtItemList.add("auth");	//														Author							ID3ｖ2；--	ID3ｖ3；--
			qtItemList.add("catg");	//ポッドキャストカテゴリ								Category						ID3ｖ2；--	ID3ｖ3；--		PODCASTCATEGORY
			qtItemList.add("cnID");	//コンテンツ識別子										AppleStoreCatalogID				ID3ｖ2；--	ID3ｖ3；--		ITUNESCATALOGID
			qtItemList.add("cprt");	//著作権												Copyright						ID3ｖ2；TCR	ID3ｖ3；TCOP	Copyright message
			qtItemList.add("dscp");	//タイトル/曲名/内容の説明	タイトル					Description						ID3ｖ2；TT2	ID3ｖ3；TIT2;	Track Title	Title/Title/Songname/Content description
			qtItemList.add("egid");	//ポッドキャストエピソードユニークID					Episode Global Unique ID		ID3ｖ2；--	ID3ｖ3；--		PODCASTID
			qtItemList.add("geID");	//ジャンル識別子		編集不可						GenreID							ID3ｖ2；--	ID3ｖ3；--
			qtItemList.add("gnre");	//ジャンル												Genre	Content type			ID3ｖ2；TCO	ID3ｖ3；TCON
			qtItemList.add("soaa");	//バンド/オーケストラ/伴奏	バンド/オーケストラ	?		SortAlbumArtistt				ID3ｖ2；TP2	ID3ｖ3；TPE2	Band/Orchestra/Accompaniment/Album Artis?
			qtItemList.add("soal");	//オリジナルのアルバム/映画/ショーのタイトル?			Sort Album						ID3ｖ2；TOT	ID3ｖ3；TOAL	Original album/Movie/Show title
			qtItemList.add("soco");	//作曲者（読み）										SortComposer					ID3ｖ2；?-	ID3ｖ3；TSOC	COMPOSERSORT
			qtItemList.add("sonm");	//タイトル（読み）										Sort Name						ID3ｖ2；--	ID3ｖ3；TIT3		TITLESORT
			qtItemList.add("cpil");	//コンピレーションの明示								Compilation						ID3ｖ2；--	ID3ｖ3；--		COMPILATION
			qtItemList.add("trkn");	//トラックナンバー										TrackNumber						ID3ｖ2；TRK	ID3ｖ3；TRCK	TTRACK/TOTALTARCK
			qtItemList.add("disk");	//ディスクナンバー										DiskNumber	Part of a set		ID3ｖ2；TPA	ID3ｖ3；TPOS	DISCNUMBER/TOTALDISC
			qtItemList.add("----");	//														iTunesInfo						ID3ｖ2；--	ID3ｖ3；--
			qtItemList.add("©nrt");	//														Narrator						ID3ｖ2；--	ID3ｖ3；--
			qtItemList.add("@PST");	//														Parent Short Title				ID3ｖ2；--	ID3ｖ3；--
			qtItemList.add("@ppi");	//														Parent ProductID				ID3ｖ2；--	ID3ｖ3；--
			qtItemList.add("AACR");	//														Unknown_AACR?					ID3ｖ2；--	ID3ｖ3；--
			qtItemList.add("CDEK");	//														Unknown_CDEK?					ID3ｖ2；--	ID3ｖ3；--
			qtItemList.add("CDET");	//														Unknown_CDET?					ID3ｖ2；--	ID3ｖ3；--
			qtItemList.add("GUID");	//														GUID							ID3ｖ2；--	ID3ｖ3；--
			qtItemList.add("VERS");	//														ProductVersion					ID3ｖ2；--	ID3ｖ3；--
			qtItemList.add("grup");	//														Grouping						ID3ｖ2；--	ID3ｖ3；--
			qtItemList.add("gshh");	//														GoogleHostHeade					ID3ｖ2；--	ID3ｖ3；--
			qtItemList.add("gspm");	//														GooglePingMessage				ID3ｖ2；--	ID3ｖ3；--
			qtItemList.add("gspu");	//														GooglePingURL					ID3ｖ2；--	ID3ｖ3；--
			qtItemList.add("gssd");	//														GoogleSourceData				ID3ｖ2；--	ID3ｖ3；--
			qtItemList.add("gsst");	//														GoogleStartTime					ID3ｖ2；--	ID3ｖ3；--
			qtItemList.add("gstd");	//														GoogleTrackDuration				ID3ｖ2；--	ID3ｖ3；--
			qtItemList.add("hdvd");	//	?													HDVideo	?						ID3ｖ2；--	ID3ｖ3；--
			qtItemList.add("hdtv");	//ビデオ解像度の明示		ITUNESHDVIDEO				HDVideo							ID3ｖ2；--	ID3ｖ3；--
			qtItemList.add("itnu");	//														iTunesU							ID3ｖ2；--	ID3ｖ3；--
			qtItemList.add("keyw");	//ポッドキャストキーワード		編集不可				Keyword							ID3ｖ2；--	ID3ｖ3；--
			qtItemList.add("ldes");	//サブタイトル/説明の追加情報							Long Description				ID3ｖ2；--	ID3ｖ3；TIT3;	Subtitle/Description refinement?
			qtItemList.add("pcst");	//ポッドキャストであることを明示						Podcast							ID3ｖ2；--	ID3ｖ3；--
			qtItemList.add("perf");	//指揮者/演奏者詳細情報									Performer						ID3ｖ2；TP3	ID3ｖ3；TPE3	Conductor/Performer refinement
			qtItemList.add("pgap");	//ギャップレスコンテンツの明示							PlayGap							ID3ｖ2；--	ID3ｖ3；--		ITUNESGAPLESS
			qtItemList.add("plID");	//プレイリスト（アルバム）識別子	編集不可			PlayListID						ID3ｖ2；--	ID3ｖ3；--
			qtItemList.add("prID");	//														ProductID						ID3ｖ2；--	ID3ｖ3；--
			qtItemList.add("purd");	//購入日												ITUNESPURCHASEDATE				ID3ｖ2；--	ID3ｖ3；--		ITUNESPURCHASEDATE
			qtItemList.add("purl");	//ポッドキャストURL										Podcast URL						ID3ｖ2；--	ID3ｖ3；--		PODCASTURL
			qtItemList.add("rate");	//人気メーター ?										RatingPercent					ID3ｖ2；POP	ID3ｖ3；POPM	Popularimeter
			qtItemList.add("rldt");	//オリジナルのリリース年?								Release Date					ID3ｖ2；TOR	ID3ｖ3；TORY	Original release year
			qtItemList.add("rtng");	//保護者のためのレートの明示?番組?番組（読み）			Rating?	TVSHOW?					ID3ｖ2；--	ID3ｖ3；--		ITUNESADVISORY?	TVSHOW?
			qtItemList.add("sfID");	//ストアの国				編集不可					AppleStore Country				ID3ｖ2；--	ID3ｖ3；--
			qtItemList.add("sosn");	//														Sort Show						ID3ｖ2；--	ID3ｖ3；--
			qtItemList.add("stik");	//メディアの種類の明示									MediaType						ID3ｖ2；TMT?ID3ｖ3；TMED?	ITUNESMEDIATYPE
			qtItemList.add("titl");	//タイトル/曲名/内容の説明	タイトル					Title							ID3ｖ2；TT2	ID3ｖ3；TIT2;	Track Title	Title/Title/Songname/Content description
			qtItemList.add("tmpo");	//一分間の拍数											BeatsPerMinute					ID3ｖ2；TBP	ID3ｖ3；TBPM	BPM (Beats Per Minute)
			qtItemList.add("tven");	//エピソードID											TVEpisodeID						ID3ｖ2；--	ID3ｖ3；--
			qtItemList.add("tves");	//														TVEpisode						ID3ｖ2；--	ID3ｖ3；--
			qtItemList.add("tvnn");	//放送局												TVNetworkName					ID3ｖ2；--	ID3ｖ3；--
			qtItemList.add("tvsh");	//														TVShow							ID3ｖ2；--	ID3ｖ3；--
			qtItemList.add("tvsn");	//シーズン												TVSeason						ID3ｖ2；--	ID3ｖ3；--
			qtItemList.add("tvsn");	//														TVSeason						ID3ｖ2；--	ID3ｖ3；--
			qtItemList.add("lrcu");	//LyricsURI							QuickTime UserData Tags								ID3ｖ2；ULT	ID3ｖ3；USLT
			qtItemList.addAll(qtSubBox);

			result_Tag =null;				//	タグ名

			stock_acc_moov =null;		//QuickTime Movie Tagsの一時保存
			stock_acc_meta =null;		//QuickTime Meta Tagsの一時保存
			stock_acc_mdat =null;		//QuickTime Meta Tagsの一時保存
			stock_acc_frea =null;		//Kodak frea Tagsの一時保存
			stock_acc_free =null;		//Kodak Free Tagsの一時保存
			stock_acc_pnot =null;		//QuickTime Preview Tagsの一時保存
			stock_acc_udta =null;		//FLIR UserData Tagsの一時保存
			stock_acc_skip =null;		//Canon Skip Tagsの一時保存
			stock_acc_uuid_XMP  =null;		//XMP Tagsの一時保存
			stock_acc_uuid_PROF  =null;		//UUID-PROFの一時保存
			stock_acc_uuid_FlipF  =null;		//UUID-Flipの一時保存

			stock_acc_movie_meta  =null;		//										QuickTime Tags.QuickTime Movie Tags.Meta		ID3ｖ2；CRM	ID3ｖ3；？	Encrypted meta frame?
			stock_acc_movie_cmov  =null;		//										QuickTime Tags.QuickTime Movie Tags.cmov		ID3ｖ2；--	ID3ｖ3；--
			stock_acc_movie_htka  =null;		//										QuickTime Tags.QuickTime Movie Tags.htka		ID3ｖ2；--	ID3ｖ3；--
			stock_acc_movie_iods  =null;		//										QuickTime Tags.QuickTime Movie Tags.iods		ID3ｖ2；--	ID3ｖ3；--
			stock_acc_movie_mvhd  =null;		//MovieHeader							QuickTime Tags.QuickTime Movie Tags.mvhd		ID3ｖ2；--	ID3ｖ3；
			stock_acc_movie_trak  =null;		//										QuickTime Tags.QuickTime Movie Tags.trak		ID3ｖ2；--	ID3ｖ3；
			stock_acc_movie_udta  =null;		//User Data								QuickTime Tags.QuickTime Movie Tags.udta		ID3ｖ2；--	ID3ｖ3；USER	Terms of use
			stock_acc_movie_uuid  =null;		//XMP/UUID-PROF/UUID-Flip/UUID-Unknown?	QuickTime Tags.QuickTime Movie Tags.uuid		ID3ｖ2；--	ID3ｖ3；--

			stock_acc_meta_ilst  =null;		//										QuickTime Tags.QuickTime Meta Tags.ilst			ID3ｖ2；--	ID3ｖ3；--
			stock_acc_meta_bxml  =null;		//BinaryXML?							QuickTime Tags.QuickTime Meta Tags.bxml			ID3ｖ2；--	ID3ｖ3；--
			stock_acc_meta_dinf  =null;		//DataInformation?						QuickTime Tags.QuickTime Meta Tags.dinf			ID3ｖ2；--	ID3ｖ3；--
			stock_acc_meta_free  =null;		//Kodak Free?	/Free?					QuickTime Tags.QuickTime Meta Tags.free			ID3ｖ2；--	ID3ｖ3；--
			stock_acc_meta_hdlr  =null;		//Handler								QuickTime Tags.QuickTime Meta Tags.hdlr			ID3ｖ2；--	ID3ｖ3；--
			stock_acc_meta_iinf  =null;		//ItemInformation?						QuickTime Tags.QuickTime Meta Tags.iinf			ID3ｖ2；--	ID3ｖ3；--
			stock_acc_meta_iloc  =null;		//ItemLocation?q						QuickTime Tags.QuickTime Meta Tags.iloc			ID3ｖ2；--	ID3ｖ3；--
			stock_acc_meta_ipmc  =null;		//IPMPControl?							QuickTime Tags.QuickTime Meta Tags.ipmc			ID3ｖ2；--	ID3ｖ3；--
			stock_acc_meta_ipro  =null;		//ItemProtection?						QuickTime Tags.QuickTime Meta Tags.ipro			ID3ｖ2；--	ID3ｖ3；--
			stock_acc_meta_keys  =null;		//Keys									QuickTime Tags.QuickTime Meta Tags.keys			ID3ｖ2；--	ID3ｖ3；--
			stock_acc_meta_pitm  =null;		//PrimaryItemReference?					QuickTime Tags.QuickTime Meta Tags.pitm			ID3ｖ2；--	ID3ｖ3；--
			stock_acc_meta_xml  =null;		//XML					QuickTime Tags.QuickTime Meta Tags.xml 			ID3ｖ2；--	ID3ｖ3；--

			result_USLT =null;	//©lyr			//非同期 歌詞/文書のコピー								Lyrics							ID3ｖ2；ULT	ID3ｖ3；USLT	Unsychronized lyric/text transcription
			result_TPE1 =null;	//©ART			//アーティスト																			ID3ｖ2；TP1	ID3ｖ3；TPE1	ARTIST[1]
			result_TALB =null;	//©alb	albm	//アルバム/映画/ショーのタイトル						Album/Movie/Show title			ID3ｖ2；TAL	ID3ｖ3；TALB
			result_COMR =null;	//©cmt			//コメント												Comment							ID3ｖ2；COM	ID3ｖ3；COMM	Comments
			result_TCOM =null;	//©com	©wrt	//作曲者																				//10cc(2)
			result_TCOP =null;	//©cpy	cprt	//著作権情報	権利元				Copyright	Copyright								ID3ｖ2；TCR	ID3ｖ3；TCOP	Copyright message
			result_TYER =null;	//yrrc			//年	レコーディング年/年*15	Date*16	Year Deprecated	Year		Year				ID3ｖ2；TYE	ID3ｖ3；TYER
								//©day");		//リリース日（年）										YEAR							ID3ｖ2；?	ID3ｖ3；TYER	YEAR
			result_TENC =null;	//©too	//©enc?	//エンコーディング ソフトウェア							EncodedBy						ID3ｖ2；TEN	ID3ｖ3；TENC	Encoded by
			result_TCON =null;	//gnre			//ジャンル												Genre	Content type			ID3ｖ2；TCO	ID3ｖ3；TCON
			result_TIT1 =null;	//				グループ												CONTENTGROUP					ID3ｖ2；?-	ID3ｖ3；TIT1
			result_TPUB =null;	//©pub			//出版社/発行元											Publisher						ID3ｖ2；TPB	ID3ｖ3；TPUB	Publisher
			result_TRCK =null;	//©trk	trkn	トラックの番号/セット中の位置							Track							ID3ｖ2；TRK	ID3ｖ3；TRCK	TrackNumber	Track number/Position in set
			result_TOAL =null;	//@pti	soal	//原題													Parent Title					ID3ｖ2；--	ID3ｖ3；TOAL	//Original album/movie/show title?
			result_TPE2 =null;	//aART	soaa		//アルバムアーティスト									ALBUMARTIST						ID3ｖ2；--	ID3ｖ3；TPE2	Lead artist(s)/Lead performer(s)/Soloist(s)/Performing group
			result_MCDI =null;	//atID			//アーティスト識別子	編集不可						Album Title ID					ID3ｖ2；MCI?ID3ｖ3；MCDI?	Music CD Identifier?
			result_APIC =null;	//covr			//付属する画像											CoverArt						ID3ｖ2；PIC	ID3ｖ3；APIC	Attached picture
			result_TIT2 =null;	//desc	dscp	//タイトル/曲名/内容の説明	タイトル					Description						ID3ｖ2；TT2	ID3ｖ3；TIT2;	Track Title	Title/Title/Songname/Content description
								//titl		;	//タイトル/曲名/内容の説明	タイトル					Title							ID3ｖ2；TT2	ID3ｖ3；TIT2;	Track Title	Title/Title/Songname/Content description
			result_TPOS =null;	//disk			//ディスクナンバー										DiskNumber	Part of a set		ID3ｖ2；TPA	ID3ｖ3；TPOS	DISCNUMBER/TOTALDISC
			result_TIT3 =null;	//ldes			//サブタイトル/説明の追加情報							Long Description				ID3ｖ2；--	ID3ｖ3；TIT3;	Subtitle/Description refinement?
								//sonm");	//タイトル（読み）											Sort Name						ID3ｖ2；--	ID3ｖ3；TIT3		TITLESORT
			result_TPE3 =null;	//perf			//指揮者/演奏者詳細情報									Performer						ID3ｖ2；TP3	ID3ｖ3；TPE3	Conductor/Performer refinement
			result_POPM =null;	//rate			//人気メーター ?										RatingPercent					ID3ｖ2；POP	ID3ｖ3；POPM	Popularimeter
			result_TORY =null;	//rldt			//オリジナルのリリース年?								Release Date					ID3ｖ2；TOR	ID3ｖ3；TORY	Original release year
			result_TMED =null;	//stik			//メディアの種類の明示									MediaType						ID3ｖ2；TMT?ID3ｖ3；TMED?	ITUNESMEDIATYPE
			result_TBPM =null;	//tmpo			//一分間の拍数											BeatsPerMinute					ID3ｖ2；TBP	ID3ｖ3；TBPM	BPM (Beats Per Minute)
			result_TSOA =null;	//©nam			//アルバム（読み）										ALBUMSORT						ID3ｖ2；--	ID3ｖ3；TSOA
			result_TSOP =null;	//soar			//アーティスト（読み）									ARTISTSORT						ID3ｖ2；--	ID3ｖ3；TSOP
			result_TSOC =null;	//soco			//作曲者（読み）										SortComposer					ID3ｖ2；?-	ID3ｖ3；TSOC	COMPOSERSORT
			result_des =null;	//©des			//説明													Description	Track				ID3ｖ2；--	ID3ｖ3；--		SUBTITLE
			result_nrt =null;	//©nrt			//														Narrator						ID3ｖ2；--	ID3ｖ3；--
			result_iTunesInfo =null;	//----			//														iTunesInfo						ID3ｖ2；--	ID3ｖ3；--
			result_PST =null;	//@PST			//														Parent Short Title				ID3ｖ2；--	ID3ｖ3；--
			result_ppi =null;	//@ppi			//														Parent ProductID				ID3ｖ2；--	ID3ｖ3；--
			result_sti =null;	//@sti			//														Short Title						ID3ｖ2；--	ID3ｖ3；--
			result_AACR =null;	//AACR");	//														Unknown_AACR?					ID3ｖ2；--	ID3ｖ3；--
			result_CDEK =null;	//CDEK");	//														Unknown_CDEK?					ID3ｖ2；--	ID3ｖ3；--
			result_CDET =null;	//CDET");	//														Unknown_CDET?					ID3ｖ2；--	ID3ｖ3；--
			result_GUID =null;	//GUID");	//														GUID							ID3ｖ2；--	ID3ｖ3；--
			result_VERS =null;	//VERS");	//														ProductVersion					ID3ｖ2；--	ID3ｖ3；--
			result_akID =null;	//akID");	//アカウントの種類			編集不可					Apple Store Account Type		ID3ｖ2；--	ID3ｖ3；--
			result_apID =null;	//apID");	//アカウント情報					ITUNESACCOUNT		Apple Store Account				ID3ｖ2；--	ID3ｖ3；--
			result_auth =null;	//auth");	//														Author							ID3ｖ2；--	ID3ｖ3；--
			result_catg =null;	//catg");	//ポッドキャストカテゴリ								Category						ID3ｖ2；--	ID3ｖ3；--		PODCASTCATEGORY
			result_cnID =null;	//cnID");	//コンテンツ識別子										AppleStoreCatalogID				ID3ｖ2；--	ID3ｖ3；--		ITUNESCATALOGID
			result_cpil =null;	//cpil");	//コンピレーションの明示								Compilation						ID3ｖ2；--	ID3ｖ3；--		COMPILATION
			result_egid =null;	//egid");	//ポッドキャストエピソードユニークID					Episode Global Unique ID		ID3ｖ2；--	ID3ｖ3；--		PODCASTID
			result_geID =null;	//geID");	//ジャンル識別子		編集不可						GenreID							ID3ｖ2；--	ID3ｖ3；--
			result_grup =null;	//grup");	//														Grouping						ID3ｖ2；--	ID3ｖ3；--
			result_gshh =null;	//gshh");	//														GoogleHostHeade					ID3ｖ2；--	ID3ｖ3；--
			result_gspm =null;	//gspm");	//														GooglePingMessage				ID3ｖ2；--	ID3ｖ3；--
			result_gspu =null;	//gspu");	//														GooglePingURL					ID3ｖ2；--	ID3ｖ3；--
			result_gssd =null;	//gssd");	//														GoogleSourceData				ID3ｖ2；--	ID3ｖ3；--
			result_gsst =null;	//gsst");	//														GoogleStartTime					ID3ｖ2；--	ID3ｖ3；--
			result_gstd =null;	//gstd");	//														GoogleTrackDuration				ID3ｖ2；--	ID3ｖ3；--
			result_hdvd =null;	//hdvd");	//	?													HDVideo	?						ID3ｖ2；--	ID3ｖ3；--
			result_hdvd =null;	//hdtv");	//ビデオ解像度の明示		ITUNESHDVIDEO				HDVideo							ID3ｖ2；--	ID3ｖ3；--
			result_itnu =null;	//itnu");	//														iTunesU							ID3ｖ2；--	ID3ｖ3；--
			result_keyw =null;	//keyw");	//ポッドキャストキーワード		編集不可				Keyword							ID3ｖ2；--	ID3ｖ3；--
			result_pcst =null;	//pcst");	//ポッドキャストであることを明示						Podcast							ID3ｖ2；--	ID3ｖ3；--
			result_pgap =null;	//pgap");	//ギャップレスコンテンツの明示							PlayGap							ID3ｖ2；--	ID3ｖ3；--		ITUNESGAPLESS
			result_plID =null;	//plID");	//プレイリスト（アルバム）識別子	編集不可			PlayListID						ID3ｖ2；--	ID3ｖ3；--
			result_prID =null;	//prID");	//														ProductID						ID3ｖ2；--	ID3ｖ3；--
			result_purd =null;	//purd");	//購入日												ITUNESPURCHASEDATE				ID3ｖ2；--	ID3ｖ3；--		ITUNESPURCHASEDATE
			result_purl =null;	//purl");	//ポッドキャストURL										Podcast URL						ID3ｖ2；--	ID3ｖ3；--		PODCASTURL
			result_rtng =null;	//rtng");	//保護者のためのレートの明示?番組?番組（読み）			Rating?	TVSHOW?					ID3ｖ2；--	ID3ｖ3；--		ITUNESADVISORY?	TVSHOW?
			result_sfID =null;	//sfID");	//ストアの国				編集不可					AppleStore Country				ID3ｖ2；--	ID3ｖ3；--
			result_sosn =null;	//sosn");	//														Sort Show						ID3ｖ2；--	ID3ｖ3；--
			result_tven =null;	//tven");	//エピソードID											TVEpisodeID						ID3ｖ2；--	ID3ｖ3；--
			result_tves =null;	//tves");	//														TVEpisode						ID3ｖ2；--	ID3ｖ3；--
			result_tvnn =null;	//tvnn");	//放送局												TVNetworkName					ID3ｖ2；--	ID3ｖ3；--
			result_tvsh =null;	//tvsh");	//														TVShow							ID3ｖ2；--	ID3ｖ3；--
			result_tvsn =null;	//tvsn");	//シーズン												TVSeason						ID3ｖ2；--	ID3ｖ3；--

			//iTunes				FourCC		Mp3Tag							https://ja.wikipedia.org/wiki/Mp3tag#cite_note-255bytes-1
			//不明	プレイバック情報	iTunSMPB	ITUNSMPB[14]
			//不明	ノーマライズ情報	iTunNORM	ITUNNORM
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}
	}

	/**
	 * AACフィールド名リストをList<String> syougouに作成
	 * 	@link http://www.sno.phy.queensu.ca/~phil/exiftool/TagNames/QuickTime.html#HTCInfo
	 * */
	public void makAACList(int reqCode) {	//AACフィールド名リストをList<String> syougouに作成
		String result =null;
		final String TAG = "makAACList";
		String dbMsg= "";
		//									https://developer.apple.com/library/mac/documentation/QuickTime/QTFF/QTFFChap1/qtff1.html
		try{
			if(syougou == null){
				syougou = new ArrayList<String>();
			} else {
				syougou.clear();
			}
			switch(reqCode) {
			case read_AAC_PRE:				//最小限の設定読取り
				syougou.addAll(qpPre);
				syougou.addAll(qtSubBox);		//2階層より下位に出現するもの
				syougou.add("soun");		//hdlr
				syougou.add("url ");		//hdlr
				syougou.add("esds");		//hdlr
				break;
			case read_AAC_LYRIC:				//@Lyrだけを読めるか試みる
			case read_AAC_ITEM:				//QuickTime Tagsの読取り
				syougou.addAll(qtItemList);
				syougou.addAll(qtMovieMeta);
				syougou.addAll(qtMovie);
				syougou.addAll(qpHead);
				break;
			case read_AAC_HEAD:				//QuickTime Tagsの読取り
				syougou.addAll(qpHead);
				break;
			case read_AAC_HEAD_Movie:				//QuickTime Tags.QuickTime Movie Tagsの読取り
				syougou.addAll(qtMovie);
				break;
			case read_AAC_Movie_Meta:				//QuickTime Tags.QuickTime Meta Tagsの読取り
				syougou.addAll(qtMovieMeta);
				break;
//			case read_AAC_ITEM:				//QuickTime Tagsの読取り
//				syougou.addAll(qtItemList);
//				break;
//				default:
//					break;
			}
			if(kensaku == null){
				kensaku = new ArrayList<String>();//検索するフレーム名
			} else {
				kensaku.clear();
			}
			switch(reqCode) {
			case read_AAC_PRE:				//最小限の設定読取り
				kensaku.addAll(qpPre);
				break;
			case read_AAC_LYRIC:				//@Lyrだけを読めるか試みる
//				kensaku.add("cprt");	//著作権												Copyright						ID3ｖ2；TCR	ID3ｖ3；TCOP	Copyright message
//				kensaku.add("©cpy");	//著作権												Copyright						ID3ｖ2；TCR	ID3ｖ3；TCOP	Copyright message
				kensaku.add("©lyr");
				break;
			case read_AAC_HEAD:				//QuickTime Tagsの読取り
				kensaku.add("moov");
				break;
			case read_AAC_HEAD_Movie:				//QuickTime Tags.QuickTime Movie Tagsの読取り
				kensaku.add("meta");	//	Index4	5	MediaLanguageCode														ID3ｖ2；CRM	ID3ｖ3；？	Encrypted meta frame?
				kensaku.add("trak");	//ビデオ、オーディオそれぞれのトラックのヘッダ情報。									ID3ｖ2；--	ID3ｖ3；
				break;
			case read_AAC_Movie_Meta:				//QuickTime Tags.QuickTime Meta Tagsの読取り
				kensaku.add("ilst");	//	ItemList > "©lyr					QuickTime Tags.QuickTime Meta Tags.ilst			ID3ｖ2；--	ID3ｖ3；--
				break;
			case read_AAC_ITEM:				//QuickTime Tagsの読取り
				kensaku.addAll(qtItemListCoar);
				break;
			default:
				kensaku.addAll(syougou);
				break;
			}
		}catch (Exception e) {
		myErrorLog(TAG,dbMsg + "で"+e.toString());
	}
}

	/**@lirからの優先読込み*/
	public void lyricReadAac(){
		result =null;
		final String TAG = "lyricReadAac";
		String dbMsg= "";
		//https://developer.apple.com/library/mac/documentation/QuickTime/QTFF/QTFFChap1/qtff1.html
			//	http://www.sno.phy.queensu.ca/~phil/exiftool/TagNames/QuickTime.html	////////////////////////////
		try{
			result = resultStock;
			int readInt = result.length();
			if(20 < readInt){
				dbMsg +=result.substring(0, 20) +  "～" + result.substring(readInt-20, readInt);
			}else{
				dbMsg +=result;
			}
			dbMsg += "(" + readInt + ">>" + result.length() +"文字)";		//"M4A mp42isom".length()
			makAACList( read_AAC_LYRIC);										//@Lyrだけを読めるか試みる
			String pdTitol = getApplicationContext().getString(R.string.tag_prog_titol1) +"" + getResources().getString(R.string.common_yomitori);				//
			pdMaxVal= kensaku.size();										//result.length();								//プログレス終端値
			dbMsg +=",pdMaxVal=" + pdMaxVal +"項目";
			reqCode = read_AAC_LYRIC ;							//@Lyrだけを読めるか試みる
			dbMsg +=",reqCode="+reqCode;
			dbMsg +=",pdTitol="+pdTitol;
			String pdMessage = "AAC ; @lyr" ;																			//    <string name="common_yomitori">読み込み</string>
			dbMsg +=",pdMessage="+pdMessage;
			plTask.execute(reqCode,kensaku,TagBrows.this.result,null);

//			String result ="";
//			TagBrows.this.result = Readloop( reqCode,kensaku,TagBrows.this.result);
//			if( result_USLT == null ){
//				TagBrows.this.result = resultStock;
//				//			headReadAac2(result);		//上位階層から順次読み込み		final RandomAccessFile newFile
//				itemReadAac(  );		//QuickTime ItemList Tagsの読取り準備
//			}else{
//				readEndAac(  );
//			}
			dbMsg +=",result="+result;
			resultStock = null;
			/////pTask
			myLog(TAG,dbMsg);
			}catch (Exception e) {
				myErrorLog(TAG,dbMsg + "で"+e.toString());
			}
		}

	public void headReadAac2(String result){		//上位階層から順次読み込み		final RandomAccessFile newFile
		result =resultStock;
		final String TAG = "headReadAac2";
		String dbMsg= "";
			//	http://www.sno.phy.queensu.ca/~phil/exiftool/TagNames/QuickTime.html	////////////////////////////
		try{
			result = resultStock;
			if(result != null){
				int readInt = result.length();
				if(20 < readInt){
					dbMsg += result.substring(0, 20) +  "～" + result.substring(readInt-20, readInt);
				}else{
					dbMsg += result;
				}
				dbMsg += "(" + readInt + "/" + result.length() +"文字)";		//"M4A mp42isom".length()
				makAACList( read_AAC_HEAD);	//QuickTime TagsリストをList<String> syougouに作成
				String pdTitol = getApplicationContext().getString(R.string.tag_prog_titol1) +"" + getResources().getString(R.string.common_yomitori);				//
				pdMaxVal= kensaku.size();										//result.length();								//プログレス終端値
				dbMsg += ",pdMaxVal=" + pdMaxVal +"項目";
				reqCode = read_AAC_HEAD ;						//QuickTime Tagsの読取り
				dbMsg += ",reqCode="+reqCode;
				dbMsg += ",pdTitol="+pdTitol;
				String pdMessage = "AAC ; QuickTime Tags" ;																			//    <string name="common_yomitori">読み込み</string>
				dbMsg += ",pdMessage="+pdMessage;
		//		pTask = (plogTask) new plogTask(this ,  this , reqCode , pdTitol ,pdMessage , pdMaxVal ).execute(reqCode,  pdMessage , result , kensaku );		//,jikkouStep,totalStep,calumnInfo
				plTask.execute(reqCode,kensaku,TagBrows.this.result,null);
	//			TagBrows.this.result = Readloop( reqCode,kensaku,TagBrows.this.result);
				dbMsg += ",result="+TagBrows.this.result;
		//		movieReadAac(  );		//QuickTime Tags.QuickTime Movie Tagsの読取りの読取り準備
				myLog(TAG,dbMsg);
			}else{
				readEndAac(  );			//呼出し元への戻り処理
			}
			}catch (Exception e) {
				myErrorLog(TAG,dbMsg + "で"+e.toString());
			}
		}

	/**
	 * AACのQuickTime Tags読取り
	 * */
	public void headReadAacBody(String readStr , String target){		//AACのQuickTime Tags読取り
		final String TAG = "headReadAacBody";
		String dbMsg= "";
		try{
			dbMsg= "target=" + target + ",";		//"M4A mp42isom".length()
			if ( readStr != null){
				if ( ! readStr.equals("")){
					int readInt = readStr.length();
					if(50 < readInt){
						dbMsg +=readStr.substring(0, 50) +  "～" + readStr.substring(readInt-50, readInt);
					}else{
						dbMsg +=readStr;
					}
					dbMsg += "(" + readInt + "/" + result.length() +"文字)";		//"M4A mp42isom".length()
					result_Tag = result_Tag + "\n"+ target + ";" ;
					int endInt =readInt;
					byte[] buffer = readStr.substring(0, readInt).getBytes();
					dbMsg += ",buffer" ;
					for(int i = 0 ; i < endInt ; i++){
						dbMsg += 	",[" + i + "]=" + buffer[i];		//
					}
					if(target.equals("ftyp")){							//ファイルタイプ	File Type		ID3ｖ2；TFT	ID3ｖ3；TFLT	File type
						result_Tag =target + ";" ;
	/*Size(getTargetFream)	: 4bite :	A 32-bit unsigned integer that specifies the number of bytes in this atom.
	 * 									この原子でバイト数を指定する32ビット・サインがない整数。
	 * Type					: 4bite :	A 32-bit unsigned integer that identifies the atom type, typically represented as a four-character code; this field must be set to 'ftyp'.
	*									Atomタイプ（4-文字コードとして典型的に見受けられる）を確認する32ビット・サインがない整数;このフィールドは、『ftyp』に設定されなければなりません。
	* Major_Brand			: 4bite :	A 32-bit unsigned integer that should be set to 'qt  ' (note the two trailing ASCII space characters) for QuickTime movie files.
	* 									If a file is compatible with multiple brands, all such brands are listed in the Compatible_Brands fields, and the Major_Brand identifies the preferred brand or best use. Minor_Version
	*									『qt』（2つの引いているアスキー・スペース性格に注意します）に、QuickTimeムービーのためにセットされなければならない32ビット・サインがない整数は、ファイルします。
	*									ファイルが複数のブランドと互換性を持つならば、すべてのそのようなブランドはCompatible_Brandsフィールドにリストされます、そして、Major_Brandは好ましいブランドまたは最高の使用を確認します。
	*									ファイルタイプcompatibility―はファイルタイプを確認して、それを類似したファイルタイプと区別します。	（例えばMPEG-4ファイルとJPEG-2000ファイル）
	*/
/*
 * Bob Dylan/Desire/01 Hurricane.m4a,				������ ftypM4A ��������M4A mp42isom��������(32/8864066文字),buffer,[0]=0,[1]=0,[2]=0,[3]=32,[4]=102,[5]=116,[6]=121,[7]=112,[8]=77,[9]=52,[10]=65,[11]=32,[12]=0,[13]=0,[14]=0,[15]=0,[16]=77,[17]=52,[18]=65,[19]=32,[20]=109,[21]=112,[22]=52,[23]=50,[24]=105,[25]=115,[26]=111,[27]=109,[28]=0,[29]=0,[30]=0,[31]=0,Major_Brand=M4A >>��������M4A mp42isom��������,2回目のMajor_Brand=12～8まで-4バイト,Minor_Version=mp42isom,残り4文字,Compatible_Brands��������
 * Bob Dylan/Desire/04 One More Cup Of Coffee.m4a,	������ ftypM4A ��������M4A mp42isom��������(32/4139400文字),buffer,[0]=0,[1]=0,[2]=0,[3]=32,[4]=102,[5]=116,[6]=121,[7]=112,[8]=77,[9]=52,[10]=65,[11]=32,[12]=0,[13]=0,[14]=0,[15]=0,[16]=77,[17]=52,[18]=65,[19]=32,[20]=109,[21]=112,[22]=52,[23]=50,[24]=105,[25]=115,[26]=111,[27]=109,[28]=0,[29]=0,[30]=0,[31]=0,Major_Brand=M4A >>��������M4A mp42isom��������,2回目のMajor_Brand=12～8まで-4バイト,buffer[0]=0,[1]=0,[2]=0,[3]=0,[4]=77,[5]=52,[6]=65,Minor_Version=mp42isom,残り4文字,buffer[0]=0,[1]=0,[2]=0,[3]=0,Compatible_Brands��������
 * Bonnie Pink/Water Me/01 Water Me.m4a,			������ ftypM4A ��������M4A mp42isom��������(32/3725658文字),buffer,[0]=0,[1]=0,[2]=0,[3]=32,[4]=102,[5]=116,[6]=121,[7]=112,[8]=77,[9]=52,[10]=65,[11]=32,[12]=0,[13]=0,[14]=0,[15]=0,[16]=77,[17]=52,[18]=65,[19]=32,[20]=109,[21]=112,[22]=52,[23]=50,[24]=105,[25]=115,[26]=111,[27]=109,[28]=0,[29]=0,[30]=0,[31]=0,Major_Brand=M4A >>��������M4A mp42isom��������,2回目のMajor_Brand=12～8まで-4バイト,buffer[0]=0,[1]=0,[2]=0,[3]=0,[4]=77,[5]=52,[6]=65,Minor_Version=mp42isom,残り4文字,buffer[0]=0,[1]=0,[2]=0,[3]=0,Compatible_Brands��������
 * 宇多田ヒカル/Keep Tryin'/01 Keep Tryin'.m4a		������ ftypM4A ��������M4A mp42isom��������(32/4763868文字),buffer,[0]=0,[1]=0,[2]=0,[3]=32,[4]=102,[5]=116,[6]=121,[7]=112,[8]=77,[9]=52,[10]=65,[11]=32,[12]=0,[13]=0,[14]=0,[15]=0,[16]=77,[17]=52,[18]=65,[19]=32,[20]=109,[21]=112,[22]=52,[23]=50,[24]=105,[25]=115,[26]=111,[27]=109,[28]=0,[29]=0,[30]=0,[31]=0,Major_Brand=M4A >>��������M4A mp42isom��������,2回目のMajor_Brand=12～8まで-4バイト,buffer[0]=0,[1]=0,[2]=0,[3]=0,[4]=77,[5]=52,[6]=65,Minor_Version=mp42isom,残り4文字,buffer[0]=0,[1]=0,[2]=0,[3]=0,Compatible_Brands��������
 * */
						int startInt = readStr.indexOf(target)+target.length();			//target.length()+ 4;
						endInt = startInt+ 4;
						String Major_Brand = readStr.substring(startInt, endInt);
						result_Tag = result_Tag + " Major_Brand;"+ Major_Brand ;
						dbMsg += ",Major_Brand=" + Major_Brand;
						startInt = endInt;
						endInt = readStr.indexOf(Major_Brand)+Major_Brand.length();		//startInt+ 4;
						readStr = readStr.substring(endInt);
						dbMsg += ">>" + readStr;
						endInt = readStr.indexOf(Major_Brand)+Major_Brand.length();		//startInt+ 4;
						dbMsg += ",2回目のMajor_Brand=" +startInt + "～" + endInt + "まで" + ( endInt - startInt ) + "バイト";
	/* Minor_Version			: 4bite :	A 32-bit field that indicates the file format specification version. For QuickTime movie files, this takes the form of four binary-coded decimal values, indicating the century, year, and month of the QuickTime File Format Specification, followed by a binary coded decimal zero. For example, for the June 2004 minor version, this field is set to the BCD values 20 04 06 00.
	*									『qt』（2つの引いているアスキー・スペース性格に注意します）に、QuickTimeムービーのためにセットされなければならない32ビット・サインがない整数は、ファイルします。
	*									ファイルが複数のブランドと互換性を持つならば、すべてのそのようなブランドはCompatible_Brandsフィールドにリストされます、そして、Major_Brandは好ましいブランドまたは最高の使用を確認します。]
	*/
						startInt = readStr.indexOf(Major_Brand)+Major_Brand.length();			//target.length()+ 4;
						String Minor_Version = readStr.substring( startInt , startInt+8);
						result_Tag = result_Tag + "\n Minor_Version;"+ Minor_Version ;
						dbMsg += ",Minor_Version=" + Minor_Version;
						startInt = readStr.indexOf(Minor_Version)+Minor_Version.length();			//target.length()+ 4;
						readStr = readStr.substring(startInt);
//						startInt = target.length()+ 4;
//						endInt = startInt+ 4;
						dbMsg += ",残り" + readStr.length() + "文字";
						if( 0 < readStr.length()){
		/* Compatible_Brands[ ]	: 4bite :	A series of unsigned 32-bit integers listing compatible file formats. The major brand must appear in the list of compatible brands. One or more “placeholder” entries with value zero are permitted; such entries should be ignored.
//				* 									If none of the Compatible_Brands fields is set to 'qt  ', then the file is not a QuickTime movie file and is not compatible with this specification. Applications should return an error and close the file, or else invoke a file importer appropriate to one of the specified brands, preferably the major brand. QuickTime currently returns an error when attempting to open a file whose file type, file extension, or MIME type identifies it as a QuickTime movie, but whose file type atom does not include the 'qt  ' brand.
//							互換性を持つファイル形式をリストしている一連のサインがない32ビット整数。
//							主要なブランドは、互換性を持つブランドのリストに記載されなければなりません。
//							価値ゼロによる一つ以上の「プレースホールダー」エントリは、許されます;
//							そのようなエントリは無視されなければなりません。
//							Compatible_Brandsフィールドのどれも『qt』に設定されないならば、ファイルはQuickTimeムービー・ファイルでなくて、この仕様と互換性を持ちません。
//							アプリケーションはエラーを返さなければならなくて、ファイルを閉じなければならなくて、でなければ、指定されたブランド（望ましくは主要なブランド）の1つにふさわしいファイル輸入業者を引き合いに出します。
//							タイプ、ファイル拡張子またはMIMEがファイルをタイプするファイルを開こうとすることがそれをQuickTimeムービーと確認するエラーを、QuickTimeは現在返します、しかし、原子はファイルタイプを含みません『qt』brand.
		*/
							String Compatible_Brands = readStr;
							result_Tag = result_Tag + "\n Compatible_Brands;"+ Compatible_Brands ;
							dbMsg += ",Compatible_Brands" + Compatible_Brands;
/*
 * Bob Dylan/Desire/01 Hurricane.m4a,				[12]=0,[13]=0,[14]=0,[15]=0,[16]=77,[17]=52,[18]=65,[19]=32,[20]=109,[21]=112,[22]=52,[23]=50,[24]=105,[25]=115,[26]=111,[27]=109,[28]=0,[29]=0,[30]=0,[31]=0,Major_Brand=M4A >>��������M4A mp42isom��������,2回目のMajor_Brand=12～8まで-4バイト,buffer[0]=0,[1]=0,[2]=0,[3]=0,[4]=77,[5]=52,[6]=65,Minor_Version=mp42isom,残り4文字,buffer[0]=0,[1]=0,[2]=0,[3]=0,Compatible_Brands��������
 * Bob Dylan/Desire/04 One More Cup Of Coffee.m4a,	[12]=0,[13]=0,[14]=0,[15]=0,[16]=77,[17]=52,[18]=65,[19]=32,[20]=109,[21]=112,[22]=52,[23]=50,[24]=105,[25]=115,[26]=111,[27]=109,[28]=0,[29]=0,[30]=0,[31]=0,Major_Brand=M4A >>��������M4A mp42isom��������,2回目のMajor_Brand=12～8まで-4バイト,buffer[0]=0,[1]=0,[2]=0,[3]=0,[4]=77,[5]=52,[6]=65,Minor_Version=mp42isom,残り4文字,buffer[0]=0,[1]=0,[2]=0,[3]=0,Compatible_Brands��������
 * Bonnie Pink/Water Me/01 Water Me.m4a,			[12]=0,[13]=0,[14]=0,[15]=0,[16]=77,[17]=52,[18]=65,[19]=32,[20]=109,[21]=112,[22]=52,[23]=50,[24]=105,[25]=115,[26]=111,[27]=109,[28]=0,[29]=0,[30]=0,[31]=0,Major_Brand=M4A >>��������M4A mp42isom��������,2回目のMajor_Brand=12～8まで-4バイト,buffer[0]=0,[1]=0,[2]=0,[3]=0,[4]=77,[5]=52,[6]=65,Minor_Version=mp42isom,残り4文字,buffer[0]=0,[1]=0,[2]=0,[3]=0,Compatible_Brands��������
 * 宇多田ヒカル/Keep Tryin'/01 Keep Tryin'.m4a		[12]=0,[13]=0,[14]=0,[15]=0,[16]=77,[17]=52,[18]=65,[19]=32,[20]=109,[21]=112,[22]=52,[23]=50,[24]=105,[25]=115,[26]=111,[27]=109,[28]=0,[29]=0,[30]=0,[31]=0,Major_Brand=M4A >>��������M4A mp42isom��������,2回目のMajor_Brand=12～8まで-4バイト,buffer[0]=0,[1]=0,[2]=0,[3]=0,[4]=77,[5]=52,[6]=65,Minor_Version=mp42isom,残り4文字,buffer[0]=0,[1]=0,[2]=0,[3]=0,Compatible_Brands��������
 * */
						}
					}else if(target.equals("mvbr")){
					}else if(target.equals("tagc")){
					}else if(target.equals("cdsc")){
					}else if(target.equals("mdhd")){
					}else if(target.equals("elng")){
/*
 * Bob Dylan/Desire/01 Hurricane.m4a,				��������ËÁÈgËÂD����¬DYd��UÄ����������"(28/8864034文字),	buffer,[0]=0,[1]=0,[2]=0,[3]=0,[4]=-61,[5]=-117,[6]=-61,[7]=-127,	[8]=-61,[9]=-120,[10]=103,[11]=-61,[12]=-117,[13]=-61,[14]=-126,[15]=2,[16]=68,[17]=0,[18]=0,[19]=-62,[20]=-84,[21]=68,[22]=1,[23]=89,[24]=100,[25]=0,[26]=85,[27]=-61
 * Bob Dylan/Desire/04 One More Cup Of Coffee.m4a,	��������ËÁÈþËÂG����¬D��t��UÄ����������"(28/4139368文字),					buffer,[0]=0,[1]=0,[2]=0,[3]=0,[4]=-61,[5]=-117,[6]=-61,[7]=-127,	[8]=-61,[9]=-120,[10]=-61,[11]=-66,[12]=-61,[13]=-117,[14]=-61,[15]=-126,[16]=2,[17]=71,[18]=0,[19]=0,[20]=-62,[21]=-84,[22]=68,[23]=0,[24]=-62,[25]=-104,[26]=116,[27]=0
 * Bonnie Pink/Water Me/01 Water Me.m4a,			��������ÂâgzËÔ]6����¬D��L��UÄ����������"(28/3725626文字),					buffer,[0]=0,[1]=0,[2]=0,[3]=0,[4]=-61,[5]=-126,[6]=-61,[7]=-94,	[8]=103,[9]=122,[10]=-61,[11]=-117,[12]=-61,[13]=-108,[14]=93,[15]=54,[16]=0,[17]=0,[18]=-62,[19]=-84,[20]=68,[21]=0,[22]=-62,[23]=-100,[24]=76,[25]=0,[26]=85,[27]=-61
 * 宇多田ヒカル/Keep Tryin'/01 Keep Tryin'.m4a		��������ÀKI£ÀLÏB����¬D��Æh��UÄ����������"(28/4763836文字),					buffer,[0]=0,[1]=0,[2]=0,[3]=0,[4]=-61,[5]=-128,[6]=75,	[7]=73,		[8]=-62,[9]=-93,[10]=-61,[11]=-128,[12]=76,[13]=-61,[14]=-113,[15]=66,[16]=0,[17]=0,[18]=-62,[19]=-84,[20]=68,[21]=0,[22]=-61,[23]=-122,[24]=104,[25]=0,[26]=85,[27]=-61
 * */
/*
 * Bob Dylan/Desire/01 Hurricane.m4a,				buffer[11]=-61,	[12]=-117,	[13]=-61,	[14]=-126,	[15]=2,		[16]=68,[17]=0,[18]=0,[19]=-62,[20]=-84,[21]=68,[22]=1,[23]=89,	[24]=100,	[25]=0,			[26]=85,[27]=-61
 * Bob Dylan/Desire/04 One More Cup Of Coffee.m4a,	buffer[12]=-61,	[13]=-117,	[14]=-61,	[15]=-126,	[16]=2,		[17]=71,[18]=0,[19]=0,[20]=-62,[21]=-84,[22]=68,[23]=0,[24]=-62,[25]=-104,	[26]=116,[27]=0
 * Bonnie Pink/Water Me/01 Water Me.m4a,			buffer[10]=-61,	[11]=-117,	[12]=-61,	[13]=-108,	[14]=93,	[15]=54,[16]=0,[17]=0,[18]=-62,[19]=-84,[20]=68,[21]=0,[22]=-62,[23]=-100,	[24]=76,[25]=0,	[26]=85,[27]=-61
 * 宇多田ヒカル/Keep Tryin'/01 Keep Tryin'.m4a		buffer[10]=-61,	[11]=-128,	[12]=76,	[13]=-61,	[14]=-113,	[15]=66,[16]=0,[17]=0,[18]=-62,[19]=-84,[20]=68,[21]=0,[22]=-61,[23]=-122,	[24]=104,[25]=0,[26]=85,[27]=-61
 * */
					}else if(target.equals("hdlr")){		//すべて	����������������(8文字),buffer,[0]=0,[1]=0,[2]=0,[3]=0,[4]=0,[5]=0,[6]=0,[7]=0
						dbMsg += ",elng" + readStr;			//Handler Reference Atoms　 	Component type/Component subtype


					}else if(target.equals("moov")){			//映画（トラック、サンプル・データの位置、などの数とタイプ）についての映画資源メタデータ。映画データがどこで見つかるか、そして、どのようにそれを解釈するべきか述べます。
						stock_acc_moov =readStr;		//QuickTime Movie Tagsの一時保存
//						result_Tag = result_Tag + readStr ;
//						String moov  = result.substring(startC, endC);
//						dbMsg +=",moov=" + moov;
//						result_Tag = result_Tag + "\n moov;"+ moov.length() + "バイト;Movie resource metadata about the movie (number and type of tracks, location of sample data, and so on). Describes where the movie data can be found and how to interpret it." ;
					}else if(target.equals("meta")){		//Meta							ID3ｖ2；CRM	ID3ｖ3；？	Encrypted meta frame?
						stock_acc_meta =readStr;		//QuickTime Meta Tagsの一時保存
					/*
						Music/Bob Dylan/Desire/01 Hurricane.m4a,
						 */
					}else if(target.equals("mdat")){			//映画サンプルdata―メディア・サンプル（例えばビデオフレームと音声サンプルのグループ）。通常、このデータは、映画資源を用いてだけ解釈されることができます。
						stock_acc_mdat = readStr ;
//						String mdat  = result.substring(startC, endC);
//						dbMsg +=",mdat=" + mdat;
//						result_Tag = result_Tag + "\n mdat;"+ mdat + ";Movie sample data—media samples such as video frames and groups of audio samples. Usually this data can be interpreted only by using the movie resource." ;
	/*
	Music/Bob Dylan/Desire/01 Hurricane.m4a,	mdat!��@h!%!+ïÐÝ&Ë～|¦Èªhy$hÔ²HCÀ��������(8345509/8864034文字)
	 */
					}else if(target.equals("free")){		//ファイルで利用できる使っていないスペース。
						stock_acc_free =readStr;		//Kodak Free Tagsの一時保存
//						result_Tag = result_Tag + readStr ;
//						String free  = result.substring(startC, endC);
//						dbMsg +=",free=" + free;
//						result_Tag = result_Tag + "\n free;"+ free + ";Unused space available in file." ;
						/*
						Music/Bob Dylan/Desire/01 Hurricane.m4a,
						 */
					}else if(target.equals("frea")){		//写真関連情報										Kodak_frea						ID3ｖ2；--	ID3ｖ3；--
						stock_acc_frea =readStr;		//Kodak frea Tagsの一時保存
						/*
						Music/Bob Dylan/Desire/01 Hurricane.m4a,
						 */
					}else if(target.equals("skip")){		//ファイルで利用できる使っていないスペース。
						stock_acc_skip =readStr;		//Canon Skip Tagsの一時保存
//						String skip  = result.substring(startC, endC);
//						dbMsg +=",skip=" + skip;
//						result_Tag = result_Tag + "\n skip;"+ skip + ";'Unused space available in file" ;
						/*
						Music/Bob Dylan/Desire/01 Hurricane.m4a,
						 */
					}else if(target.equals("wide")){		//以下の原子が2^32バイトを超えるならば、予約のspace―は拡張サイズ・フィールドによって上書きされることができます、以下の原子の内容を置き換えることなく。
//						String wide  = result.substring(startC, endC);
//						dbMsg +=",wide=" + wide;
//						result_Tag = result_Tag + "\n wide;"+ wide + ";Reserved space—can be overwritten by an extended size field if the following atom exceeds 2^32 bytes, without displacing the contents of the following atom." ;
		//
//						startC = endC;
						result_Tag = result_Tag + readStr ;
						/*
						Music/Bob Dylan/Desire/01 Hurricane.m4a,
						 */
					}else if(target.equals("pnot")){		////					//映画プレビュー・データの参照。
						stock_acc_pnot =readStr;		//QuickTime Preview Tagsの一時保存
//						endC = endC + 4+4+4+2+4+2;
//						String pnot  = result.substring(startC, endC);
//						dbMsg +=",pnot=" + pnot;
//						result_Tag = result_Tag + "\n pnot;"+ pnot + ";'Reference to movie preview data." ;
//			/*
//			 * Size					: 4bite :	A 32-bit integer that specifies the number of bytes in this preview atom.
//			 * Type					: 4bite	:	A 32-bit integer that identifies the atom type; this field must be set to 'pnot'.
//			 * Modification date	: 4bite	:	A 32-bit unsigned integer containing a date that indicates when the preview was last updated. The data is in standard Macintosh format.
//			 * Version number		: 2bite	:	A 16-bit integer that must be set to 0.
//			 * Atom type			: 4bite	:	A 32-bit integer that indicates the type of atom that contains the preview data. Typically, this is set to 'PICT' to indicate a QuickDraw picture.
//			 * Atom index			: 2bite	:	A 16-bit integer that identifies which atom of the specified type is to be used as the preview. Typically, this field is set to 1 to indicate that you should use the first atom of the type specified in the atom type field.
//			 * */
						/*
						Music/Bob Dylan/Desire/01 Hurricane.m4a,
						 */
					}else if(target.equals("mvhd")){			//映画（トラック、サンプル・データの位置、などの数とタイプ）についての映画資源メタデータ。映画データがどこで見つかるか、そして、どのようにそれを解釈するべきか述べます。
						result_Tag = result_Tag + readStr ;
//						String moov  = result.substring(startC, endC);
//						dbMsg +=",moov=" + moov;
//						result_Tag = result_Tag + "\n moov;"+ moov.length() + "バイト;Movie resource metadata about the movie (number and type of tracks, location of sample data, and so on). Describes where the movie data can be found and how to interpret it." ;
					}else if(target.equals("mdat-offse")){		//Movie Data Offset				ID3ｖ2；--	ID3ｖ3；--
						result_Tag = result_Tag + readStr ;
	/*
	Music/Bob Dylan/Desire/01 Hurricane.m4a,
	 */
					}else if(target.equals("mdat-size")){		//サイズ											MovieDataSize					ID3ｖ2；TSI	ID3ｖ3；TSIZ;	Size(Size	 Deprecated?)
						result_Tag = result_Tag + readStr ;
						/*
						Music/Bob Dylan/Desire/01 Hurricane.m4a,
						 */
					}else if(target.equals("PICT")){		//付属する画像										PreviewPICT						ID3ｖ2；PIC	ID3ｖ3；APIC	Attached picture
						result_Tag = result_Tag + readStr ;
						/*
						Music/Bob Dylan/Desire/01 Hurricane.m4a,
						 */
					}else if(target.equals("thum")){		//画像のサムネール									ThumbnailImage					ID3ｖ2；--	ID3ｖ3；--
						result_Tag = result_Tag + readStr ;
						/*
						Music/Bob Dylan/Desire/01 Hurricane.m4a,
						 */
					}else if(target.equals("_htc")){		//HTCInfo							ID3ｖ2；--	ID3ｖ3；--
						result_Tag = result_Tag + readStr ;
						/*
						Music/Bob Dylan/Desire/01 Hurricane.m4a,
						 */
					}else if(target.equals("slmt")){		//Unknown_slmt?					ID3ｖ2；--	ID3ｖ3；--
						result_Tag = result_Tag + readStr ;
						/*
						Music/Bob Dylan/Desire/01 Hurricane.m4a,
						 */
					}else if(target.equals("ardt")){		//ARDrone File					ID3ｖ2；--	ID3ｖ3；--
						result_Tag = result_Tag + readStr ;
						/*
						Music/Bob Dylan/Desire/01 Hurricane.m4a,
						 */
					}else if(target.equals("junk")){		//Junk?							ID3ｖ2；--	ID3ｖ3；--
						result_Tag = result_Tag + readStr ;
						/*
						Music/Bob Dylan/Desire/01 Hurricane.m4a,
						 */
					}else if(target.equals("prrt")){		//ARDrone Telemetry				ID3ｖ2；--	ID3ｖ3；--
						result_Tag = result_Tag + readStr ;
						/*
						Music/Bob Dylan/Desire/01 Hurricane.m4a,
						 */
					}else if(target.equals("udta")){		//User Data						ID3ｖ2；--	ID3ｖ3；USER	Terms of use
						stock_acc_udta =readStr;		//FLIR UserData Tagsの一時保存
						/*
						Music/Bob Dylan/Desire/01 Hurricane.m4a,
						 */
					}else if(target.equals("uuid")){		//XMP/UUID-PROF/UUID-Flip/UUID-Unknown?	ID3ｖ2；--	ID3ｖ3；--
						stock_acc_uuid_XMP  =readStr;		//XMP Tagsの一時保存
						stock_acc_uuid_PROF  =null;		//UUID-PROFの一時保存
						stock_acc_uuid_FlipF  =null;		//UUID-Flipの一時保存
						/*
						Music/Bob Dylan/Desire/01 Hurricane.m4a,
						 */
					}else{
						itemReadAacBody( readStr , target);									//AACのQQuickTime ItemList Tags読取り
						dbMsg = null;
					}
				}
			}if(dbMsg != null){
				myLog(TAG,dbMsg);
			}
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}
	}

	/**
	 * QuickTime Movie Tagsの読取り
	 * */
	public void movieReadAac(  ){		//QuickTime Tags.QuickTime Movie Tagsの読取りの読取り準備
		final String TAG = "movieReadAac";
		String dbMsg= "";
		try{
			if (stock_acc_moov != null || result != null){
				stock_acc_moov = stock_acc_moov + result;
				int resultLen = result.length();
				dbMsg= "残り=" + resultLen + "文字";					//newFile.length=11897286
				makAACList(read_AAC_HEAD_Movie);			//QuickTime ItemList TagsをList<String> syougouに作成
				pdMaxVal= kensaku.size();										//result.length();								//プログレス終端値
				dbMsg +=",pdMaxVal=" + pdMaxVal +"項目";
				reqCode = read_AAC_HEAD_Movie ;						//QuickTime Tagsの読取り
				dbMsg +=",reqCode="+reqCode;
				String pdTitol = getApplicationContext().getString(R.string.tag_prog_titol1) +"" + getResources().getString(R.string.common_yomitori);				//
				dbMsg +=",pdTitol="+pdTitol;
				String pdMessage ="AAC ; QuickTime Movie Tags" ; //歌詞を探しています。</string>
				dbMsg +=",pdMessage="+pdMessage;
//				pTask = (plogTask) new plogTask(this ,  this , reqCode , pdTitol ,pdMessage , pdMaxVal ).execute(reqCode,  pdMessage , stock_acc_moov , kensaku );		//,jikkouStep,totalStep,calumnInfo
				plTask.execute(reqCode,kensaku,TagBrows.this.result,null);
//				TagBrows.this.result = Readloop( reqCode,kensaku,TagBrows.this.result);
				dbMsg += ",result="+TagBrows.this.result;
//				metaReadAac(  );		//QuickTime Tags.QuickTime Movie Tagsの読取りの読取り準備
				myLog(TAG,dbMsg);
			}else{
				readEndAac(  );			//呼出し元への戻り処理
			}
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}
	}

	/**
	 * QuickTime Movie Tagsの格納
	 * */
	public void movieReadAacBody( String readStr , String target ){		//QuickTime Tags.QuickTime Movie Tagsの読取りの読取り準備
		final String TAG = "movieReadAacBody";
		String dbMsg= "";
		try{
			dbMsg= "target =" + target + ",";		//"M4A mp42isom".length()
			if ( readStr != null){
				int readInt = readStr.length();
				if(50 < readInt){
					dbMsg +=readStr.substring(0, 50) +  "～" + readStr.substring(readInt-50, readInt);
				}else{
					dbMsg +=readStr;
				}
				dbMsg += "(" + readInt + "/" + result.length() +"文字)";		//"M4A mp42isom".length()
				result_Tag = result_Tag + "\n"+ target + ";" ;
				if(target.equals("meta")){							//ファイルタイプ	File Type		ID3ｖ2；TFT	ID3ｖ3；TFLT	File type
					stock_acc_movie_meta  =readStr;		//										QuickTime Tags.QuickTime Movie Tags.Meta		ID3ｖ2；CRM	ID3ｖ3；？	Encrypted meta frame?
				}
				if(target.equals("cmov")){
					stock_acc_movie_cmov  =readStr;		//										QuickTime Tags.QuickTime Movie Tags.cmov		ID3ｖ2；--	ID3ｖ3；--
				}
				if(target.equals("htka")){
					stock_acc_movie_htka  =readStr;		//										QuickTime Tags.QuickTime Movie Tags.htka		ID3ｖ2；--	ID3ｖ3；--
				}
				if(target.equals("iods")){
					stock_acc_movie_iods  =readStr;		//										QuickTime Tags.QuickTime Movie Tags.iods		ID3ｖ2；--	ID3ｖ3；--
				}
				if(target.equals("mvhd")){
					stock_acc_movie_mvhd  =readStr;		//MovieHeader							QuickTime Tags.QuickTime Movie Tags.mvhd		ID3ｖ2；--	ID3ｖ3；
				}
				if(target.equals("trak")){
					stock_acc_movie_trak  =readStr;		//										QuickTime Tags.QuickTime Movie Tags.trak		ID3ｖ2；--	ID3ｖ3；
				}
				if(target.equals("udta")){
					stock_acc_movie_udta  =readStr;		//User Data								QuickTime Tags.QuickTime Movie Tags.udta		ID3ｖ2；--	ID3ｖ3；USER	Terms of use
				}
				if(target.equals("uuid")){
					stock_acc_movie_uuid  =readStr;		//XMP/UUID-PROF/UUID-Flip/UUID-Unknown?	QuickTime Tags.QuickTime Movie Tags.uuid		ID3ｖ2；--	ID3ｖ3；--
				}
			}
			myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}
	}

	/**
	 * QuickTime Movie Tagsの読取り
	 * */
	public void metaReadAac(  ){		//QuickTime Tags.QuickTime Movie Tagsの読取りの読取り準備
		final String TAG = "metaReadAac";
		String dbMsg= "";
		try{
			if (stock_acc_movie_meta != null || result != null){
				stock_acc_movie_meta = stock_acc_movie_meta + result;
				int resultLen = result.length();
				dbMsg= "残り=" + resultLen + "文字";					//newFile.length=11897286
				makAACList(read_AAC_Movie_Meta);						//QuickTime Tags.QuickTime Meta TagsをList<String> syougouに作成
				pdMaxVal= kensaku.size();										//result.length();								//プログレス終端値
				dbMsg +=",pdMaxVal=" + pdMaxVal +"項目";
				reqCode = read_AAC_Movie_Meta ;									//QuickTime Tags.QuickTime Meta Tagsの読取り
				dbMsg +=",reqCode="+reqCode;
				String pdTitol = getApplicationContext().getString(R.string.tag_prog_titol1) +"" + getResources().getString(R.string.common_yomitori);				//
				dbMsg +=",pdTitol="+pdTitol;
				String pdMessage ="AAC ; QuickTime Movie >> Meta Tags" ; //歌詞を探しています。</string>
				dbMsg +=",pdMessage="+pdMessage;
//				pTask = (plogTask) new plogTask(this ,  this , reqCode , pdTitol ,pdMessage , pdMaxVal ).execute(reqCode,  pdMessage , stock_acc_movie_meta , kensaku );		//,jikkouStep,totalStep,calumnInfo
				plTask.execute(reqCode,kensaku,TagBrows.this.result,null);
//				String result =null;
//				TagBrows.this.result = Readloop( reqCode,kensaku,TagBrows.this.result);
				dbMsg += ",result="+TagBrows.this.result;
//				itemReadAac(  );		//QuickTime ItemList Tagsの読取り準備
			}else{
				readEndAac(  );			//呼出し元への戻り処理
			}
			myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}
	}

	/**
	 * QuickTime Movie Tagsの格納
	 * */
	public void metaReadAacBody( String readStr , String target ){		//QuickTime Tags.QuickTime Movie Tagsの読取りの読取り準備
		final String TAG = "metaReadAacBody";
		String dbMsg= "";
		try{
			if ( readStr != null){
				int readInt = readStr.length();
				if(50 < readInt){
					dbMsg +=readStr.substring(0, 50) +  "～" + readStr.substring(readInt-50, readInt);
				}else{
					dbMsg +=readStr;
				}
				dbMsg += "(" + readInt + "/" + result.length() +"文字)";		//"M4A mp42isom".length()
				result_Tag = result_Tag + "\n"+ target + ";" ;
				if(target.equals("ilst")){
					stock_acc_meta_ilst  =readStr;		//										QuickTime Tags.QuickTime Meta Tags.ilst			ID3ｖ2；--	ID3ｖ3；--
				}
				if(target.equals("bxml")){
					stock_acc_meta_bxml  =readStr;		//BinaryXML?							QuickTime Tags.QuickTime Meta Tags.bxml			ID3ｖ2；--	ID3ｖ3；--
				}
				if(target.equals("dinf")){
					stock_acc_meta_dinf  =readStr;		//DataInformation?						QuickTime Tags.QuickTime Meta Tags.dinf			ID3ｖ2；--	ID3ｖ3；--
				}
				if(target.equals("free")){
					stock_acc_meta_free  =readStr;		//Kodak Free?	/Free?					QuickTime Tags.QuickTime Meta Tags.free			ID3ｖ2；--	ID3ｖ3；--
				}
				if(target.equals("hdlr")){
					stock_acc_meta_hdlr  =readStr;		//Handler								QuickTime Tags.QuickTime Meta Tags.hdlr			ID3ｖ2；--	ID3ｖ3；--
				}
				if(target.equals("iinf")){
					stock_acc_meta_iinf  =readStr;		//ItemInformation?						QuickTime Tags.QuickTime Meta Tags.iinf			ID3ｖ2；--	ID3ｖ3；--
				}
				if(target.equals("iloc")){
					stock_acc_meta_iloc  =readStr;		//ItemLocation?q						QuickTime Tags.QuickTime Meta Tags.iloc			ID3ｖ2；--	ID3ｖ3；--
				}
				if(target.equals("ipmc")){
					stock_acc_meta_ipmc  =readStr;		//IPMPControl?							QuickTime Tags.QuickTime Meta Tags.ipmc			ID3ｖ2；--	ID3ｖ3；--
				}
				if(target.equals("ipro")){
					stock_acc_meta_ipro  =readStr;		//ItemProtection?						QuickTime Tags.QuickTime Meta Tags.ipro			ID3ｖ2；--	ID3ｖ3；--
				}
				if(target.equals("keys")){
					stock_acc_meta_keys  =readStr;		//Keys									QuickTime Tags.QuickTime Meta Tags.keys			ID3ｖ2；--	ID3ｖ3；--
				}
				if(target.equals("pitm")){
					stock_acc_meta_pitm  =readStr;		//PrimaryItemReference?					QuickTime Tags.QuickTime Meta Tags.pitm			ID3ｖ2；--	ID3ｖ3；--
				}
				if(target.equals("xml ")){
					stock_acc_meta_xml  =readStr;		//XML					QuickTime Tags.QuickTime Meta Tags.xml 			ID3ｖ2；--	ID3ｖ3；--
				}
			}
			myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}
	}

	/*
	* QuickTime ItemList Tagsの読取り
	 * */
	public void itemReadAac(  ){		//QuickTime ItemList Tagsの読取り準備
		final String TAG = "itemReadAac";
		String dbMsg= "";
		try{
			if (stock_acc_meta_ilst != null || result != null ){
				stock_acc_meta_ilst = stock_acc_meta_ilst + result;
				int resultLen = result.length();
				dbMsg= "残り=" + resultLen + "文字";					//newFile.length=11897286
				makAACList(read_AAC_ITEM);			//QuickTime ItemList TagsをList<String> syougouに作成
				pdMaxVal= kensaku.size();										//result.length();								//プログレス終端値
				dbMsg +=",pdMaxVal=" + pdMaxVal +"項目";
				reqCode = read_AAC_ITEM ;						//QuickTime Tagsの読取り
				dbMsg +=",reqCode="+reqCode;
				String pdTitol = getApplicationContext().getString(R.string.tag_prog_titol1) +"" + getResources().getString(R.string.common_yomitori);				//
				dbMsg +=",pdTitol="+pdTitol;
				String pdMessage ="AAC ; QuickTime QuickTime Movie >> Meta Tags >> ItemList Tags" ; //歌詞を探しています。</string>
				dbMsg +=",pdMessage="+pdMessage;
//				pTask = (plogTask) new plogTask(this ,  this , reqCode , pdTitol ,pdMessage , pdMaxVal ).execute(reqCode,  pdMessage , stock_acc_meta_ilst , kensaku );		//,jikkouStep,totalStep,calumnInfo
				plTask.execute(reqCode,kensaku,TagBrows.this.result,null);
//				String result =null;
//				TagBrows.this.result = Readloop( reqCode,kensaku,TagBrows.this.result);
//				////				List<String> kensaku=(List<String>) params[3] ;													//3.検索するフレーム名, kensaku
////				pdMaxVal = kensaku.size();
////				dbMsg +=", kensaku = " + pdMaxVal + "項目" ;
////				myLog(TAG,dbMsg);
////				for(int i = 0; i < pdMaxVal ; i++){
////					dbMsg= reqCode + ";" + i + "/ " + pdMaxVal +")" ;
////					String freamName = kensaku.get(i);
////					dbMsg +=freamName + ";";
////					int sInt = TagBrows.this.result.length();
////					dbMsg +="残り" + sInt + "文字";
////					if(freamName.equals("USLT") || freamName.equals("USLT")){
////						pdMessage =getApplicationContext().getString(R.string.tag_prog_msg1) + " ; " + freamName;		//歌詞を探しています。
////					} else {
////						pdMessage =getApplicationContext().getString(R.string.tag_prog_msg2) + " ; " + freamName;		//その他の書き込みを検索しています。
////					}
////					TagBrows.this.result = getTargetFream( TagBrows.this.result , freamName , reqCode);			//<UNSYNCED LYRICS>	非同期 歌詞/文書のコピー	渡された文字列から指定されたフレームを切り出す
//////					pdCoundtVal = i + 1 ;
//////					//		pdCoundtVal = result2.length();
////					if( TagBrows.this.result_USLT != null ||  TagBrows.this.result_SYLT != null){			//歌詞情報が取得できたところで
////						dbMsg +="result=null";
////						i = pdMaxVal;
////					}
////					int eInt = TagBrows.this.result.length();
////					dbMsg += ">>" + eInt + "文字(処理" + (sInt - eInt ) + "文字)";
////					myLog(TAG,dbMsg);
////				}
//				readEndAac(  );
				/////pTask
			}else{
				readEndAac(  );			//呼出し元への戻り処理
			}
			myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}
	}

	/**
	 * AACのQuickTime ItemList Tags読取り
	 * */
	public void itemReadAacBody(String readStr , String target){		//AACのQQuickTime ItemList Tags読取り
		final String TAG = "itemReadAacBody";
		String dbMsg= "";
		try{
			dbMsg= "target=" + target +",";		//"M4A mp42isom".length()
			if ( readStr != null){
				if (target.equals("covr")) {
				}else{
					int readInt = readStr.length();
					if(60 < readInt){
						dbMsg +=readStr.substring(0, 40) +  "～" + readStr.substring(readInt-20, readInt);
					}else{
						dbMsg +=readStr;
					}
					dbMsg += "(" + readInt + "/" + result.length() +"文字)";		//"M4A mp42isom".length()
				int endInt = readStr.lastIndexOf("data");
				dbMsg += 	",dataの開始=" + endInt ;
				if(0 < endInt ){				//"data"が有れば
					endInt = endInt + 8;		//"data"の文字数+αをシフトして
					endInt = retEndNullPoint( readStr , endInt );				//渡された文字列の先頭のnullが無くなるポイントを返す
					char[] testChar = readStr.substring(0, endInt + 1 ).toCharArray();
					dbMsg += 	",testChar=" + testChar[endInt] ;
					if( testChar[endInt] == 1 ){
						endInt = retEndNullPoint( readStr , endInt + 2 );				//渡された文字列の先頭のnullが無くなるポイントを返す
					}
				} else {
					endInt = readStr.lastIndexOf(target) + target.length() + 8;
				}
				dbMsg += 	",endInt=" + endInt ;
//				int startInt = readStr.lastIndexOf("data");				//渡された文字列の先頭のnullが無くなるポイントを返す
				String sentouMoji = readStr.substring( 0, endInt );
				dbMsg += 	",sentouMoji=" + sentouMoji ;
				dbMsg += ",buffer" ;
				char[] testChar = sentouMoji.toCharArray();
				for(int i = 0 ; i < endInt ; i++){
					dbMsg += 	",[" + i + "]=" + testChar[i];
					String testStr =  "0x" + Integer.toHexString(testChar[i]);
					dbMsg += 	"=" + testStr;
				}
				int startInt = endInt;					//retEndNullPoint( readStr , endInt );				//渡された文字列の先頭のnullが無くなるポイントを返す
				dbMsg += ",retEndNullPoint=" + startInt ;
				readStr = readStr.substring(startInt);						//	☆現物合わせ		readStr.substring(28, readStr.length());
				/*Tag ID's beginning with the copyright symbol (hex 0xa9) are multi-language text.
				 * 		UInt8 = 0xA9 // © (copyright sign)で始まれば多言語テキスト
				 * Alternate language tags are accessed by adding a dash followed by the language/country code to the tag name.
				 * 		タグ名に言語/国名コードが続くダッシュを加えることによって、交互の言語タグはアクセスされます。
				 * ExifTool will extract any multi-language user data tags found, even if they don't exist in this table.
				 * 		ExifTool will extract any multi-language user data tags found, even if they don't exist in this table.
				 * */
			//	if( target.startsWith("©") ){									//マルチバイト文字が入っていたらtrueを返す
					if( isMultiByte(readStr)){
						readStr = getEncordStr( 3 , motoEncrod ,readStr);			//"UTF-8"に変換；フレームに設定されたエンコードフラグを読み、変換した文字を返す
					}
			//	}
				readInt = readStr.length();
				if(50 < readInt){
					dbMsg +=">>" + readStr.substring(0, 20) +  "～" + readStr.substring(readInt-20, readInt);
				}else{
					dbMsg +=">>" + readStr;
				}
				dbMsg += "(" + readInt + "/" + result.length() +"文字)";
					 }

			if(target.equals("©lyr")){
				result_USLT = readStr;					//©lyr			//非同期 歌詞/文書のコピー								Lyrics							ID3ｖ2；ULT	ID3ｖ3；USLT	Unsychronized lyric/text transcription
			}else if(target.equals("covr")){
				result_APIC =target + ";"+ "binary data";					//covr			//付属する画像											CoverArt						ID3ｖ2；PIC	ID3ｖ3；APIC	Attached picture
			}else if(target.equals("©gen")){
				result_TCON = target + ";"+  readStr;	//gnre			//ジャンル												Genre	Content type			ID3ｖ2；TCO	ID3ｖ3；TCON
			}else if(target.equals("©ART")){
				result_TPE1 = target + ";"+  readStr;	//©ART			//アーティスト																			ID3ｖ2；TP1	ID3ｖ3；TPE1	ARTIST[1]
			}else if(target.equals("©cpy") || target.equals("cprt")){
				result_TCOP = target + ";"+ readStr;	//©cpy	cprt	//著作権情報	権利元				Copyright	Copyright								ID3ｖ2；TCR	ID3ｖ3；TCOP	Copyright message
			}else if(target.equals("tagc")){	//Media characteristic optionally present in Track user data—specialized text that describes something of interest about the track. For more information, see Media Characteristic Tags.
				dbMsg += ",tagc=" + readStr;		//
			}else if(target.equals("mvbr")){	//Media characteristic optionally present in Track user data—specialized text that describes something of interest about the track. For more information, see Media Characteristic Tags.
				dbMsg += ",mvbr=" + readStr;
			}else if(target.equals("tagc")){
				dbMsg += ",tagc=" + readStr;		////Media characteristic optionally present in Track user data—specialized text that describes something of interest about the track. For more information, see Media Characteristic Tags.
			}else if(target.equals("dscｖ")){
				dbMsg += ",cdsc=" + readStr;		//Track reference types		TThe track reference is contained in a timed metadata track (see Timed Metadata Media for more detail) and provides links to the tracks for which it contains descriptive characteristics.
//			}else if(target.equals("mdhd")){
//				dbMsg += ",mdhd=" + readStr;		//Media Header Atoms 	28バイト目にLanguage
//			}else if(target.equals("hdlr")){
//				dbMsg += ",hdlr=" + readStr;		//Handler Reference Atoms　 	Component type/Component subtype
			}else if(target.equals("elng")){
				dbMsg += ",elng=" + readStr;		//Extended Language Tag Atom

			}else if(target.equals("©alb") || target.equals("albm")){
				result_TALB = target + ";"+ readStr;	//©alb	albm	//アルバム/映画/ショーのタイトル						Album/Movie/Show title			ID3ｖ2；TAL	ID3ｖ3；TALB
			}else if(target.equals("©cmt")){
				result_COMR = target + ";"+ readStr;	//©cmt			//コメント												Comment							ID3ｖ2；COM	ID3ｖ3；COMM	Comments
			}else if(target.equals("©com") || target.equals("©wrt")){
				result_TCOM = target + ";"+ readStr;	//©com	©wrt	//作曲者																				//10cc(2)
			}else if(target.equals("©day") || target.equals("yrrc")){
				result_TYER = target + ";"+ readStr;	///©day	yrrc			//年	レコーディング年/年*15	Date*16	Year Deprecated	Year		Year				ID3ｖ2；TYE	ID3ｖ3；TYER
			}else if(target.equals("©too") || target.equals("©enc")){
				result_TENC = target + ";"+ readStr;	//©too	//©enc?	//エンコーディング ソフトウェア							EncodedBy						ID3ｖ2；TEN	ID3ｖ3；TENC	Encoded by
			}else if(target.equals("gnre")){
				result_TCON = target + ";"+ readStr;	//gnre			//ジャンル												Genre	Content type			ID3ｖ2；TCO	ID3ｖ3；TCON
			}else if(target.equals("©grp")){
				result_TIT1 = target + ";"+ readStr;//				グループ												CONTENTGROUP					ID3ｖ2；?-	ID3ｖ3；TIT1
			}else if(target.equals("©pub")){
				result_TPUB  = target + ";"+ readStr;	//©pub			//出版社/発行元											Publisher						ID3ｖ2；TPB	ID3ｖ3；TPUB	Publisher
			}else if(target.equals("©trk") || target.equals("trkn")){
				result_TRCK= target + ";"+ readStr;	//©trk	trkn	トラックの番号/セット中の位置							Track							ID3ｖ2；TRK	ID3ｖ3；TRCK	TrackNumber	Track number/Position in set
			}else if(target.equals("©pti") || target.equals("soal")){
				result_TOAL =target + ";"+ readStr;		//@pti	soal	//原題													Parent Title					ID3ｖ2；--	ID3ｖ3；TOAL	//Original album/movie/show title?
			}else if(target.equals("aART") || target.equals("soaa")){
				result_TPE2 =target + ";"+ readStr;			//aART	soaa		//アルバムアーティスト									ALBUMARTIST						ID3ｖ2；--	ID3ｖ3；TPE2	Lead artist(s)/Lead performer(s)/Soloist(s)/Performing group
			}else if(target.equals("atID")){
				result_MCDI =target + ";"+ readStr;				//atID			//アーティスト識別子	編集不可						Album Title ID					ID3ｖ2；MCI?ID3ｖ3；MCDI?	Music CD Identifier?
			}else if(target.equals("desc") || target.equals("dscp") || target.equals("titl")){
				result_TIT2=target + ";"+ readStr;						//desc	dscp	titl	//タイトル/曲名/内容の説明	タイトル					Description						ID3ｖ2；TT2	ID3ｖ3；TIT2;	Track Title	Title/Title/Songname/Content description
			}else if(target.equals("disk")){
				result_TPOS=target + ";"+ readStr;						//disk			//ディスクナンバー										DiskNumber	Part of a set		ID3ｖ2；TPA	ID3ｖ3；TPOS	DISCNUMBER/TOTALDISC
			}else if(target.equals("ldes") || target.equals("sonm")){
				result_TIT3=target + ";"+ readStr;							//ldes			//サブタイトル/説明の追加情報							Long Description				ID3ｖ2；--	ID3ｖ3；TIT3;	Subtitle/Description refinement?
																			//sonm");	//タイトル（読み）											Sort Name						ID3ｖ2；--	ID3ｖ3；TIT3		TITLESORT
			}else if(target.equals("perf")){
				result_TPE3 =target + ";"+ readStr;								//perf			//指揮者/演奏者詳細情報									Performer						ID3ｖ2；TP3	ID3ｖ3；TPE3	Conductor/Performer refinement
			}else if(target.equals("rate")){
				result_POPM =target + ";"+ readStr;									//rate			//人気メーター ?										RatingPercent					ID3ｖ2；POP	ID3ｖ3；POPM	Popularimeter
			}else if(target.equals("rldt")){
				result_TORY =target + ";"+ readStr;									//rldt			//オリジナルのリリース年?								Release Date					ID3ｖ2；TOR	ID3ｖ3；TORY	Original release year
			}else if(target.equals("stik")){
				result_TMED =target + ";"+ readStr;										//stik			//メディアの種類の明示									MediaType						ID3ｖ2；TMT?ID3ｖ3；TMED?	ITUNESMEDIATYPE
			}else if(target.equals("tmpo")){
				result_TBPM  =target + ";"+ readStr;							//tmpo			//一分間の拍数											BeatsPerMinute					ID3ｖ2；TBP	ID3ｖ3；TBPM	BPM (Beats Per Minute)
			}else if(target.equals("©nam")){
				result_TSOA =target + ";"+ readStr;							//©nam			//アルバム（読み）										ALBUMSORT						ID3ｖ2；--	ID3ｖ3；TSOA
			}else if(target.equals("soar")){
				result_TSOP =target + ";"+ readStr;							//soar			//アーティスト（読み）									ARTISTSORT						ID3ｖ2；--	ID3ｖ3；TSOP
			}else if(target.equals("soco")){
				result_TSOC =target + ";"+ readStr;						//soco			//作曲者（読み）										SortComposer					ID3ｖ2；?-	ID3ｖ3；TSOC	COMPOSERSORT
			}else if(target.equals("©des")){
				result_des  =target + ";"+ readStr;						//©des			//説明													Description	Track				ID3ｖ2；--	ID3ｖ3；--		SUBTITLE
			}else if(target.equals("©nrt")){
				result_nrt =target + ";"+ readStr;							//©nrt			//														Narrator						ID3ｖ2；--	ID3ｖ3；--
			}else if(target.equals("----")){
				result_iTunesInfo =target + ";"+ readStr;							//----			//														iTunesInfo						ID3ｖ2；--	ID3ｖ3；--
			}else if(target.equals("@PST")){
				result_PST  =target + ";"+ readStr;							//@PST			//														Parent Short Title				ID3ｖ2；--	ID3ｖ3；--
			}else if(target.equals("@ppi")){
				result_ppi =target + ";"+ readStr;							//@ppi			//														Parent ProductID				ID3ｖ2；--	ID3ｖ3；--
			}else if(target.equals("sti")){
				result_sti =target + ";"+ readStr;							//@sti			//														Short Title						ID3ｖ2；--	ID3ｖ3；--
			}else if(target.equals("AACR")){
				result_AACR =target + ";"+ readStr;							//");	//														Unknown_AACR?					ID3ｖ2；--	ID3ｖ3；--
			}else if(target.equals("CDEK")){
				result_CDEK  =target + ";"+ readStr;							//CDEK");	//														Unknown_CDEK?					ID3ｖ2；--	ID3ｖ3；--
			}else if(target.equals("CDET")){
				result_CDET =target + ";"+ readStr;						//CDET");	//														Unknown_CDET?					ID3ｖ2；--	ID3ｖ3；--
			}else if(target.equals("GUID")){
				result_GUID  =target + ";"+ readStr;						//GUID");	//														GUID							ID3ｖ2；--	ID3ｖ3；--
			}else if(target.equals("VERS")){
				result_VERS =target + ";"+ readStr;						//VERS");	//														ProductVersion					ID3ｖ2；--	ID3ｖ3；--
			}else if(target.equals("akID")){
				result_akID =target + ";"+ readStr;						//akID");	//アカウントの種類			編集不可					Apple Store Account Type		ID3ｖ2；--	ID3ｖ3；--
			}else if(target.equals("apID")){
				result_apID  =target + ";"+ readStr;					//apID");	//アカウント情報					ITUNESACCOUNT		Apple Store Account				ID3ｖ2；--	ID3ｖ3；--
			}else if(target.equals("auth")){
				result_auth =target + ";"+ readStr;					//auth");	//														Author							ID3ｖ2；--	ID3ｖ3；--
			}else if(target.equals("catg")){
				result_catg =target + ";"+ readStr;						//catg");	//ポッドキャストカテゴリ								Category						ID3ｖ2；--	ID3ｖ3；--		PODCASTCATEGORY
			}else if(target.equals("cnID")){
				result_cnID=target + ";"+ readStr;						//cnID");	//コンテンツ識別子										AppleStoreCatalogID				ID3ｖ2；--	ID3ｖ3；--		ITUNESCATALOGID
			}else if(target.equals("cpil")){
				result_cpil =target + ";"+ readStr;	//cpil");	//コンピレーションの明示								Compilation						ID3ｖ2；--	ID3ｖ3；--		COMPILATION
			}else if(target.equals("egid")){
				result_egid =target + ";"+ readStr;			//egid");	//ポッドキャストエピソードユニークID					Episode Global Unique ID		ID3ｖ2；--	ID3ｖ3；--		PODCASTID
			}else if(target.equals("geID")){
				result_geID =target + ";"+ readStr;				//geID");	//ジャンル識別子		編集不可						GenreID							ID3ｖ2；--	ID3ｖ3；--
			}else if(target.equals("grup")){
				result_grup =target + ";"+ readStr;					//grup");	//														Grouping						ID3ｖ2；--	ID3ｖ3；--
			}else if(target.equals("gshh")){
				result_gshh =target + ";"+ readStr;					//gshh");	//														GoogleHostHeade					ID3ｖ2；--	ID3ｖ3；--
			}else if(target.equals("gspm")){
				result_gspm =target + ";"+ readStr;					//gspm");	//														GooglePingMessage				ID3ｖ2；--	ID3ｖ3；--
			}else if(target.equals("gspu")){
				result_gspu=target + ";"+ readStr;					//gspu");	//														GooglePingURL					ID3ｖ2；--	ID3ｖ3；--
			}else if(target.equals("gssd")){
				result_gssd =target + ";"+ readStr;						//gssd");	//														GoogleSourceData				ID3ｖ2；--	ID3ｖ3；--
			}else if(target.equals("gsst")){
				result_gsst =target + ";"+ readStr;						//gsst");	//														GoogleStartTime					ID3ｖ2；--	ID3ｖ3；--
			}else if(target.equals("gstd")){
				result_gstd =target + ";"+ readStr;						//gstd");	//														GoogleTrackDuration				ID3ｖ2；--	ID3ｖ3；--
			}else if(target.equals("hdvd")){
				result_hdvd  =target + ";"+ readStr;						//hdvd");	//	?													HDVideo	?						ID3ｖ2；--	ID3ｖ3；--
			}else if(target.equals("hdtv")){
				result_hdvd =target + ";"+ readStr;						//hdtv");	//ビデオ解像度の明示		ITUNESHDVIDEO				HDVideo							ID3ｖ2；--	ID3ｖ3；--
			}else if(target.equals("itnu")){
				result_itnu =target + ";"+ readStr;						//itnu");	//														iTunesU							ID3ｖ2；--	ID3ｖ3；--
			}else if(target.equals("keyw")){
				result_keyw =target + ";"+ readStr;						//keyw");	//ポッドキャストキーワード		編集不可				Keyword							ID3ｖ2；--	ID3ｖ3；--
			}else if(target.equals("pcst")){
				result_pcst =target + ";"+ readStr;						//pcst");	//ポッドキャストであることを明示						Podcast							ID3ｖ2；--	ID3ｖ3；--
			}else if(target.equals("pgap")){
				result_pgap =target + ";"+ readStr;					//pgap");	//ギャップレスコンテンツの明示							PlayGap							ID3ｖ2；--	ID3ｖ3；--		ITUNESGAPLESS
			}else if(target.equals("plID")){
				result_plID =target + ";"+ readStr;						//plID");	//プレイリスト（アルバム）識別子	編集不可			PlayListID						ID3ｖ2；--	ID3ｖ3；--
			}else if(target.equals("prID")){
				result_prID =target + ";"+ readStr;						//prID");	//														ProductID						ID3ｖ2；--	ID3ｖ3；--
			}else if(target.equals("purd")){
				result_purd =target + ";"+ readStr;							//purd");	//購入日												ITUNESPURCHASEDATE				ID3ｖ2；--	ID3ｖ3；--		ITUNESPURCHASEDATE
			}else if(target.equals("purl")){
				result_purl =target + ";"+ readStr;					//purl");	//ポッドキャストURL										Podcast URL						ID3ｖ2；--	ID3ｖ3；--		PODCASTURL
			}else if(target.equals("rtng")){
				result_rtng  =target + ";"+ readStr;					//rtng");	//保護者のためのレートの明示?番組?番組（読み）			Rating?	TVSHOW?					ID3ｖ2；--	ID3ｖ3；--		ITUNESADVISORY?	TVSHOW?
			}else if(target.equals("sfID")){
				result_sfID =target + ";"+ readStr;				//sfID");	//ストアの国				編集不可					AppleStore Country				ID3ｖ2；--	ID3ｖ3；--
			}else if(target.equals("sosn")){
				result_sosn =target + ";"+ readStr;					//sosn");	//														Sort Show						ID3ｖ2；--	ID3ｖ3；--
			}else if(target.equals("tven")){
				result_tven =target + ";"+ readStr;						//tven");	//エピソードID											TVEpisodeID						ID3ｖ2；--	ID3ｖ3；--
			}else if(target.equals("tves")){
				result_tves =target + ";"+ readStr;							//tves");	//														TVEpisode						ID3ｖ2；--	ID3ｖ3；--
			}else if(target.equals("tvnn")){
				result_tvnn =target + ";"+ readStr;							//tvnn");	//放送局												TVNetworkName					ID3ｖ2；--	ID3ｖ3；--
			}else if(target.equals("tvsh")){
				result_tvsh =target + ";"+ readStr;							//tvsh");	//														TVShow							ID3ｖ2；--	ID3ｖ3；--
			}else if(target.equals("tvsn")){
				result_tvsn =target + ";"+ readStr;							//tvsn");	//シーズン												TVSeason						ID3ｖ2；--	ID3ｖ3；--
			}

			}
			myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}
	}


	/**
	 * エンコードを返す
	 * 	Well-Known Types	https://developer.apple.com/library/mac/documentation/QuickTime/QTFF/Metadata/Metadata.html#//apple_ref/doc/uid/TP40000939-CH1-SW35
	 * */
	public String retAacEncord( int Code ){
		String retStr =null;
		final String TAG = "retAacEncord";
		String dbMsg= "";
		try{
			switch(Code) {
			case 1:
				retStr = "UTF-8";		//Without any count or NULL terminator
				break;
			case 2:
				retStr = "UTF-16";		//Also known as UTF-16BE
				break;
			case 3:
				retStr = "S/JIS";		//Deprecated unless it is needed for special Japanese characters
				break;
			case 4:
				retStr = "UTF-8 sort";		//Variant storage of a string for sorting only
				break;
			case 5:
				retStr = "UTF-16 sort";		//Variant storage of a string for sorting only
				break;
			case 13:
				retStr = "JPEG";		//In a JFIF wrapper
				break;
			case 14:
				retStr = "PNG";		//In a PNG wrapper
				break;
			case 21:
				retStr = "BE Signed Integer";		//A big-endian signed integer in 1,2,3 or 4 bytes
													//Note: This data type is not supported in Timed Metadata Media. Use one of the fixed-size signed integer data types (that is, type codes 65, 66, or 67) instead.
				break;
			case 22:
				retStr = "BE Unsigned Integer";		//A big-endian unsigned integer in 1,2,3 or 4 bytes; size of value determines integer size
													//				Note: This data type is not supported in Timed Metadata Media. Use one of the fixed-size unsigned integer data types (that is, type codes 75, 76, or 77) instead.
				break;
			case 23:
				retStr = "BE Float32";		//A big-endian 32-bit floating point value (IEEE754)
				break;
			case 24:
				retStr = "BE Float64";		//A big-endian 64-bit floating point value (IEEE754)
				break;
			case 27:
				retStr = "BMP";		//Windows bitmap format graphics
				break;
			case 28:
				retStr = "QuickTime Metadata atom";		//A block of data having the structure of the Metadata atom defined in this specification
				break;
			case 65:
				retStr = "8-bit Signed Integer";		//An 8-bit signed integer
				break;
			case 66:
				retStr = "BE 16-bit Signed Integer";		//A big-endian 16-bit signed integer
				break;
			case 67:
				retStr = "BE 32-bit Signed Integer";		//A big-endian 32-bit signed integer
				break;
			case 70:
				retStr = "BE PointF32";		//A block of data representing a two dimensional (2D) point with 32-bit big-endian floating point x and y coordinates. It has the structure:
											//				struct { BEFloat32 x; BEFloat32 y; }
				break;
			case 71:
				retStr = "BE DimensionsF32";		//A block of data representing 2D dimensions with 32-bit big-endian floating point width and height. It has the structure:
													//				struct { BEFloat32 width; BEFloat32 height; }
				break;
			case 72:
				retStr = "BE RectF32";		//A block of data representing a 2D rectangle with 32-bit big-endian floating point x and y coordinates and a 32-bit big-endian floating point width and height size. It has the structure:
											//				struct { BEFloat32 x; BEFloat32 y; BEFloat32 width; BEFloat32 height;}
											//				or the equivalent structure:
											//				struct { PointF32 origin; DimensionsF32 size; }
				break;
			case 74:
				retStr = "BE 64-bit Signed Integer";		//A big-endian 64-bit signed integer
				break;
			case 75:
				retStr = "8-bit Unsigned Integer";		//An 8-bit unsigned integer
				break;
			case 76:
				retStr = "BE 16-bit Unsigned Integer";		//A big-endian 16-bit unsigned integer
				break;
			case 77:
				retStr = "BE 32-bit Unsigned Integer";		//A big-endian 32-bit unsigned integer
				break;
			case 78:
				retStr = "BE 64-bit Unsigned Integer";		//A big-endian 64-bit unsigned integer
				break;
			case 79:
				retStr = "AffineTransformF64";		//A block of data representing a 3x3 transformation matrix. It has the structure:
													//				struct { BEFloat64 matrix[3][3]; }
				break;
			default:
				retStr = "ISO-8859-1";
				break;
			}
			myLog(TAG,dbMsg);
			/*
			 * 	User Data Text Strings and Language Codes		https://developer.apple.com/library/mac/documentation/QuickTime/QTFF/QTFFChap2/qtff2.html#//apple_ref/doc/uid/TP40000939-CH204-58499
			 * */
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}
		return retStr;
	}


	/**
	 * 呼出し元への戻り処理
	 * */
	public void readEndAac(  ){
		result =null;
		final String TAG = "readEndAac";
		String dbMsg= "";
		try{
			if(result != null){
				int resultLen = result.length();
				dbMsg= "残り=" + resultLen + "文字";					//newFile.length=11897286
			}
			back2Activty();			//呼び出しの戻り処理
			myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}
	}
///WMA/////////////////////////////////////////////////////////////////////////////////////////////////////AAC////
	/**
	 *リファレンス
	 * 	Windows Media Audio コーデック について				https://msdn.microsoft.com/ja-jp/library/dd148594.aspx
	 *	WAVE波形ファイルフォーマット						http://wisdom.sakura.ne.jp/system/winapi/media/mm10.html
	 *		ファイルを覗いてみる							http://norte.coron.jp/sound/sound.html
	 *		WAV ファイルフォーマット						http://www.kk.iij4u.or.jp/~kondo/wave/index.html
	 * 	コーデックのバージョン								https://blogs.msdn.microsoft.com/windows_multimedia_jp/2010/01/12/windows-media/	WMP11以降	Format Tag0x161;WMA9.2		0x162;WMA10 Pro		0x163;WMA 9.2 Loseless
	 *														http://tomorrowscode.blogspot.jp/2010/02/javaiorandomaccessfilereadlineutf-8.html
	 *		Attribute List									https://msdn.microsoft.com/en-us/library/dd743066(VS.85).aspx
	 *キーワード
	 *	ファイルシステム上はUTF-16LE//
	 *	RIFFタグ;ファイル先頭付近に置かれたinfoチャンク（システムデフォルトコード、日本語WinならシフトJIS）
	 *	id3チャンク（ID3v2.3、utf16が主流）
	 *	RIFF（Resource Interchange File Format）
	 *	LPCSTR	>> const char *	>>	string		NULL で終わる変更できないマルチバイト文字列へのポインタ			WMP Tag Plusで使用
	 *	LPCWSTR	>> const wchar_t *	string		NULL で終わる変更できないワイド文字列へのポインタ
	 *サンプル
	 *	ASF（wmv,wma） ファイルフォーマット					http://uguisu.skr.jp/Windows/format_asf.html
	 *	アカベコマイリ										http://akabeko.me/blog/memo/asf/objects/#asffile_content_description
	 *	Delphi 6 ローテクTips >ID3タグの取得と書き込み		http://drang.s4.xrea.com/program/tips/id3tag/id3tag_write.html
	 *	Supported Encodings									http://docs.oracle.com/javase/1.5.0/docs/guide/intl/encoding.doc.html
	 *	WMP Tag Plus										http://bmproductions.fixnum.org/wiki/Tag_Support_API
	 *	さぁ、Waveファイルをいじってみよう					http://nals.main.jp/cms/?p=146
	 *アプリ紹介
	 *	説明；mediamonkey.うぃき							http://mediamonkey.xn--m8jfw.jp/index.php?FAQ#za29b48b
	 *xWMA													https://msdn.microsoft.com/ja-jp/library/cc308025(v=vs.85).aspx
	 * */

/** Attributes					https://msdn.microsoft.com/en-us/library/windows/desktop/dd743061%28v=vs.85%29.aspx?f=255&MSPPError=-2147217396
 * Attribute List				https://msdn.microsoft.com/en-us/library/windows/desktop/dd743066(v=vs.85).aspx
 * WM/Lyrics					https://msdn.microsoft.com/en-us/library/windows/desktop/dd757943%28v=vs.85%29.aspx?f=255&MSPPError=-2147217396
 * WM/Lyrics_Synchronised		https://msdn.microsoft.com/en-us/library/windows/desktop/dd757944(v=vs.85).aspx
 *
 * Resource Interchange File Format Services	https://msdn.microsoft.com/en-us/library/ms713231.aspx
 * */
	public List<String> wmaCoreItem;						//WMAで検索するアイテム
	public List<String> wmaItem;							//具体的なアイテム
	public List<String> id32CoreItem;						//ID3v2の検索対象アイテム
	public List<String> id32Item;							//ID3v2の全アイテム
	public List<String> id33CoreItem;						//ID3v3の検索対象アイテム
	public List<String> id33Item;							//ID3v3の全アイテム
	public String umekomiTag = null;						//埋め込まれたいる別のタグ
	public String result_Writer =null;					//WM/Writer
	public String result_Engineer =null;					//WM/Engineer");							//												Engineer						ID3ｖ2；--	v3；TIPL	v4:IPLS	qt;--
	public String result_Producer =null;					//WM/Producer");							//制作者										Producer						ID3ｖ2；--	v3；TIPL	v4:IPLS	qt;--
	public String result_Mixer =null;						//WM/Mixer");							//												Mixer							ID3ｖ2；--	v3；TIPL	v4:IPLS	qt;--
	public String result_CatalogNo =null;					//WM/CatalogNo");						//ユーザー定義文字情報フレーム			User defined text information frame		ID3ｖ2；TXX	v3；TXXX			qt;--
	public String result_Album_Status =null;				//MusicBrainz/Album Status");			//												Release Status					ID3ｖ2；TXX	v3；TXXX			qt;--
	public String result_Album_Type =null;				//MusicBrainz/Album Type");				//												Release Type					ID3ｖ2；TXX	v3；TXXX			qt;--
	public String result_Album_Release_Country =null;		//MusicBrainz/Album Release Country");	//												Release Country					ID3ｖ2；TXX	v3；TXXX			qt;--
	public String result_Script =null;					//WM/Script");							//												Release Country					ID3ｖ2；TXX	v3；TXXX			qt;--
	public String result_Barcode =null;					//WM/Barcode");							//												Barcode							ID3ｖ2；TXX	v3；TXXX			qt;--
	public String result_Release_Track_Id =null;			//MusicBrainz/Release Track Id");		//												MusicBrainz Release Id			ID3ｖ2；TXX	v3；TXXX			qt;--
	public String result_Artist_Id =null;					//MusicBrainz/Artist Id");				//												MusicBrainz Artist Id			ID3ｖ2；TXX	v3；TXXX			qt;--
	public String result_Album_Artist_Id =null;			//MusicBrainz/Album Artist Id");			//												MusicBrainz Release Artist Id	ID3ｖ2；TXX	v3；TXXX			qt;--
	public String result_Release_Group_Id =null;			//MusicBrainz/Release Group Id");		//												MusicBrainz Release Group Id	ID3ｖ2；TXX	v3；TXXX			qt;--
	public String result_Work_Id =null;					//MusicBrainz/Work Id");					//												MusicBrainz Work Id				ID3ｖ2；TXX	v3；TXXX			qt;--
	public String result_TRM_Id =null;					//"MusicBrainz/TRM Id");					//												MusicBrainz TRM Id				ID3ｖ2；TXX	v3；TXXX			qt;--
	public String result_Disc_Id =null;					//"MusicBrainz/Disc Id");					//												MusicBrainz Disc Id				ID3ｖ2；TXX	v3；TXXX			qt;--
	public String result_Acoustid_Id =null;				//Acoustid/Id");							//												AcoustID						ID3ｖ2；TXX	v3；TXXX			qt;--
	public String result_Fingerprint =null;				//Acoustid/Fingerprint");				//												AcoustID Fingerprint			ID3ｖ2；TXX	v3；TXXX			qt;--
	public String result_PUID =null;						//MusicIP/PUID");						//												MusicIP PUID					ID3ｖ2；TXX	v3；TXXX			qt;--

	/**
	 * WMAの処理開始
	 * */
	public void headReadWma(final File file) throws  IOException {		//WMAの処理開始
		String result =null;
		final String TAG = "headReadWma";
		String dbMsg= "";
		try{
			dbMsg= filePath;					//newFile.length=11897286
			RandomAccessFile newFile = null;			//任意の位置からデータの読み込み処理、書き込み処理を行うことができます。これは余計な読み込み処理、書き込み処理を行う必要がないため、効率的な処理
			if( 18< Integer.valueOf(String.valueOf(Build.VERSION.SDK))){	//Android4.4からはファイルの書き込み制限で
				newFile = new RandomAccessFile(file, "r");					//java.io.FileNotFoundException: : open failed: EACCES (Permission denied)
			}else{
				newFile = new RandomAccessFile(file,  "rw");					//(file, writeable ? "rw" : "r");
			}
			long fileLen = newFile.length();
			dbMsg +=",newFile.length=" + fileLen + "バイト";					//newFile.length=11897286
			int size = 0;
			byte[] buffer = new byte[(int) fileLen];									//タグヘッダ読込開始	☆unsigned char同等？
			int bufferSize = buffer.length;
			dbMsg += ",buffer=" + bufferSize + "バイト;";//Atom Size
			int readEnd = newFile.read(buffer);
		////[0]=0=0x30,[1]=&=0x26,[2]=²=0xb2,[3]=u=0x75,[4]==0x8e,[5]=f=0x66,[6]=Ï=0xcf,[7]==0x11,[8]=¦=0xa6,[9]=Ù=0xd9,[10]=��=0x0,[11]=ª=0xaa,[12]=��=0x0,[13]=b=0x62,[14]=Î=0xce,[15]=l=0x6c,[16]=b=0x62,[17]=!=0x21,[18]=��=0x0,[19]=��=0x0,[20]=��=0x0,[21]=��=0x0,[22]=��=0x0,[23]=��=0x0,[24]==0x7,[25]=��=0x0,[26]=��=0x0,[27]=��=0x0,[28]==0x1,[29]==0x2,[30]=¡=0xa1,[31]=Ü=0xdc,[32]=«=0xab,[33]==0x8c,[34]=G=0x47,[35]=©=0xa9,[36]=Ï=0xcf,[37]==0x11,[38]==0x8e,[39]=ä=0xe4,[40]=��=0x0,[41]=À=0xc0,[42]==0xc,[43]= =0x20,[44]=S=0x53,[45]=e=0x65,[46]=h=0x68,[47]=��=0x0,[48]=��=0x0,[49]=��=0x0>>0&²ufÏ¦Ù��ª��bÎlb!������������������¡Ü«G©Ïä��À Seh�������������� ÈÔ°ðG¯J0îÛs89����������¶áÐv������������ ÛD����～X¡
//			for(int i = 0 ; i < bufferSize ; i++){
//		//		if(buffer[i] < 0){
//					buffer[i] = (byte) (127 + buffer[i]);						//(0x88 & buffer[i])
////				} else {
////					testChar[i] = (byte) (testChar[i] + 127);
//		//		}
//			}
////[0]=0=0x30,[1]=&=0x26,[2]=²=0xb2,[3]=u=0x75,[4]==0x8e,[5]=f=0x66,[6]=Ï=0xcf,[7]==0x11,[8]=¦=0xa6,[9]=Ù=0xd9,[10]=��=0x0,[11]=ª=0xaa,[12]=��=0x0,[13]=b=0x62,[14]=Î=0xce,[15]=l=0x6c,[16]=b=0x62,[17]=!=0x21,[18]=��=0x0,[19]=��=0x0,[20]=��=0x0,[21]=��=0x0,[22]=��=0x0,[23]=��=0x0,[24]==0x7,[25]=��=0x0,[26]=��=0x0,[27]=��=0x0,[28]==0x1,[29]==0x2,[30]=¡=0xa1,[31]=Ü=0xdc,[32]=«=0xab,[33]==0x8c,[34]=G=0x47,[35]=©=0xa9,[36]=Ï=0xcf,[37]==0x11,[38]==0x8e,[39]=ä=0xe4,[40]=��=0x0,[41]=À=0xc0,[42]==0xc,[43]= =0x20,[44]=S=0x53,[45]=e=0x65,[46]=h=0x68,[47]=��=0x0,[48]=��=0x0,[49]=��=0x0>>0&²ufÏ¦Ù��ª��bÎlb!������������������¡Ü«G©Ïä��À Seh�������������� ÈÔ°ðG¯J0îÛs89����������¶áÐv������������ ÛD����～X¡

			motoEncrod = "ISO-8859-1";
			dbMsg +=",Encrod=" + motoEncrod;
			result = new String( buffer, motoEncrod );
			buffer = result.getBytes();									//タグヘッダ読込開始		result.substring(maeoki, startInt).getBytes()
			bufferSize = buffer.length;
			dbMsg += ",buffer=" + bufferSize + "バイト;";//Atom Size
			char[] testChar = result.toCharArray();
			for(int i = 0 ; i < 50 ; i++){
				dbMsg += 	",[" + i + "]=" + testChar[i];
				String testStr =  "0x" + Integer.toHexString(testChar[i]);
				dbMsg += 	"=" + testStr;
			}
//			DataInputStream in = new DataInputStream(new BufferedInputStream( new FileInputStream(filePath)));
//			result = in.readUTF();  //文字列の読み込み	:java.io.UTFDataFormatException: bad byte at 0
//			in.readChar();  //改行の読み込み
/*
 * ISO-8859-1(ISO8859_2)	21168613バイト	0&²ufÏ¦Ù��ª��bÎly������������������¡Ü«G©Ïä��À Seh��������������ÞÍòÇÎÆK­ýá>=ó4-åC��������aJCÏ×óR>5BúA��æÊ2¶A©RúRP¿ERªrGúP`$¤ $V +$¦:7"a0j¤%Ì21168613文字,target=ULT,rTarget=ULT,fleamStart=13178816,再読取=21168613文字,0&²ufÏ¦Ù��ª��bÎly������������ðÊM��～é@¢>·Hh6äHl
 * Cp1252					21168613バイト	0&²uŽfÏ¦Ù��ª��bÎly������������������¡Ü«ŒG©ÏŽä��À Seh��������������ÞÍòÇ†ÎÆK­ýá>=ó4-åC��������ˆaJCžÏ×ó†€RšŠ>5BúA��€æÊ2¶€’A©R‚úRP¿“„ERª‹rGúP`˜$¤ $”ƒV +$¦Œ:7"a0j„Š¤%Ì21168613文字,target=ULT,rTarget=ULT,fleamStart=13178816,再読取=21168613文字,0&²uŽfÏ¦Ù��ª��bÎly������������ðÊM��～é@¢„>·€Hh6äHl
 * windows-1252				21168613バイト	0&²uŽfÏ¦Ù��ª��bÎly������������������¡Ü«ŒG©ÏŽä��À Seh��������������ÞÍòÇ†ÎÆK­ýá>=ó4-åC��������ˆaJCžÏ×������������ðÊM��～é@¢„>·€Hh6äHl
 * iso-2022-jp				21168613バイト	0&�u�f��������b�ly����������������������G������� Seh���������������������K���>=�4-�C���������aJC�����������������M��～�@��>��Hh6�Hl���R��>5B�A�����2����A�R��RP���ER��rG��P`�$� $��V�+$��:�7"a0j���%�21167185文字,target=ULT,rTarget=ULT,fleamStart=13178140,再読取=21167185文字,0&�u�f��������b�ly
 * US_ASCII					4601918バイト/4601918文字,	0&�u�f��������b�l�����������������������G������� Seh���������������c�&tW�N���.�o)��$F����������yn��|��������������`������～y�o��m'nW9C��v�A��D(�n.��h�����Y��~!���a��9�z�5�*�D(H���������mA����66I�$���>He9��}�	m�
 * ASCII					4601918バイト/4601918文字,	0&�u�f��������b�l�����������������������G������� Seh���������������c�&tW�N���.�o)��$F����������yn��|��������������`������～y�o��m'nW9C��v�A��D(�n.��h�����Y��~!���a��9�z�5�*�D(H���������mA����66I�$���>He9��}�	m�
 * UTF-8(UTF8)				4601918バイト/4390459文字	0&�u�f��������b�l��������������������ܫ�G������� Seh���������������c�&tW�N���.�o)��$F����������yn��|��������������`۫��������～ɲ�Ჿs*iY��y�o��m'nW9C��v�AɝD(�n.�h�����Y�~!���a��9�z�5�*�D(H���鶉�mA����66I�$�>He9}�	m�
 * UTF-7					4601918バイト/4596005文字	0&�u�f�������b�l������������������G�������� Seh��������c�&tW�N���.�o)��$F�����yn��|�����������`�����～y�o��m'nW9C���v�A��D(���n.��h������Y���!���a��9�z�5�*�D(H����������mA�����66I��$���>H�e�9��}��	m���
 * unsuppot		ASCII169	RFC1468	IEC646(IEC-646)	ISO646(ISO-646)	ISO-8859	EBCDIC	unicodeFFFE	Windows-31
 * Clash		ASCII-169	Cp930	ANSI
 * 文字化け(タグを検出できる)		Windows-31j
 * 文字化け(タグを検出できない)		UTF-16LE = UnicodeLittleUnmarked	UTF-16(BE = UnicodeBigUnmarked)	  S-JIS		MS932(ms932)	utf-32(BE)	UCS-2	EUC-JP	IBM437
 * */
			newFile.close();
			buffer = null;
			int readInt = result.length();
			if(200 < readInt){
				dbMsg += ">>" + result.substring(0, 100) +  "～" + result.substring(readInt-100, readInt);
			}else{
				dbMsg += ">>" + result;
			}
			dbMsg +=readInt + "文字";
			dbMsg +=",WAVE.infoチャンク；RIFF=" + result.indexOf("RIFF") + "文字目";
			dbMsg +=",DATA=" + result.indexOf("DATA") + "文字目";
			dbMsg +=",fmt=" + result.indexOf("fmt") + "文字目";				//<fmt >: フォーマット定義(必須)\n"
			dbMsg +=",data=" + result.indexOf("data") + "文字目";			// <data>: 波形データ(必須)\n"
			dbMsg +=",fact=" + result.indexOf("fact") + "文字目";			//<fact>: 全サンプル数\n"
			dbMsg +=",LIST=" + result.indexOf("LIST") + "文字目";			//<LIST>: 各種情報\n"
			dbMsg +=",DISP=" + result.indexOf("DISP") + "文字目";			//<DISP>: 表\示情報\n"

			if( result != null){
				String pdMessage ="WMA ; Objects Reading..." ; //歌詞を探しています。</string>
				reqCode = read_WMA_ITEM ;										//WMAのオブジェクト読取り
				umekomiTag = null;
				String target = "ULT";
				String rTarget = fremeMeiSyougouBody( result , target);	//渡された文字列を先頭からindexOfで照合し、該当すればその文字を返し、無ければnullを返す
				dbMsg +=",target=" + target + ",rTarget=" + rTarget;
				if(target.equals(rTarget)){
					umekomiTag = "ID3v2";						//埋め込まれたいる別のタグ
					reqCode = read_WMA_ID32 ;					//WMAに埋め込まれたID3v2タグの読取り
				} else {
					target = "USLT";
					rTarget = fremeMeiSyougouBody( result , target);	//渡された文字列を先頭からindexOfで照合し、該当すればその文字を返し、無ければnullを返す
					dbMsg +=",target=" + target + ",rTarget=" + rTarget;
					if(target.equals(rTarget)){
						umekomiTag = "ID3v3";						//埋め込まれたいる別のタグ
						reqCode = read_WMA_ID33 ;										//WMAに埋め込まれたID3v3タグの読取り
					} else {
						target = "©lyr";
						rTarget = fremeMeiSyougouBody( result , target);	//渡された文字列を先頭からindexOfで照合し、該当すればその文字を返し、無ければnullを返す
						dbMsg +=",target=" + target + ",rTarget=" + rTarget;
						if(target.equals(rTarget)){
							umekomiTag = "AAC";						//埋め込まれたいる別のタグ
							reqCode = read_WMA_AAC ;					//WMAに埋め込まれたID3v2タグの読取り
						} else {

						}
					}
				}

				if( umekomiTag != null ){
					pdMessage = umekomiTag +  getResources().getString(R.string.tag_wma_ume);			//が埋め込まれたwma</string>
					result_Tag = pdMessage;
					dbMsg +=",fleamStart=" + fleamStart + "/" + fileLen ;
					result = result.substring(fleamStart);
//					newFile.seek(fleamStart);
//					dbMsg +=",getFilePointer=" + newFile.getFilePointer();
//					fileLen =  fileLen - fleamStart;
//					buffer = new byte[(int) fileLen];
//					dbMsg +=",サイズ=" + buffer.length + "バイト";
//					readEnd = newFile.read(buffer , 0 , (int) fileLen );
//					dbMsg +=">>" + readEnd+ "バイト";
//			//		motoEncrod = "windows-1252";						//UTF-8
//					result = new String( buffer, motoEncrod );
					readInt = result.length();
					dbMsg +=",再読取=" + readInt + "文字,";					//newFile.length=11897286
					if(200 < readInt){
						dbMsg +=result.substring(0, 100) +  "～" + result.substring(readInt-100, readInt);
					}else{
						dbMsg +=result;
					}
					dbMsg +=readInt + "文字";
				}
				dbMsg +="," + readInt + ",reqCode=" + reqCode;
		//		newFile.close();
				initWmaResult(reqCode);								//Wma戻り値の初期化
				makeWmaList(reqCode);								//WMAフィールド名リストをList<String> syougouに作成
				int resultLen = result.length();
				pdMaxVal= kensaku.size();										//result.length();								//プログレス終端値
				dbMsg +=",pdMaxVal=" + pdMaxVal +"項目";
				dbMsg +=",reqCode="+reqCode;
				String pdTitol = getApplicationContext().getString(R.string.tag_prog_titol1) +"" + getResources().getString(R.string.common_yomitori);				//
				dbMsg +=",pdTitol="+pdTitol;
				dbMsg +=",pdMessage="+pdMessage;
//				pTask = (plogTask) new plogTask(this ,  this , reqCode , pdTitol ,pdMessage , pdMaxVal ).execute(reqCode,  pdMessage , result , kensaku );		//,jikkouStep,totalStep,calumnInfo
				plTask.execute(reqCode,kensaku,TagBrows.this.result,null);
				TagBrows.this.result = Readloop( reqCode,kensaku,TagBrows.this.result);
				dbMsg += ",result="+TagBrows.this.result;
		//		itemReadWmaEnd();		//WMAのオブジェクト読取り終了処理
				myLog(TAG,dbMsg);
			} else {
				itemReadWmaEnd();		//WMAのオブジェクト読取り終了処理
			}

		}catch(FileNotFoundException e){
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}catch(IOException e){
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}
	}

	public void initWmaResult(int reqCode) {								//Wma戻り値の初期化
		String result =null;
		final String TAG = "initWmaResult";
		String dbMsg= "";
		try{
			switch(reqCode) {
			case read_WMA_ITEM:				//WMAのオブジェクト読取り
				//https://msdn.microsoft.com/en-us/library/windows/desktop/dd743066(v=vs.85).aspx
				/*typedef struct _WMSynchronisedLyrics {
				  BYTE   bTimeStampFormat;
				  BYTE   bContentType;
				  LPWSTR pwszContentDescriptor;
				  DWORD  dwLyricsLen;
				  BYTE   *pbLyrics;
				} WM_SYNCHRONISED_LYRICS;*/
				wmaCoreItem= new ArrayList<String>();		//WMAで検索するアイテム
				wmaCoreItem.add("WM/Picture");					//付属する画像											CoverArt						ID3ｖ2；PIC	v3；APIC			qt;covr	Attached picture
				wmaCoreItem.add("WM/AlbumTitle");						//albm?アルバム/映画/ショーのタイトル			Album							ID3ｖ2；TAL	v3；TALB			qt;©alb		Album/Movie/Show title
				wmaCoreItem.add("WM/AlbumSortOrder");					//オリジナルのアルバム/映画/ショーのタイトル?	Album Sort Order				ID3ｖ2；TOT	v3；TOAL			qt;soal		Original album/Movie/Show title
				wmaCoreItem.add("Title");								//アルバム（読み）								Track Title						ID3ｖ2；--	v3；TSOA			qt;©nam		ALBUMSORT[1][2]
				wmaCoreItem.add("WM/TitleSortOrder");					//タイトル（読み）								Sort Name						ID3ｖ2；--	v3；TIT3			qt;sonm
				wmaCoreItem.add("Author");								//アーティスト									TITLESORT						ID3ｖ2；TP1	v3；TPE1			qt;©ART
				wmaCoreItem.add("WM/ArtistSortOrder");					//アーティスト（読み）							ARTISTSORT						ID3ｖ2；--	v3；TSOP			qt;soar
				wmaCoreItem.add("WM/AlbumArtist");						//アルバムアーティスト							ALBUMARTIST						ID3ｖ2；--	v3；TPE2			qt;aART	Lead artist(s)/Lead performer(s)/Soloist(s)/Performing group
				wmaCoreItem.add("WM/AlbumArtistSortOrder");				//バンド/オーケストラ/伴奏						SortAlbumArtistt				ID3ｖ2；TP2	v3；TPE2			qt;soaa	Band/Orchestra/Accompaniment/Album Artis?
				wmaCoreItem.add("WM/Year");								//リリース日（年）								YEAR							ID3ｖ2；?	v3；TYER			qt;©day	YEAR
				wmaCoreItem.add("WM/Composer");							//作曲者										COMPOSER						ID3ｖ2；TCM	v3；TCOM			qt;©wrt	Composer
				wmaCoreItem.add("WM/ComposerSortOrder");				//作曲者（読み）								SortComposer					ID3ｖ2；?-	v3；TSOC			qt;soco	COMPOSERSORT
				wmaCoreItem.add("WM/Writer");							//作詞			）								Lyricist						ID3ｖ2；?-	v3；TEXT			qt;--	----:com.apple.iTunes:LYRICIST
				wmaCoreItem.add("WM/Conductor");						//指揮者/演奏者詳細情報							Performer						ID3ｖ2；TP3	v3；TPE3			qt;perf	Conductor/Performer refinement
				wmaCoreItem.add("WM/ContentGroupDescription");			//内容の属するグループの説明					Grouping[						ID3ｖ3；--	v3；TIT1			qt;©grp
				wmaCoreItem.add("WM/SubTitle");							//												Subtitle						ID3ｖ3；--	v3；TIT3	v4:TSST	qt;--
				wmaCoreItem.add("WM/TrackNumber");						//トラックの番号/セット中の位置					Track							ID3ｖ2；TRK	v3；TRCK			qt;©trk	TrackNumber	Track number/Position in set
				wmaCoreItem.add("WM/PartOfSet");						//ディスクナンバー								DiskNumber	Part of a set		ID3ｖ2；TPA	v3；TPOS			qt;disk	DISCNUMBER/TOTALDISC
				wmaCoreItem.add("WM/Lyrics");							//非同期 歌詞/文書のコピー						Lyrics	APEv2： 				ID3ｖ2；ULT	v3；USLT			qt;©lyr		Unsychronized lyric/text transcription
				//													//同期 歌詞/文書																ID3ｖ2；SLT	v3；SYLT			qt;--		Synchronized lyric/text
				wmaItem= new ArrayList<String>();			// ;							//具体的なアイテム
				wmaItem.add("WM/IsCompilation");					//コンピレーションの明示						Compilation						ID3ｖ2；--	v3；--				qt;cpil	COMPILATION
				wmaItem.add("Description");							//コメント										Comment							ID3ｖ2；COM	v3；COMM			qt;©cmt	Comments
				wmaItem.add("WM/Genre");							//ユーザ定義ジャンル							GENRE							ID3ｖ2；TCO	v3；TCON			qt;©gen	GENRE
				wmaItem.add("WM/SharedUserRating");					//人気メーター ?								RatingPercent					ID3ｖ2；POP	v3；POPM			qt;rate	Popularimeter
				wmaItem.add("WM/BeatsPerMinute");					//一分間の拍数									BeatsPerMinute					ID3ｖ2；TBP	v3；TBPM			qt;tmpo	BPM (Beats Per Minute)
				wmaItem.add("WWM/Mood");							//												Mood							ID3ｖ2；--	v3；TMOO			qt;--
				wmaItem.add("WM/Media");							//メディアの種類の明示							Media							ID3ｖ2；TMT?v3；TMED?			qt;stik?	ITUNESMEDIATYPE
				wmaItem.add("WM/Language");							//言語											Language(s)						ID3ｖ2；TLA	v3；TLAN			qt;--
				wmaItem.add("Copyright");							//著作権										Copyright						ID3ｖ2；TCR	v3；TCOP			qt;cprt	Copyright message
				wmaItem.add("LICENSE");								//著作権/法的情報								Copyright/Legal information		ID3ｖ2；WCP	v3；WCOP			qt;--
				wmaItem.add("WM/EncodedBy");						//©enc?エンコーディング ソフトウェア			Encoded by						ID3ｖ2；TEN	v3；TENC			qt;©too
				wmaItem.add("WM/EncodingSettings");					//エンコードに使用したソフトウエア/ハードウエアとセッティング					ID3ｖ2；TSS	v3；TSSE			qt;		Software/hardware and settings used for encoding
				wmaItem.add("WM/Barcode");							//												Barcode							ID3ｖ2；TXX	v3；TXXX			qt;--
				wmaItem.add("WM/ISRC");			//国際標準レコーディングコード						ISRC (International Standard Recording Code)	ID3ｖ2；TRC	v3；TSRC			qt;--
				wmaItem.add("WM/ModifiedBy");						//												Remixer							ID3ｖ2；--	v3；TPE4			qt;--
				wmaItem.add("WM/Engineer");							//												Engineer						ID3ｖ2；--	v3；TIPL	v4:IPLS	qt;--
				wmaItem.add("WM/Producer");							//制作者										Producer						ID3ｖ2；--	v3；TIPL	v4:IPLS	qt;--
				wmaItem.add("WM/Mixer");							//												Mixer							ID3ｖ2；--	v3；TIPL	v4:IPLS	qt;--
				wmaItem.add("WM/CatalogNo");						//ユーザー定義文字情報フレーム			User defined text information frame		ID3ｖ2；TXX	v3；TXXX			qt;--
				wmaItem.add("MusicBrainz/Album Status");			//												Release Status					ID3ｖ2；TXX	v3；TXXX			qt;--
				wmaItem.add("MusicBrainz/Album Type");				//												Release Type					ID3ｖ2；TXX	v3；TXXX			qt;--
				wmaItem.add("MusicBrainz/Album Release Country");	//												Release Country					ID3ｖ2；TXX	v3；TXXX			qt;--
				wmaItem.add("WM/Script");							//												Release Country					ID3ｖ2；TXX	v3；TXXX			qt;--
				wmaItem.add("MusicBrainz/Track Id");				//												Unique file identifier			ID3ｖ2；UFI	v3；UFID			qt;--
				wmaItem.add("MusicBrainz/Release Track Id");		//												MusicBrainz Release Id			ID3ｖ2；TXX	v3；TXXX			qt;--
				wmaItem.add("MusicBrainz/Artist Id");				//												MusicBrainz Artist Id			ID3ｖ2；TXX	v3；TXXX			qt;--
				wmaItem.add("MusicBrainz/Album Artist Id");			//												MusicBrainz Release Artist Id	ID3ｖ2；TXX	v3；TXXX			qt;--
				wmaItem.add("MusicBrainz/Release Group Id");		//												MusicBrainz Release Group Id	ID3ｖ2；TXX	v3；TXXX			qt;--
				wmaItem.add("MusicBrainz/Work Id");					//												MusicBrainz Work Id				ID3ｖ2；TXX	v3；TXXX			qt;--
				wmaItem.add("MusicBrainz/TRM Id");					//												MusicBrainz TRM Id				ID3ｖ2；TXX	v3；TXXX			qt;--
				wmaItem.add("MusicBrainz/Disc Id");					//												MusicBrainz Disc Id				ID3ｖ2；TXX	v3；TXXX			qt;--
				wmaItem.add("Acoustid/Id");							//												AcoustID						ID3ｖ2；TXX	v3；TXXX			qt;--
				wmaItem.add("Acoustid/Fingerprint");				//												AcoustID Fingerprint			ID3ｖ2；TXX	v3；TXXX			qt;--
				wmaItem.add("MusicIP/PUID");						//												MusicIP PUID					ID3ｖ2；TXX	v3；TXXX			qt;--
															//作曲者												Composer						ID3ｖ2；TCM	v3；TCOM	qt;--	Composer?
															//権利元												Copyright	Copyright			ID3ｖ2；TCR	v3；TCOP	qt;©cpy	Copyright message
															//原題													Parent Title					ID3ｖ2；--	v3；TOAL	qt;--	//Original album/movie/show title?
															//																						ID3ｖ2；--	v3；TDOR	qt;--
															//														Performer						ID3ｖ2；--	v3；TMCL ｖ4；IPLS	qt;--
															//														Arranger/Engineer/Producer		ID3ｖ2；--	v3；TIPL ｖ4；IPLS	qt;--
															//トラックナンバー										TrackNumber						ID3ｖ2；TRK	v3；TRCK	qt;--	TTRACK/TOTALTARCK
															//														TVShow							ID3ｖ2；--	v3；--	qt;tvsh
															//														Sort Show						ID3ｖ2；--	v3；--	qt;sosn
															//ポッドキャストであることを明示						Podcast							ID3ｖ2；--	v3；--	qt;pcst
															//ポッドキャストURL										Podcast URL						ID3ｖ2；--	v3；--	qt;purl	PODCASTURL
															//ギャップレスコンテンツの明示							PlayGap							ID3ｖ2；--	v3；--	qt;pgap	ITUNESGAPLESS
															//														ASIN							ID3ｖ2；TXX	v3；TXXX
															//														MusicIP Fingerprint				ID3ｖ2；TXX	v3；TXXX
															//アーティスト/演奏者の公式Webページ				Official artist/performer webpage	ID3ｖ2；--	v3；WOAR	qt;--
															//出版社/発行元											Publisher						ID3ｖ2；TPB	v3；TPUB	qt;©pub	Publisher
															//説明													Description	Track				ID3ｖ2；--	v3；--		qt;©des	SUBTITLE
															//														Short Title						ID3ｖ2；--	v3；--		qt;@sti
															//アルバム/映画/ショーのタイトル						Album/Movie/Show title			ID3ｖ2；TAL	v3；TALB	qt;albm
															//タイトル/曲名/内容の説明	タイトル					Description						ID3ｖ2；TT2	v3；TIT2	qt;desc	Track Title	Title/Title/Songname/Content description
															//年	レコーディング年/年*15	Date*16	Year Deprecated	Year		Year				ID3ｖ2；TYE	v3；TYER	qt;yrrc
															//アカウントの種類			編集不可					Apple Store Account Type		ID3ｖ2；--	v3；--		qt;akID
															//アカウント情報					ITUNESACCOUNT		Apple Store Account				ID3ｖ2；--	v3；--		qt;apID
															//アーティスト識別子	編集不可						Album Title ID					ID3ｖ2；MCI?v3；MCDI	qt;atID	Music CD Identifier?
															//														Author							ID3ｖ2；--	v3；--	qt;auth
															//ポッドキャストカテゴリ								Category						ID3ｖ2；--	v3；--		qt;catg		PODCASTCATEGORY
															//コンテンツ識別子										AppleStoreCatalogID				ID3ｖ2；--	v3；--		qt;cnID	ITUNESCATALOGID
															//タイトル/曲名/内容の説明	タイトル					Description						ID3ｖ2；TT2	v3；TIT2;	qt;dscp	Track Title	Title/Title/Songname/Content description
															//ポッドキャストエピソードユニークID					Episode Global Unique ID		ID3ｖ2；--	v3；--		qt;egid	PODCASTID
															//ジャンル識別子		編集不可						GenreID							ID3ｖ2；--	v3；--	qt;geID
															//ジャンル												Genre	Content type			ID3ｖ2；TCO	v3；TCON	qt;gnre
															//														iTunesInfo						ID3ｖ2；--	v3；--	qt;"----"
															//														Narrator						ID3ｖ2；--	v3；--	qt;©nrt
															//														Parent Short Title				ID3ｖ2；--	v3；--	qt;@PST
															//														Parent ProductID				ID3ｖ2；--	v3；--	qt;@ppi
															//														Unknown_AACR?					ID3ｖ2；--	v3；--	qt;AACR
															//														Unknown_CDEK?					ID3ｖ2；--	v3；--	qt;CDEK
															//														Unknown_CDET?					ID3ｖ2；--	v3；--	qt;CDET
															//														GUID							ID3ｖ2；--	v3；--	qt;GUID
															//														ProductVersion					ID3ｖ2；--	v3；--	qt;VERS
															//														Grouping						ID3ｖ2；--	v3；--	qt;grup
															//														GoogleHostHeade					ID3ｖ2；--	v3；--	qt;gshh
															//														GooglePingMessage				ID3ｖ2；--	v3；--	qt;gspm
															//														GooglePingURL					ID3ｖ2；--	v3；--	qt;gspu
															//														GoogleSourceData				ID3ｖ2；--	v3；--	qt;gssd
															//														GoogleStartTime					ID3ｖ2；--	v3；--	qt;gsst
															//														GoogleTrackDuration				ID3ｖ2；--	v3；--	qt;gstd
															//	?													HDVideo	?						ID3ｖ2；--	v3；--	qt;hdvd
															//ビデオ解像度の明示		ITUNESHDVIDEO				HDVideo							ID3ｖ2；--	v3；--	qt;hdtv
															//														iTunesU							ID3ｖ2；--	v3；--	qt;itnu
															//ポッドキャストキーワード		編集不可				Keyword							ID3ｖ2；--	v3；--	qt;keyw
															//サブタイトル/説明の追加情報							Long Description				ID3ｖ2；--	v3；TIT3;	qt;ldes	Subtitle/Description refinement?
															//プレイリスト（アルバム）識別子	編集不可			PlayListID						ID3ｖ2；--	v3；--	qt;plID
															//														ProductID						ID3ｖ2；--	v3；--	qt;prID
															//購入日												ITUNESPURCHASEDATE				ID3ｖ2；--	v3；--		qt;purd	ITUNESPURCHASEDATE
															//オリジナルのリリース年?								Release Date					ID3ｖ2；TOR	v3；TORY	qt;rldt	Original release year
															//保護者のためのレートの明示?番組?番組（読み）			Rating?	TVSHOW?					ID3ｖ2；--	v3；--		qt;rtng	ITUNESADVISORY?	TVSHOW?
															//ストアの国				編集不可					AppleStore Country				ID3ｖ2；--	v3；--	qt;sfID
															//タイトル/曲名/内容の説明	タイトル					Title							ID3ｖ2；TT2	v3；TIT2;	qt;titl	Track Title	Title/Title/Songname/Content description
															//エピソードID											TVEpisodeID						ID3ｖ2；--	v3；--	qt;tven
															//														TVEpisode						ID3ｖ2；--	v3；--	qt;tves
															//放送局												TVNetworkName					ID3ｖ2；--	v3；--	qt;tvnn
															//シーズン												TVSeason						ID3ｖ2；--	v3；--	qt;tvsn
															//LyricsURI							QuickTime UserData Tags								ID3ｖ2；ULT	v3；USLT	qt;lrcu
				break;
			case read_WMA_ID32:				//WMAに埋め込まれたID3v2タグの読取り
				id32CoreItem= new ArrayList<String>();		//ID3v2の検索対象アイテム
				id32CoreItem.add("TCO");		//ジャンル														Content type											ID3ｖ3；TCON
				id32CoreItem.add("TCM");		//作曲者														Composer												ID3ｖ3；TCOM
				id32CoreItem.add("TP1");		//主な演奏者/ソリスト/トラック アーティスト				Lead artist(s)/Lead performer(s)/Soloist(s)/Performing group	ID3ｖ3；TPE1
				id32CoreItem.add("TP2");		//バンド/オーケストラ/伴奏	バンド/オーケストラ					Band/Orchestra/Accompaniment/Album Artist				ID3ｖ3；TPE2
				id32CoreItem.add("TP3");		//指揮者/演奏者詳細情報	指揮者									Conductor/Performer refinement							ID3ｖ3；TPE3
				id32CoreItem.add("TAL");		//アルバム/映画/ショーのタイトル								Album/Movie/Show title									ID3ｖ3；TALB
				id32CoreItem.add("TPA");		//セット中の位置/ディスク #	Disc Number	Disc#					Part of a set											ID3ｖ3；TPOS
				id32CoreItem.add("TRK");		//トラックの番号/セット中の位置									Track number/Position in set							ID3ｖ3；TRCK
				id32CoreItem.add("TT1");		//内容の属するグループの説明									Content group description								ID3ｖ3；TIT1;
				id32CoreItem.add("TT2");		//タイトル/曲名/内容の説明	タイトル							Track Title	Title/Title/Songname/Content description	ID3ｖ3；TIT2;
				id32CoreItem.add("TT3");		//サブタイトル/説明の追加情報									Subtitle/Description refinement							ID3ｖ3；TIT3;
				id32CoreItem.add("TRD");		//録音日付														Recording dates											ID3ｖ3；TRDA
				id32CoreItem.add("TYE");		//年	レコーディング年/年*15	Date*16	Year Deprecated			Year													ID3ｖ3；TYER
				id32CoreItem.add("TDA");		//日付	Date*11	Date*12	Year*13 Deprecated						Date													ID3ｖ3；TDAT
				id32CoreItem.add("TEN");		//エンコーディング ソフトウェア									Encoded by												ID3ｖ3；TENC
				id32CoreItem.add("PIC");		//付属する画像													Attached picture										ID3ｖ3；APIC
				id32CoreItem.add("SLT");		//同期 歌詞/文書												Synchronized lyric/text									ID3ｖ3；SYLT
				id32CoreItem.add("ULT");		//非同期 歌詞/文書のコピー										Unsychronized lyric/text transcription					ID3ｖ3；USLT
				id32Item= new ArrayList<String>();			//ID3v2の全アイテム
				id32Item.add("BUF");		//推奨バッファサイズ											Recommended buffer size									ID3ｖ3；RBUF
				id32Item.add("CNT");		//演奏回数														Play counter											ID3ｖ3；PCNT
				id32Item.add("COM");		//コメント														Comments												ID3ｖ3；COMM
				id32Item.add("CRA");		//オーディオの暗号化											Audio encryption										ID3ｖ3；AENC
				id32Item.add("CRM");		//																Encrypted meta frame									ID3ｖ3；？
				id32Item.add("ETC");		//イベントタイムコード											Event timing codes										ID3ｖ3；ETCO
				id32Item.add("EQU");		//音質調整														Equalization											ID3ｖ3；EQUA
				id32Item.add("GEO");		//パッケージ化された一般的なオブジェクト						General encapsulated object								ID3ｖ3；GEOB
				id32Item.add("IPL");		//協力者一覧													Involved people list									ID3ｖ3；IPLS
				id32Item.add("LNK");		//リンク情報	 												Linked information										ID3ｖ3；LINK
				id32Item.add("MCI");		//音楽ＣＤ識別子												Music CD Identifier										ID3ｖ3；MCDI
				id32Item.add("MLL");		//MPEGロケーションルックアップテーブル							MPEG location lookup table								ID3ｖ3；MLLT
				id32Item.add("POP");		//人気メーター 													Popularimeter											ID3ｖ3；POPM
				id32Item.add("REV");		//リバーブ	 													Reverb													ID3ｖ3；RVRB
				id32Item.add("RVA");		//相対的ボリューム調整											Relative volume adjustment								ID3ｖ3；RVAD
				id32Item.add("STC");		//																Synced tempo codes										ID3ｖ3；?
				id32Item.add("TBP");		//一分間の拍数													BPM (Beats Per Minute)									ID3ｖ3；TBPM
				id32Item.add("TCR");		//																Copyright message										ID3ｖ3；TCOP
				id32Item.add("TDY");		//プレイリスト遅延時間											Playlist delay											ID3ｖ3；TDLY
				id32Item.add("TFT");		//ファイルタイプ												File type												ID3ｖ3；TFLT
				id32Item.add("TIM");		//時間															Time(Time Deprecated?)									ID3ｖ3；TIME
				id32Item.add("TKE");		//初めの調														Initial key												ID3ｖ3；TKEY
				id32Item.add("TLA");		//言語															Language(s)												ID3ｖ3；TLAN
				id32Item.add("TLE");		//長さ															Length													ID3ｖ3；TLEN
				id32Item.add("TMT");		//メディアタイプ												Media type												ID3ｖ3；TMED
				id32Item.add("TOA");		//オリジナルアーティスト/演奏者									Original artist(s)/performer(s)							ID3ｖ3；TOPE
				id32Item.add("TOF");		//オリジナルファイル名											Original filename										ID3ｖ3；TOFN
				id32Item.add("TOL");		//オリジナルの作詞家/文書作成者									Original Lyricist(s)/text writer(s)						ID3ｖ3；TOLY
				id32Item.add("TOR");		//オリジナルのリリース年										Original release year									ID3ｖ3；TORY
				id32Item.add("TOT");		//オリジナルのアルバム/映画/ショーのタイトル					Original album/Movie/Show title							ID3ｖ3；TOAL
				id32Item.add("TP4");		//翻訳者, リミックス, その他の修正								Interpreted, remixed, or otherwise modified by			ID3ｖ3；TPE4
				id32Item.add("TPB");		//出版社/発行元													Publisher												ID3ｖ3；TPUB
				id32Item.add("TRC");		//国際標準レコーディングコード									ISRC (International Standard Recording Code)			ID3ｖ3；TSRC
				id32Item.add("TSI");		//サイズ														Size(Size	 Deprecated?)								ID3ｖ3；TSIZ;
				id32Item.add("TSS");		//エンコードに使用したソフトウエア/ハードウエアとセッティング	Software/hardware and settings used for encoding		ID3ｖ3；TSSE;
				id32Item.add("TXT");		//作詞家/文書作成者												Lyricist/text writer									ID3ｖ3；TEXT
				id32Item.add("TXX");		//ユーザー定義文字情報フレーム									User defined text information frame						ID3ｖ3；TXXX
				id32Item.add("UFI");		//一意的なファイル識別子/タグID?								Unique file identifier									ID3ｖ3；UFID
				id32Item.add("WAF");		//オーディオファイルの公式Webページ								Official audio file webpage								ID3ｖ3；WOAF
				id32Item.add("WAR");		//アーティスト/演奏者の公式Webページ							Official artist/performer webpage						ID3ｖ3；WOAR
				id32Item.add("WAS");		//音源の公式Webページ											Official audio source webpage							ID3ｖ3；WOAS
				id32Item.add("WCM");		//商業上の情報													Commercial information									ID3ｖ3；WCOM
				id32Item.add("WCP");		//著作権/法的情報												Copyright/Legal information								ID3ｖ3；WCOP
				id32Item.add("WPB");		//出版社の公式Webページ											Publishers official webpage								ID3ｖ3；WPUB
				id32Item.add("WXX");		//ユーザー定義URLリンクフレーム											User defined URL link frame						ID3ｖ3；WXXX
				break;
			case read_WMA_ID33:				//WMAに埋め込まれたID3v3タグの読取り
				id33CoreItem= new ArrayList<String>();		//ID3v3の検索対象アイテム
				id33CoreItem.add("TPE2");		//バンド/オーケストラ/伴奏	バンド/オーケストラ	Album Artist	Album Artist			//10cc(2)TPE2������������10cc
				id33CoreItem.add("TCOM");		//作曲者																				//10cc(2)
				id33CoreItem.add("TYER"); 		//年	レコーディング年/年*15	Date*16	Year Deprecated									//10cc(5)TYER������������1975
				id33CoreItem.add("TALB");		//アルバム/映画/ショーのタイトル														//10cc(6)
				id33CoreItem.add("TPOS");		//セット中の位置	ディスク #	Disc Number	Disc#										//10cc(7')TPOS������������1/1
				id33CoreItem.add("TRCK");		//トラックの番号/セット中の位置	トラック #	Track Number	Track#						//10cc(7)TRCK������������2/8
				id33CoreItem.add("TIT2");		//タイトル/曲名/内容の説明	タイトル	Track Title	Title
				id33CoreItem.add("TPE1");		//主な演奏者/ソリスト	トラック アーティスト	Artist Name	Artist
				id33CoreItem.add("TDAT"); 		//日付	Date*11	Date*12	Year*13 Deprecated
				id33CoreItem.add("TPE3");		//指揮者/演奏者詳細情報	指揮者	<CONDUCTOR>	Conductor
				id33CoreItem.add("TCON");		//ジャンル																				//10cc(8)TCON������������
				id33CoreItem.add("TCOP");		//著作権情報
				id33CoreItem.add("TENC");		//エンコーディング ソフトウェア	<ENCODED BY>	Encoder
				id33CoreItem.add("TEXT");		//作詞家/文書作成者	作詞者	<LYRICIST>	Lyricist
				id33CoreItem.add("APIC");		//付属する画像																			//10cc(5')APIC��(������JPG������ÿØÿà��
				id33CoreItem.add("SYLT");		//同期 歌詞/文書
				id33CoreItem.add("USLT");		//USLT	<UNSYNCED LYRICS>	非同期 歌詞/文書のコピー									//10cc(3)
				id33Item= new ArrayList<String>();			//ID3v3の全アイテム
				id33Item.add("TMOO");			//ムード	ムード	<MOOD>	Mood	ID3v2.4フレーム
				id33Item.add("TPE4");		//翻訳者, リミックス, その他の修正	<ModifiedBy>	<REMIXED BY>	Mix Artist, Artists: Remixer
				id33Item.add("TPUB");		//出版社	発行元	<PUBLISHER>	Publisher												//10cc(11)TPUB������������Universal Distribu
				id33Item.add("TXXX");		//ユーザー定義文字情報フレーム
				id33Item.add("UFID");		//一意的なファイル識別子	タグID
				id33Item.add("AENC");		//Audio encryption
				id33Item.add("COMR");		//Commercial frame]
				id33Item.add("ENCR");		//Encryption method registration
				id33Item.add("EQUA");		//Equalization
				id33Item.add("ETCO");		// Event timing codes]
				id33Item.add("GEOB");		//パッケージ化された一般的なオブジェクト	General encapsulated object]
				id33Item.add("GRID");		//Group identification registration]
				id33Item.add("IPLS");		//協力者一覧 Involved people list]
				id33Item.add("LINK");		//Linked information]
				id33Item.add("MCDI");		//Music CD identifier
				id33Item.add("MLLT");		//MPEG location lookup table]
				id33Item.add("OWNE");		//Ownership frame]
				id33Item.add("PCNT");		//Play counter]
				id33Item.add("POSS");		//Position synchronisation frame]
				id33Item.add("RBUF");		//    [#sec4.19 Recommended buffer size]
				id33Item.add("RVAD");		// Relative volume adjustment]
				id33Item.add("SYTC");		//Synchronized tempo codes
				id33Item.add("TBPM");		//BPM (beats per minute)
				id33Item.add("TDLY");		//Playlist delay
				id33Item.add("TFLT");		//File type
				id33Item.add("TIME"); 		//Time Deprecated
				id33Item.add("TIT1");		//Content group description
				id33Item.add("TIT3");		// Subtitle/Description refinement
				id33Item.add("TKEY");		//Initial key
				id33Item.add("TLAN");		// Language(s)]
				id33Item.add("TLEN");		// Length]
				id33Item.add("TMED");		// Media type]
				id33Item.add("TOAL");		//Original album/movie/show title
				id33Item.add("TOFN");		//Original filename]
				id33Item.add("TOLY");		//  Original lyricist(s)/text writer(s)]
				id33Item.add("TOPE"); 		//Original artist(s)/performer(s)	 Deprecated
				id33Item.add("TORY");		//Original release year]
				id33Item.add("TOWN");		// File owner/licensee
				id33Item.add("TRDA");		// Recording dates	Deprecated
				id33Item.add("TRSN");		// Internet radio station name
				id33Item.add("TRSO");		// Internet radio station owner
				id33Item.add("TSIZ");		 //Size	 Deprecated
				id33Item.add("TSRC");		//ISRC (international standard recording code)
				id33Item.add("TSSE");		//Software/Hardware and settings used for encoding
				id33Item.add("WCOM");		//Commercial information]
				id33Item.add("WCOP");		// Copyright/Legal information
				id33Item.add("WOAF");		// Official audio file webpage
				id33Item.add("WOAR");		// Official artist/performer webpage
				id33Item.add("WOAS");		// Official audio source webpage
				id33Item.add("WORS");		//  Official internet radio station homepage
				id33Item.add("WPAY");		//  Payment
				id33Item.add("WPUB"); 		//Publishers official webpage
				id33Item.add("USER");		//Terms of use
				id33Item.add("WXXX");		//User defined URL link frame
				id33Item.add("COMM");		//コメント																				//10cc(7')
				id33Item.add("POPM");		//人気メーター																				//10cc(2')Windows Media Player 9 Seri
				id33Item.add("PRIV");		//プライベートフレーム									//☆複数出現する；10cc(4)(10)PRIV
				id33Item.add("TOLY");		//Original lyricist(s)/text writer(s)]
				id33Item.add("RVRB");		//Reverb
				id33Item.add("TOWN");		//ファイルの所有者/ライセンシー
				id33Item.add("TSOA");		//アルバム（読み）										ALBUMSORT						ID3ｖ2；--	ID3ｖ3；
				id33Item.add("TSOP");		//アーティスト（読み）									ARTISTSORT						ID3ｖ2；--	ID3ｖ3；
				id33Item.add("TSOC");		//作曲者（読み）										SortComposer					ID3ｖ2；?-	ID3ｖ3；TSOC	COMPOSERSORT
				break;
			case read_WMA_AAC:				//WMAに埋め込まれたAAC Atomの読取り
				qtItemListCoar= new ArrayList<String>();		//QuickTime ItemList Tagsで確実に書き込まれている部分
				qtItemListCoar.add("trkn");	//トラックナンバー										TrackNumber						ID3ｖ2；TRK	ID3ｖ3；TRCK	TTRACK/TOTALTARCK
				qtItemListCoar.add("disk");	//ディスクナンバー										DiskNumber	Part of a set		ID3ｖ2；TPA	ID3ｖ3；TPOS	DISCNUMBER/TOTALDISC
				qtItemListCoar.add("PICT");			//10付属する画像									Preview PICT					ID3ｖ2；PIC	ID3ｖ3；APIC	Attached picture
				qtItemListCoar.add("pict");			//11付属する画像									PreviewPICT						ID3ｖ2；PIC	ID3ｖ3；APIC	Attached picture
				qtItemListCoar.add("thum");			//12画像のサムネール								ThumbnailImage					ID3ｖ2；--	ID3ｖ3；--
				qtItemListCoar.add("covr");	//付属する画像											CoverArt						ID3ｖ2；PIC	ID3ｖ3；APIC	Attached picture
				qtItemListCoar.add("©gen");	//ユーザ定義ジャンル									GENRE							ID3ｖ2；TCO	ID3ｖ3；TCON	GENRE
				qtItemListCoar.add("©grp");	//グループ												CONTENTGROUP					ID3ｖ2；?-	ID3ｖ3；TIT1
				qtItemListCoar.add("©ART");	//アーティスト																			ID3ｖ2；TP1	ID3ｖ3；TPE1	ARTIST[1]
				qtItemListCoar.add("©alb");	//albm?アルバム/映画/ショーのタイトル					Album							ID3ｖ2；TAL	ID3ｖ3；TALB	Album/Movie/Show title
				qtItemListCoar.add("©nam");	//アルバム（読み）										Track Title						ID3ｖ2；--	ID3ｖ3；TSOA	ALBUMSORT[1][2]
				qtItemListCoar.add("©com");	//作曲者												Composer						ID3ｖ2；TCM	ID3ｖ3；TCOM	Composer?
				qtItemListCoar.add("©cpy");	//権利元												Copyright	Copyright			ID3ｖ2；TCR	ID3ｖ3；TCOP	Copyright message
				qtItemListCoar.add("©day");	//リリース日（年）										YEAR							ID3ｖ2；?	ID3ｖ3；TYER	YEAR
				qtItemListCoar.add("©trk");	//trkn?トラックの番号/セット中の位置					Track							ID3ｖ2；TRK	ID3ｖ3；TRCK	TrackNumber	Track number/Position in set
				qtItemListCoar.add("©wrt");	//作曲者												COMPOSER						ID3ｖ2；TCM	ID3ｖ3；TCOM	Composer
				qtItemListCoar.add("aART");	//アルバムアーティスト									ALBUMARTIST						ID3ｖ2；--	ID3ｖ3；TPE2	Lead artist(s)/Lead performer(s)/Soloist(s)/Performing group
				qtItemListCoar.add("@pti");	//原題													Parent Title					ID3ｖ2；--	ID3ｖ3；TOAL	//Original album/movie/show title?
				qtItemListCoar.add("©too");	//©enc?エンコーディング ソフトウェア					Encoder							ID3ｖ2；TEN	ID3ｖ3；TENC	Encoded by
				qtItemListCoar.add("©lyr");	//非同期 歌詞/文書のコピー								Lyrics							ID3ｖ2；ULT	ID3ｖ3；USLT	Unsychronized lyric/text transcription

				qtItemList= new ArrayList<String>();			//QuickTime ItemList Tags
				qtItemList.add("moov");			//2ヘッダ情報。複数のBOXの集合。								Movie				ID3ｖ2；--	ID3ｖ3；--
				qtItemList.add("mdat");			//3ビデオやオーディオデータ自体を格納。複数に分割することもある。	Movie Data		ID3ｖ2；--	ID3ｖ3；--		>ItemList>©lyr
				qtItemList.add("frea");			//4写真関連情報										Kodak_frea						ID3ｖ2；--	ID3ｖ3；--
				qtItemList.add("skip");			//5							Canon Skip Tags			CanonSkip						ID3ｖ2；--	ID3ｖ3；--
				qtItemList.add("wide");			//6													Wide?							ID3ｖ2；--	ID3ｖ3；USER	Terms of use
				qtItemList.add("pnot");			//7付属する画像			QuickTime Preview Tags		Preview							ID3ｖ2；PIC	ID3ｖ3；APIC	Attached picture
				//有無不明
				qtItemList.add("mdat-offset");	//8													Movie Data Offset				ID3ｖ2；--	ID3ｖ3；--
				qtItemList.add("mdat-size");	//9サイズ											MovieDataSize					ID3ｖ2；TSI	ID3ｖ3；TSIZ;	Size(Size	 Deprecated?)
				qtItemList.add("_htc");			//13												HTCInfo							ID3ｖ2；--	ID3ｖ3；--
				qtItemList.add("slmt");			//14												Unknown_slmt?					ID3ｖ2；--	ID3ｖ3；--
				qtItemList.add("ardt");			//15												ARDrone File					ID3ｖ2；--	ID3ｖ3；--
				qtItemList.add("junk");			//16												Junk?							ID3ｖ2；--	ID3ｖ3；--
				qtItemList.add("prrt");			//17												ARDrone Telemetry				ID3ｖ2；--	ID3ｖ3；--
				qtItemList.add("meta");	//	Index4	5	MediaLanguageCode														ID3ｖ2；CRM	ID3ｖ3；？	Encrypted meta frame?
				qtItemList.add("cmov");	//							QuickTime Tags.QuickTime Movie Tags.Compressed movie		ID3ｖ2；--	ID3ｖ3；--
				qtItemList.add("htka");	//										QuickTime Tags.QuickTime Movie Tags.htka		ID3ｖ2；--	ID3ｖ3；--
				qtItemList.add("iods");	//										QuickTime Tags.QuickTime Movie Tags.iods		ID3ｖ2；--	ID3ｖ3；--
				qtItemList.add("mvhd");	//作成日時などビデオ、オーディオには関係ない全体的な情報。		MovieHeader				ID3ｖ2；--	ID3ｖ3；
				qtItemList.add("trak");	//ビデオ、オーディオそれぞれのトラックのヘッダ情報。									ID3ｖ2；--	ID3ｖ3；
				qtItemList.add("trak");	//	trak> tagc	Media Characteristic Tags												ID3ｖ2；--	ID3ｖ3；
				qtItemList.add("udta");	//						User Data		QuickTime Tags.QuickTime Movie Tags.udta		ID3ｖ2；--	ID3ｖ3；USER	Terms of use
				qtItemList.add("uuid");	//エンコーダ固有の拡張情報。デコーダは無視してよい。									ID3ｖ2；--	ID3ｖ3；--
				qtItemList.add("ilst");	//	ItemList > "©lyr					QuickTime Tags.QuickTime Meta Tags.ilst			ID3ｖ2；--	ID3ｖ3；--
				qtItemList.add("bxml");	//						BinaryXML?		QuickTime Tags.QuickTime Meta Tags.bxml			ID3ｖ2；--	ID3ｖ3；--
				qtItemList.add("dinf");	//					DataInformation?	QuickTime Tags.QuickTime Meta Tags.dinf			ID3ｖ2；--	ID3ｖ3；--
				qtItemList.add("free");	//	Kodak Free?	/Free?					QuickTime Tags.QuickTime Meta Tags.free			ID3ｖ2；--	ID3ｖ3；--
				qtItemList.add("iinf");	//					ItemInformation?	QuickTime Tags.QuickTime Meta Tags.iinf			ID3ｖ2；--	ID3ｖ3；--
				qtItemList.add("loca");	//A metadata value may optionally be tagged with its locale so that it may be chosen based upon the user's language, 		ID3ｖ2；--	ID3ｖ3；--
				qtItemList.add("iloc");	//					ItemLocation?q		QuickTime Tags.QuickTime Meta Tags.iloc			ID3ｖ2；--	ID3ｖ3；--
				qtItemList.add("ipmc");	//					IPMPControl?		QuickTime Tags.QuickTime Meta Tags.ipmc			ID3ｖ2；--	ID3ｖ3；--
				qtItemList.add("ipro");	//					ItemProtection?		QuickTime Tags.QuickTime Meta Tags.ipro			ID3ｖ2；--	ID3ｖ3；--
				qtItemList.add("keys");	//					Keys				QuickTime Tags.QuickTime Meta Tags.keys			ID3ｖ2；--	ID3ｖ3；--
				qtItemList.add("pitm");	//				PrimaryItemReference?	QuickTime Tags.QuickTime Meta Tags.pitm			ID3ｖ2；--	ID3ｖ3；--
				qtItemList.add("xml ");	//					XML					QuickTime Tags.QuickTime Meta Tags.xml 			ID3ｖ2；--	ID3ｖ3；--
				qtItemList.add("tkhd");	//trak>トラックの基本属性。再生時間、表示解像度など。									ID3ｖ2；--	ID3ｖ3；--
				qtItemList.add("edts");	//trak>トラック上のデータと再生の情報。													ID3ｖ2；--	ID3ｖ3；--
				qtItemList.add("elst");	//trak>edts>データ上の再生範囲と速度のリスト。											ID3ｖ2；--	ID3ｖ3；--
				qtItemList.add("mdia");	//trak>トラックのデータに関するさまざまな情報。											ID3ｖ2；--	ID3ｖ3；--
				qtItemList.add("mdhd");	//trak>mdia>トラックの基本属性。該当トラックの再生時間など。							ID3ｖ2；--	ID3ｖ3；--
				qtItemList.add("hdlr");	//trak>mdia>トラックの種別。該当トラックがビデオかオーディオかを示す。				ID3ｖ2；--	ID3ｖ3；--
				qtItemList.add("minf");	//?>?>トラックデータの固有情報。複数のBOXの集合。										ID3ｖ2；--	ID3ｖ3；--
				qtItemList.add("vmhd");	//?>?>ビデオトラックデータ固有の情報。													ID3ｖ2；--	ID3ｖ3；--
				qtItemList.add("smhd");	//?>?>オーディオトラックデータ固有の情報。												ID3ｖ2；--	ID3ｖ3；--
				qtItemList.add("dinf");	//?>?>トラックデータの存在場所の情報。													ID3ｖ2；--	ID3ｖ3；--
				qtItemList.add("dref");	//?>?>トラックデータの存在場所を示す。別ファイルに存在することもある。					ID3ｖ2；--	ID3ｖ3；--
				qtItemList.add("stbl");	//trak>mdia>mdhd>トラックデータの単位（ビデオの場合フレーム）ごとの位置情報。			ID3ｖ2；--	ID3ｖ3；--
				qtItemList.add("stsd");	//trak>mdia>mdhd>トラックデータ再生のためのヘッダ情報。									ID3ｖ2；--	ID3ｖ3；--
				qtItemList.add("stts");	//trak>mdia>mdhd>トラックデータの単位ごとの再生時間の表。								ID3ｖ2；--	ID3ｖ3；--
				qtItemList.add("stsc");	//trak>mdia>mdhd>mdat上のトラックデータの固まりごとの長さ（ビデオはフレーム数）の表。	ID3ｖ2；--	ID3ｖ3；--
				qtItemList.add("stco");	//trak>mdia>mdhd>ファイル上のトラックデータの固まりの先頭位置。stscと連携。				ID3ｖ2；--	ID3ｖ3；--
				qtItemList.add("stsz");	//trak>mdia>mdhd>トラックデータ再生単位ごとのデータ長の表。								ID3ｖ2；--	ID3ｖ3；--
				qtItemList.add("stss");	//trak>mdia>mdhd>トラックデータのランダムアクセス可能な位置（フレーム番号）の表			ID3ｖ2；--	ID3ｖ3；--
				qtItemList.add("©pub");	//出版社/発行元											Publisher						ID3ｖ2；TPB	ID3ｖ3；TPUB	Publisher
				qtItemList.add("soar");	//アーティスト（読み）									ARTISTSORT						ID3ｖ2；--	ID3ｖ3；TSOP
				qtItemList.add("©cmt");	//コメント												Comment							ID3ｖ2；COM	ID3ｖ3；COMM	Comments
				qtItemList.add("©des");	//説明													Description	Track				ID3ｖ2；--	ID3ｖ3；--		SUBTITLE
				qtItemList.add("©enc");	//エンコーディング ソフトウェア							EncodedBy						ID3ｖ2；TEN	ID3ｖ3；TENC	Encoded by
				qtItemList.add("@sti");	//														Short Title						ID3ｖ2；--	ID3ｖ3；--
				qtItemList.add("albm");	//アルバム/映画/ショーのタイトル						Album/Movie/Show title			ID3ｖ2；TAL	ID3ｖ3；TALB
				qtItemList.add("desc");	//タイトル/曲名/内容の説明	タイトル					Description						ID3ｖ2；TT2	ID3ｖ3；TIT2;	Track Title	Title/Title/Songname/Content description
				qtItemList.add("yrrc");	//年	レコーディング年/年*15	Date*16	Year Deprecated	Year		Year				ID3ｖ2；TYE	ID3ｖ3；TYER
				qtItemList.add("akID");	//アカウントの種類			編集不可					Apple Store Account Type		ID3ｖ2；--	ID3ｖ3；--
				qtItemList.add("apID");	//アカウント情報					ITUNESACCOUNT		Apple Store Account				ID3ｖ2；--	ID3ｖ3；--
				qtItemList.add("atID");	//アーティスト識別子	編集不可						Album Title ID					ID3ｖ2；MCI?ID3ｖ3；MCDI?	Music CD Identifier?
				qtItemList.add("auth");	//														Author							ID3ｖ2；--	ID3ｖ3；--
				qtItemList.add("catg");	//ポッドキャストカテゴリ								Category						ID3ｖ2；--	ID3ｖ3；--		PODCASTCATEGORY
				qtItemList.add("cnID");	//コンテンツ識別子										AppleStoreCatalogID				ID3ｖ2；--	ID3ｖ3；--		ITUNESCATALOGID
				qtItemList.add("cprt");	//著作権												Copyright						ID3ｖ2；TCR	ID3ｖ3；TCOP	Copyright message
				qtItemList.add("dscp");	//タイトル/曲名/内容の説明	タイトル					Description						ID3ｖ2；TT2	ID3ｖ3；TIT2;	Track Title	Title/Title/Songname/Content description
				qtItemList.add("egid");	//ポッドキャストエピソードユニークID					Episode Global Unique ID		ID3ｖ2；--	ID3ｖ3；--		PODCASTID
				qtItemList.add("geID");	//ジャンル識別子		編集不可						GenreID							ID3ｖ2；--	ID3ｖ3；--
				qtItemList.add("gnre");	//ジャンル												Genre	Content type			ID3ｖ2；TCO	ID3ｖ3；TCON
				qtItemList.add("soaa");	//バンド/オーケストラ/伴奏	バンド/オーケストラ	?		SortAlbumArtistt				ID3ｖ2；TP2	ID3ｖ3；TPE2	Band/Orchestra/Accompaniment/Album Artis?
				qtItemList.add("soal");	//オリジナルのアルバム/映画/ショーのタイトル?			Sort Album						ID3ｖ2；TOT	ID3ｖ3；TOAL	Original album/Movie/Show title
				qtItemList.add("soco");	//作曲者（読み）										SortComposer					ID3ｖ2；?-	ID3ｖ3；TSOC	COMPOSERSORT
				qtItemList.add("sonm");	//タイトル（読み）										Sort Name						ID3ｖ2；--	ID3ｖ3；TIT3		TITLESORT
				qtItemList.add("cpil");	//コンピレーションの明示								Compilation						ID3ｖ2；--	ID3ｖ3；--		COMPILATION
				qtItemList.add("----");	//														iTunesInfo						ID3ｖ2；--	ID3ｖ3；--
				qtItemList.add("©nrt");	//														Narrator						ID3ｖ2；--	ID3ｖ3；--
				qtItemList.add("@PST");	//														Parent Short Title				ID3ｖ2；--	ID3ｖ3；--
				qtItemList.add("@ppi");	//														Parent ProductID				ID3ｖ2；--	ID3ｖ3；--
				qtItemList.add("AACR");	//														Unknown_AACR?					ID3ｖ2；--	ID3ｖ3；--
				qtItemList.add("CDEK");	//														Unknown_CDEK?					ID3ｖ2；--	ID3ｖ3；--
				qtItemList.add("CDET");	//														Unknown_CDET?					ID3ｖ2；--	ID3ｖ3；--
				qtItemList.add("GUID");	//														GUID							ID3ｖ2；--	ID3ｖ3；--
				qtItemList.add("VERS");	//														ProductVersion					ID3ｖ2；--	ID3ｖ3；--
				qtItemList.add("grup");	//														Grouping						ID3ｖ2；--	ID3ｖ3；--
				qtItemList.add("gshh");	//														GoogleHostHeade					ID3ｖ2；--	ID3ｖ3；--
				qtItemList.add("gspm");	//														GooglePingMessage				ID3ｖ2；--	ID3ｖ3；--
				qtItemList.add("gspu");	//														GooglePingURL					ID3ｖ2；--	ID3ｖ3；--
				qtItemList.add("gssd");	//														GoogleSourceData				ID3ｖ2；--	ID3ｖ3；--
				qtItemList.add("gsst");	//														GoogleStartTime					ID3ｖ2；--	ID3ｖ3；--
				qtItemList.add("gstd");	//														GoogleTrackDuration				ID3ｖ2；--	ID3ｖ3；--
				qtItemList.add("hdvd");	//	?													HDVideo	?						ID3ｖ2；--	ID3ｖ3；--
				qtItemList.add("hdtv");	//ビデオ解像度の明示		ITUNESHDVIDEO				HDVideo							ID3ｖ2；--	ID3ｖ3；--
				qtItemList.add("itnu");	//														iTunesU							ID3ｖ2；--	ID3ｖ3；--
				qtItemList.add("keyw");	//ポッドキャストキーワード		編集不可				Keyword							ID3ｖ2；--	ID3ｖ3；--
				qtItemList.add("ldes");	//サブタイトル/説明の追加情報							Long Description				ID3ｖ2；--	ID3ｖ3；TIT3;	Subtitle/Description refinement?
				qtItemList.add("pcst");	//ポッドキャストであることを明示						Podcast							ID3ｖ2；--	ID3ｖ3；--
				qtItemList.add("perf");	//指揮者/演奏者詳細情報									Performer						ID3ｖ2；TP3	ID3ｖ3；TPE3	Conductor/Performer refinement
				qtItemList.add("pgap");	//ギャップレスコンテンツの明示							PlayGap							ID3ｖ2；--	ID3ｖ3；--		ITUNESGAPLESS
				qtItemList.add("plID");	//プレイリスト（アルバム）識別子	編集不可			PlayListID						ID3ｖ2；--	ID3ｖ3；--
				qtItemList.add("prID");	//														ProductID						ID3ｖ2；--	ID3ｖ3；--
				qtItemList.add("purd");	//購入日												ITUNESPURCHASEDATE				ID3ｖ2；--	ID3ｖ3；--		ITUNESPURCHASEDATE
				qtItemList.add("purl");	//ポッドキャストURL										Podcast URL						ID3ｖ2；--	ID3ｖ3；--		PODCASTURL
				qtItemList.add("rate");	//人気メーター ?										RatingPercent					ID3ｖ2；POP	ID3ｖ3；POPM	Popularimeter
				qtItemList.add("rldt");	//オリジナルのリリース年?								Release Date					ID3ｖ2；TOR	ID3ｖ3；TORY	Original release year
				qtItemList.add("rtng");	//保護者のためのレートの明示?番組?番組（読み）			Rating?	TVSHOW?					ID3ｖ2；--	ID3ｖ3；--		ITUNESADVISORY?	TVSHOW?
				qtItemList.add("sfID");	//ストアの国				編集不可					AppleStore Country				ID3ｖ2；--	ID3ｖ3；--
				qtItemList.add("sosn");	//														Sort Show						ID3ｖ2；--	ID3ｖ3；--
				qtItemList.add("stik");	//メディアの種類の明示									MediaType						ID3ｖ2；TMT?ID3ｖ3；TMED?	ITUNESMEDIATYPE
				qtItemList.add("titl");	//タイトル/曲名/内容の説明	タイトル					Title							ID3ｖ2；TT2	ID3ｖ3；TIT2;	Track Title	Title/Title/Songname/Content description
				qtItemList.add("tmpo");	//一分間の拍数											BeatsPerMinute					ID3ｖ2；TBP	ID3ｖ3；TBPM	BPM (Beats Per Minute)
				qtItemList.add("tven");	//エピソードID											TVEpisodeID						ID3ｖ2；--	ID3ｖ3；--
				qtItemList.add("tves");	//														TVEpisode						ID3ｖ2；--	ID3ｖ3；--
				qtItemList.add("tvnn");	//放送局												TVNetworkName					ID3ｖ2；--	ID3ｖ3；--
				qtItemList.add("tvsh");	//														TVShow							ID3ｖ2；--	ID3ｖ3；--
				qtItemList.add("tvsn");	//シーズン												TVSeason						ID3ｖ2；--	ID3ｖ3；--
				qtItemList.add("tvsn");	//														TVSeason						ID3ｖ2；--	ID3ｖ3；--
				qtItemList.add("lrcu");	//LyricsURI							QuickTime UserData Tags								ID3ｖ2；ULT	ID3ｖ3；USLT
				break;
//				default:
//					break;
			}
			result_USLT =null;	//WM/Lyricsr			//非同期 歌詞/文書のコピー								Lyrics							ID3ｖ2；ULT	ID3ｖ3；USLT	Unsychronized lyric/text transcription
			result_TALB =null;	//WM/AlbumTitlem	//アルバム/映画/ショーのタイトル						Album/Movie/Show title			ID3ｖ2；TAL	ID3ｖ3；TALB
			result_TSOA =null;	//Title			//アルバム（読み）										ALBUMSORT						ID3ｖ2；--	ID3ｖ3；TSOA
			result_TIT3 =null;	//WM/TitleSortOrder	M/SubTitle//		//サブタイトル/説明の追加情報							Long Description				ID3ｖ2；--	ID3ｖ3；TIT3;	Subtitle/Description refinement?
			//sonm");	//タイトル（読み）											Sort Name						ID3ｖ2；--	ID3ｖ3；TIT3		TITLESORT
			result_TPE1 =null;	//Author			//アーティスト																			ID3ｖ2；TP1	ID3ｖ3；TPE1	ARTIST[1]
			result_TSOP =null;	//WM/ArtistSortOrder		//アーティスト（読み）									ARTISTSORT						ID3ｖ2；--	ID3ｖ3；TSOP
			result_TPE2 =null;	//WM/AlbumArtistM/AlbumArtistSortOrder//		//アルバムアーティスト									ALBUMARTIST						ID3ｖ2；--	ID3ｖ3；TPE2	Lead artist(s)/Lead performer(s)/Soloist(s)/Performing group
			result_TYER =null;	//WM/Year		//年	レコーディング年/年*15	Date*16	Year Deprecated	Year		Year				ID3ｖ2；TYE	ID3ｖ3；TYER
			//©day");		//リリース日（年）										YEAR							ID3ｖ2；?	ID3ｖ3；TYER	YEAR
			result_TCOM =null;	//WM/Composer	//作曲者																				//10cc(2)
			result_TSOC =null;	//WM/ComposerSortOrder			//作曲者（読み）										SortComposer					ID3ｖ2；?-	ID3ｖ3；TSOC	COMPOSERSORT
			result_TPE3 =null;	//WM/Conductor					//指揮者/演奏者詳細情報									Performer						ID3ｖ2；TP3	ID3ｖ3；TPE3	Conductor/Performer refinement
			result_TPE4 =null;	//WM/ModifiedBy					翻訳者, リミックス, その他の修正	<ModifiedBy>	<REMIXED BY>	Mix Artist, Artists: Remixer
			result_TIT1 =null;	//WM/ContentGroupDescription				グループ												CONTENTGROUP					ID3ｖ2；?-	ID3ｖ3；TIT1
			result_TRCK =null;	//WM/TrackNumber				トラックの番号/セット中の位置							Track							ID3ｖ2；TRK	ID3ｖ3；TRCK	TrackNumber	Track number/Position in set
			result_TPOS =null;	//WM/PartOfSet					//ディスクナンバー										DiskNumber	Part of a set		ID3ｖ2；TPA	ID3ｖ3；TPOS	DISCNUMBER/TOTALDISC
			result_cpil =null;	//WM/IsCompilation			//コンピレーションの明示								Compilation						ID3ｖ2；--	ID3ｖ3；--		COMPILATION
			result_COMR =null;	//Description				//コメント												Comment							ID3ｖ2；COM	ID3ｖ3；COMM	Comments
			result_TCON =null;	//WM/Genre					//ジャンル												Genre	Content type			ID3ｖ2；TCO	ID3ｖ3；TCON
			result_POPM =null;	//WM/SharedUserRating			//人気メーター ?										RatingPercent					ID3ｖ2；POP	ID3ｖ3；POPM	Popularimeter
			result_TBPM =null;	//WM/BeatsPerMinute			//一分間の拍数											BeatsPerMinute					ID3ｖ2；TBP	ID3ｖ3；TBPM	BPM (Beats Per Minute)
			result_TMOO =null;	//WWM/Mood"					ムード	ムード	<MOOD>	Mood	ID3v2.4フレーム
			result_TMED =null;	//WM/Media					//メディアの種類の明示									MediaType						ID3ｖ2；TMT?ID3ｖ3；TMED?	ITUNESMEDIATYPE
			result_TLAN =null;	//WM/Language				 Language(s)]
			result_TCOP =null;	//Copyright					//著作権情報	権利元				Copyright	Copyright								ID3ｖ2；TCR	ID3ｖ3；TCOP	Copyright message
			result_WCOP =null;	//LICENSE					 Copyright/Legal information
			result_TENC =null;	//WM/EncodedBy				//エンコーディング ソフトウェア							EncodedBy						ID3ｖ2；TEN	ID3ｖ3；TENC	Encoded by
			result_TSSE =null;	//WM/EncodingSettings		Software/Hardware and settings used for encoding
			result_TSRC =null;	//ISRC (international standard recording code)
			result_UFID =null;	//MusicBrainz/Track Id		一意的なファイル識別子	タグID
			result_Writer =null;					//WM/Writer
			result_Engineer =null;					//WM/Engineer");							//												Engineer						ID3ｖ2；--	v3；TIPL	v4:IPLS	qt;--
			result_Producer =null;					//WM/Producer");							//制作者										Producer						ID3ｖ2；--	v3；TIPL	v4:IPLS	qt;--
			result_Mixer =null;						//WM/Mixer");							//												Mixer							ID3ｖ2；--	v3；TIPL	v4:IPLS	qt;--
			result_CatalogNo =null;					//WM/CatalogNo");						//ユーザー定義文字情報フレーム			User defined text information frame		ID3ｖ2；TXX	v3；TXXX			qt;--
			result_Album_Status =null;				//MusicBrainz/Album Status");			//												Release Status					ID3ｖ2；TXX	v3；TXXX			qt;--
			result_Album_Type =null;				//MusicBrainz/Album Type");				//												Release Type					ID3ｖ2；TXX	v3；TXXX			qt;--
			result_Album_Release_Country =null;		//MusicBrainz/Album Release Country");	//												Release Country					ID3ｖ2；TXX	v3；TXXX			qt;--
			result_Script =null;					//WM/Script");							//												Release Country					ID3ｖ2；TXX	v3；TXXX			qt;--
			result_Barcode =null;					//WM/Barcode");							//												Barcode							ID3ｖ2；TXX	v3；TXXX			qt;--
			result_Release_Track_Id =null;			//MusicBrainz/Release Track Id");		//												MusicBrainz Release Id			ID3ｖ2；TXX	v3；TXXX			qt;--
			result_Artist_Id =null;					//MusicBrainz/Artist Id");				//												MusicBrainz Artist Id			ID3ｖ2；TXX	v3；TXXX			qt;--
			result_Album_Artist_Id =null;			//MusicBrainz/Album Artist Id");			//												MusicBrainz Release Artist Id	ID3ｖ2；TXX	v3；TXXX			qt;--
			result_Release_Group_Id =null;			//MusicBrainz/Release Group Id");		//												MusicBrainz Release Group Id	ID3ｖ2；TXX	v3；TXXX			qt;--
			result_Work_Id =null;					//MusicBrainz/Work Id");					//												MusicBrainz Work Id				ID3ｖ2；TXX	v3；TXXX			qt;--
			result_TRM_Id =null;					//"MusicBrainz/TRM Id");					//												MusicBrainz TRM Id				ID3ｖ2；TXX	v3；TXXX			qt;--
			result_Disc_Id =null;					//"MusicBrainz/Disc Id");					//												MusicBrainz Disc Id				ID3ｖ2；TXX	v3；TXXX			qt;--
			result_Acoustid_Id =null;				//Acoustid/Id");							//												AcoustID						ID3ｖ2；TXX	v3；TXXX			qt;--
			result_Fingerprint =null;				//Acoustid/Fingerprint");				//												AcoustID Fingerprint			ID3ｖ2；TXX	v3；TXXX			qt;--
			result_PUID =null;						//MusicIP/PUID");						//												MusicIP PUID					ID3ｖ2；TXX	v3；TXXX			qt;--
//			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}
	}

	public void makeWmaList(int reqCode) {	//WMAフィールド名リストをList<String> syougouに作成
		String result =null;
		final String TAG = "makeWmaList";
		String dbMsg= "";
		try{
			if(syougou == null){
				syougou = new ArrayList<String>();
			} else {
				syougou.clear();
			}
			switch(reqCode) {
			case read_WMA_ITEM:
				syougou.addAll(wmaCoreItem);	//WMAで検索するアイテム
				syougou.addAll(wmaItem);
				break;
			case read_WMA_ID32:				//WMAに埋め込まれたID3v2タグの読取り
				syougou.addAll(id32CoreItem);		//ID3v2の検索対象アイテム
				syougou.addAll(id32Item);			//ID3v2の全アイテム
				break;
			case read_WMA_ID33:					//WMAに埋め込まれたID3v3タグの読取り
				syougou.addAll(id33CoreItem);		//ID3v3の検索対象アイテム
				syougou.addAll(id33Item);			//ID3v3の全アイテム
				break;
			case read_WMA_AAC:					//WMAに埋め込まれたAAC Atomの読取り
				syougou.addAll(qtItemListCoar);		//QuickTime ItemList Tagsで確実に書き込まれている部分
				syougou.addAll(qtItemList);			//QuickTime ItemList Tags
				break;
//				default:
//					break;
			}
			if(kensaku == null){
				kensaku = new ArrayList<String>();//検索するフレーム名
			} else {
				kensaku.clear();
			}
			switch(reqCode) {
			case read_WMA_ITEM:				//最小限の設定読取り
				kensaku.add("WM/Lyrics");							//非同期 歌詞/文書のコピー						Lyrics	APEv2： 				ID3ｖ2；ULT	v3；USLT			qt;©lyr		Unsychronized lyric/text transcription
				kensaku.addAll(wmaCoreItem);	//WMAで検索するアイテム
				break;
			case read_WMA_ID32:					//WMAに埋め込まれたID3v2タグの読取り
				kensaku.add("ULT");					//非同期 歌詞/文書のコピー										Unsychronized lyric/text transcription					ID3ｖ3；USLT
				kensaku.add("SLT");					//同期 歌詞/文書												Synchronized lyric/text									ID3ｖ3；SYLT
				kensaku.addAll(id32CoreItem);		//ID3v2の検索対象アイテム
				break;
			case read_WMA_ID33:					//WMAに埋め込まれたID3v3タグの読取り
				kensaku.add("USLT");				//USLT	<UNSYNCED LYRICS>	非同期 歌詞/文書のコピー									//10cc(3)
				kensaku.add("SYLT");				//同期 歌詞/文書
				kensaku.addAll(id33CoreItem);		//ID3v3の検索対象アイテム
				break;
			case read_WMA_AAC:					//WMAに埋め込まれたAAC Atomの読取り
				kensaku.add("©lyr");				//非同期 歌詞/文書のコピー								Lyrics							ID3ｖ2；ULT	ID3ｖ3；USLT	Unsychronized lyric/text transcription
				kensaku.addAll(qtItemListCoar);		//QuickTime ItemList Tagsで確実に書き込まれている部分
				break;
			}
		}catch (Exception e) {
		myErrorLog(TAG,dbMsg + "で"+e.toString());
	}
}

	public void itemReadWmaBody(String readStr , String target){		//WMAのオブジェクト読取り
		final String TAG = "itemReadWmaBody";
		String dbMsg= "";
		try{
			dbMsg= "target = " + target ;
			int readInt = readStr.length();
			if(200 < readInt){
				dbMsg +=";" + readStr.substring(0, 100) +  "～";			// + readStr.substring(readInt-100, readInt);
			}else{
				dbMsg +=";" + readStr;
			}
/*			String henkann = "UTF-16LE";
			dbMsg +=",henkann=" + henkann;
			int startInt = 	retNextNullPoint( readStr , 5 );					//渡された文字列にnullが発生するポイントを返す
			dbMsg= dbMsg  + "," + startInt +"文字以降";
			if( readInt <= startInt){
				startInt = 0;
				dbMsg= dbMsg  +">>" + startInt +"文字以降";
			}
			readStr = readStr.substring(startInt);
			readStr = new String(readStr.getBytes(motoEncrod), henkann);
*/
		//	byte[] buffer = str2byteEncord( motoEncrod,  result);			//readStr.toCharArray();
//			int bufferSize = buffer.length;
//			for(int i = 0 ; i < bufferSize ; i++){
////				dbMsg += 	",[" + i + "]=" + testChar[i];
////				String testStr =  "0x" + Integer.toHexString(testChar[i]);
////				dbMsg += 	"=" + testStr;
//			//	if(buffer[i] < 0){
//					buffer[i] = (byte) (buffer[i] + 128);
////			//	} else {
////					testChar[i] = (byte) (testChar[i] + 127);
//			//	}
//			}

		//	readStr = new String( buffer, "UTF-16LE" );		// =buffer.toString();		//


//			readInt = readStr.length();
//			if(200 < readInt){
//				dbMsg +=">>" + readStr.substring(0, 100) +  "～";			// + readStr.substring(readInt-100, readInt);
//			}else{
//				dbMsg +=">>" + readStr;
//			}

			if(target.equals("ULT")){
				result_USLT = readStr;					//©lyr			//非同期 歌詞/文書のコピー								Lyrics							ID3ｖ2；ULT	ID3ｖ3；USLT	Unsychronized lyric/text transcription
			}else if(target.equals("USLT")){
				result_USLT = readStr;					//©lyr			//非同期 歌詞/文書のコピー								Lyrics							ID3ｖ2；ULT	ID3ｖ3；USLT	Unsychronized lyric/text transcription
			}else if(target.equals("©lyr")){
				result_USLT = readStr;					//©lyr			//非同期 歌詞/文書のコピー								Lyrics							ID3ｖ2；ULT	ID3ｖ3；USLT	Unsychronized lyric/text transcription
			}else if(target.equals("WM/Lyrics")){
				result_USLT = readStr;					//©lyr			//非同期 歌詞/文書のコピー								Lyrics							ID3ｖ2；ULT	ID3ｖ3；USLT	Unsychronized lyric/text transcription
			}
			/*
 * ISO-8859-1(ISO8859_2)	SO-8859-1,	31文字以降;MR" ¤UnÅ&UÍ¹ý¥(B$ÓgàJA)©U(2µP$TC@H%	%"B]JP-¬_~Å8pPQI!ô=I		JA)©U(2µP$TC@H%	%"B]JP-¬_~Å8pPQI!ô
 * Cp1252					Cp1252,		31文字以降;MR" ¤UnÅ&UÍ¹ý¥(B$Ógà	JA)©U(2µP$TC@H%	%"B]JP-¬_~Å8pPQI!ô	JA™)©U(2µP$TC@Š†‘H%	%"B]JP”-¬_~Å8pPQI!ô
 * windows-1252				21168613バイト	0&²uŽfÏ¦Ù��ª��bÎly������������������¡Ü«ŒG©ÏŽä��À Seh��������������ÞÍòÇ†ÎÆK­ýá>=ó4-åC��������ˆaJCžÏ×������������ðÊM��～é@¢„>·€Hh6äHl
 * iso-2022-jp				21168613バイト	0&�u�f��������b�ly����������������������G������� Seh���������������������K���>=�4-�C���������aJC�����������������M��～�@��>��Hh6�Hl���R��>5B�A�����2����A�R��RP���ER��rG��P`�$� $��V�+$��:�7"a0j���%�21167185文字,target=ULT,rTarget=ULT,fleamStart=13178140,再読取=21167185文字,0&�u�f��������b�ly
 * US_ASCII					4601918バイト/4601918文字,	0&�u�f��������b�l�����������������������G������� Seh���������������c�&tW�N���.�o)��$F����������yn��|��������������`������～y�o��m'nW9C��v�A��D(�n.��h�����Y��~!���a��9�z�5�*�D(H���������mA����66I�$���>He9��}�	m�
 * ASCII					4601918バイト/4601918文字,	0&�u�f��������b�l�����������������������G������� Seh���������������c�&tW�N���.�o)��$F����������yn��|��������������`������～y�o��m'nW9C��v�A��D(�n.��h�����Y��~!���a��9�z�5�*�D(H���������mA����66I�$���>He9��}�	m�
 * UTF-8(UTF8)				4601918バイト/4390459文字	0&�u�f��������b�l��������������������ܫ�G������� Seh���������������c�&tW�N���.�o)��$F����������yn��|��������������`۫��������～ɲ�Ჿs*iY��y�o��m'nW9C��v�AɝD(�n.�h�����Y�~!���a��9�z�5�*�D(H���鶉�mA����66I�$�>He9}�	m�
 * UTF-7					4601918バイト/4596005文字	0&�u�f�������b�l������������������G�������� Seh��������c�&tW�N���.�o)��$F�����yn��|�����������`�����～y�o��m'nW9C���v�A��D(���n.��h������Y���!���a��9�z�5�*�D(H����������mA�����66I��$���>H�e�9��}��	m���
 * unsuppot		ASCII169	RFC1468	IEC646(IEC-646)	ISO646(ISO-646)	ISO-8859	EBCDIC	unicodeFFFE
 * Clash		ASCII-169	Cp930	ANSI
 * 文字化け		UTF-16LE = UnicodeLittleUnmarked	UTF-16(BE = UnicodeBigUnmarked)	  S-JIS		MS932(ms932)	utf-32(BE)	UCS-2	EUC-JP	IBM437	Windows-31j
 * */
			readInt = readStr.length();
			if(200 < readInt){
				dbMsg +=">>" + readStr.substring(0, 100) +  "～" + readStr.substring(readInt-100, readInt);
			}else{
				dbMsg +=">>" + readStr;
			}

			dbMsg +=readInt + "文字";
			myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}
	}

	public void itemReadWmaEnd(){		//WMAのオブジェクト読取り終了処理
		final String TAG = "itemReadWmaEnd";
		String dbMsg= "";
		try{

			back2Activty(  );			//呼び出しの戻り処理
			myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}
	}


/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////WMA///
	/**
	 * ID3v1の処理
	 * */
	public void freamReadID3v1(final File file) throws  IOException {		//ID3v1の処理
		String result =null;
		final String TAG = "freamReadID3v1";
		String dbMsg= "";
		try{
			back2Activty(  );			//呼び出しの戻り処理
			myLog(TAG,dbMsg);
//		}catch(FileNotFoundException e){
//			myErrorLog(TAG,dbMsg + "で"+e.toString());
//		}catch(IOException e){
//			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}
	}

	public void freamReadID3v2(final RandomAccessFile file) throws IOException, InvalidTagException {		//RandomAccessFileを'0'で区切ってフレームを読み込む
		final String TAG = "freamReadID3v2";
		String dbMsg= "";
		try{
			makeSyougouList();	//フィールド名リストをList<String> syougouに作成
			final byte[] bufferbIdentifier = new byte[3];
			long filePointer;			// lets scan for a non-zero byte;
			byte b;
			long fLen = file.length();
			long i;
			for(i =0 ; i < fLen;i++){								//ファイルの先頭から最後まで読み出す
				file.seek(i);											//そのカウンターまで進めて
				do {
					filePointer = file.getFilePointer();				//現在地点を格納
					b = file.readByte();								//現在の位置から８ビット・バイトを読みます。;Reads an 8-bit byte from the current position in this file. Blocks until one byte has been read, the end of the file is reached or an exception is thrown.
		//			AbstractID3v2.incrementPaddingCounter();								//org.farng.mp3.id3.
				} while (b == 0);										//nullが見つかれば止めて
				dbMsg= i + "/" + fLen + ")=" + filePointer + ")";
				file.seek(filePointer);
			//	AbstractID3v2.decrementPaddingCounter();									//org.farng.mp3.id3.
				file.read(bufferbIdentifier, 0, 3);								//４文字読んで read the 3 chracter identifier
				final String identifier = new String(bufferbIdentifier, 0, 3);	//フレーム名として格納
				dbMsg +=identifier;
				int kennsakuKekka = kensaku.indexOf(identifier);		//
				dbMsg +="(" + kennsakuKekka + "番目)";
				myLog(TAG,dbMsg);
				if( -1 < kennsakuKekka ){
					final byte[] bufferbDatar = new byte[(int) (fLen- filePointer)];
					do {
						filePointer = file.getFilePointer();				//現在地点を格納
						b = file.readByte();								//現在の位置から８ビット・バイトを読みます。;Reads an 8-bit byte from the current position in this file. Blocks until one byte has been read, the end of the file is reached or an exception is thrown.
			//			AbstractID3v2.incrementPaddingCounter();								//org.farng.mp3.id3.
					} while (b == 0);										//nullが見つかれば止めて
					file.seek(filePointer);
					//	AbstractID3v2.decrementPaddingCounter();									//org.farng.mp3.id3.
					file.read(bufferbDatar, 0, (int) filePointer);								//４文字読んで read the 3 chracter identifier
					String result = new String(bufferbDatar, 0, (int) filePointer);	//フレーム名として格納
					int uketori = result.length();
					if(20 < uketori){
						dbMsg= result.substring(5, 20) +  "～";
					}else{
						dbMsg= result +  "～";
					}
					dbMsg +=uketori + "文字" ;
					myLog(TAG,dbMsg);
				}
				i = filePointer;
			}

//	        if (isValidID3v2FrameIdentifier(identifier) == false) {			// is this a valid identifier?
//	            file.seek(file.getFilePointer() - 2);
//	            throw new InvalidTagException(identifier + " is not a valid ID3v2.20 frame");
//	        }
//	        this.setBody(readBody(identifier, file));
			file.close();
			myLog(TAG,dbMsg);
		}catch(IOException e){
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}
    }

	public String file2Tag2(String result){			//文字列からフィールドを抽出
		final String TAG = " file2Tag2";
		String dbMsg= "";
		try{
			if( result != null){
				dbMsg +=",result=" + result.length() +"文字";
				makeSyougouList();	//フィールド名リストをList<String> syougouに作成
				tagData = new ArrayList<Object>();
				tagData.clear();
				//		result =  file2TagLylic(result);			//歌詞の検索
				pdMaxVal= kensaku.size();										//result.length();								//プログレス終端値
				dbMsg +=",pdMaxVal=" + pdMaxVal +"文字";
				reqCode = read_USLT;										//	<UNSYNCED LYRICS>	非同期 歌詞/文書のコピー									//10cc(3)
				dbMsg +=",reqCode="+reqCode;
				String pdTitol = getApplicationContext().getString(R.string.tag_prog_titol1) +"" + getResources().getString(R.string.common_yomitori);				//
				dbMsg +=",pdTitol="+pdTitol;
				String pdMessage =getApplicationContext().getString(R.string.tag_prog_msg1) + " ; USLT" ;																			//歌詞を探しています。</string>
				dbMsg +=",pdMessage="+pdMessage;
//				pTask = (plogTask) new plogTask(this ,  this , reqCode , pdTitol ,pdMessage , pdMaxVal ).execute(reqCode,  pdMessage , result , kensaku );		//,jikkouStep,totalStep,calumnInfo
				plTask.execute(reqCode,kensaku,TagBrows.this.result,null);
//				TagBrows.this.result = Readloop( reqCode,kensaku,TagBrows.this.result);
				dbMsg += ",result="+TagBrows.this.result;
//				back2Activty(  );			//呼び出しの戻り処理
				myLog(TAG,dbMsg);
			}else{
				dbMsg +=",result==null；ファイルから情報が取れなかかった";
				result_Samary = result_Samary + "\n" + this.getApplicationContext().getResources().getString(R.string.yomikomi_hunou);				//	<string name="">この曲はタグ情報を読み込めませんでした。</string>
				back2Activty(  );			//呼び出しの戻り処理
			}
	//		myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}
		return result;
	}

	public int fleamStart = 0;
	public boolean loopNow = false;					//現在ループ処理
	/**
	 * 渡された文字列から指定されたフレームを切り出す。
	 * 残りは文字列で返す.
	 * 文字情報はここで適切な長さに切ってsetTargetFreamに渡す
	 * @return String result 抜き出した後の文字列文字列
	 * **/
	public String getTargetFream(String result , String target , int reqCode ){			//渡された文字列から指定されたフレームを切り出す
		final String TAG = "getTargetFream";
		String dbMsg= "";
		try{
			int uketori = String.valueOf(result).length();
			if(60 < uketori){
				dbMsg= result.substring(0, 30) +  "～" +  result.substring(uketori -30, uketori) + ";";
			}else{
				dbMsg= result +  ";";
			}
			dbMsg +=uketori + "文字から" + target +  "を検索;reqCode=" + reqCode;
			int startInt = 0;
			int maeoki= 0;
			int backStart = 0;
			int endInt = 0;
			String fremeMei2 = null;		//データに混ざった次のフィールド名
			String kakikomiStr = null;		//結果として書き込む文字列
			String testStr = null;			//検索中の文字列
			String maekStr = null;			//検索対象より前の文字列
			String maekNokoriStr = null;	//検索対象より前のフレーム,Atom
			String backStr = null;			//検索対象より後の文字列
			target = fremeMeiSyougouBody( result , target);	//渡された文字列を先頭からindexOfで照合し、該当すればその文字を返し、無ければnullを返す
	//		dbMsg +=",検出結果は "+ target;
			if(target == null){												//見つからなければ受け取った文字列をそのまま返して終了
				return result;
			}else{																//見つかれば
				startInt = fleamStart + target.length();						//開始値設定		target.getBytes("ISO-8859-1").length
				int rEnd = result.length();
			//	byte[] byte[] buffer;
				switch(reqCode) {
				case read_AAC_LYRIC:				//@Lyrだけを読めるか試みる
				case read_AAC_ITEM:				//QuickTime Tagsの読取り
				case read_AAC_HEAD:				//1005;QuickTime Tagsの読取り
//				case read_WMA_ID32:									//WMAに埋め込まれたID3v2タグの読取り
//				case read_WMA_ID33:									//WMAに埋め込まれたID3v3タグの読取り
					maeoki = startInt - 8;
					maekStr= result.substring(maeoki, startInt + target.length() + 2);			//識別子以前の関連データ
					byte[] buffer = maekStr.getBytes();									//タグヘッダ読込開始		result.substring(maeoki, startInt).getBytes()
					int bufferSize = buffer.length;
					dbMsg += ",buffer=" + bufferSize + "バイト;";//Atom Size
					for(int i = 0 ; i < bufferSize ; i++){
						dbMsg += 	",[" + i + "]=" + buffer[i];		//
					}
					rEnd = (buffer[0] << 22) + (buffer[1] << 15) + (buffer[2] << 8) + buffer[3];		// (buffer[0] << 21) + (buffer[1] << 14) + (buffer[2] << 7) + buffer[3];
					dbMsg +=",startInt="+ startInt + "～計算="+ rEnd + "まで";
/*				この計算は失敗
 * maeoki = startInt - 8;
 * Bob Dylan/Desire/01 Hurricane.m4a,				[-2]=0,[-1]=49,	buffer[0]=0,[1]=0,	[2]=18,	[3]=59,[4]=-62,[5]=-87,[6]=108,startInt=94338～294971まで,startInt=94338～294971;8769696文字),fremeMei2=aARTまで419852文字;419852文字),fremeMei2=covrまで4663文字;4663文字)4663文字,次は99001から；covr��UÍdata��������������～8765033文字)、書込み=����3data��������������by Bob Dylan a～The champion of the world.��UÕ:4663文字、処理後=covr��UÍdata��������������～：8765033文字)
 * Bob Dylan/Desire/04 One More Cup Of Coffee.m4a,	[-2]=0,[-1]=52,	buffer[0]=0,[1]=0,	[2]=4,	[3]=72,[4]=-62,[5]=-87,[6]=108,startInt=42604～65608まで,startInt=42604～65608;4096764文字),fremeMei2=aARTまで416281文字;416281文字),fremeMei2=covrまで1092文字;1092文字)1092文字,次は43696から；covr��UÍdata��������������～4095672文字)、書込み=����@data��������������
 * Bonnie Pink/Water Me/01 Water Me.m4a,			[-2]=52,[-1]=50,buffer[0]=0,[1]=0,	[2]=3,	[3]=-61,[4]=-94,[5]=-62,[6]=-87,startInt=43376～49091まで,startInt=43376～49091;3682250文字),fremeMei2=©genまで990文字;990文字)990文字,次は44366から；©gen������data��������������～3681260文字)、書込み=����Údata��������������å°ããªåã～¤ã®æ¬å½ããããã������:990文字、処理後=©gen������data��������������～：3681260文字)
* 宇多田ヒカル/Keep Tryin'/01 Keep Tryin'.m4a		[-2]=54,[-1]=48,buffer[0]=0,[1]=0,	[2]=7,	[3]=21,[4]=-62,[5]=-87,[6]=108,startInt=54471～114709まで,startInt=54471～114709;4709365文字),fremeMei2=©genまで18977文字;18977文字),fremeMei2=covrまで1809文字;1809文字)1809文字,次は56280から；covr����Cdata������
 */
					maekNokoriStr = result.substring(0, maeoki-1);	//検索対象より前のフレーム,Atom
			//		rEnd = rEnd-4;
					break;
//				case read_AAC_HEAD:				//1005;QuickTime Tagsの読取り
//					///*QuickTime Tags	QuickTime FileType Tags								������ ftypM4A ��������M4A mp42
//					// *					QuickTime FileType Tags	 QuickTime FileType Tags	isom����������
//					// *QuickTime Tags														àmoov������
//					// *					QuickTime Movie Tags		MovieHeader	lmvhd����～ ]6U66"Å
//					//
//					// *  startInt + endInt - 8;
//					// * */
//					kakikomiStr = result.substring(startInt-8,  fleamStart + target.length());		//Atom Size + Type を保持
				default:
		//			maekNokoriStr = result.substring(0, startInt-1);	//検索対象より前のフレーム,Atom
					break;
				}
				dbMsg +=">>～"+ rEnd;
				testStr = result.substring(startInt, uketori);		//検索対象の文字列を設定
				int endC = testStr.length();
				do{
					uketori = testStr.length();
					dbMsg += ";検索対象" + testStr.length() +  "文字)";
					fremeMei2 = fremeMeiSyougou( testStr , reqCode);							//検索対象より後の文字列からフレーム名を検索し、該当するフレーム名を返す
					if( fremeMei2 == null ){										//拾えなければ
					}else{															//拾えれば
						dbMsg += ",fremeMei2=" + fremeMei2 +"まで";
						endInt = fleamStart;										//終端をセットして
						testStr = testStr.substring(0, endInt);
						backStart = startInt + endInt;
					}
					dbMsg +=endInt +  "文字";
				}while(fremeMei2 != null);
		//		dbMsg +=">次のfleamStart>"+ endInt;
				uketori = testStr.length();
				if(target.equals("covr")){
				} else if(10000 < uketori){							//無関係な情報が混在している可能性がある場合
//					int ofsetrt = 1000;
//					if(testStr.length() < 1000){
//						ofsetrt = testStr.length()-2;
//					}
//					endInt = retNextNullPoint( testStr ,  ofsetrt );					//渡された文字列にnullが発生するポイントを返す
					dbMsg += ";;endInt=" + endInt+ "/" + testStr.length() + "文字)";
					if(endInt < testStr.length()){
						testStr = testStr.substring(0, 1000);
					} else{
						testStr = testStr.substring(0 , 2000);
					}
				}
				myLog(TAG,dbMsg);
				switch(reqCode) {
				case read_AAC_HEAD:				//1003;QuickTime Tagsの読取り
//					kakikomiStr = kakikomiStr + testStr.substring(0, testStr.length()-8);		//Atom Size + Type  + 次のフレームの前までを書き込む
					backStart = backStart-4;
//					if( backStart < 0 ){
//						backStart = 0;
//					}
////					if(target.startsWith("ftyp") ){
////					}else{
////					}
					break;
				case read_AAC_LYRIC:				//@Lyrだけを読めるか試みる
				case read_AAC_ITEM:				//QuickTime Tagsの読取り
//					if( fremeMei2 == null ){										//次のAtomが拾えなければ
//						kakikomiStr = maekStr + testStr.substring(0, testStr.length());
//					}else {
						kakikomiStr = maekStr + testStr.substring(0, testStr.length()-4);			//次のAtomのtype前情報を削除
//					}
				//	backStart = startInt + endInt-4 ;
					backStart = startInt +  kakikomiStr.length() - maekStr.length();
			//		backStart = backStart + 1;
					break;
				default:
					kakikomiStr = testStr;		//結果として書き込む文字列
					break;
				}
		//		myLog(TAG,dbMsg);
				uketori = String.valueOf(kakikomiStr).length();
				dbMsg +="、書込み=" ;
				if(60 < uketori){
					dbMsg +=kakikomiStr.substring(0, 30) +  "～" + kakikomiStr.substring(uketori- 30 , uketori);
				}else{
					dbMsg +=kakikomiStr;
				}
				dbMsg +=":" + uketori +  "文字";
				switch(reqCode) {
				case read_USLT:				//	<UNSYNCED LYRICS>	非同期 歌詞/文書のコピー									//10cc(3)
					setTargetFream( kakikomiStr , target);			//指定されたフレームのデータをこのクラスのグローバル変数にセットする
					break;
				case read_AAC_PRE:				//最小限の設定読取り
				case read_AAC_HEAD:				//QuickTime Tagsの読取り
					myLog(TAG,dbMsg);
					headReadAacBody( kakikomiStr , target);									//AACのQuickTime Tags読取り
					break;
				case read_AAC_HEAD_Movie:		//QuickTime Tags.QuickTime Movie Tagsの読取り
					movieReadAacBody(  kakikomiStr , target );		//QuickTime Tags.QuickTime Movie Tagsの読取りの読取り準備
					break;
				case read_AAC_Movie_Meta:		//QuickTime Tags.QuickTime Meta Tagsの読取り
					metaReadAacBody( kakikomiStr , target );		//QuickTime ItemList Tagsの読取り準備
					break;
				case read_AAC_LYRIC:				//@Lyrだけを読めるか試みる
				case read_AAC_ITEM:				//QuickTime Tagsの読取り
					itemReadAacBody( kakikomiStr , target);									//AACのQQuickTime ItemList Tags読取り
					break;
				case read_WMA_ID32:									//WMAに埋め込まれたID3v2タグの読取り
				case read_WMA_ID33:									//WMAに埋め込まれたID3v3タグの読取り
				case read_WMA_ITEM:									//WMAのオブジェクト読取り
				case read_WMA_AAC:									//WMAに埋め込まれたAAC Atomの読取り
					itemReadWmaBody(kakikomiStr , target);		//WMAのオブジェクト読取り
						break;
//				default:
//					break;
				}
			}
			dbMsg +=",次は" + backStart;
			backStr = result.substring(backStart);	//検索対象より後の文字列を更新
			dbMsg +="から；" + backStr.substring(0, 20);
			dbMsg += "；" + backStr.length() +  "文字)";
			if(maekNokoriStr != null){
				dbMsg +=",前のAtom="+ maekNokoriStr.length() +  "文字";
				result =maekNokoriStr;
			}else{
				result = "";
			}
//			if(maekStr == null){
//				dbMsg +=",maekStr=null";
//				result = result +  backStr;
//			} else {
//				dbMsg +=",maekStr="+ maekStr.length() +  "文字";
//				result = result + maekStr + backStr;
//			}
			if( backStr != null ){
				result = result + backStr;
			}
			uketori = result.length();
			if(20 < uketori){
				dbMsg += "、処理後=" + result.substring(0, 20) +  "～"+ result.substring( result.length()-20);
			}else{
				dbMsg += "、処理後=" + result;
			}
			dbMsg += "：" + uketori +  "文字)";
			myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
			return result;
		}
		return result;
	}

	/**
	 * フレームに設定されたエンコードフラグを読み、変換した文字を返す
	 * @param  encFlag フィールドにセットされている0～4のフラグ
	 * @param  motoEncrod 変換前のエンコード名。基本はISO-8859-1
	 * @param  retStr 変換する文字、エラーが発生したらそのまま返す
	 * */
	public String getEncordStr( int encFlag , String motoEncrod ,String retStr){			//フレームに設定されたエンコードフラグを読み、変換した文字を返す
		final String TAG = "getEncordStr";
		String dbMsg= "";
		try{
			dbMsg= "Encrod=" + motoEncrod  ;		//フラグ？ここから文字？
			int mojisuu = retStr.length();
			if( 20< mojisuu ){
				dbMsg += ",受取り=" + retStr.substring(0, 20) +"～" + retStr.substring( mojisuu - 20, mojisuu) ;
			}else{
				dbMsg += ",受取り=" + retStr ;
			}
			dbMsg += ">encFlag>" + encFlag  ;		//フラグ？ここから文字？
			if(encFlag != 0){	//Locale.getDefault().equals( Locale.getDefault().JAPAN)	アプリで使用されているロケール情報を取得し、日本語の場合のみconstant for ja_JP.
//				dbMsg +=",dataBuffer= "+ dataBuffer.length + "バイト";
				switch(encFlag) {
				case 0:
					saiEncrod = "ISO-8859-1";			//00h: ISO-8859-1
					break;
				case 1:
					saiEncrod = "UTF-16";				//01h: UTF-16 / BOMあり
					break;
				case 2:
					saiEncrod = "UTF-16BE";				//(id3v2.4) 02h: UTF-16BE / BOMなし
					break;
				case 3:
					saiEncrod = "UTF-8";				//id3v2.4) 03h: UTF-8
					break;
				default:
					saiEncrod = "ISO-8859-1";
					break;
				}
				dbMsg += ">saiEncrod>" + saiEncrod ;		//フラグ？ここから文字？
				retStr = saiEncordBody( retStr , motoEncrod , saiEncrod);										//再エンコードした文字を返す
				//終端文字： "00h" (ISO-8859-1, UTF-8)	　"00h 00h" (UTF-16)　でカットするか？
				mojisuu = retStr.length();
				if( 20< mojisuu ){
					dbMsg += ">saiEncordBody>" + retStr.substring(0, 20) +"～" + retStr.substring( mojisuu - 20, mojisuu) ;
				}else{
					dbMsg += ">saiEncordBody>" + retStr ;
				}
			}
			dbMsg += "(" + retStr.length() + "文字)" ;		//フラグ？ここから文字？
			myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
			return retStr;
		}
		return retStr;
	}


	/**
	 * ID3;指定されたフレームのデータをこのクラスのグローバル変数にセットする
	 * @param  bodyData フレーム名から始まるフレーム一つ分の全データ
	 * */
	@SuppressLint("ByteOrderMark")
	public void setTargetFream(String bodyData , String target){			//指定されたフレームのデータをこのクラスのグローバル変数にセットする
		final String TAG = "setTargetFream";
		String dbMsg= "";
		String tuikaMoji = null;
		try{
			dbMsg=  filePath + "," +target +  "=";
			int encFlag = 0;
			if ( bodyData != null ) {
				int uketori = bodyData.length();
				if(20 < uketori){
					dbMsg += bodyData.substring(5, 20) +  "～";
				}else{
					dbMsg += bodyData +  "～";
				}
				dbMsg += uketori +  "文字)";
				int retType = retFeldType(target);				//渡されたフィールドのタイプを返す
				dbMsg=  dbMsg +".retType=" + retType;
				int startInt = target.length() + 4;			//target.getBytes("ISO-8859-1").length;						//\0の検索開始点を設定
				dbMsg +=",startInt= "+ startInt;
				int endC = bodyData.length();
				dbMsg +=",endC=" + endC;
				byte[] buffer = bodyData.substring(0, endC).getBytes();									//タグヘッダ読込開始			"ISO-8859-1"
				dbMsg += ",buffer[0]=" + buffer[0];//常に0
				dbMsg += ",[1]=" + buffer[1];		//常に0
				dbMsg += ",[2]=" + buffer[2];		//常に0
				dbMsg += ",[3]=" + buffer[3];		//フレームサイズ = 書込み文字.length() + 1
				dbMsg += ",[4]=" + buffer[4];		//常に0
				dbMsg += ",[5]=" + buffer[5];		//常に0
				dbMsg += ",[6]=" + buffer[6];		//常に0
				switch(retType) {
				case FELDE_TYPE_TEXTINFO:							//テキスト情報フレーム
//TPE1のデータ(bodyData)=����10cc����～4733788文字)	,buffer[0]=0,[1]=0,[2]=0,[3]=5,	[4]=0,[5]=0,[6]=0,[7]=49,[8]=48,	[9]=99,	書込み=10cc�����	��			～4733781文字,tagData2件
//TPE2のデータ(bodyData)=������������10cc～11文字)	,buffer[0]=0,[1]=0,[2]=0,[3]=5,	[4]=0,[5]=0,[6]=0,[7]=49,[8]=48,	[9]=99,	書込み=10cc							～4文字,tagData3件
//TALBのデータ(bodyData)=����ORIGNAL SOUND～24文字)		,buffer[0]=0,[1]=0,[2]=0,[3]=18,[4]=0,[5]=0,[6]=0,[7]=79,[8]=82,	[9]=73,	書込み=ORIGNAL SOUNDTRAK			～17文字,tagData4件
//TYERのデータ(bodyData)=������������1975～11文字)	,buffer[0]=0,[1]=0,[2]=0,[3]=5,	[4]=0,[5]=0,[6]=0,[7]=49,[8]=57,	[9]=55,	書込み=1975							～4文字,tagData5件
//TRCKのデータ(bodyData)=������������2/8～10文字).	,buffer[0]=0,[1]=0,[2]=0,[3]=4,	[4]=0,[5]=0,[6]=0,[7]=50,[8]=47,	[9]=56,	書込み=2/8							～3文字,tagData7件
//TIT2のデータ(bodyData)=����I'm not in Lo～22文字)		,buffer[0]=0,[1]=0,[2]=0,[3]=16,[4]=0,[5]=0,[6]=0,[7]=73,[8]=39,	[9]=109,書込み=I'm not in Love				～15文字,tagData6件
//TCONのデータ(bodyData)=������������Rock～11文字)		,buffer[0]=0,[1]=0,[2]=0,[3]=5,	[4]=0,[5]=0,[6]=0,[7]=82,[8]=111,	[9]=99,	書込み=Rock							～4文字,tagData7件
//TPUBのデータ(bodyData)=����Universal Dis～29文字).		,buffer[0]=0,[1]=0,[2]=0,[3]=23,[4]=0,[5]=0,[6]=0,[7]=85,[8]=110,	[9]=105,書込み=Universal Distribution		～22文字,tagData17件
//TCOMのデータ(bodyData)=����Eric Stewart/～35文字).		,buffer[0]=0,[1]=0,[2]=0,[3]=29,[4]=0,[5]=0,[6]=0,[7]=69,[8]=114,	[9]=105,書込み=Eric Stewart/Graham Gouldman	～28文字,tagData20件
//ID3v2.3.0		宇多田ヒカル/FirstLove/First Love -Strings Mix.mp3
//TPE2=��ÿþ[Y0uÒ0«0ë～3230文字)						buffer[0]=0,[1]=0,[2]=0,	[3]=15,	[4]=0,[5]=0,[6]=1,受取り3230文字>>5～14文字>>14文字、encFlag=1,Encrod=ISO-8859-1>saiEncordBody>￾蝛ᩙふ툰ꬰ(7文字),書込み=￾蝛ᩙふ툰ꬰ;7文字
//TRCK=������������2～8文字)							buffer[0]=0,[1]=0,[2]=0,	[3]=2,	[4]=0,[5]=0,[6]=0,受取り8文字>>5～1文字>>1文字、encFlag=0,Encrod=ISO-8859-1>saiEncordBody>��(1文字),書込み=��;1文字
//TPUB=��ÿþ¤0ü0¹0È0ï0ü～25文字)						buffer[0]=0,[1]=0,[2]=0,	[3]=19,	[4]=0,[5]=0,[6]=1,受取り25文字>>5～18文字>>18文字、encFlag=1,Encrod=ISO-8859-1>saiEncordBody>￾ꐰﰰ뤰젰ﰰ(9文字),書込み=￾ꐰﰰ뤰젰ﰰ;9文字
//ID3v2.3.0		宇多田ヒカル/Can You Keep A Secret_/Can You Keep A Secret_.mp3
//TPE2=��ÿþ[Y0uÒ0«0ë～7269文字)						buffer[0]=0,[1]=0,[2]=0,	[3]=15,	[4]=0,[5]=0,[6]=1,受取り7269文字>>7～14文字>>14文字、encFlag=1,Encrod=ISO-8859-1>saiEncordBody>宇多田ヒカル��(7文字),書込み=宇多田ヒカル��;7文字
//TRCK=������������1～8文字)							buffer[0]=0,[1]=0,[2]=0,	[3]=2,	[4]=0,[5]=0,[6]=0,受取り8文字>>7～1文字>>1文字、encFlag=0,Encrod=ISO-8859-1>saiEncordBody>1(1文字),書込み=1;1文字
//TPUB=��ÿþ¤0ü0¹0È0ï0ü～25文字)						buffer[0]=0,[1]=0,[2]=0,	[3]=19,	[4]=0,[5]=0,[6]=1,受取り25文字>>7～18文字>>18文字、encFlag=1,Encrod=ISO-8859-1>saiEncordBody>イーストワールド��(9文字),書込み=イーストワールド��;9文字

//ID3v2.2.0		宇多田ヒカル/Wait&See 〜リスク〜/01 Wait&See 〜リスク〜.mp3,buffer[0]=73,[1]=68,[2]=51,[3]=2,[4]=0,[5]=0,[6]=0,[7]=2,[8]=89,[9]=84,tag=ID3������Y,result_Tag=,compression(ID3v2.2)=false,unsynchronization(共通)=false,size=44244,filePointer=10,4660133文字>>4660143文字>ID3v2のサイズフラグでカット>44244文字
//TP1=����ÿþ[Y0uÒ0«0ë0����～20文字)				buffer[0]=0,[1]=0,[2]=17,	[3]=1,	[4]=-61,[5]=-65,[6]=-61,受取り20文字>>4～16文字>>16文字、encFlag=1,Encrod=ISO-8859-1>saiEncordBody>宇多田ヒカル����(8文字),書込み=宇多田ヒカル����;8文字
//TPA=������1/1��～8文字)								buffer[0]=0,[1]=0,[2]=5,	[3]=0,	[4]=49,[5]=47,[6]=49,受取り8文字>>4～4文字>>4文字、encFlag=0,Encrod=ISO-8859-1>saiEncordBody>1/1��(4文字),書込み=1/1��;4文字
//TRK=������1/5��～8文字).retType=1,startInt= 7,endC=8,buffer[0]=0,[1]=0,[2]=5,	[3]=0,	[4]=49,[5]=47,[6]=53,受取り8文字>>4～4文字>>4文字、encFlag=0,Encrod=ISO-8859-1>saiEncordBody>1/5��(4文字),書込み=1/5��;4文字
//TT2=þW��a��i��t��&��S��e��～36文字)			buffer[0]=0,[1]=0,[2]=33,	[3]=1,	[4]=-61,[5]=-65,[6]=-61,受取り36文字>>4～32文字>>32文字、encFlag=1,Encrod=ISO-8859-1>saiEncordBody>Wait&See 〜リスク〜����(16文字),書込み=Wait&See 〜リスク〜����;16文字
//TAL=þW��a��i��t��&��S��e��～36文字)			buffer[0]=0,[1]=0,[2]=33,	[3]=1,	[4]=-61,[5]=-65,[6]=-61,受取り36文字>>4～32文字>>32文字、encFlag=1,Encrod=ISO-8859-1>saiEncordBody>Wait&See 〜リスク〜����(16文字),書込み=Wait&See 〜リスク〜����;16文字

					/*「テキスト情報フレーム」のヘッダ、ID: "T000" - "TZZZ", ただし"TXXX"を除く>
					 * 文字コード	$xx					$xxは、未知の１バイトのデータ
					 * ******************
					 * 情報	<エンコード指定文字列*/
					int mojiSuu  = bodyData.length();
					char[] cArry = bodyData.toCharArray();				//char型配列のに変換し
					dbMsg += ",受取り" + cArry.length + "文字";		//フレームサイズ = 書込み文字.length() + 1
					switch (this.majorVersion) {
					case 2:
						startInt = target.length() + 1;										//	target.getBytes("ISO-8859-1").length
						mojiSuu = buffer[2]-1;						//ごみ残り？
						encFlag = buffer[3];
						break;
					default:
						startInt = target.length() + 3;										//	target.getBytes("ISO-8859-1").length
						mojiSuu = buffer[3]-1;
						encFlag = buffer[6];
						break;
					}
					dbMsg += ">>" + startInt + "～" + mojiSuu + "文字" ;
			//		dbMsg += ">>" + tuikaMoji.length() ;		//フレームサイズ = 書込み文字.length() + 1
					tuikaMoji = new String(cArry, startInt, mojiSuu );		//offset７バイト、charCountはフレームサイズに指定された文字数
	//				backStart = startInt + 7 + kakikomiStr.length();
					dbMsg += ">>" + tuikaMoji.length()  + "文字、encFlag=" + encFlag;		//フレームサイズ = 書込み文字.length() + 1
					cArry = null;
//					dbMsg += ",startInt=" + startInt  ;
//					tuikaMoji =  bodyData.substring(startInt, bodyData.length());		//ヘッダーをカット
					dbMsg += ",Encrod=" + motoEncrod  ;		//フラグ？ここから文字？
					tuikaMoji = getEncordStr( encFlag , motoEncrod ,tuikaMoji);			//フレームに設定されたエンコードフラグを読み、変換した文字を返す
					int mojisuu = tuikaMoji.length();
					if( 20< mojisuu ){
						dbMsg += ">saiEncordBody>" + tuikaMoji.substring(0, 20) +"～" + tuikaMoji.substring( mojisuu - 20, mojisuu) ;
					}else{
						dbMsg += ">saiEncordBody>" + tuikaMoji ;
					}
					dbMsg += "(" + tuikaMoji.length() + "文字)" ;		//フラグ？ここから文字？
					if(target.equals("TPE2") || target.equals("TP2")){		//バンド/オーケストラ/伴奏	バンド/オーケストラ					Band/Orchestra/Accompaniment/Album Artist				ID3ｖ3；TPE2
						result_TPE2 =target + ";" + tuikaMoji;					//バンド/オーケストラ/伴奏	バンド/オーケストラ		//10cc(2)TPE2������������10cc
					}else if(target.equals("TPE1") || target.equals("TP1")){		//主な演奏者/ソリスト/トラック アーティスト				Lead artist(s)/Lead performer(s)/Soloist(s)/Performing group	ID3ｖ3；TPE1
						result_TPE1 =target + ";" + tuikaMoji;
					}else if(target.equals("TCOM") || target.equals("TCM")){		//作曲者														Composer												ID3ｖ3；TCOM
						result_TCOM =target + ";" + tuikaMoji;				//	作曲者																				//10cc(2)
					}else if(target.equals("TALB") || target.equals("TAL")){		//アルバム/映画/ショーのタイトル								Album/Movie/Show title									ID3ｖ3；TALB
						result_TALB =target + ";" + tuikaMoji;					//	アルバム/映画/ショーのタイトル														//10cc(6)
					}else if(target.equals("TYER") || target.equals("TYE")){		//年	レコーディング年/年*15	Date*16	Year Deprecated			Year													ID3ｖ3；TYER
						result_TYER =target + ";" + tuikaMoji;					//	年	レコーディング年/年*15	Date*16	 Deprecated									//10cc(5)TYER������������1975
					}else if(target.equals("TRCK") || target.equals("TRK")){			//トラックの番号/セット中の位置									Track number/Position in set							ID3ｖ3；TRCK
						result_TRCK =target + ";" + tuikaMoji;				//	トラックの番号/セット中の位置	トラック #	Track Number	Track#						//10cc(7)TRCK������������2/8
					}else if(target.equals("TIT2") || target.equals("TT2")){		//タイトル/曲名/内容の説明	タイトル							Track Title	Title/Title/Songname/Content description	ID3ｖ3；TIT2;
						result_TIT2 =target + ";" + tuikaMoji;					//タイトル/曲名/内容の説明	タイトル	Track Title	Title
					}else if(target.equals("TDAT") || target.equals("TDA")){		//日付	Date*11	Date*12	Year*13 Deprecated						Date													ID3ｖ3；TDAT
						result_TDAT =target + ";" + tuikaMoji;					//日付	Date*11	Date*12	Year*13 Deprecated
					}else if(target.equals("TPE3") || target.equals("TP3")){		//指揮者/演奏者詳細情報	指揮者									Conductor/Performer refinement							ID3ｖ3；TPE3
						result_TPE3 =target + ";" + tuikaMoji;					//指揮者/演奏者詳細情報	指揮者	<CONDUCTOR>	Conductor
					}else if(target.equals("TCON") || target.equals("TCO")){		//ジャンル														Content type											ID3ｖ3；TCON
						result_TCON =target + ";" + tuikaMoji;					//ジャンル																				//10cc(8)TCON������������
					}else if(target.equals("TENC") || target.equals("TEN")){		//エンコーディング ソフトウェア									Encoded by												ID3ｖ3；TENC
						result_TENC =target + ";" + tuikaMoji;						//エンコーディング ソフトウェア	<ENCODED BY>	Encoder
					}else if(target.equals("TEXT") || target.equals("TXT")){		//作詞家/文書作成者												Lyricist/text writer									ID3ｖ3；TEXT
						result_TEXT =target + ";" + tuikaMoji;					//作詞家/文書作成者	作詞者	<LYRICIST>	Lyricist
					}else if(target.equals("TPE4") || target.equals("TP4")){		//翻訳者, リミックス, その他の修正								Interpreted, remixed, or otherwise modified by			ID3ｖ3；TPE4
						result_TPE4 =target + ";" + tuikaMoji;					//翻訳者, リミックス, その他の修正	<ModifiedBy>	<REMIXED BY>	Mix Artist, Artists: Remixer
					}else if(target.equals("TPUB") || target.equals("TPB")){		//出版社/発行元													Publisher												ID3ｖ3；TPUB
						result_TPUB =target + ";" + tuikaMoji;						//出版社	発行元	<PUBLISHER>	Publisher												//10cc(11)TPUB������������Universal Distribu
					}else if(target.equals("TPOS ") || target.equals("TPA")){		//セット中の位置/ディスク #	Disc Number	Disc#					Part of a set											ID3ｖ3；TPOS
						result_TPOS =target + ";" + tuikaMoji;					//セット中の位置	ディスク #	Disc Number	Disc#										//10cc(7')TPOS������������1/1
					}else if(target.equals("TDLY") || target.equals("TDY")){		//プレイリスト遅延時間											Playlist delay											ID3ｖ3；TDLY
						result_TDLY =target + ";" + tuikaMoji;						//Playlist delay
					}else if(target.equals("TFLT") || target.equals("TFT")){		//ファイルタイプ												File type												ID3ｖ3；TFLT
						result_TFLT =target + ";" + tuikaMoji;					//File type
					}else if(target.equals("TIME") || target.equals("TIM")){		//時間															Time(Time Deprecated?)									ID3ｖ3；TIME
						result_TIME =target + ";" + tuikaMoji;						//Time Deprecated
					}else if(target.equals("TIT1") || target.equals("TT1")){		//内容の属するグループの説明									Content group description								ID3ｖ3；TIT1;
						result_TIT1 =target + ";" + tuikaMoji;					//Content group description
					}else if(target.equals("TIT3") || target.equals("TT3")){		//サブタイトル/説明の追加情報									Subtitle/Description refinement							ID3ｖ3；TIT3;
						result_TIT3 =target + ";" + tuikaMoji;						// Subtitle/Description refinement
					}else if(target.equals("TKEY") || target.equals("TKE")){		//初めの調														Initial key												ID3ｖ3；TKEY
						result_TKEY =target + ";" + tuikaMoji;						//Initial key
					}else if(target.equals("TLAN") || target.equals("TLA")){		//言語															Language(s)												ID3ｖ3；TLAN
						result_TLAN =target + ";" + tuikaMoji;						// Language(s)]
					}else if(target.equals("TLEN") || target.equals("TLE")){		//長さ															Length													ID3ｖ3；TLEN
						result_TLEN =target + ";" + tuikaMoji;						// Length]
					}else if(target.equals("TMED") || target.equals("TMT")){		//メディアタイプ												Media type												ID3ｖ3；TMED
						result_TMED =target + ";" + tuikaMoji;					// Media type]
					}else if(target.equals("TOAL") || target.equals("TOT")){		//オリジナルのアルバム/映画/ショーのタイトル					Original album/Movie/Show title							ID3ｖ3；TOAL
						result_TOAL =target + ";" + tuikaMoji;					//Original album/movie/show title
					}else if(target.equals("TOFN") || target.equals("TOF")){		//オリジナルファイル名											Original filename										ID3ｖ3；TOFN
						result_TOFN =target + ";" + tuikaMoji;					//Original filename]
					}else if(target.equals("TOLY") || target.equals("TOL")){		//オリジナルの作詞家/文書作成者									Original Lyricist(s)/text writer(s)						ID3ｖ3；TOLY
						result_TOLY =target + ";" + tuikaMoji;						//  Original lyricist(s)/text writer(s)]
					}else if(target.equals("TOPE") || target.equals("TOA")){		//オリジナルアーティスト/演奏者									Original artist(s)/performer(s)							ID3ｖ3；TOPE
						result_TOPE =target + ";" + tuikaMoji;						//Original artist(s)/performer(s)	 Deprecated
					}else if(target.equals("TORY") || target.equals("TOR")){		//オリジナルのリリース年										Original release year									ID3ｖ3；TORY
						result_TORY =target + ";" + tuikaMoji;						//Original release year]
					}else if(target.equals("TOWN") || target.equals("TSI")){		//サイズ														Size(Size	 Deprecated?)								ID3ｖ3；TSIZ;
						result_TOWN =target + ";" + tuikaMoji;					// File owner/licensee
					}else if(target.equals("TRDA ") || target.equals("TRD")){		//録音日付														Recording dates											ID3ｖ3；TRDA
						result_TRDA =target + ";" + tuikaMoji;						// Recording dates	Deprecated
					}else if(target.equals("TSIZ ") || target.equals("TSI")){
						result_TSIZ =target + ";" + tuikaMoji;					//Size	 Deprecated
					}else if(target.equals("TSRC ") || target.equals("TRC")){		//国際標準レコーディングコード									ISRC (International Standard Recording Code)			ID3ｖ3；TSRC
						result_TSRC =target + ";" + tuikaMoji;						//ISRC (international standard recording code)
					}else if(target.equals("TSSE ") || target.equals("TSS")){		//エンコードに使用したソフトウエア/ハードウエアとセッティング	Software/hardware and settings used for encoding		ID3ｖ3；TSSE;
						result_TSSE =target + ";" + tuikaMoji;					//Software/Hardware and settings used for encoding
					}else if(target.equals("TBPM ") || target.equals("TBP")){		//一分間の拍数													BPM (Beats Per Minute)									ID3ｖ3；TBPM
						result_TBPM =target + ";" + tuikaMoji;						//BPM (beats per minute)
					}else if(target.equals("TCOP") || target.equals("TCR")){
						result_TCOP =target + ";" + tuikaMoji;    //著作権情報
					}else if(target.equals("TMOO")){
						result_TMOO =target + ";" + tuikaMoji;					//ムード	ムード	<MOOD>	Mood	ID3v2.4フレーム
					}else if(target.equals("TRSN ")){
						result_TRSN =target + ";" + tuikaMoji;					// Internet radio station name
					}else if(target.equals("TRSO ")){
						result_TRSO =target + ";" + tuikaMoji;						// Internet radio station owner
					}else if(target.equals("TSOA ")){
						result_TSOA =target + ";" + tuikaMoji;						//©nam			//アルバム（読み）										ALBUMSORT						ID3ｖ2；--	ID3ｖ3；TSOA
					}else if(target.equals("TSOP ")){
						result_TSOP =target + ";" + tuikaMoji;						//soar			//アーティスト（読み）									ARTISTSORT						ID3ｖ2；--	ID3ｖ3；TSOP
					}else if(target.equals("TSOC ")){
						result_TSOC =target + ";" + tuikaMoji;					//soar			//アーティスト（読み）									ARTISTSORT						ID3ｖ2；--	ID3ｖ3；TSOP
					}
					break;
				case FELDE_TYPE_URL_LINK:								//リンクフレーム
					//文字列情報中に終端文字($00 (00))が含まれていた場合は、それ以後の情報はすべて破棄され、表示されることはない。
					startInt = target.length() + 3;
					tuikaMoji =  bodyData.substring(startInt);
					if(target.equals("WCOP") || target.equals("WCP")){		//著作権/法的情報												Copyright/Legal information								ID3ｖ3；WCOP
						result_WCOP =tuikaMoji;					// Copyright/Legal information
					}else if(target.equals("WCOM") || target.equals("WCM")){		//商業上の情報													Commercial information									ID3ｖ3；WCOM
						result_WCOM =tuikaMoji;					//Commercial information]
					}else if(target.equals("WOAF") || target.equals("WAF")){		//オーディオファイルの公式Webページ								Official audio file webpage								ID3ｖ3；WOAF
						result_WOAF =tuikaMoji;					// Official audio file webpage
					}else if(target.equals("WOAR") || target.equals("WAR")){		//アーティスト/演奏者の公式Webページ							Official artist/performer webpage						ID3ｖ3；WOAR
						result_WOAR =tuikaMoji;					// Official artist/performer webpage
					}else if(target.equals("WOAS") || target.equals("WAS")){		//音源の公式Webページ											Official audio source webpage							ID3ｖ3；WOAS
						result_WOAS =tuikaMoji;					// Official audio source webpage
					}else if(target.equals("WPUB")|| target.equals("WPB")){		//出版社の公式Webページ											Publishers official webpage								ID3ｖ3；WPUB
						result_WPUB =tuikaMoji;					//Publishers official webpage
					}else if(target.equals("WCAF")){
						result_WCAF =tuikaMoji;					//ファイルの所有者/ライセンシー
					}else if(target.equals("WORS")){
						result_WORS =tuikaMoji;					//  Official internet radio station homepage
					}else if(target.equals("WPAY")){
						result_WPAY =tuikaMoji;					//  Payment
					}
					break;
				case FELDE_TYPE_USRE_TEIGI_MOJI:					//ユーザー定義テキスト情報フレーム
					startInt = target.length() + 3;
					tuikaMoji =  bodyData.substring(startInt);
					result_TXXX =tuikaMoji;					//ユーザー定義文字情報フレーム
					break;
				case FELDE_TYPE_USRE_TEIGI_URL_LINK:					//WXXX	ユーザー定義URLリンクフレーム
					startInt = target.length() + 3;
					tuikaMoji =  bodyData.substring(startInt);
					result_WXXX =tuikaMoji;					//User defined URL link frame
					break;
				case FELDE_TYPE_KYOURYOKUSYA_ITIRAN:					//5:IPLS	協力者一覧
					startInt = target.length() + 3;
					tuikaMoji =  bodyData.substring(startInt);
					result_IPLS =tuikaMoji;					//Involved people list]
					break;
				case FELDE_TYPE_HIDOPUKI_KASI:							//非同期の歌詞/文章のコピー				headSize = 26;//　����eng��I'm not i～
					dbMsg += ",[7]=" + buffer[7];		//フラグ？ここから文字？
					dbMsg += ",[8]=" + buffer[8];		//
					dbMsg += ",[9]=" + buffer[9];		//
					dbMsg += ",[10]=" + buffer[10];		//フラグ？ここから文字？
					dbMsg += ",[11]=" + buffer[11];		//
					dbMsg += ",[12]=" + buffer[12];		//
/* ID: "USLT"		$xx xx xx xx						16進数 4バイト(ID3v2.2は2バイト)
 * 文字コード		$xx									16進数 1バイト
 * 言語				$xx xx xx							16進数 3バイト？
 * 内容に関する情報	<エンコード指定文字列> $00 (00)		16進数 2バイト？
 * 歌詞/文章		<エンコード指定全文字列>*/
//ヌル文字で終わる文字列として格納される文字列の説明と、実際の文字列
/*
 *01 Une Nuit A Paris.mp3����eng��Part One:～4092文字)								buffer[0]=0,[1]=0,[2]=11,	[3]=-61,[4]=-66,[5]=0,[6]=0,	[7]=0,	[8]=101,[9]=110,[10]=103,	[11]=0,	[12]=80,	dataBuffer= 4081バイト,書込み=Part One: One Night ～4081文字
 *02 I'm not in Love.mp3,=����eng��I'm not i～1145文字)							buffer[0]=0,[1]=0,[2]=4,	[3]=115,[4]=0,	[5]=0,[6]=0,	[7]=101,[8]=110,[9]=103,[10]=0,		[11]=73,[12]=39,	dataBuffer= 1134バイト,		書込み=I'm not in love, so ～1134文字
 *03 Black Mail.mp3,=����eng��She doesn～8701268文字).								buffer[0]=0,[1]=0,[2]=4,	[3]=4,	[4]=0,	[5]=0,[6]=0,	[7]=101,[8]=110,[9]=103,[10]=0,		[11]=83,[12]=104,	dataBuffer= 4081バイト,書込み=She doesn't need mon～4081文字
 *04 The Second Sitting For The Last S.mp3,=����eng��Another f～4092文字)			buffer[0]=0,[1]=0,[2]=4,	[3]=72,	[4]=0,	[5]=0,[6]=0,	[7]=101,[8]=110,[9]=103,[10]=0,		[11]=65,[12]=110,dataBuffer= 4081バイト,書込み=Another fish head in～4081文字
 *05 Brand New Day.mp3=����eng��Another f～4092文字)								buffer[0]=0,[1]=0,[2]=4,	[3]=72,	[4]=0,	[5]=0,[6]=0,	[7]=101,[8]=110,[9]=103,[10]=0,		[11]=65,[12]=110,dataBuffer= 4081バイト,書込み=Another fish head in～4081文字
 *06 Flying Junk.mp3=����eng��Another f～4092文字)									buffer[0]=0,[1]=0,[2]=4,	[3]=72,	[4]=0,	[5]=0,[6]=0,	[7]=101,[8]=110,[9]=103,[10]=0,		[11]=65,[12]=110,dataBuffer= 4081バイト,書込み=Another fish head in～4081文字
 *07 Life Is A Minestrone.mp=����eng��When you ～4092文字)							buffer[0]=0,[1]=0,[2]=4,	[3]=0,	[4]=0,	[5]=0,[6]=0,	[7]=101,[8]=110,[9]=103,[10]=0,		[11]=87,[12]=104,dataBuffer= 4081バイト,書込み=When you open your e～4081文字
 *08 The Film Of My Love.mp3=����eng��When you ～4092文字)							buffer[0]=0,[1]=0,[2]=4,	[3]=0,	[4]=0,	[5]=0,[6]=0,	[7]=101,[8]=110,[9]=103,[10]=0,		[11]=87,[12]=104,dataBuffer= 4081バイト,書込み=When you open your e～4081文字
 *宇多田/FirstLove/FirstLove.mp3=��engÿþ����ÿþ��g_～11530文字)			buffer[0]=0,[1]=0,[2]=5,	[3]=4,	[4]=0,	[5]=0,[6]=1,	[7]=101,[8]=110,[9]=103,[10]=-61,	[11]=-65,[12]=-61,dataBuffer= 11519バイト,書込み=þ����ÿþ��g_n0­0¹0o0
 *宇多田/01 For You.mp3=��engÿþ����ÿþØ0Ã0～4092文字)						buffer[0]=0,[1]=0,[2]=6,	[3]=-61,[4]=-78,[5]=0,[6]=0,	[7]=1,	[8]=101,[9]=110,[10]=103,[11]=-61,[12]=-65デフォルトエンコーディングセット= UTF-8,Encrod=ISO-8859-1(4082文字),書込み=ÿþ����ÿþØ0Ã0É0Õ0©0ó00～����������������������������������������;4082文字
 *宇多田u/01 Addicted To You (UP-IN-HEAVEN MIX.mp3,=��eng～8092文字)			buffer[0]=0,[1]=0,[2]=6,	[3]=-61,[4]=-100,[5]=0,[6]=0,	[7]=1,	[8]=101,[9]=110,[10]=103,	[11]=-61,[12]=-65デフォルトエンコーディングセット= UTF-8,Encrod=ISO-8859-1(8082文字),書込み=ÿþ����ÿþ%R;8082文字
 *宇多田/04 Deep River.mp3=��engÿþ����ÿþ¹ph0～9840文字)					buffer[0]=0,[1]=0,[2]=2,	[3]=-61,[4]=-114,[5]=0,[6]=0,	[7]=1,	[8]=101,[9]=110,[10]=103,		[11]=-61,[12]=-65デフォルトエンコーディングセット= UTF-8,Encrod=ISO-8859-1(9830文字),書込み=ÿþ����ÿþ¹ph0¹p0d0j0P0～����������������������������������������;9830文字
 *ID3v2.2.0,compression(ID3v2.2)=false,unsynchronization(共通)=false,
 *宇多田/01 Automatic -Album Edit-.mp3=ngÿþ����ÿþ´0ü0ë0²～149596文字)		buffer[0]=0,[1]=4,[2]=-62,	[3]=-104,[4]=1,	[5]=101,[6]=110,[7]=103,[8]=-61,[9]=-65,[10]=-61,	[11]=-66,[12]=0
 *宇多田/02 Movin' on without you.mp3=ID3��������<(	result_Tag=ID3v2.2.0	buffer[0]=73,[1]=68,[2]=51,[3]=2,[4]=0,[5]=0,[6]=0,[7]=0,[8]=60,[9]=108,tag,,compression(ID3v2.2)=false,unsynchronization(共通)=false,size=7788,filePointer=10,8911438文字>>8911448文字>ID3v2のサイズフラグでカット>7788文字
 */
//ID3v2.3.0		宇多田ヒカル/FirstLove/First Love -Strings Mix.mp3
//USLT=��engÿþ����ÿþ��g_～4092文字)		buffer[0]=0,[1]=0,[2]=5,[3]=4,[4]=0,	[5]=0,	[6]=1,	[7]=101,[8]=110,[9]=103,[10]=-61,[11]=-65,[12]=-61,startInt=10,Encrod=ISO-8859-1>saiEncordBody>��﻿最後のキスは
//ID3v2.2.0		宇多田ヒカル/Wait&See 〜リスク〜/01 Wait&See 〜リスク〜.mp3
//ULT=ngÿþ����ÿþ(��Y��e��a～11789文字)			buffer[0]=0,[1]=6,[2]=10,	[3]=1,[4]=101,[5]=110,[6]=103,[7]=-61,[8]=-65,[9]=-61,[10]=-66,[11]=0,[12]=0,startInt=7,Encrod=ISO-8859-1>saiEncordBody>��﻿(Yeah)
//宇多田ヒカル/Wait&See 〜リスク〜/02 はやとちり.mp3,
//ULT=ngÿþ����ÿþ]00]0～971文字)					buffer[0]=0,[1]=3,[2]=-61,	[3]=-120,[4]=1,[5]=101,[6]=110,[7]=103,[8]=-61,[9]=-65,[10]=-61,[11]=-66,[12]=0,startInt=7,Encrod=ISO-8859-1>saiEncordBody>��﻿そろそろ部屋から出ておいで

//					String defoltES = Charset.defaultCharset().name();
//					dbMsg += "デフォルトエンコーディングセット= " + defoltES;
					switch (this.majorVersion) {
					case 2:
						startInt = target.length() + 4;										//	target.getBytes("ISO-8859-1").length
						if( -1 < buffer[3] &&  buffer[3] < 5){
							encFlag = buffer[3];
						}else{
							encFlag = buffer[4];
						}
						break;
					default:
						startInt = target.length() + 6;										//	target.getBytes("ISO-8859-1").length
						if( 5 < buffer[7] ){
							encFlag = buffer[6];
						}else{
							encFlag = buffer[7];
						}
						break;
					}
					dbMsg += ",startInt=" + startInt  ;
					tuikaMoji =  bodyData.substring(startInt);		//ヘッダーをカット
					dbMsg += ",Encrod=" + motoEncrod  ;		//フラグ？ここから文字？
					tuikaMoji = getEncordStr( encFlag , motoEncrod ,tuikaMoji);			//フレームに設定されたエンコードフラグを読み、変換した文字を返す
					mojisuu = tuikaMoji.length();
					if( 20< mojisuu ){
						dbMsg += ">saiEncordBody>" + tuikaMoji.substring(0, 20) +"～" + tuikaMoji.substring( mojisuu - 20, mojisuu) ;
					}else{
						dbMsg += ">saiEncordBody>" + tuikaMoji ;
					}
					dbMsg += "(" + tuikaMoji.length() + "文字)" ;		//フラグ？ここから文字？
					result_USLT =tuikaMoji;				//	<UNSYNCED LYRICS>	非同期 歌詞/文書のコピー									//10cc(3)
					break;
				case FELDE_TYPE_DOPUKI_KASI:							//同期をとった歌詞/文章
					/*ID: "SYLT"			$xx xx xx xx
					 * 文字コード				$xx							16進数 1バイト
					 * 															$00  ISO-8859-1を文字コードとして用いる => 同期識別子として$00を用いる
					 * 															$01  Unicodeを文字コードとして用いる => 同期識別子として$00 00を用いる
					 * 言語                       $xx xx xx
					 * タイムスタンプフォーマット $xx
					 * 内容のタイプ               $xx
					 * 				$00  はその他
					 * 				$01  は歌詞
					 * 				$02  は文書のコピー
					 * 				$03  は動作/部分の名前（例：「アダージョ」）
					 * 				$04  はイベント（例：「ドン・キホーテが舞台に登場」）
					 * 				$05  はコード（例：「Bb F Fsus」）
					 * 				$06  はひとくちメモ（ポップアップインフォメーション）
					 * 内容に関する情報           <エンコード指定文字列> $00 (00)*/
					startInt = target.getBytes(StandardCharsets.ISO_8859_1).length + 7;
					tuikaMoji =  bodyData.substring(startInt);
					result_SYLT =tuikaMoji;				//同期 歌詞/文書
					break;
				case FELDE_TYPE_ATTACHED_PICTURE:						//APIC;Attached picture
	//10cc;APICのデータ(bodyData)=����JPG������ÿØÿà��J～75936文字).retType=16,startInt= 8,buffer[0]=0,[1]=1,[2]=40,[3]=-62,[4]=-102,[5]=0,[6]=0,[7]=0,[8]=74,[9]=80,[10]=71,書込み=binary data～11文字,tagData8件
					tuikaMoji = "binary data";
					result_APIC =tuikaMoji;				//付属する画像																			//10cc(5')APIC��(������JPG������ÿØÿà��
					break;
				case FELDE_TYPE_ONGAKU_CD_SIKIBETUSI:					//音楽ＣＤ識別子
					//バイナリダンプで構成されている。最大は804バイト
					//これは、4バイトのヘッダで始まり、８バイトの「CD中のトラック」がトラック数だけ続き、そして８バイトの「リードアウト領域」で終わる
					startInt = target.length() + 3;									//	target.getBytes("ISO-8859-1").length
					tuikaMoji =  bodyData.substring(startInt);
					result_MCDI =tuikaMoji;					//Music CD identifier
					break;
				case FELDE_TYPE_DOUKI_TEMPO_CORD:						//同期テンポコード
					/*
					 *<「同期テンポコード」のヘッダ、ID: "SYTC">
					 *タイムスタンプフォーマット	$xx
					 *テンポデータ					 <binary data> */
					startInt = target.length() + 3;									//	target.getBytes("ISO-8859-1").length
					tuikaMoji =  bodyData.substring(startInt);
					result_SYTC =tuikaMoji;					//Synchronized tempo codes
					break;
				case FELDE_TYPE_ITITEKISIKIBETUSI:						//一意的なファイル識別子
					//終了を表すヌル文字以外に一字以上
					startInt = target.length() + 3;									//	target.getBytes("ISO-8859-1").length
					tuikaMoji =  bodyData.substring(startInt);
					result_UFID =tuikaMoji;					//一意的なファイル識別子	タグID
					break;
				case FELDE_TYPE_IVENT_TIME_CORD:						//イベントタイムコード
					startInt = target.length() + 3;									//	target.getBytes("ISO-8859-1").length
					tuikaMoji =  bodyData.substring(startInt);
					result_ETCO =tuikaMoji;					// Event timing codes]
					break;
				case FELDE_TYPE_MPRG_LCATION_TABLE:					//MPEG ロケーションルックアップテーブル
					startInt = target.length() + 3;									//	target.getBytes("ISO-8859-1").length
					tuikaMoji =  bodyData.substring(startInt);
					result_MLLT =tuikaMoji;					//MPEG location lookup table]
					break;
				case FELDE_TYPE_RELEATIVE_VOLUME_ADJUSTMENT:			//Relative volume adjustment
					startInt = target.length() + 3;									//	target.getBytes("ISO-8859-1").length
					tuikaMoji =  bodyData.substring(startInt);
					result_RVAD =tuikaMoji;					// Relative volume adjustment]
					break;
				case FELDE_TYPE_EQUALISATION:							//Equalisation
					startInt = target.length() + 3;									//	target.getBytes("ISO-8859-1").length
					tuikaMoji =  bodyData.substring(startInt);
					result_EQUA =tuikaMoji;					//Equalization
					break;
				case FELDE_TYPE_REVERB:									//Reverb
					startInt = target.length() + 3;									//	target.getBytes("ISO-8859-1").length
					tuikaMoji =  bodyData.substring(startInt);
					result_RVRB =tuikaMoji;					//Reverb
					break;
				case FELDE_TYPE_GENERAL_ENCAPSULATED_OBJECT:			//General encapsulated object
					startInt = target.length() + 3;									//	target.getBytes("ISO-8859-1").length
					tuikaMoji =  bodyData.substring(startInt);
					result_GEOB =tuikaMoji;					//General encapsulated object]
					break;
				case FELDE_TYPE_PLAY_CONUNTRE:							//Play counter
					startInt = target.length() + 3;									//	target.getBytes("ISO-8859-1").length
					tuikaMoji =  bodyData.substring(startInt);
					result_PCNT =tuikaMoji;					//Play counter]
					break;
				case FELDE_TYPE_RECOMMENEDED_BUFFFER_SIZE:			//Recommended buffer size
					startInt = target.length() + 3;									//	target.getBytes("ISO-8859-1").length
					tuikaMoji =  bodyData.substring(startInt);
					result_RBUF =tuikaMoji;					// Recommended buffer size]
					break;
				case FELDE_TYPE_AUDIO_ENCRYOTION:						//Audio encryption
					startInt = target.length() + 3;									//	target.getBytes("ISO-8859-1").length
					tuikaMoji =  bodyData.substring(startInt);
					result_AENC =tuikaMoji;					//Audio encryption
					break;
				case FELDE_TYPE_LINKED_INFOMATION:						//Linked information
					startInt = target.length() + 3;									//	target.getBytes("ISO-8859-1").length
					tuikaMoji =  bodyData.substring(startInt);
					result_LINK =tuikaMoji;					//Linked information]
					break;
				case FELDE_TYPE_POSITION_SYNCHRONISATION_FREAME:		//Position synchronisation frame
					startInt = target.length() + 3;									//	target.getBytes("ISO-8859-1").length
					tuikaMoji =  bodyData.substring(startInt);
					result_POSS =tuikaMoji;					//Position synchronisation frame]
				case FELDE_TYPE_TERMS_OF_USE_FREAME:					//Terms of use frame
					startInt = target.length() + 3;									//	target.getBytes("ISO-8859-1").length
					tuikaMoji =  bodyData.substring(startInt);
					result_USER =tuikaMoji;					//Terms of use
				case FELDE_TYPE_OWNERSHIP_FREAME:						//Ownership frame
					startInt = target.length() + 3;									//	target.getBytes("ISO-8859-1").length
					tuikaMoji =  bodyData.substring(startInt);
					result_OWNE =tuikaMoji;					//Ownership frame]
					break;
				case FELDE_TYPE_COMMERICAL_FREAME:						//Commercial frame
					startInt = target.length() + 3;									//	target.getBytes("ISO-8859-1").length
					tuikaMoji =  bodyData.substring(startInt);
					result_COMR =tuikaMoji;					//Commercial frame]
					break;
				case FELDE_TYPE_ENCRYPTION_METHOD_REGISTRATION:		//Encryption method registration
					startInt = target.length() + 3;									//	target.getBytes("ISO-8859-1").length
					tuikaMoji =  bodyData.substring(startInt);
					result_ENCR =tuikaMoji;					//Encryption method registration
					break;
				case FELDE_TYPE_GRUPE_IDENTIFICTION_REGISTRATION:	//Group identification registration
					startInt = target.length() + 3;									//	target.getBytes("ISO-8859-1").length
					tuikaMoji =  bodyData.substring(startInt);
					result_GRID =tuikaMoji;					//Group identification registration]
					break;
				case FELDE_TYPE_PRIVATE_FREAME:							//Private frame
	//PRIVのデータ(bodyData)=��WM/MediaClassS～47文字).retType=29,startInt= 8,buffer[0]=0,[1]=0,[2]=0,[3]=41,[4]=0,[5]=0,[6]=87,[7]=77,[8]=47,[9]=77,[10]=101,書込み=M/MediaClassSecondaryID���������～40文字,tagData11件
	//PRIVのデータ(bodyData)=��WM/MediaClassP～45文字).retType=29,startInt= 8,buffer[0]=0,[1]=0,[2]=0,[3]=39,[4]=0,[5]=0,[6]=87,[7]=77,[8]=47,[9]=77,[10]=101,書込み=M/MediaClassPrimaryID��¼}`Ñ#ãâK¡H¤*(D～38文字,tagData12件
	//PRIVのデータ(bodyData)=��WM/Provider��A��～26文字).retType=29,startInt= 8,buffer[0]=0,[1]=0,[2]=0,[3]=20,[4]=0,[5]=0,[6]=87,[7]=77,[8]=47,[9]=80,[10]=114,書込み=M/Provider��A��M��G������～19文字,tagData13件
	//PRIVのデータ(bodyData)=��WM/WMContentID～37文字).retType=29,startInt= 8,buffer[0]=0,[1]=0,[2]=0,[3]=31,[4]=0,[5]=0,[6]=87,[7]=77,[8]=47,[9]=87,[10]=77,書込み=M/WMContentID��QÚ401`°M¥gÃkÄKµ～30文字,tagData14件
	//PRIVのデータ(bodyData)=��WM/WMCollectio～40文字).retType=29,startInt= 8,buffer[0]=0,[1]=0,[2]=0,[3]=34,[4]=0,[5]=0,[6]=87,[7]=77,[8]=47,[9]=87,[10]=77,書込み=M/WMCollectionID��&±:¿SðìBsµáy¥6µ～33文字,tagData15件
	//PRIVのデータ(bodyData)=��WM/WMCollectio～45文字).retType=29,startInt= 8,buffer[0]=0,[1]=0,[2]=0,[3]=39,[4]=0,[5]=0,[6]=87,[7]=77,[8]=47,[9]=87,[10]=77,書込み=M/WMCollectionGroupID��&±:¿SðìBsµáy¥6µ～38文字,tagData16件
	//PRIVのデータ(bodyData)=��WM/UniqueFileI～144文字).retType=29,startInt= 8,buffer[0]=0,[1]=0,[2]=0,[3]=-62,[4]=-118,[5]=0,[6]=0,[7]=87,[8]=77,[9]=47,[10]=85,書込み=M/UniqueFileIdentifi～137文字,tagData18件
		//			startInt = target.length() + 3;									//	target.getBytes("ISO-8859-1").length
		//			tuikaMoji =  bodyData.substring(startInt, bodyData.length());
					if( result_PRIV == null ){
						result_PRIV = "1";
					}else{
						result_PRIV =String.valueOf(Integer.valueOf(result_PRIV) + 1);
					}
					break;
//				case FELDE_TYPE_FUMEI:								//不明
//					break;
				case FELDE_TYPE_COMENTS:									//12;Comments
	//COMMのデータ(bodyData)=����eng�� 000001BB～101文字).retType=12,startInt= 8,buffer[0]=0,[1]=0,[2]=0,[3]=95,[4]=0,[5]=0,[6]=0,[7]=101,[8]=110,[9]=103,[10]=0,書込み=eng�� 000001BB 000001～94文字,tagData9件
	//COMMのデータ(bodyData)=��engÿþ����ÿþm=～214文字).retType=12,startInt= 8,buffer[0]=0,[1]=0,[2]=0,[3]=-61,[4]=-112,[5]=0,[6]=0,[7]=1,[8]=101,[9]=110,[10]=103,書込み=engÿþ����ÿþm=c0l_0c～207文字,tagData10件
	//COMMのデータ(bodyData)=2rÄ\d¨¥ä`ñÚ～7085426文字).retType=12,startInt= 8,buffer[0]=0,[1]=0,[2]=-62,[3]=-102,[4]=34,[5]=79,[6]=50,[7]=114,[8]=-62,[9]=-123,[10]=-61,書込み=Ä\d¨¥ä`ñÚ`1Æ>～7085419文字,tagData21件
			//		startInt = target.length() + 3;									//	target.getBytes("ISO-8859-1").length
			//		tuikaMoji =  bodyData.substring(startInt, bodyData.length());
					if( result_COMM == null ){
						result_COMM = "1";
					}else{
						result_COMM =String.valueOf(Integer.valueOf(result_COMM) + 1);
					}
					break;
				case FELDE_TYPE_POPULARIMETER:							//19;Popularimeter
	//POPMのデータ(bodyData)=��Windows Media ～37文字).retType=19,startInt= 8,buffer[0]=0,[1]=0,[2]=0,[3]=31,[4]=0,[5]=0,[6]=87,[7]=105,[8]=110,[9]=100,[10]=111,書込み=indows Media Player 9 Series��～30文字,tagData19件
					startInt = target.length() + 3;									//	target.getBytes("ISO-8859-1").length
					tuikaMoji =  bodyData.substring(startInt);
					result_POPM =tuikaMoji;					//人気メーター																				//10cc(2')Windows Media Player 9 Seri
					break;
				case FELDE_TYPE_Encrypted_META_fREAM:				//ID3v2.2
					result_CRM =tuikaMoji;			//ID3v2.2
				default:
					tuikaMoji =  bodyData.substring(startInt);
					break;
				}			//switch(retType) {
				if( tuikaMoji != null ){
					int mojisuu = tuikaMoji.length();
					if(40 < mojisuu){
						dbMsg= dbMsg+ ",書込み="+ tuikaMoji.substring(0, 20) + "～"+ tuikaMoji.substring(mojisuu-20, mojisuu);
					}else{
						dbMsg= dbMsg+ ",書込み="+ tuikaMoji;
					}
					dbMsg= dbMsg+ ";" + mojisuu + "文字";
//					tagData.add(target + ";" + tuikaMoji);
//					dbMsg +=",tagData"+ tagData.size() + "件";
				}
				buffer = null;
			}
			myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}
	}

	public int nullPosition;
	public String kiremeKensaku(String result ,int startInt){			//渡された文字列から'\0'が検出されたポイントまでの文字を返す
		String retStr = null;
		final String TAG = "kiremeKensaku";
		String dbMsg= "";
		try{
			dbMsg= startInt + "/" ;
			char[] str = result.toCharArray();
			dbMsg +=str.length + "文字目から";
			if( startInt < str.length ){
				//		dbMsg=  dbMsg +"(検索対象" + str.length +"文字)" ;				//+ bodyStr;
				for(nullPosition = startInt ; str[nullPosition] != '\0' ; nullPosition++ ) {
					String readChar = String.valueOf(str[nullPosition]);
					if( readChar != null ){
						if( ! readChar.equals("null") ){
							if( retStr != null ){
								retStr = retStr + str[nullPosition];
							}else{
								retStr =  String.valueOf(str[nullPosition]);
							}
						}
					}
				}
				dbMsg +="retStr=" + retStr;
			}
			if(retStr != null){
				dbMsg += "(" + retStr.length() +"文字)";
			}
	//		myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}
		return retStr;
	}
//
	public String fremeMeiSyougouBody(String result, String taget){	//渡された文字列を先頭からindexOfで照合し、該当すればその文字を返し、無ければnullを返す
		String retStr = null;
		final String TAG = "fremeMeiSyougouBody";
		String dbMsg= "";
		try{
			dbMsg=  result + "(";
	//		dbMsg +=result.length() + "文字)から";
			dbMsg +=taget + "を検索";
			if(result != null){
				int startP = result.indexOf(taget);
				dbMsg += "," + startP + "文字目";
				if( -1 < startP ){
					dbMsg +="(" + startP + ")";			// + result.length() + ")";
					this.fleamStart= startP ;
					retStr = taget;
					dbMsg +=retStr;
				}
			}
		//	myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}
		return retStr;
	}

	public String fremeMeiSyougou(String result , int reqCode){			//渡された文字列を先頭からフレーム名を照合し、該当するフレーム名を返す
		String retStr = null;
		int startP = 0;
		String chStr ="";
		final String TAG = "fremeMeiSyougou[]";
		String dbMsg= "";
		try{
			int hikakuMojisuu =  result.length();
			dbMsg=  result.length() + "文字から";
			int syougouSize = syougou.size();
			if(4 <= hikakuMojisuu){
				for(int i= 0 ; i < syougouSize ; i++){
					chStr= syougou.get(i);									//Frame ID
			//		retStr = fremeMeiSyougouBody( result, chStr);	//渡された文字列を先頭からindexOfで照合し、該当すればその文字を返し、無ければnullを返す
					startP = result.indexOf(chStr);
					if( -1 < startP ){
						dbMsg +="(" + i + "/" + syougouSize + ")";
						retStr = chStr;
						dbMsg +=retStr;
						switch(reqCode) {
//						case read_USLT:				//	<UNSYNCED LYRICS>	非同期 歌詞/文書のコピー									//10cc(3)
//							break;
//						case read_AAC_HEAD:				//QuickTime Tagsの読取り
//							break;
//						case read_AAC_HEAD_Movie:		//QuickTime Tags.QuickTime Movie Tagsの読取り
//							break;
//						case read_AAC_Movie_Meta:		//QuickTime Tags.QuickTime Meta Tagsの読取り
//							break;
						case read_AAC_LYRIC:				//@Lyrだけを読めるか試みる
						case read_AAC_ITEM:				//QuickTime Tagsの読取り
							byte[] buffer = result.substring(startP + 4, startP + 7).getBytes();									//タグヘッダ読込開始			startInt-maeoki
							int bufferSize = buffer.length;
							dbMsg += ",buffer=" + bufferSize + "バイト";//Atom Size
							dbMsg += ",buffer[0]=" + buffer[0];//Atom Size
							dbMsg += ",[1]=" + buffer[1];		//
							dbMsg += ",[2]=" + buffer[2];		//
/*
 * Bob Dylan/Desire/01 Hurricane.m4a,				8769696文字から(16/122)aART,buffer=4バイト,	buffer[0]=0,	[1]=0,	[2]=0,
 * 		authorities came to ～	で"auth"に反応		419852文字から(24/122)auth,buffer=4バイト,	buffer[0]=111,	[1]=114,[2]=105,
 * Bob Dylan/Desire/04 One More Cup Of Coffee.m4a,	3361835文字から(16/122)aART,buffer=3バイト,	buffer[0]=0,	[1]=0,	[2]=0
 * 													415961文字から(27/122)covr,buffer=3バイト,	buffer[0]=0,	[1]=6,	[2]=85
 *  	free Upon the beach～	で"free"に反応		772文字から(88/122)free,buffer=3バイト,		buffer[0]=13,	[1]=10,	[2]=85
 */
							if(buffer[0] == 0){
								i = syougou.size();
							}else{
								retStr = null;
							}
				//			myLog(TAG,dbMsg);
							break;
						default:
							i = syougou.size();
							break;
						}
					}
				}
			}
			this.fleamStart= startP ;
	//		myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}
		return retStr;
	}

	/**
	 *４文字のフレーム名らしいパターンを探して照合。該当すればフレーム名を返して、グローバル変数にポジションを設定
	 *
	 * * */
	public String freameNamePosition(final String identifier) {			//４文字のフレーム名らしいパターンを探して照合。該当すればフレーム名を返して、グローバル変数にポジションを設定
		String stock = null;
		String retStr= null;
		final String TAG = "freameNamePosition";
		String dbMsg= "";
		try{
			dbMsg= "identifier=" + identifier.length() + "文字";
			char character;
			int endC = identifier.length();
			for (int i = 0; i < endC; i++) {
		//		dbMsg +="[" + i + "/" + endC + "]";
				character = identifier.charAt(i);
		//		dbMsg +=",character=" + character;
				if ( 'A' <= character && character <= 'Z' || ( '0' <= character && character <= '9')) {		//アルファベットの大文字のみストック	//
					stock = stock + character;
		//			dbMsg +=i + ")" + stock;
					if( 4< stock.length()){								//大文字で４文字を超えたら
						stock =stock.substring(1);		//１文字目を消去
					}
					if( 3< stock.length()){								//大文字で４文字溜まっていたら
						dbMsg +=i + ")" + stock + ",fleamStart= " + this.fleamStart;
						retStr = fremeMeiSyougou( stock  , reqCode);			//渡された文字列を先頭からフレーム名を調合し、該当するフレーム名を返す
//					} else if( 2< stock.length()){						//大文字で3文字溜まっていて
//						if((character >= '0' && character <= '9')){		//4文字目が数字なら
//							numCount = numCount+1;
//							if(1 < numCount){
//								dbMsg +=i + ")" + stock + ",fleamStart= " + this.fleamStart;
//								retStr = fremeMeiSyougou( stock);			//渡された文字列を先頭からフレーム名を調合し、該当するフレーム名を返す
//							}
//						}
					}
					if( retStr != null ){				//stock.equals(retStr)
						this.fleamStart= i ;
						dbMsg +=">>" + this.fleamStart;
						dbMsg +=",retStr= " + retStr;
						i = identifier.length();
		//				myLog(TAG,dbMsg);
						return retStr;
					}
				} else   {										//アルファベットの大文字以外なら
					stock =null;							//ストックをリセット
				}
//				if(stock != null){
//				}
			}
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}
		return retStr;
	}

	public static final int FELDE_TYPE_FUMEI = -1;						//不明
	public static final int FELDE_TYPE_TEXTINFO = 1;						//テキスト情報フレーム
	public static final int FELDE_TYPE_USRE_TEIGI_MOJI = FELDE_TYPE_TEXTINFO + 1;				//ユーザー定義テキスト情報フレーム
	public static final int FELDE_TYPE_URL_LINK = FELDE_TYPE_USRE_TEIGI_MOJI + 1;							//リンクフレーム
	public static final int FELDE_TYPE_USRE_TEIGI_URL_LINK  = FELDE_TYPE_URL_LINK + 1;					//ユーザー定義URLリンクフレーム
	public static final int FELDE_TYPE_KYOURYOKUSYA_ITIRAN = FELDE_TYPE_USRE_TEIGI_URL_LINK + 1;		//協力者一覧
	public static final int FELDE_TYPE_ONGAKU_CD_SIKIBETUSI = FELDE_TYPE_KYOURYOKUSYA_ITIRAN + 1;		//音楽ＣＤ識別子
	public static final int FELDE_TYPE_IVENT_TIME_CORD = FELDE_TYPE_ONGAKU_CD_SIKIBETUSI + 1;			//イベントタイムコード
	public static final int FELDE_TYPE_MPRG_LCATION_TABLE = FELDE_TYPE_IVENT_TIME_CORD + 1;			//MPEG ロケーションルックアップテーブル
	public static final int FELDE_TYPE_DOUKI_TEMPO_CORD = FELDE_TYPE_MPRG_LCATION_TABLE + 1;			//同期テンポコード
	public static final int FELDE_TYPE_HIDOPUKI_KASI = FELDE_TYPE_DOUKI_TEMPO_CORD + 1;			//非同期の歌詞/文章のコピー
	public static final int FELDE_TYPE_DOPUKI_KASI = FELDE_TYPE_HIDOPUKI_KASI + 1;			//同期をとった歌詞/文章
	public static final int FELDE_TYPE_COMENTS = FELDE_TYPE_DOPUKI_KASI + 1;			//Comments
	public static final int FELDE_TYPE_RELEATIVE_VOLUME_ADJUSTMENT = FELDE_TYPE_COMENTS + 1;			//Relative volume adjustment
	public static final int FELDE_TYPE_EQUALISATION = FELDE_TYPE_RELEATIVE_VOLUME_ADJUSTMENT + 1;			//Equalisation
	public static final int FELDE_TYPE_REVERB = FELDE_TYPE_EQUALISATION + 1;			//Reverb
	public static final int FELDE_TYPE_ATTACHED_PICTURE = FELDE_TYPE_REVERB + 1;			//Attached picture
	public static final int FELDE_TYPE_GENERAL_ENCAPSULATED_OBJECT = FELDE_TYPE_ATTACHED_PICTURE + 1;			//General encapsulated object
	public static final int FELDE_TYPE_PLAY_CONUNTRE = FELDE_TYPE_GENERAL_ENCAPSULATED_OBJECT + 1;			//Play counter
	public static final int FELDE_TYPE_POPULARIMETER = FELDE_TYPE_PLAY_CONUNTRE + 1;			//Popularimeter
	public static final int FELDE_TYPE_RECOMMENEDED_BUFFFER_SIZE = FELDE_TYPE_POPULARIMETER + 1;			//Recommended buffer size
	public static final int FELDE_TYPE_AUDIO_ENCRYOTION = FELDE_TYPE_RECOMMENEDED_BUFFFER_SIZE + 1;			//Audio encryption
	public static final int FELDE_TYPE_LINKED_INFOMATION = FELDE_TYPE_AUDIO_ENCRYOTION + 1;			//Linked information
	public static final int FELDE_TYPE_POSITION_SYNCHRONISATION_FREAME = FELDE_TYPE_LINKED_INFOMATION + 1;			//Position synchronisation frame
	public static final int FELDE_TYPE_TERMS_OF_USE_FREAME = FELDE_TYPE_POSITION_SYNCHRONISATION_FREAME + 1;			//Terms of use frame
	public static final int FELDE_TYPE_OWNERSHIP_FREAME = FELDE_TYPE_TERMS_OF_USE_FREAME + 1;			//Ownership frame
	public static final int FELDE_TYPE_COMMERICAL_FREAME = FELDE_TYPE_OWNERSHIP_FREAME + 1;			//Commercial frame
	public static final int FELDE_TYPE_ENCRYPTION_METHOD_REGISTRATION = FELDE_TYPE_COMMERICAL_FREAME + 1;			//Encryption method registration
	public static final int FELDE_TYPE_GRUPE_IDENTIFICTION_REGISTRATION = FELDE_TYPE_ENCRYPTION_METHOD_REGISTRATION + 1;			//Group identification registration
	public static final int FELDE_TYPE_PRIVATE_FREAME = FELDE_TYPE_GRUPE_IDENTIFICTION_REGISTRATION + 1;			//Private frame
	public static final int FELDE_TYPE_ITITEKISIKIBETUSI = FELDE_TYPE_PRIVATE_FREAME + 1;			//一意的なファイル識別子
	public static final int FELDE_TYPE_Encrypted_META_fREAM = FELDE_TYPE_ITITEKISIKIBETUSI + 1;			//ID3v2.2

	public int retFeldType(String frameIdentifier){			//渡されたフィールドのタイプを返す
		int retType = -1;
		final String TAG = "retFeldType";
		String dbMsg= "";
		try{
			if (frameIdentifier.equals("TALB") ||				//	アルバム/映画/ショーのタイトル
				frameIdentifier.equals("TAL")  ||		//アルバム/映画/ショーのタイトル								Album/Movie/Show title									ID3ｖ3；TALB
				frameIdentifier.equals("TBPM") ||		//一分間の拍数													BPM (Beats Per Minute)									ID3ｖ3；TBPM
				frameIdentifier.equals("TBP")  ||		//一分間の拍数													BPM (Beats Per Minute)									ID3ｖ3；TBPM
				frameIdentifier.equals("TCOM") ||		//作曲者														Composer												ID3ｖ3；TCOM
				frameIdentifier.equals("TCM")  ||		//作曲者														Composer												ID3ｖ3；TCOM
				frameIdentifier.equals("TCON") ||		//ジャンル														Content type											ID3ｖ3；TCON
				frameIdentifier.equals("TCO")  ||		//ジャンル														Content type											ID3ｖ3；TCON
				frameIdentifier.equals("TCOP") ||		//																Copyright message										ID3ｖ3；TCOP?
				frameIdentifier.equals("TCR")  ||		//																Copyright message										ID3ｖ3；TCOP?
				frameIdentifier.equals("TDAT") ||		//日付	Date*11	Date*12	Year*13 Deprecated						Date													ID3ｖ3；TDAT
				frameIdentifier.equals("TDA")  ||		//日付	Date*11	Date*12	Year*13 Deprecated						Date													ID3ｖ3；TDAT
				frameIdentifier.equals("TDLY") ||		//プレイリスト遅延時間											Playlist delay											ID3ｖ3；TDLY
				frameIdentifier.equals("TDY")  ||		//プレイリスト遅延時間											Playlist delay											ID3ｖ3；TDLY
				frameIdentifier.equals("TENC") ||		//エンコーディング ソフトウェア									Encoded by												ID3ｖ3；TENC
				frameIdentifier.equals("TEN")  ||		//エンコーディング ソフトウェア									Encoded by												ID3ｖ3；TENC
				frameIdentifier.equals("TEXT") ||		//作詞家/文書作成者												Lyricist/text writer									ID3ｖ3；TEXT
				frameIdentifier.equals("TXT")  ||		//作詞家/文書作成者												Lyricist/text writer									ID3ｖ3；TEXT
				frameIdentifier.equals("TFLT") ||		//ファイルタイプ												File type												ID3ｖ3；TFLT
				frameIdentifier.equals("TFT")  ||		//ファイルタイプ												File type												ID3ｖ3；TFLT
				frameIdentifier.equals("TIME") ||		//時間															Time(Time Deprecated?)									ID3ｖ3；TIME
				frameIdentifier.equals("TIM")  ||		//時間															Time(Time Deprecated?)									ID3ｖ3；TIME
				frameIdentifier.equals("TIT1") ||		//内容の属するグループの説明									Content group description								ID3ｖ3；TIT1;
				frameIdentifier.equals("TT1")  ||		//内容の属するグループの説明									Content group description								ID3ｖ3；TIT1;
				frameIdentifier.equals("TIT2") ||		//タイトル/曲名/内容の説明	タイトル							Track Title	Title/Title/Songname/Content description	ID3ｖ3；TIT2;
				frameIdentifier.equals("TT2")  ||		//タイトル/曲名/内容の説明	タイトル							Track Title	Title/Title/Songname/Content description	ID3ｖ3；TIT2;
				frameIdentifier.equals("TIT3") ||		//サブタイトル/説明の追加情報									Subtitle/Description refinement							ID3ｖ3；TIT3;
				frameIdentifier.equals("TT3")  ||		//サブタイトル/説明の追加情報									Subtitle/Description refinement							ID3ｖ3；TIT3;
				frameIdentifier.equals("TKEY") ||		//初めの調														Initial key												ID3ｖ3；TKEY
				frameIdentifier.equals("TKE")  ||		//初めの調														Initial key												ID3ｖ3；TKEY
				frameIdentifier.equals("TLAN") ||		//言語															Language(s)												ID3ｖ3；TLAN
				frameIdentifier.equals("TLA")  ||		//言語															Language(s)												ID3ｖ3；TLAN
				frameIdentifier.equals("TLEN") ||		//長さ															Length													ID3ｖ3；TLEN
				frameIdentifier.equals("TLE")  ||		//長さ															Length													ID3ｖ3；TLEN
				frameIdentifier.equals("TMED") ||		//メディアタイプ												Media type												ID3ｖ3；TMED
				frameIdentifier.equals("TMT")  ||		//メディアタイプ												Media type												ID3ｖ3；TMED
				frameIdentifier.equals("TOAL") ||		//オリジナルのアルバム/映画/ショーのタイトル					Original album/Movie/Show title							ID3ｖ3；TOAL
				frameIdentifier.equals("TOT")  ||		//オリジナルのアルバム/映画/ショーのタイトル					Original album/Movie/Show title							ID3ｖ3；TOAL
				frameIdentifier.equals("TOFN") ||		//オリジナルファイル名											Original filename										ID3ｖ3；TOFN
				frameIdentifier.equals("TOF")  ||		//オリジナルファイル名											Original filename										ID3ｖ3；TOFN
				frameIdentifier.equals("TOLY") ||		//オリジナルの作詞家/文書作成者									Original Lyricist(s)/text writer(s)						ID3ｖ3；TOLY
				frameIdentifier.equals("TOL")  ||		//オリジナルの作詞家/文書作成者									Original Lyricist(s)/text writer(s)						ID3ｖ3；TOLY
				frameIdentifier.equals("TOPE") ||		//オリジナルアーティスト/演奏者									Original artist(s)/performer(s)							ID3ｖ3；TOPE
				frameIdentifier.equals("TOA")  ||		//オリジナルアーティスト/演奏者									Original artist(s)/performer(s)							ID3ｖ3；TOPE
				frameIdentifier.equals("TORY") ||		//オリジナルのリリース年										Original release year									ID3ｖ3；TORY
				frameIdentifier.equals("TOR")  ||		//オリジナルのリリース年										Original release year									ID3ｖ3；TORY
				"TOWN".equals(frameIdentifier) ||				// 	ファイルの所有者/ライセンシー	File owner/licensee
				frameIdentifier.equals("TPE1") ||		//主な演奏者/ソリスト/トラック アーティスト				Lead artist(s)/Lead performer(s)/Soloist(s)/Performing group	ID3ｖ3；TPE1
				frameIdentifier.equals("TP1")  ||		//主な演奏者/ソリスト/トラック アーティスト				Lead artist(s)/Lead performer(s)/Soloist(s)/Performing group	ID3ｖ3；TPE1
				frameIdentifier.equals("TPE2") ||		//バンド/オーケストラ/伴奏	バンド/オーケストラ					Band/Orchestra/Accompaniment/Album Artist				ID3ｖ3；TPE2
				frameIdentifier.equals("TP2")  ||		//バンド/オーケストラ/伴奏	バンド/オーケストラ					Band/Orchestra/Accompaniment/Album Artist				ID3ｖ3；TPE2
				frameIdentifier.equals("TPE3") ||		//指揮者/演奏者詳細情報	指揮者									Conductor/Performer refinement							ID3ｖ3；TPE3
				frameIdentifier.equals("TP3")  ||		//指揮者/演奏者詳細情報	指揮者									Conductor/Performer refinement							ID3ｖ3；TPE3
				frameIdentifier.equals("TPE4") ||		//翻訳者, リミックス, その他の修正								Interpreted, remixed, or otherwise modified by			ID3ｖ3；TPE4
				frameIdentifier.equals("TP4")  ||		//翻訳者, リミックス, その他の修正								Interpreted, remixed, or otherwise modified by			ID3ｖ3；TPE4
				frameIdentifier.equals("TPOS") ||		//セット中の位置/ディスク #	Disc Number	Disc#					Part of a set											ID3ｖ3；TPOS
				frameIdentifier.equals("TPA")  ||		//セット中の位置/ディスク #	Disc Number	Disc#					Part of a set											ID3ｖ3；TPOS
				frameIdentifier.equals("TPUB") ||		//出版社/発行元													Publisher												ID3ｖ3；TPUB
				frameIdentifier.equals("TPB")  ||		//出版社/発行元													Publisher												ID3ｖ3；TPUB
				frameIdentifier.equals("TRCK") ||		//トラックの番号/セット中の位置									Track number/Position in set							ID3ｖ3；TRCK
				frameIdentifier.equals("TRK")  ||		//トラックの番号/セット中の位置									Track number/Position in set							ID3ｖ3；TRCK
				frameIdentifier.equals("TRDA") ||		//録音日付														Recording dates											ID3ｖ3；TRDA
				frameIdentifier.equals("TRD")  ||		//録音日付														Recording dates											ID3ｖ3；TRDA
				"TRSN".equals(frameIdentifier) ||				// インターネットラジオ局の名前	Internet radio station name
				"TRSO".equals(frameIdentifier) ||				// 	インターネットラジオ局の所有者	Internet radio station owner
				frameIdentifier.equals("TSIZ") ||		//サイズ														Size(Size	 Deprecated?)								ID3ｖ3；TSIZ;
				frameIdentifier.equals("TSI")  ||		//サイズ														Size(Size	 Deprecated?)								ID3ｖ3；TSIZ;
				frameIdentifier.equals("TSRC") ||		//国際標準レコーディングコード									ISRC (International Standard Recording Code)			ID3ｖ3；TSRC
				frameIdentifier.equals("TRC")  ||		//国際標準レコーディングコード									ISRC (International Standard Recording Code)			ID3ｖ3；TSRC
				frameIdentifier.equals("TSSE") ||		//エンコードに使用したソフトウエア/ハードウエアとセッティング	Software/hardware and settings used for encoding		ID3ｖ3；TSSE;
				frameIdentifier.equals("TSS")  ||		//エンコードに使用したソフトウエア/ハードウエアとセッティング	Software/hardware and settings used for encoding		ID3ｖ3；TSSE;
				frameIdentifier.equals("TSOA") ||		//アルバム（読み）										ALBUMSORT						ID3ｖ2；--	ID3ｖ3；TSOA
				frameIdentifier.equals("TSOP") ||		//アーティスト（読み）									ARTISTSORT						ID3ｖ2；--	ID3ｖ3；TSOP
				frameIdentifier.equals("TSOC") ||		//作曲者（読み）										SortComposer					ID3ｖ2；?-	ID3ｖ3；TSOC	COMPOSERSORT
				frameIdentifier.equals("TYER") ||			//	年	レコーディング年/年*15	Date*16	 Deprecated
				frameIdentifier.equals("TYE")) 		//年	レコーディング年/年*15	Date*16	Year Deprecated			Year													ID3ｖ3；TYER
			{
				retType = FELDE_TYPE_TEXTINFO;			//テキスト情報フレーム
			}else if(frameIdentifier.equals("TXXX") ||
					frameIdentifier.equals("TXX") 		//ユーザー定義文字情報フレーム									User defined text information frame						ID3ｖ3；TXXX
					){				//ユーザー定義文字情報フレーム
				retType = FELDE_TYPE_USRE_TEIGI_MOJI;			//テキスト情報フレーム
			}else if(frameIdentifier.equals("WCOM")  ||		//商業上の情報													Commercial information									ID3ｖ3；WCOM
				frameIdentifier.equals("WCM")  ||		//商業上の情報													Commercial information									ID3ｖ3；WCOM
				frameIdentifier.equals("WCOP") ||		//著作権/法的情報												Copyright/Legal information								ID3ｖ3；WCOP
				frameIdentifier.equals("WCP")  ||		//著作権/法的情報												Copyright/Legal information								ID3ｖ3；WCOP
				frameIdentifier.equals("WOAF") ||		//オーディオファイルの公式Webページ								Official audio file webpage								ID3ｖ3；WOAF
				frameIdentifier.equals("WAF")  ||		//オーディオファイルの公式Webページ								Official audio file webpage								ID3ｖ3；WOAF
				frameIdentifier.equals("WOAR") ||		//アーティスト/演奏者の公式Webページ							Official artist/performer webpage						ID3ｖ3；WOAR
				frameIdentifier.equals("WAR")  ||		//アーティスト/演奏者の公式Webページ							Official artist/performer webpage						ID3ｖ3；WOAR
				frameIdentifier.equals("WOAS") ||		//音源の公式Webページ											Official audio source webpage							ID3ｖ3；WOAS
				frameIdentifier.equals("WAS")  ||		//音源の公式Webページ											Official audio source webpage							ID3ｖ3；WOAS
				"WORS".equals(frameIdentifier) ||				// 	インターネットラジオ局の公式ホームページ	 Official internet radio station homepage
				"WPAY".equals(frameIdentifier) ||				//支払い
				frameIdentifier.equals("WPUB") ||		//出版社の公式Webページ											Publishers official webpage								ID3ｖ3；WPUB
				frameIdentifier.equals("WPB"))			//出版社の公式Webページ											Publishers official webpage								ID3ｖ3；WPUB
			{
				retType = FELDE_TYPE_URL_LINK;				//リンクフレーム
			}else if(frameIdentifier.equals("WXXX") ||
					frameIdentifier.equals("WXX")		//ユーザー定義URLリンクフレーム											User defined URL link frame						ID3ｖ3；WXXX
					){				//	ユーザー定義URLリンクフレーム	User defined URL link frame
				retType = FELDE_TYPE_USRE_TEIGI_URL_LINK ;		//ユーザー定義URLリンクフレーム
			}else if(frameIdentifier.equals("IPLS") ||
					frameIdentifier.equals("IPL")		//協力者一覧													Involved people list									ID3ｖ3；IPLS
					){			//協力者	Involved people list]
				retType = FELDE_TYPE_KYOURYOKUSYA_ITIRAN ;		//協力者一覧
			}else if(frameIdentifier.equals("MCDI") ||
					frameIdentifier.equals("MCI") 		//音楽ＣＤ識別子												Music CD Identifier										ID3ｖ3；MCDI
					){			//	音楽ＣＤ識別子	Music CD identifier
				retType = FELDE_TYPE_ONGAKU_CD_SIKIBETUSI;		//音楽ＣＤ識別子
			}else if(frameIdentifier.equals("ETCO") ||
					frameIdentifier.equals("ETC") 		//イベントタイムコード											Event timing codes										ID3ｖ3；ETCO
					){			//イベントタイムコード	 Event timing codes]
				retType = FELDE_TYPE_IVENT_TIME_CORD ;			//イベントタイムコード
			}else if(frameIdentifier.equals("MLLT") ||
					frameIdentifier.equals("MLL") 		//MPEGロケーションルックアップテーブル							MPEG location lookup table								ID3ｖ3；MLLT
					){			//	MPEGロケーションルックアップテーブル	MPEG location lookup table]
				retType = FELDE_TYPE_MPRG_LCATION_TABLE ;			//MPEG ロケーションルックアップテーブル
			}else if(frameIdentifier.equals("SYTC") ||
					frameIdentifier.equals("STC") 		//																Synced tempo codes										ID3ｖ3；?
					){			//	同期 テンポコード	Synchronized tempo codes
				retType = FELDE_TYPE_DOUKI_TEMPO_CORD ;			//同期テンポコード
			}else if(frameIdentifier.equals("USLT") ||
					frameIdentifier.equals("ULT") 		//非同期 歌詞/文書のコピー										Unsychronized lyric/text transcription					ID3ｖ3；USLT
					){			//非同期 歌詞/文書のコピー		UNSYNCED LYRICS
				retType = FELDE_TYPE_HIDOPUKI_KASI ;			//非同期の歌詞/文章のコピー
			}else if(frameIdentifier.equals("SYLT") ||
					frameIdentifier.equals("SLT") 		//同期 歌詞/文書												Synchronized lyric/text									ID3ｖ3；SYLT
					){			//同期 歌詞/文書
				retType = FELDE_TYPE_DOPUKI_KASI ;			//同期をとった歌詞/文章
			}else if(frameIdentifier.equals("COMM") ||
					frameIdentifier.equals("COM") 		//コメント														Comments												ID3ｖ3；COMM
					){			//コメント
				retType = FELDE_TYPE_COMENTS ;			//Comments
			}else if(frameIdentifier.equals("RVAD") ||
					frameIdentifier.equals("RVA") 		//相対的ボリューム調整											Relative volume adjustment								ID3ｖ3；RVAD
					){		//	相対的ボリューム調整	 Relative volume adjustment]
				retType = FELDE_TYPE_RELEATIVE_VOLUME_ADJUSTMENT ;			//Relative volume adjustment
			}else if(frameIdentifier.equals("EQUA") ||
					frameIdentifier.equals("EQU") 		//音質調整														Equalization											ID3ｖ3；EQUA
					){		//Equalization
				retType = FELDE_TYPE_EQUALISATION ;			//Equalisation
			}else if(frameIdentifier.equals("RVRB") ||
					frameIdentifier.equals("REV") 		//リバーブ	 													Reverb													ID3ｖ3；RVRB
					){		// リバーブ
				retType = FELDE_TYPE_REVERB ;			//Reverb
			}else if(frameIdentifier.equals("APIC") ||
					frameIdentifier.equals("PIC") 		//付属する画像													Attached picture										ID3ｖ3；APIC
					){		//付属する画像
				retType = FELDE_TYPE_ATTACHED_PICTURE;			//Attached picture
			}else if(frameIdentifier.equals("GEOB") ||
					frameIdentifier.equals("GEO") 		//パッケージ化された一般的なオブジェクト						General encapsulated object								ID3ｖ3；GEOB
					){		//パッケージ化された一般的なオブジェクト	General encapsulated object]
				retType = FELDE_TYPE_GENERAL_ENCAPSULATED_OBJECT ;			//General encapsulated object
			}else if(frameIdentifier.equals("PCNT") ||
					frameIdentifier.equals("CNT") 		//演奏回数														Play counter											ID3ｖ3；PCNT
					){					//演奏回数	Play counter]
				retType = FELDE_TYPE_PLAY_CONUNTRE ;				//Play counter
			}else if(frameIdentifier.equals("POPM") ||
					frameIdentifier.equals("POP") 		//人気メーター 													Popularimeter											ID3ｖ3；POPM
					){						//	人気メーター	人気メーター
				retType = FELDE_TYPE_POPULARIMETER ;					//Popularimeter
			}else if(frameIdentifier.equals("RBUF") ||
					frameIdentifier.equals("BUF") 		//推奨バッファサイズ											Recommended buffer size									ID3ｖ3；RBUF
					){							//	おすすめバッファサイズ	 Recommended buffer size]
				retType = FELDE_TYPE_RECOMMENEDED_BUFFFER_SIZE ;			//Recommended buffer size
			}else if(frameIdentifier.equals("AENC") ||
					frameIdentifier.equals("CRA") 		//オーディオの暗号化											Audio encryption										ID3ｖ3；AENC
					){							//オーディオの暗号化
				retType = FELDE_TYPE_AUDIO_ENCRYOTION ;							//Audio encryption
			}else if(frameIdentifier.equals("LINK") ||
					frameIdentifier.equals("LNK") 		//リンク情報	 												Linked information										ID3ｖ3；LINK
					){								//	リンク情報	Linked information]
				retType = FELDE_TYPE_LINKED_INFOMATION ;						//Linked information
			}else if(frameIdentifier.equals("POSS")){								//同期位置フレーム	Position synchronisation frame]
				retType = FELDE_TYPE_POSITION_SYNCHRONISATION_FREAME ;			//Position synchronisation frame
			}else if(frameIdentifier.equals("USER")){								//使用条件	Terms of use
				retType = FELDE_TYPE_TERMS_OF_USE_FREAME ;			//Terms of use frame
			}else if(frameIdentifier.equals("OWNE")){							//	所有権フレーム	Ownership frame]
				retType = FELDE_TYPE_OWNERSHIP_FREAME;			//Ownership frame
			}else if(frameIdentifier.equals("COMR")){				//コマーシャルフレーム
				retType = FELDE_TYPE_COMMERICAL_FREAME ;			//Commercial frame
			}else if(frameIdentifier.equals("ENCR")){				//均一化/暗号化の手法の登録	Encryption method registration
				retType = FELDE_TYPE_ENCRYPTION_METHOD_REGISTRATION ;			//Encryption method registration
			}else if(frameIdentifier.equals("GRID")){						//グループ識別子の登録	Group identification registration]
				retType = FELDE_TYPE_GRUPE_IDENTIFICTION_REGISTRATION ;			//Group identification registration
			}else if(frameIdentifier.equals("PRIV")){									//プライベートフレーム									//☆複数出現する；10cc(4)(10)PRIV
				retType = FELDE_TYPE_PRIVATE_FREAME ;			//Private frame
			}else if(frameIdentifier.equals("UFID") ||
					frameIdentifier.equals("UFI") 		//一意的なファイル識別子/タグID?								Unique file identifier									ID3ｖ3；UFID
					){													//	一意的なファイル識別子	タグID
				retType = FELDE_TYPE_ITITEKISIKIBETUSI ;			//一意的なファイル識別子
			}else if(frameIdentifier.equals("CRM")){
				retType = FELDE_TYPE_Encrypted_META_fREAM ;			//ID3v2.2
			} else {
				retType = FELDE_TYPE_FUMEI;						//不明
			}
	//		myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}
		return retType;
	}

	/**
	 * 受け取った文字列をbyte[]に戻す
	 * @param encordingSetName エンコーディングセット名
	 * @param chars エンコード対象の文字配列
	 * @return 指定された文字配列をエンコードしたバイト配列
	 * @throws CharacterCodingException エンコードに失敗した場合にスローされる
	 * @see CharsetEncoder#encode(CharBuffer)
	 * {@link http://d.hatena.ne.jp/higher_tomorrow/20100404/1270377293}
	 */

	/**
	 * 渡された文字列の先頭のnullが無くなるポイントを返す
	 * */
	public int retEndNullPoint(String readStr , int offset ) throws CharacterCodingException {					//渡された文字列の先頭のnullが無くなるポイントを返す
		int retInt = offset;
		final String TAG = "retEndNullPoint";
		String dbMsg= "";
		try{
			if(readStr != null){
				int readInt = readStr.length();
				if(60 < readInt){
					dbMsg +=readStr.substring(offset, 40) +  "～" + readStr.substring(readInt-20, readInt);
				}else{
					dbMsg +=readStr;
				}
				dbMsg += "(" + readInt + "/" + result.length() +"文字)";
				byte[] buffer = readStr.substring( 0, readInt ).getBytes();
				readInt = buffer.length;
				dbMsg +=">>" + readInt +",buffer" ;
				for(int i = offset ; i < readInt ; i++){
					if(buffer[i] != 0 ){
						dbMsg += 	",[" + i + "]=" + buffer[i];
						retInt = i - 1;
						i = readInt;
		//				buffer = null;
	//					return i;
					}
				}
				buffer = null;
			}
			dbMsg += 	",retInt=" + retInt;
	//		myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}
		return retInt;
	}

	/**
	 * 渡された文字列にnullが発生するポイントを返す
	 * */
	public int retNextNullPoint(String readStr , int offset ) throws CharacterCodingException {					//渡された文字列にnullが発生するポイントを返す
		int retInt = offset;
		final String TAG = "retNextNullPoint";
		String dbMsg= "";
		try{
			if(readStr != null){
				int readInt = readStr.length();
				if(60 < readInt){
					dbMsg +=readStr.substring(offset, offset + 40) +  "～" + readStr.substring(readInt-20, readInt);
				}else{
					dbMsg +=readStr;
				}
				dbMsg += "(" + readInt + "/" + result.length() +"文字)";
				byte[] buffer = readStr.substring( 0, readInt ).getBytes();
				readInt = buffer.length;
				dbMsg +=">>" + readInt +",buffer" ;
				for(int i = offset ; i < readInt ; i++){
					if(buffer[i] == 0 ){
						dbMsg += 	",[" + i + "]=" + buffer[i];
						retInt = i - 1;
						i = readInt;
		//				buffer = null;
	//					return i;
					}
				}
				buffer = null;
			}
			dbMsg += 	",retInt=" + retInt;
	//		myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}
		return retInt;
	}

	/**
	 * マルチバイト文字が入っていたらtrueを返す
	 * */
	public boolean isMultiByte(String readStr) throws CharacterCodingException {					//マルチバイト文字が入っていたらtrueを返す
		boolean retBool = false;
		final String TAG = "isMultiByte";
		String dbMsg= "";
		//文字コードの基礎と処理方法	http://www7a.biglobe.ne.jp/~tsuneoka/cgitech/4.html
		//ASCIIコード	0x21～0x7e /２バイト文字	１バイト目	0x81～0x9f 0xe0～0xef,２バイト目	0x40～0x7e 0x80～0xfc
		//////////
		try{
			if(readStr != null){
				int readInt = readStr.length();
				if(60 < readInt){
					dbMsg +=readStr.substring(0, 40) +  "～" + readStr.substring(readInt-20, readInt);
				}else{
					dbMsg +=readStr;
				}
				dbMsg += "(" + readInt + "/" + result.length() +"文字)";		//"M4A mp42isom".length()
				byte[] buffer = readStr.substring( 0, readInt ).getBytes();
				readInt = buffer.length;
				dbMsg += ",buffer" ;
				for(int i = 0 ; i < readInt ; i++){
					if(buffer[i] < 0){
						dbMsg += 	",[" + i + "]=" + buffer[i];
						i = readInt;
						retBool = true;
					}
				}
				buffer = null;
			}
	//		myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}
		return retBool;
	}


	public static byte[] str2byteEncord(String encordingSetName, String uketori) throws CharacterCodingException {					//char... chars
		byte[] retByte = null;
		ByteBuffer out = null;
		final String TAG = "str2byteEncord";
		String dbMsg= "";
		try{
			dbMsg= "encordingSetName=" + encordingSetName;
			dbMsg += ",uketori=" + uketori.length() + "文字";
			char[] chars = uketori.toCharArray();
			dbMsg +=">>" + chars.length + "文字";
			Charset charset = Charset.forName(encordingSetName);
			CharsetEncoder encoder = charset.newEncoder();						//char列から byte列への変換を行う
			CharBuffer in = CharBuffer.wrap(chars);
			out = encoder.encode(in);
			retByte = out.array();
			int baitoduu = retByte.length;
			if(0 < baitoduu){
				dbMsg += ">>" + retByte[0] +  "," + retByte[1] +  ","+ retByte[2] +  "," + retByte[3]+ retByte[4] +  "," + retByte[5];
			}
			dbMsg += "...(" + baitoduu +  "バイト)";
	//		myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}
		return retByte;
	}

	/**
	 * 受け取ったbyte[]を文字列に戻す
	 * @param encordingSetName エンコーディングセット名
	 * @param bytes デコード対象のバイト配列
	 * @return 指定されたバイト配列をデコードした文字配列
	 * @throws CharacterCodingException デコードに失敗した場合にスローされる
	 * @see CharsetDecoder#decode(ByteBuffer)
	 */
	public static String bytes2StrDecode(String encordingSetName, byte... bytes) throws IOException{					//char[]
		String retStr = null;
		final String TAG = "bytes2StrDecode";
		String dbMsg= "";
		try{
			dbMsg= "encordingSetName=" + encordingSetName +  ",chars=" + bytes.length + "文字";
			Charset charset = Charset.forName(encordingSetName);
			dbMsg += ",Charset=" + charset.name();
//java.nio.charset.MalformedInputException: Length: 1
			CharsetDecoder decoder = charset.newDecoder();
			ByteBuffer in = ByteBuffer.wrap(bytes);
			CharBuffer out = decoder.decode(in);
			char[] outArray =  out.array();
			retStr = String.valueOf(outArray);
			int mojisuu = retStr.length();
			if(20 < mojisuu){
				dbMsg += ",retStr=" + retStr.substring(0, 20) + "～" + retStr.substring(mojisuu - 20, mojisuu);
			}else{
				dbMsg += ",retStr=" + retStr;
			}
			dbMsg += "(" + mojisuu + "文字)";
	//		myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}
		return retStr;			//out.array();
	}

	/**
	 *再エンコードして呼出し元に返す
	 * */
	public String saiEncordBody(String songLyric , String motoEncrod , String saiEncrod) {										//再エンコードした文字を返す
		String retStr = null;
		final String TAG = "saiEncordBody";
		String dbMsg= "";
		try{
			int uketori = String.valueOf(songLyric).length();
			if(20 < uketori){
				dbMsg += songLyric.substring(0, 20) +  "～" +  songLyric.substring(uketori - 20, uketori);
			}else{
				dbMsg += songLyric +  "～";
			}
			dbMsg += uketori +  "文字)";
			dbMsg +="元エンコード=" + motoEncrod ;
			if( motoEncrod == null ){
				motoEncrod =  "ISO-8859-1";
				dbMsg +=">>" + motoEncrod ;
			}
			dbMsg +="再エンコード=" + saiEncrod ;
			if( saiEncrod == null ){
				saiEncrod =  "EUC-16";
				dbMsg +=">>" + saiEncrod ;
			}
			byte[] cByte = str2byteEncord(motoEncrod, songLyric);
			if( cByte != null){
				dbMsg +="(" + cByte.length +  "バイト)";
				retStr = bytes2StrDecode( saiEncrod, cByte);
				if(retStr != null){
					uketori = retStr.length();
					if(20 < uketori){
						dbMsg +="," +  retStr.substring(0, 20) +  "～" +  retStr.substring(uketori - 20, uketori);
					}else{
						dbMsg +=","  +  retStr +  "～";
					}
					dbMsg +="(" + uketori +  "文字)";
				}else{
					dbMsg +=",Decode失敗";
					retStr = songLyric;
				}
			}else{
				dbMsg +=",Encord失敗";
				retStr = songLyric;
			}
			myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}
		return retStr;
	}

	/**
	 *再エンコードして呼出し元のActivtyに返す
	 * */
	public void saiEncord(String songLyric , String motoEncrod , String saiEncrod) {										//再エンコードして呼出し元のActivtyに返す
		String retStr = null;
		final String TAG = "saiEncord";
		String dbMsg= "";
		try{
			dbMsg= "受取り" + motoEncrod;
			int uketori = String.valueOf(songLyric).length();
			if(20 < uketori){
				dbMsg +=";" + songLyric.substring(0, 20) +  "～" +  songLyric.substring(uketori - 20, uketori);
			}else{
				dbMsg +=";" +  songLyric +  ";";
			}
			dbMsg +="," +uketori +  "文字)";
			retStr = saiEncordBody( songLyric , motoEncrod , saiEncrod);										//再エンコードして呼出し元のActivtyに返す
		//	if(Locale.getDefault().equals( Locale.getDefault().JAPAN)){										//アプリで使用されているロケール情報を取得し、日本語の場合のみconstant for ja_JP.
//				byte[] dataBuffer = songLyric.getBytes(motoEncrod);			//songLyric.substring(0, songLyric.length()).getBytes(motoEncrod);									//データ部分を抜出			ISO-8859-1		"EUC_JP"
//				dbMsg +=",dataBuffer= "+ dataBuffer.length + "バイト";
//				songLyric = new String(dataBuffer, saiEncrod);
		//	}
//			uketori = String.valueOf(songLyric).length();
//			if(20 < uketori){
//				dbMsg +="," +  songLyric.substring(0, 20) +  "～" +  songLyric.substring(uketori - 20, uketori);
//			}else{
//				dbMsg +=","  +  songLyric +  "～";
//			}
//			dbMsg +="(" + uketori +  "文字)";
			dbMsg +="、結果=" + saiEncrod;
			uketori = String.valueOf(retStr).length();
			if(20 < uketori){
				dbMsg +=";" + retStr.substring(0, 20) +  "～" +  retStr.substring(uketori - 20, uketori);
			}else{
				dbMsg +=";" +  songLyric +  ";";
			}
			dbMsg +="," +uketori +  "文字)";
			Intent data = new Intent();			// 返すデータ(Intent&Bundle)の作成
			Bundle bundle = new Bundle();
			bundle.putString("songLyric", retStr);
			bundle.putString("lyricEncord", saiEncrod);
			bundle.putBoolean("lyricAri", true);			//歌詞を取得できた
			TagBrows.this.finish();
			myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}
	}

	/**
	 * タグから読み取った歌詞を返す
	 * */
    public String getSongLyric() {			//タグから読み取った歌詞を返す
		String retStr= null;
		final String TAG = "getSongLyric";
		String dbMsg= "";
		try{
			if( this.result_USLT != null){
				retStr = this.result_USLT;
			}else{
				retStr = this.result_Samary;
			}
			//		myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}
		return retStr;
	}

	public String makeSammry() {			//戻り値を作る
		String retStr= null;
		final String TAG = "makeSammry";
		String dbMsg= "";
		try{
			dbMsg= "result_Tag=" + result_Tag;
			if( result_Tag != null){
				retStr = result_Tag;
		//		if( this.result_Tag.startsWith("ID3")){
					if(result_TSOP != null){				//soar			//アーティスト（読み）									ARTISTSORT						ID3ｖ2；--	ID3ｖ3；TSOP
			//			retStr = retStr + "\nTSOP;";
						retStr = retStr + "" + result_TSOP + "\n(ARTISTSORT)";			//オリジナルの作詞家/文書作成者
					}
					if(result_TPE2 != null){				//バンド/オーケストラ/伴奏	バンド/オーケストラ		//10cc(2)TPE2������������10cc
						retStr = retStr + "\n" + this.getApplicationContext().getResources().getString(R.string.tag_fn_tpe2) + ")" + "" + result_TPE2;		//バンド/オーケストラ/伴奏</string>
					}

					if(result_TSOC != null){				//soco			//作曲者（読み）										SortComposer					ID3ｖ2；?-	ID3ｖ3；TSOC	COMPOSERSORT
						retStr = retStr + "\n(SortComposer)" + result_TSOC;			//オリジナルの作詞家/文書作成者
					}
					if(result_TCOM != null){				//作曲者																				//10cc(2)
						retStr = retStr + "\n" + this.getApplicationContext().getResources().getString(R.string.tag_fn_tcom) + ")" + result_TCOM;				//作曲者</string>
					}
					if(result_TYER != null){				//年	レコーディング年/年*15	Date*16	 Deprecated									//10cc(5)TYER������������1975
						retStr = retStr + "\n" + this.getApplicationContext().getResources().getString(R.string.tag_fn_tyer) + ")" + result_TYER;					//年/レコーディング年</string><!-- 年*15	Date*16	 Deprecated -->
					}

					if(result_TSOA != null){				//©nam			//アルバム（読み）										ALBUMSORT						ID3ｖ2；--	ID3ｖ3；TSOA
						retStr = retStr  + "\n(ALBUMSORT)" + result_TSOA;			//オリジナルの作詞家/文書作成者
					}
					if(result_TALB != null){				//アルバム/映画/ショーのタイトル														//10cc(6)
						retStr = retStr + "\n" + this.getApplicationContext().getResources().getString(R.string.tag_fn_talb) + ")" + result_TALB;					//アルバム/映画/ショーのタイトル </string>
					}
					if(result_TPOS != null){				//セット中の位置	ディスク #	Disc Number	Disc#										//10cc(7')TPOS������������1/1
						retStr = retStr + "\n" + this.getApplicationContext().getResources().getString(R.string.tag_fn_tpos) + ")" + result_TPOS;		//ディスク番号/セット中の位置
					}
					if(result_TRCK != null){				//トラックの番号/セット中の位置	トラック #	Track Number	Track#
						retStr = retStr + "\n" + this.getApplicationContext().getResources().getString(R.string.tag_fn_trak) + ")" + result_TRCK;				//トラックの番号/セット中の位置</string><!-- Track Number	Track# -->
					}
					if(result_TIT2 != null){				//タイトル/曲名/内容の説明	タイトル	Track Title	Title
						retStr = retStr + "\n" + this.getApplicationContext().getResources().getString(R.string.tag_fn_tit2) + ")" + result_TIT2;				//タイトル/曲名/内容の説明</string>
					}
					if(result_TIT3 != null){				//Subtitle/Description refinement
						retStr = retStr + "\n" + this.getApplicationContext().getResources().getString(R.string.tag_fn_tit3) + ")" + result_TIT3;				//サブタイトル/説明の追加情報
					}
					if(result_TDAT != null){				//日付	Date*11	Date*12	Year*13 Deprecated
						retStr = retStr + "\n" + this.getApplicationContext().getResources().getString(R.string.tag_fn_tdat) + ")" + result_TDAT;				//日付
					}
					if(result_TRDA != null){				// Recording dates	Deprecated
						retStr = retStr +"\n" + this.getApplicationContext().getResources().getString(R.string.tag_fn_trda) + ")" + result_TRDA;				//録音日付
					}
					if(result_TCON != null){				//ジャンル																				//10cc(8)TCON������������
						retStr = retStr + "\n" + this.getApplicationContext().getResources().getString(R.string.tag_fn_tcon) + ")" + result_TCON;			//ジャンル
					}
					if(result_TCOP != null){				//著作権情報
						retStr = retStr + "\n" + this.getApplicationContext().getResources().getString(R.string.tag_fn_tcop) + ")" + result_TCOP ;				//著作権情報
					}
					if(result_TENC != null){				//エンコーディング ソフトウェア	<ENCODED BY>	Encoder
						retStr = retStr + "\n" + this.getApplicationContext().getResources().getString(R.string.tag_fn_tenc) + ")" + result_TENC;			//エンコーディング ソフトウェア
					}
					if(result_TEXT != null){				//作詞家/文書作成者	作詞者	<LYRICIST>	Lyricist
						retStr = retStr + "\n" + this.getApplicationContext().getResources().getString(R.string.tag_fn_text) + ")" + result_TEXT;		//作詞家/文書作成者
					}
					if(result_TPE1 != null){				//主な演奏者/ソリスト	トラック アーティスト	Artist Name	Artist
						retStr = retStr + "\n" + this.getApplicationContext().getResources().getString(R.string.tag_fn_tpe1) + ")" + result_TPE1;	//主な演奏者/ソリスト	トラック アーティスト
					}
					if(result_TPE3 != null){				//指揮者/演奏者詳細情報	指揮者	<CONDUCTOR>	Conductor
						retStr = retStr + "\n" + this.getApplicationContext().getResources().getString(R.string.tag_fn_tpe3) + ")" + result_TPE3;	//指揮者/演奏者詳細情報
					}
					if(result_TMOO != null){				//ムード	ムード	<MOOD>	Mood	ID3v2.4フレーム
						retStr = retStr + "\n"+ this.getApplicationContext().getResources().getString(R.string.tag_fn_tmoo) + ")" + result_TMOO;		//ムード
					}
					if(result_TPE4 != null){				//翻訳者, リミックス, その他の修正	<ModifiedBy>	<REMIXED BY>	Mix Artist, Artists: Remixer
						retStr = retStr  + "\n" + this.getApplicationContext().getResources().getString(R.string.tag_fn_tpe4) + ")" + result_TPE4;	//翻訳者, リミックス, その他の修正
					}
					if(result_TPUB != null){				//出版社	発行元	<PUBLISHER>	Publisher												//10cc(11)TPUB������������Universal Distribu
						retStr = retStr + "\n" + this.getApplicationContext().getResources().getString(R.string.tag_fn_tpub) + ")" + result_TPUB;			//出版社/発行元
					}
					if(result_POPM != null){				//人気メーター																				//10cc(2')Windows Media Player 9 Seri
						retStr = retStr + "\n" + this.getApplicationContext().getResources().getString(R.string.tag_fn_pppm) + ")" + result_POPM;			//人気メーター
					}
					if(result_TXXX != null){				//ユーザー定義文字情報フレーム
						retStr = retStr + "\n" + this.getApplicationContext().getResources().getString(R.string.tag_fn_txxx) + ")" + result_TXXX;		//ユーザー定義文字情報フレーム
					}
					if(result_UFID != null){				//一意的なファイル識別子	タグID
						retStr = retStr + "\n" + this.getApplicationContext().getResources().getString(R.string.tag_fn_ufid) + ")" + result_UFID;		//一意的なファイル識別子/タグID
					}
					if(result_COMR != null){				//Commercial frame]
						retStr = retStr + "\n" + this.getApplicationContext().getResources().getString(R.string.tag_fn_oomr) + ")" + result_COMR;		//コマーシャルフレーム
					}
					if(result_AENC != null){				//Audio encryption
						retStr = retStr + "" + "\n" + this.getApplicationContext().getResources().getString(R.string.tag_fn_aenc) + ")" + result_AENC;		//オーディオの暗号化
					}
					if(result_ENCR != null){				//Encryption method registration
						retStr = retStr + "\n" + this.getApplicationContext().getResources().getString(R.string.tag_fn_encr) + ")" + result_ENCR;		//均一化/暗号化の手法の登録
					}
					if(result_EQUA != null){				//Equalization
						retStr = retStr + "\n" + this.getApplicationContext().getResources().getString(R.string.tag_fn_equa) + ")" + result_EQUA;		//音質調整
					}
					if(result_ETCO != null){				//Event timing codes]
						retStr = retStr + "\n" + this.getApplicationContext().getResources().getString(R.string.tag_fn_etco) + ")" + result_ETCO;		//イベントタイムコード
					}
					if(result_GEOB != null){				//General encapsulated object]
						retStr = retStr + "\n" + this.getApplicationContext().getResources().getString(R.string.tag_fn_geob) + ")" + result_GEOB;		//パッケージ化された一般的なオブジェクト
					}
					if(result_GRID != null){				//Group identification registration]
						retStr = retStr + "\n"+ this.getApplicationContext().getResources().getString(R.string.tag_fn_grid) + ")" + result_GRID;	//グループ識別子の登録
					}
					if(result_IPLS != null){				//Involved people list]
						retStr = retStr + "\n" + this.getApplicationContext().getResources().getString(R.string.tag_fn_ipls) + ")" + result_IPLS;		//協力者一覧
					}
					if(result_LINK != null){				//Linked information]
						retStr = retStr + "\n" + this.getApplicationContext().getResources().getString(R.string.tag_fn_link) + ")" + result_LINK;			//リンク情報
					}
					if(result_MCDI != null){				//Music CD identifier
						retStr = retStr + "\n" + this.getApplicationContext().getResources().getString(R.string.tag_fn_mcdi) + ")" + result_MCDI;			//音楽ＣＤ識別子
					}
					if(result_TSRC != null){				//ISRC (international standard recording code)
						retStr = retStr + "\n" + this.getApplicationContext().getResources().getString(R.string.tag_fn_tsrc) + ")" + result_TSRC;			//国際標準レコーディングコード
					}
					if(result_MLLT != null){				//MPEG location lookup table]
						retStr = retStr + "\n" + this.getApplicationContext().getResources().getString(R.string.tag_fn_mllt) + ")" + result_MLLT;			//MPEGロケーションルックアップテーブル
					}
					if(result_OWNE != null){				//Ownership frame]
						retStr = retStr + "\n" + this.getApplicationContext().getResources().getString(R.string.tag_fn_owne) + ")OWNE;" + result_OWNE;		//所有権フレーム
					}
					if(result_PCNT != null){				//Play counter]
						retStr = retStr + "\n" + this.getApplicationContext().getResources().getString(R.string.tag_fn_pcnt) + ")" + result_PCNT;			//演奏回数
					}
					if(result_POSS != null){				//Position synchronisation frame]
						retStr = retStr + "\n"+ this.getApplicationContext().getResources().getString(R.string.tag_fn_poss) + ")POSS;" + result_POSS;	//同期位置フレーム
					}
					if(result_RBUF != null){				//Recommended buffer size]
						retStr = retStr+ "\n" + this.getApplicationContext().getResources().getString(R.string.tag_fn_rbuf) + ")" + result_RBUF;			//推奨バッファサイズ
					}
					if(result_RVAD != null){				//Relative volume adjustment]
						retStr = retStr + "\n" + this.getApplicationContext().getResources().getString(R.string.tag_fn_rvad) + ")" + result_RVAD;			//相対的ボリューム調整
					}
					if(result_SYTC != null){				//Synchronized tempo codes
						retStr = retStr + "\n" + this.getApplicationContext().getResources().getString(R.string.tag_fn_sytc) + ")" + result_SYTC;			//同期 テンポコード
					}
					if(result_TBPM != null){				//BPM (beats per minute)
						retStr = retStr+ "\n" + this.getApplicationContext().getResources().getString(R.string.tag_fn_tbpm) + ")" + result_TBPM;			//一分間の拍数
					}
					if(result_TDLY != null){				//Playlist delay
						retStr = retStr + "\n" + this.getApplicationContext().getResources().getString(R.string.tag_fn_tdly) + ")" + result_TDLY;					//プレイリスト遅延時間
					}
					if(result_TFLT != null){				//File type
						retStr = retStr + "\n" + this.getApplicationContext().getResources().getString(R.string.tag_fn_tflt) + ")" + result_TFLT;				//ファイルタイプ
					}
					if(result_TIME != null){				//Time Deprecated
						retStr = retStr + "\n" + this.getApplicationContext().getResources().getString(R.string.tag_fn_time) + ")" + result_TIME;		//時間
					}
					if(result_TIT1 != null){				//Time Deprecated
						retStr = retStr + "\n" + this.getApplicationContext().getResources().getString(R.string.tag_fn_tit1) + ")" + result_TIT1;		//内容の属するグループの説明
					}
					if(result_TKEY != null){				//Initial key
						retStr = retStr + "\n" + this.getApplicationContext().getResources().getString(R.string.tag_fn_tkey) + ")" + result_TKEY;		//初めの調
					}
					if(result_TLAN != null){				//Language(s)]
						retStr = retStr + "\n" + this.getApplicationContext().getResources().getString(R.string.tag_fn_tlan) + ")TLAN;" + result_TLAN;		//言語
					}
					if(result_TLEN != null){				// Length]
						retStr = retStr + "\n" + this.getApplicationContext().getResources().getString(R.string.tag_fn_tlen) + ")" + result_TLEN;		//長さ
					}
					if(result_TSIZ != null){				//Size	 Deprecated
						retStr = retStr + "\n" + this.getApplicationContext().getResources().getString(R.string.tag_fn_tsiz) + ")" + result_TSIZ;		//サイズ
					}
					if(result_TMED != null){				//Media type]
						retStr = retStr + "\n" + this.getApplicationContext().getResources().getString(R.string.tag_fn_tmed) + ")" + result_TMED;		//メディアタイプ
					}
					if(result_TOAL != null){				//Original album/movie/show title
						retStr = retStr + "\n" + this.getApplicationContext().getResources().getString(R.string.tag_fn_toal) + ")" + result_TOAL;		//オリジナルのアルバム/映画/ショーのタイトル
					}
					if(result_TOFN != null){				//Original filename]
						retStr = retStr + "\n" + this.getApplicationContext().getResources().getString(R.string.tag_fn_tofn) + ")" + result_TOFN;		//オリジナルファイル名
					}
					if(result_TOPE != null){				//Original artist(s)/performer(s)	 Deprecated
						retStr = retStr + "\n" + this.getApplicationContext().getResources().getString(R.string.tag_fn_tope) + ")" + result_TOPE;		//オリジナルアーティスト/演奏者
					}
					if(result_TORY != null){				//Original release year]
						retStr = retStr + "\n" + this.getApplicationContext().getResources().getString(R.string.tag_fn_tory) + ")" + result_TORY;		//オリジナルのリリース年
					}
					if(result_TRSN != null){				//Internet radio station name
						retStr = retStr + "\n("+ this.getApplicationContext().getResources().getString(R.string.tag_fn_trsn) + ")TRSN;"  + result_TRSN;	//インターネットラジオ局の名前
					}
					if(result_TRSO != null){				//Internet radio station owner
						retStr = retStr + "\n("+ this.getApplicationContext().getResources().getString(R.string.tag_fn_trso) + ")TRSO;"  + result_TRSO;	//インターネットラジオ局の所有者
					}
					if(result_TSSE != null){				//Software/Hardware and settings used for encoding
						retStr = retStr + "\n" + this.getApplicationContext().getResources().getString(R.string.tag_fn_tsse) + ")" + result_TSSE;		//エンコードに使用したソフトウエア/ハードウエアとセッティング
					}
					if(result_RVRB != null){				//Reverb
						retStr = retStr + "\n" + this.getApplicationContext().getResources().getString(R.string.tag_fn_rvrb) + ")" + result_RVRB;		//リバーブ
					}
					if(result_TOWN != null){				//ファイルの所有者/ライセンシー
						retStr = retStr + "\n"+ this.getApplicationContext().getResources().getString(R.string.tag_fn_town) + ")TOWN;"  + result_TOWN;	//ファイルの所有者/ライセンシー
					}
					if(result_WCOM != null){				//Commercial information]
						retStr = retStr + "\n" + this.getApplicationContext().getResources().getString(R.string.tag_fn_wcom) + ")" + result_WCOM;		//商業上の情報
					}
					if(result_WCOP != null){				//Copyright/Legal information
						retStr = retStr + "\n" + this.getApplicationContext().getResources().getString(R.string.tag_fn_wcop) + ")" + result_WCOP;			//著作権/法的情報
					}
					if(result_WOAF != null){				//Official audio file webpage
						retStr = retStr + "\n" + this.getApplicationContext().getResources().getString(R.string.tag_fn_woaf) + ")"+ result_WOAF;			//オーディオファイルの公式Webページ
					}
					if(result_WOAR != null){				//Official artist/performer webpage
						retStr = retStr + "\n" + this.getApplicationContext().getResources().getString(R.string.tag_fn_woar) + ")" + result_WOAR;			//アーティスト/演奏者の公式Webページ
					}
					if(result_WOAS != null){				//Official audio source webpage
						retStr = retStr + "\n" + this.getApplicationContext().getResources().getString(R.string.tag_fn_woas) + ")" + result_WOAS;			//音源の公式Webページ
					}
					if(result_WORS != null){				//Official internet radio station homepage
						retStr = retStr + "\n"+ this.getApplicationContext().getResources().getString(R.string.tag_fn_wors) + ")WORS;" + result_WORS;	//インターネットラジオ局の公式ホームページ
					}
					if(result_WPAY != null){				//Payment
						retStr = retStr + "\n"+ this.getApplicationContext().getResources().getString(R.string.tag_fn_wpay) + ")WPAY;"  + result_WPAY;		//支払い
					}
					if(result_WPUB != null){				//Publishers official webpage
						retStr = retStr + "\n" + this.getApplicationContext().getResources().getString(R.string.tag_fn_wpub) + ")" + result_WPUB;			//出版社の公式Webページ
					}
					if(result_WCAF != null){				//ファイルの所有者/ライセンシー
						retStr = retStr + "\n"+ this.getApplicationContext().getResources().getString(R.string.tag_fn_wcaf) + ")WCAF;"  + result_WCAF;	//オーディオファイルの公式Webページ
					}
					if(result_USER != null){				//Terms of use
						retStr = retStr + "\n"+ this.getApplicationContext().getResources().getString(R.string.tag_fn_user) + ")USER;"  + result_USER;	//使用条件
					}
					if(result_WXXX != null){				//User defined URL link frame
						retStr = retStr + "\n" + this.getApplicationContext().getResources().getString(R.string.tag_fn_wxxx) + ")"  + result_WXXX;			//ユーザー定義URLリンクフレーム
					}
					if(result_TOLY != null){				//Original lyricist(s)/text writer(s)]
						retStr = retStr + "\n" + this.getApplicationContext().getResources().getString(R.string.tag_fn_toly) + ")" + result_TOLY;			//オリジナルの作詞家/文書作成者
					}
					if(result_des != null){		//©des			//説明				Description	Track				ID3ｖ2；--	ID3ｖ3；--		SUBTITLE
						retStr = retStr + "\nDescription	Track)" + result_des;
					}
					if(result_iTunesInfo != null){		//----			//														iTunesInfo						ID3ｖ2；--	ID3ｖ3；--
						retStr = retStr + "\niTunesInfo)" + result_iTunesInfo;
					}
					if(result_nrt != null){		//©nrt			//														Narrator						ID3ｖ2；--	ID3ｖ3；--
						retStr = retStr + "\nNarrator)" + result_nrt;
					}
					if(result_PST != null){		//@PST			//														Parent Short Title				ID3ｖ2；--	ID3ｖ3；--
						retStr = retStr + "\nParent Short Title)" + result_PST;
					}
					if(result_ppi != null){			//@ppi			//														Parent ProductID				ID3ｖ2；--	ID3ｖ3；--
						retStr = retStr + "\nParent ProductID)" + result_ppi;
					}
					if(result_sti != null){		//@sti			//														Short Title						ID3ｖ2；--	ID3ｖ3；--
						retStr = retStr + "\nShort Title)" + result_sti;
					}
					if(result_AACR != null){		//AACR");	//														Unknown_AACR?					ID3ｖ2；--	ID3ｖ3；--
						retStr = retStr + "\nUnknown_AACR?)" + result_sti;
					}
					if(result_CDEK != null){		//CDEK");	//														Unknown_CDEK?					ID3ｖ2；--	ID3ｖ3；--
						retStr = retStr + "\nUnknown_CDEK?)" + result_CDEK;
					}
					if(result_CDET != null){		//CDET");	//														Unknown_CDET?					ID3ｖ2；--	ID3ｖ3；--
						retStr = retStr + "\nUnknown_CDET?)" + result_CDET;
					}
					if(result_GUID != null){		//GUID");	//														GUID							ID3ｖ2；--	ID3ｖ3；--
						retStr = retStr + "\nGUID)" + result_GUID;
					}
					if(result_VERS != null){		//VERS");	//														ProductVersion					ID3ｖ2；--	ID3ｖ3；--
						retStr = retStr + "\nProductVersion)" + result_VERS;
					}
					if(result_akID != null){		//akID");	//アカウントの種類			編集不可					Apple Store Account Type		ID3ｖ2；--	ID3ｖ3；--
						retStr = retStr + "\nApple Store Account Type)" + result_akID;
					}
					if(result_apID != null){		//apID");	//アカウント情報					ITUNESACCOUNT		Apple Store Account				ID3ｖ2；--	ID3ｖ3；--
						retStr = retStr + "\nApple Store Account)" + "" + result_apID;
					}
					if(result_auth != null){		//auth");	//														Author							ID3ｖ2；--	ID3ｖ3；--
						retStr = retStr + "\nAuthor)" + result_auth;
					}
					if(result_catg != null){		//catg");	//ポッドキャストカテゴリ								Category						ID3ｖ2；--	ID3ｖ3；--		PODCASTCATEGORY
						retStr = retStr + "\nCategory)" + result_catg;
					}
					if(result_cnID != null){		///cnID");	//コンテンツ識別子										AppleStoreCatalogID				ID3ｖ2；--	ID3ｖ3；--		ITUNESCATALOGID
						retStr = retStr + "\nAppleStoreCatalogID)" + result_cnID;
					}
					if(result_cpil != null){		//cpil");	//コンピレーションの明示								Compilation						ID3ｖ2；--	ID3ｖ3；--		COMPILATION
						retStr = retStr + "\nCompilation)" + result_cpil;
					}
					if(result_egid != null){		//egid");	//ポッドキャストエピソードユニークID					Episode Global Unique ID		ID3ｖ2；--	ID3ｖ3；--		PODCASTID
						retStr = retStr + "\nEpisode Global Unique ID)" + result_egid;
					}
					if(result_geID != null){		//geID");	//ジャンル識別子		編集不可						GenreID							ID3ｖ2；--	ID3ｖ3；--
						retStr = retStr + "\nGenreID)" + "" + result_geID;
					}
					if(result_grup != null){			//grup");	//														Grouping						ID3ｖ2；--	ID3ｖ3；--
						retStr = retStr + "\nGrouping)" + "" + result_grup;
					}
					if(result_gshh != null){			//gshh");	//														GoogleHostHeade					ID3ｖ2；--	ID3ｖ3；--
						retStr = retStr + "\nGoogleHostHeade)" + result_gshh;
					}
					if(result_gspm != null){		//gspm");	//														GooglePingMessage				ID3ｖ2；--	ID3ｖ3；--
						retStr = retStr + "\nGooglePingMessage)" + result_gspm;
					}
					if(result_gspu != null){		//gspu");	//														GooglePingURL					ID3ｖ2；--	ID3ｖ3；--
						retStr = retStr + "\nGooglePingURL)" + "" + result_gspu;
					}
					if(result_gssd != null){		//gssd");	//														GoogleSourceData				ID3ｖ2；--	ID3ｖ3；--
						retStr = retStr + "\nGoogleSourceData)" + "" + result_gssd;
					}
					if(result_gsst != null){		//gsst");	//														GoogleStartTime					ID3ｖ2；--	ID3ｖ3；--
						retStr = retStr  + "\nGoogleStartTime)" + result_gssd;
					}
					if(result_gstd != null){		//gstd");	//														GoogleTrackDuration				ID3ｖ2；--	ID3ｖ3；--
						retStr = retStr + "\nGoogleTrackDuration)" + result_gstd;
					}
					if(result_hdvd != null){			//hdvd");	//	?													HDVideo	?						ID3ｖ2；--	ID3ｖ3；--
						retStr = retStr + "\nHDVideo)" + result_hdvd;
					}
					if(result_hdvd != null){				//hdtv");	//ビデオ解像度の明示		ITUNESHDVIDEO				HDVideo							ID3ｖ2；--	ID3ｖ3；--
						retStr = retStr + "\nHDVideo)" + result_hdvd;
					}
					if(result_itnu != null){				//itnu");	//														iTunesU							ID3ｖ2；--	ID3ｖ3；--
						retStr = retStr + "\niTunesU)" + "" + result_itnu;
					}
					if(result_keyw != null){				//keyw");	//ポッドキャストキーワード		編集不可				Keyword							ID3ｖ2；--	ID3ｖ3；--
						retStr = retStr + "\nKeyword)" + result_keyw;
					}
					if(result_pcst != null){				//pcst");	//ポッドキャストであることを明示						Podcast							ID3ｖ2；--	ID3ｖ3；--
						retStr = retStr + "\nPodcast)" + result_pcst;
					}
					if(result_pgap != null){				//pgap");	//ギャップレスコンテンツの明示							PlayGap							ID3ｖ2；--	ID3ｖ3；--		ITUNESGAPLESS
						retStr = retStr + "\nPlayGap)" + result_pgap;
					}
					if(result_plID != null){				//plID");	//プレイリスト（アルバム）識別子	編集不可			PlayListID						ID3ｖ2；--	ID3ｖ3；--
						retStr = retStr + "\nPlayListID)" + "" + result_plID;
					}
					if(result_prID != null){				//prID");	//														ProductID						ID3ｖ2；--	ID3ｖ3；--
						retStr = retStr + "\nProductID)" + result_prID;
					}
					if(result_purd != null){				//purd");	//購入日												ITUNESPURCHASEDATE				ID3ｖ2；--	ID3ｖ3；--		ITUNESPURCHASEDATE
						retStr = retStr + "\nITUNESPURCHASEDATE)" + "" + result_purd;
					}
					if(result_purl != null){				//purl");	//ポッドキャストURL										Podcast URL						ID3ｖ2；--	ID3ｖ3；--		PODCASTURL
						retStr = retStr + "\nPodcast URL)" + result_purl;
					}
					if(result_rtng != null){				//rtng");	//保護者のためのレートの明示?番組?番組（読み）			Rating?	TVSHOW?					ID3ｖ2；--	ID3ｖ3；--		ITUNESADVISORY?	TVSHOW?
						retStr = retStr + "\nRating/TVSHOW	)" + result_rtng;
					}
					if(result_sfID != null){					//sfID");	//ストアの国				編集不可					AppleStore Country				ID3ｖ2；--	ID3ｖ3；--
						retStr = retStr + "\nAppleStore Country)" + result_sfID;
					}
					if(result_sosn != null){				//sosn");	//														Sort Show						ID3ｖ2；--	ID3ｖ3；--
						retStr = retStr + "\nSort Show)" + "" + result_sosn;
					}
					if(result_tven != null){			//tven");	//エピソードID											TVEpisodeID						ID3ｖ2；--	ID3ｖ3；--
						retStr = retStr + "\nTVEpisodeID)" + "" + result_tven;
					}
					if(result_tves != null){			//tves");	//														TVEpisode						ID3ｖ2；--	ID3ｖ3；--
						retStr = retStr + "\nTVEpisode)" + "" + result_tves;
					}
					if(result_tvnn != null){			//tvnn");	//放送局												TVNetworkName					ID3ｖ2；--	ID3ｖ3；--
						retStr = retStr + "\nTVNetworkName)" + "" + result_tvnn;
					}
					if(result_tvsh != null){			//tvsh");	//														TVShow							ID3ｖ2；--	ID3ｖ3；--
						retStr = retStr + "\nTVShow)" + "" + result_tvsh;
					}
					if(result_tvsn != null){			//tvsn");	//シーズン												TVSeason						ID3ｖ2；--	ID3ｖ3；--
						retStr = retStr + "\nTVSeason)" + "" + result_tvsn;
					}

					if(result_APIC != null){				//付属する画像																			//10cc(5')APIC��(������JPG������ÿØÿà��
						retStr = retStr + "\n" + this.getApplicationContext().getResources().getString(R.string.tag_fn_apic) + ")" + result_APIC;			//
						dbMsg +=",付属する画像=" + result_APIC;
					}
					if(result_COMM != null){				//コメント																				//10cc(7')
						retStr = retStr + "\n" + this.getApplicationContext().getResources().getString(R.string.tag_fn_comm) + ")"
						+ result_COMM  + this.getApplicationContext().getResources().getString(R.string.comon_ken) ;                                            //件
					}
					if(result_PRIV != null){				//プライベートフレーム									//☆複数出現する；10cc(4)(10)PRIV
						retStr = retStr + "\n" + this.getApplicationContext().getResources().getString(R.string.tag_fn_priv) + ")" +
					result_COMM  + this.getApplicationContext().getResources().getString(R.string.comon_ken) ;                                            //件
					}
					if(result_CRM != null){				//ID3v2.2
						retStr = retStr + "\n" + this.getApplicationContext().getResources().getString(R.string.tag_fn_priv) + ")  " +
								result_CRM + this.getApplicationContext().getResources().getString(R.string.comon_ken);				//件
					}
		//		}
			}else{
				retStr = this.getApplicationContext().getResources().getString(R.string.lyric_nasi);				//me="">この曲は歌詞が設定されていないか、読み込めませんでした。</string>
			}
			dbMsg +=",retStr=" + retStr;
			myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}
		return retStr;
	}

	/**
	 * 呼び出しの戻り処理
	 * */
	public void back2Activty( ){			//呼び出しの戻り処理
		final String TAG = "back2Player";
		String dbMsg= "";
		try{
			String lylicHTM = null;				//html変換した歌詞のフルパス名
			Intent data = new Intent();			// 返すデータ(Intent&Bundle)の作成
			Bundle bundle = new Bundle();
			String retStr;
			result_Samary = makeSammry();			//戻り値を作る
			if( this.result_USLT != null){
				int readInt = this.result_USLT.length();
				if(60 < readInt){
					dbMsg +=this.result_USLT.substring(0, 40) +  "～" + this.result_USLT.substring(readInt-20, readInt);
				}else{
					dbMsg +=this.result_USLT;
				}
				dbMsg += "(" + readInt + "/" + this.result_USLT.length() +"文字)";
				dbMsg += ",unix;n" +  this.result_USLT.indexOf("\n") +"文字目)";
				dbMsg += ",mac;r" +  this.result_USLT.indexOf("\r") +"文字目)";
				dbMsg += ",win;rn" +  this.result_USLT.indexOf("\r\n") +"文字目)";
				if(this.result_USLT.indexOf("\n") < 0){									//Mac改行コードの場合は
					this.result_USLT = this.result_USLT.replaceAll("\r", "\n");			//unixに統一
				}
				retStr = this.result_USLT + "\n\n" + filePath + "\n\n" + result_Samary ;
				lyricAri = true;			//歌詞を取得できた
				lylicHTM = lyric2webSouce( result_USLT );					//歌詞をhtmlに書き出す
			}else if( this.result_SYLT != null){			//同期 歌詞/文書
					retStr = this.result_SYLT + "\n\n" + filePath + "\n\n" + result_Samary ;
					lyricAri = true;			//歌詞を取得できた
					lylicHTM = lyric2webSouce( result_SYLT );					//歌詞をhtmlに書き出す
			}else{
				lyricAri = false;
				retStr = filePath + "\n\n" + result_Samary ;
				lylicHTM = retStr;				//html変換した歌詞のフルパス名
				lylicHTM = lyric2webSouce( retStr );					//歌詞をhtmlに書き出す
			}
			dbMsg += ",retStr="+retStr.substring(0, 20) + "～" + retStr.substring(retStr.length()-20) ;
			dbMsg += ";="+ retStr.length() +"文字" ;
//03-05 00:17:36.045: E/JavaBinder(9831): !!! FAILED BINDER TRANSACTION !!!	メモリが累積してIPCの許容を超える
			if(10000 < retStr.length()){
				retStr.substring(0, 1000);
			}
			dbMsg += ",backCode=" + backCode;
			bundle.putInt("reqCode", backCode);	//192
			bundle.putString("songLyric", retStr);
			bundle.putBoolean("lyricAri", lyricAri);			//歌詞を取得できた
			bundle.putString("lyricEncord", saiEncrod);
			bundle.putString("lylicHTM", lylicHTM);			//html変換した歌詞のフルパス名
			myLog(TAG,dbMsg);
			data.putExtras(bundle);
			setResult(RESULT_OK, data);		// setResult() で bundle を載せた送るIntent dataをセットする		// 第一引数は…Activity.RESULT_OK, Activity.RESULT_CANCELED など
			TagBrows.this.finish();
		} catch (Exception e) {		//汎用
			myErrorLog(TAG,dbMsg+"で"+e.toString());
		}
	}

	/**
	 * 歌詞をhtmlに書き出す
	 * @param  songLyric 変換する歌詞
	 * */
	private String lyric2webSouce( String songLyric ) {					//歌詞をhtmlに書き出す
		String fName = null;
		//http://www.nilab.info/z3/20120806_02.html
		final String TAG = "lyric2webSouce";
		String dbMsg= "";
		try{
			dbMsg= "songLyric=";
			int uketori = String.valueOf(songLyric).length();
			if(30 < uketori){
				dbMsg= dbMsg+ songLyric.substring(0, 30) +"～" + songLyric.substring(songLyric.length()-30);
			}else{
				dbMsg += songLyric +  "～";
			}
			dbMsg += uketori +  "文字)";
			String eucjpStr;
			fName = "lyric.htm";
			String hozonnsaki = "/data/data/" + this.getPackageName() + "/files/" + fName;
			String lyricStr = null;
			eucjpStr = songLyric.replace("\n", "<br>");			//☆<PRE>では拡大すると横スクロールが発生するので改行を置換え
			dbMsg= dbMsg+",eucjpStr=" + eucjpStr.substring(0, 30) +"～" + eucjpStr.substring(eucjpStr.length()-30);
//			dbMsg= dbMsg+",EUC_JP=" + checkCharacterCode(songLyric, "EUC_JP") ;					//true
//			dbMsg= dbMsg+",Shift_JIS=" + checkCharacterCode(songLyric, "Shift_JIS") ;			//true
//			dbMsg= dbMsg+",UTF-8=" + checkCharacterCode(songLyric, "UTF-8") ;					//true
//			dbMsg= dbMsg+",UTF-16=" + checkCharacterCode(songLyric, "UTF-16") ;					//true
//			dbMsg= dbMsg+",8859_1=" + checkCharacterCode(songLyric, "iso-8859-1") ;					//false
			dbMsg= dbMsg+",filePath=" + filePath ;
			String[] titolStrs = filePath.split(String.valueOf(File.separatorChar));
			dbMsg= dbMsg+">>" + titolStrs.length + "分割" ;
			String artittStr = titolStrs[(titolStrs.length - 3)] ;
			dbMsg= dbMsg+",artit=" + artittStr ;
			String albumStr = titolStrs[(titolStrs.length - 2)] ;
			dbMsg= dbMsg+",album=" + albumStr ;
			String titolStr = titolStrs[(titolStrs.length - 1)];
			dbMsg= dbMsg+",titolStr=" + titolStr ;
			titolStrs = titolStr.split(".");
			int bunkatu = titolStrs.length;
			dbMsg= dbMsg+">>" + bunkatu + "分割" ;
			if(bunkatu == 0){												//拡張子を分割できなければ
				titolStr = titolStr.substring(0, titolStr.length()-4);		//末尾４文字カット
			}else{
				titolStr = titolStrs[0];
			}
			dbMsg= dbMsg+",titolStr=" + titolStr ;
			eucjpStr = titolStr + "<br><br>" +eucjpStr;
			if(albumStr != null){
				titolStr = albumStr + "/" + titolStr;
				eucjpStr = albumStr + "<br>" +eucjpStr;
			}
			if(artittStr != null){
				titolStr = artittStr + "/" + titolStr;
				eucjpStr = artittStr + "<br>" +eucjpStr;
			}


			lyricStr = "<HTML><HEAD><meta http-equiv=\"content-type\" content=\"text/html;charset=UTF-8\"><TITLE>" + titolStr + "</TITLE></HEAD><BODY>" + eucjpStr + "</BODY></HTML>";			// " + metaStr + "
			FileOutputStream out = openFileOutput( fName, MODE_PRIVATE );
			out.write( lyricStr.getBytes() );
			File fObj = new File(hozonnsaki);
			fName = fObj.getPath();
			dbMsg= dbMsg+",fName=" + fName ;/////////////////////////////////////
//読込
//			File wFile = new File(fName);
//			dbMsg= dbMsg+",wFile=" + wFile.getPath() ;/////////////////////////////////////

//			FileInputStream in = openFileInput( fName );
//			BufferedReader reader = new BufferedReader( new InputStreamReader( in , "UTF-8") );
//			String str = "";
//			String tmp;
//			while( (tmp = reader.readLine()) != null ){
//				str = str + tmp + "\n";
//			}
//			reader.close();
//			dbMsg= dbMsg+",str=" + str ;/////////////////////////////////////
			myLog(TAG,dbMsg);
		}catch( IOException e ){
			e.printStackTrace();
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}
		return fName;
	}

//////////////////////////////////////////////////////////////////////////////////////////////////////////
//	public plogTask pTask;
	public int reqCode=0;								//何の処理か
	public int plogInt=0;								//プログレス値
	public int pdMaxVal=0;								//プログレス終端値
	public String pdTitol = null;
	public String pdMessage = null;
	public String result = null;

	/**plogTask.doInBackground相当*/
	public String Readloop(int reqCode,List<String> kensaku,String result) {
		final String TAG = "Readloop";
		String dbMsg="";
	//	String result = null;
		try {
			dbMsg = ":reqCode="+reqCode;///////////////////////////
			int pdMaxVal = kensaku.size();
			dbMsg += ",pdMaxVal="+pdMaxVal;///////////////////////////
			dbMsg +=", kensaku = " + pdMaxVal + "項目" ;
			for(int i = 0; i < pdMaxVal ; i++){
				dbMsg= reqCode + "\n" + i + "/ " + pdMaxVal +")" ;
				String freamName = kensaku.get(i);
				dbMsg +=freamName + ";";
				int sInt = 0;
				if(result != null){
					sInt = result.length();
					dbMsg +="残り" + sInt + "文字";
				}
				dbMsg +="残り" + sInt + "文字";
				if(freamName.equals("USLT") || freamName.equals("USLT")){
					pdMessage =getApplicationContext().getString(R.string.tag_prog_msg1) + " ; " + freamName;		//歌詞を探しています。
				} else {
					pdMessage =getApplicationContext().getString(R.string.tag_prog_msg2) + " ; " + freamName;		//その他の書き込みを検索しています。
				}
				result = getTargetFream( result , freamName , reqCode);			//<UNSYNCED LYRICS>	非同期 歌詞/文書のコピー	渡された文字列から指定されたフレームを切り出す
//					pdCoundtVal = i + 1 ;
//					//		pdCoundtVal = result2.length();
				if( TagBrows.this.result_USLT != null ||  TagBrows.this.result_SYLT != null){			//歌詞情報が取得できたところで
					dbMsg +="result=null";
					i = pdMaxVal;
				}
				if(result != null){
					int eInt = result.length();
					dbMsg += ">>" + eInt + "文字(処理" + (sInt - eInt ) + "文字)";
				}
			}





//			for(int i = 0; i < pdMaxVal ; i++){
//				dbMsg= reqCode + ";" + i + "/ " + pdMaxVal +")" ;
//				String freamName = kensaku.get(i);
//				dbMsg +=freamName + ";";
//				int sInt = 0;
//				if(result != null){
//					sInt = result.length();
//					dbMsg +="残り" + sInt + "文字";
//				}
////				if(freamName.equals("USLT") || freamName.equals("USLT")){
////					pdMessage =getApplicationContext().getString(R.string.tag_prog_msg1) + " ; " + freamName;		//歌詞を探しています。
////				} else {
////					pdMessage =getApplicationContext().getString(R.string.tag_prog_msg2) + " ; " + freamName;		//その他の書き込みを検索しています。
////				}
//				result = getTargetFream( result , freamName , reqCode);			//<UNSYNCED LYRICS>	非同期 歌詞/文書のコピー	渡された文字列から指定されたフレームを切り出す
////					pdCoundtVal = i + 1 ;
////					//		pdCoundtVal = result2.length();
//				if( TagBrows.this.result_USLT != null ||  TagBrows.this.result_SYLT != null){			//歌詞情報が取得できたところで
//					dbMsg +="result=null";
//					i = pdMaxVal;
//				}
//				if(result != null){
//					int eInt = result.length();
//					dbMsg += ">>" + eInt + "文字(処理" + (sInt - eInt ) + "文字)";
//				}
//				myLog(TAG,dbMsg);
//			}
			myLog(TAG,dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG,"でエラー発生；"+e.toString());
		}
		return result;
	}


	long startPart;		// 開始時刻の取得
	/**
	 * public class plogTask extends AsyncTask<Object, Integer , AsyncTaskResult<Integer>>の置き換え
	 * Android 11でAsyncTaskがdeprecated
	 * サンプルは　 MyTask　https://akira-watson.com/android/asynctask.html
	 * */
	public class ploglessTask {
		ExecutorService executorService;
		private Context cContext = null;
		//		public DialogProgress progressDialog;	// 処理中ダイアログ	ProgressDialog	AlertDialog
		Intent intentDP;

		public int reqCode = 0;						//処理番号
		public List<String> kensaku;
		public String result;
		public File file;


//		public String pdTitol;			//ProgressDialog のタイトルを設定
//		public String pdMessage;			//ProgressDialog のメッセージを設定

		public String pdMessage_stok;			//ProgressDialog のメッセージを設定
		public int pdMaxVal = 0;					//ProgressDialog の最大値を設定 (水平の時)
		public int pdStartVal=0;					//ProgressDialog の初期値を設定 (水平の時)
		public int pdCoundtVal=0;					//ProgressDialog表示値

		public String _numberFormat = "%d/%d";
		public  NumberFormat _percentFormat = NumberFormat.getPercentInstance();
		double num;

		/**
		 * プログレス表示クラスのコンテキスト
		 * */
		public ploglessTask(Context context) {
			super();
			final String TAG = "ploglessTask[ploglessTask]";
			String dbMsg="";
			try {
				executorService  = Executors.newSingleThreadExecutor();
				this.cContext = context;
				//		this.cCallback = callback;
				myLog(TAG,dbMsg );
			} catch (Exception e) {
				myErrorLog(TAG,dbMsg+" でエラー発生；"+e.toString());
			}
		}

		public class TaskRun implements Runnable {

			@Override
			public void run() {
				final String TAG = "run[ploglessTask]";
				String dbMsg="";
				try {
					dbMsg +="["+ reqCode + "]";
					long id = 0;
					switch(reqCode) {
						case read_FILE:				//ファイル読込
////							File file=(File) params[2] ;																	//2.file , null );
//							do{
//								result2 = raf2Str(file, true);			//RandomAccessFileをString変換
//							}while( result2 == null );
//							TagBrows.this.result = result2;
//							dbMsg +=", result = " + TagBrows.this.result.substring(0, 20) +"～"  + TagBrows.this.result.length() +"文字" ;
//							break;
						default:
							dbMsg +=", result = " + TagBrows.this.result.substring(0, 20) +"～"  + TagBrows.this.result.length() +"文字" ;
					//		List<String> kensaku=(List<String>) params[3] ;													//3.検索するフレーム名, kensaku
							pdMaxVal = kensaku.size();
							dbMsg +=", kensaku = " + pdMaxVal + "項目" ;
							for(int i = 0; i < pdMaxVal ; i++){
								dbMsg= reqCode + ";" + i + "/ " + pdMaxVal +")" ;
								String freamName = kensaku.get(i);
								dbMsg +=freamName + ";";
								int sInt = TagBrows.this.result.length();
								dbMsg +="残り" + sInt + "文字";
								if(freamName.equals("USLT") || freamName.equals("USLT")){
									pdMessage =getApplicationContext().getString(R.string.tag_prog_msg1) + " ; " + freamName;		//歌詞を探しています。
								} else {
									pdMessage =getApplicationContext().getString(R.string.tag_prog_msg2) + " ; " + freamName;		//その他の書き込みを検索しています。
								}
								TagBrows.this.result = getTargetFream( TagBrows.this.result , freamName , reqCode);			//<UNSYNCED LYRICS>	非同期 歌詞/文書のコピー	渡された文字列から指定されたフレームを切り出す
								pdCoundtVal = i + 1 ;
								if( TagBrows.this.result_USLT != null ||  TagBrows.this.result_SYLT != null){			//歌詞情報が取得できたところで
									pdCoundtVal = pdMaxVal ;																//ループ中断
									i = pdMaxVal;
								}
								int eInt = TagBrows.this.result.length();
								dbMsg += ">>" + eInt + "文字(処理" + (sInt - eInt ) + "文字)";
								myLog(TAG,dbMsg);
//								publishProgress( pdCoundtVal );		//progressDialog.progBar1.setProgress(step1);
							}
							break;
					}
					new Handler(Looper.getMainLooper())
							.post(() -> onPostExecute());
					myLog(TAG,dbMsg );
				} catch (Exception e) {
					myErrorLog(TAG,dbMsg+" でエラー発生；"+e.toString());
				}
			}
		}

		/**
		 * ploglessTaskの入口
		 * */
		void execute(int reqCode,List<String> kensaku,String result,File file) {
			final String TAG = "execute[ploglessTask]";
			String dbMsg="";
			try {
				onPreExecute(reqCode , kensaku,result,file);
				executorService.submit(new TagBrows.ploglessTask.TaskRun());
				myLog(TAG,dbMsg );
			} catch (Exception e) {
				myErrorLog(TAG,dbMsg+" でエラー発生；"+e.toString());
			}
		}

		/**
		 * ploglessTaskの前処理
		 * Backgroundメソッドの実行前にメインスレッドで実行
		 * */
		void onPreExecute(int req_code ,List<String> kensaku,String result,File file) {           //,SQLiteDatabase write_db
			final String TAG = "onPreExecute";
			String dbMsg="[ploglessTask]";
			try {
				this.reqCode = req_code;
				dbMsg += "[" + this.reqCode + "]";
				this.kensaku = kensaku;
				if(this.kensaku != null){
					dbMsg += " , " + this.kensaku.size() + "件";
					pdMaxVal = this.kensaku.size();
				}
				dbMsg +=",getMax=" + pdMaxVal;
				this.result = result;
				dbMsg +=",result=" + result;
				this.file = file;
				myLog(TAG,dbMsg );
			} catch (Exception e) {
				myErrorLog(TAG,dbMsg+" でエラー発生；"+e.toString());
			}
		}

		/**
		 * ploglessTaskの終端処理
		 * Backgroundメソッドの実行前にメインスレッドで実行
		 * */
		void onPostExecute() {
			final String TAG = "onPostExecute";
			String dbMsg = "";
			try{
				dbMsg +=  "reqCode=" + reqCode;/////////////////////////////////////
				switch(reqCode) {
					case read_FILE:				//ファイル読込
						dbMsg +=", result = " + TagBrows.this.result.substring(0, 20) +"～"  + TagBrows.this.result.length() +"文字" ;
						file2Tag2(TagBrows.this.result);			//文字列からフィールドを抽出				break;
					case read_USLT:				//	<UNSYNCED LYRICS>	非同期 歌詞/文書のコピー									//10cc(3)
						back2Activty(  );			//呼び出しの戻り処理
						break;
					case read_AAC_PRE:				//最小限の設定読取り
						lyricReadAac();		//@lirからの優先読込み
						break;
					case read_AAC_LYRIC:				//@Lyrだけを読めるか試みる
						if( result_USLT == null ){
							result = resultStock;
							//			headReadAac2(result);		//上位階層から順次読み込み		final RandomAccessFile newFile
							itemReadAac(  );		//QuickTime ItemList Tagsの読取り準備
						}else{
							readEndAac(  );
						}
						resultStock = null;
						break;
					case read_AAC_HEAD:				//QuickTime Tagsの読取り
						movieReadAac(  );		//QuickTime Tags.QuickTime Movie Tagsの読取りの読取り準備
						break;
					case read_AAC_HEAD_Movie:		//QuickTime Tags.QuickTime Movie Tagsの読取り
						metaReadAac(  );		//QuickTime Tags.QuickTime Movie Tagsの読取りの読取り準備
						break;
					case read_AAC_Movie_Meta:		//QuickTime Tags.QuickTime Meta Tagsの読取り
						itemReadAac(  );		//QuickTime ItemList Tagsの読取り準備
						break;
					case read_AAC_ITEM:				//QuickTime Tagsの読取り
						readEndAac(  );
						break;
					case read_WMA_ITEM:									//WMAのオブジェクト読取り
					case read_WMA_ID32:									//WMAに埋め込まれたID3v2タグの読取り
					case read_WMA_ID33:									//WMAに埋め込まれたID3v3タグの読取り
					case read_WMA_AAC:									//WMAに埋め込まれたAAC Atomの読取り
						itemReadWmaEnd();		//WMAのオブジェクト読取り終了処理
						break;
					default:
						break;
				}
				myLog(TAG, dbMsg);
			}catch (Exception e) {
				myErrorLog(TAG ,  dbMsg + "で" + e);
			}
		}
	}

	/**
	 * 第一引数;タスク開始時:doInBackground()に渡す引数の型,
	 * 第二引数;進捗率を表示させるとき:onProgressUpdate()に使う型,
	 * 第三引数;タスク終了時のdoInBackground()の返り値の型			AsyncTaskResult<Object>
	 * 		http://d.hatena.ne.jp/tomorrowkey/20100824/1282655538
	 * 		http://pentan.info/android/app/multi_thread.html**/
//	public class plogTask extends AsyncTask<Object, Integer , AsyncTaskResult<Integer>> {		//myResult	元は<Object, Integer, Boolean>
//		private plogTaskCallback callback;
//		public long start = 0;				// 開始時刻の取得
//		public Boolean isShowProgress;
//		public ProgressDialog progressDialog = null;	// 処理中ダイアログ	ProgressDialog	AlertDialog
//		public int reqCode = 0;						//処理番号
//		public CharSequence pdTitol;			//ProgressDialog のタイトルを設定
//		public CharSequence pdMessage;			//ProgressDialog のメッセージを設定
//		public CharSequence pdMessage_stok;			//ProgressDialog のメッセージを設定
//		public int pdMaxVal = 0;					//ProgressDialog の最大値を設定 (水平の時)
//		public int pdStartVal=0;					//ProgressDialog の初期値を設定 (水平の時)
//		public int pdCoundtVal=0;					//ProgressDialog表示値
//		public int pd2MaxVal;					//ProgressDialog の最大値を設定 (水平の時)
//		public int pd2CoundtVal;					//ProgressDialog表示値
//		public String _numberFormat = "%d/%d";
//		public  NumberFormat _percentFormat = NumberFormat.getPercentInstance();
//
//		public Boolean preExecuteFiniSh=false;	//ProgressDialog生成終了
//		public Bundle extras;
//
//		long stepKaisi = System.currentTimeMillis();		//この処理の開始時刻の取得
//		long stepSyuuryou;		//この処理の終了時刻の取得
//
//		public plogTask(Context cContext , plogTaskCallback callback ,int reqCode, CharSequence pdTitol ,CharSequence pdMessage ,int pdMaxVal){
//			super();
//		final String TAG = "plogTask[plogTask.TagBrows]";
//		try{
//			String dbMsg = "cContext="+cContext;///////////////////////////
//			if( cContext != null ){
//				this.callback = callback;
//				dbMsg += ",callback="+callback;///////////////////////////
//				dbMsg += ",reqCode=" + reqCode;
//				dbMsg += ",Titol=" + pdTitol;
//				dbMsg += ",Message=" + pdMessage;
//				if(progressDialog != null ){
//					if(progressDialog.isShowing()){
//						progressDialog.dismiss();
//					}
//				}
//				progressDialog = new ProgressDialog(cContext);			//.getApplicationContext()
//				progressDialog.setTitle(pdTitol);
//				progressDialog.setMessage(pdMessage);
//				switch(reqCode) {
//				case read_FILE:				//ファイル読込
//					progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);			// プログレスダイアログのスタイルを円形に設定します
//					break;
//				default:
//					progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);			// プログレスダイアログのスタイルを水平スタイルに設定します
//					dbMsg += ",Max=" + pdMaxVal;
//					progressDialog.setMax(pdMaxVal);			// プログレスダイアログの最大値を設定します
//					break;
//				}
//				progressDialog.setCancelable(true);			// プログレスダイアログのキャンセルが可能かどうかを設定します
//				progressDialog.show();
//				dbMsg += ">isShowing>" + progressDialog.isShowing();
//			}
//	//		myLog(TAG, dbMsg);
//		} catch (Exception e) {
//			myErrorLog(TAG,"でエラー発生；"+e.toString());
//		}
//	}
//		@Override
//	/*最初にUIスレッドで呼び出されます。 , UIに関わる処理をします。
//	 * doInBackgroundメソッドの実行前にメインスレッドで実行されます。
//	 * 非同期処理前に何か処理を行いたい時などに使うことができます。 */
//		protected void onPreExecute() {			// onPreExecuteダイアログ表示
//			super.onPreExecute();
//			final String TAG = "onPreExecute";
//			String dbMsg="[plogTask.TagBrows]";
//			try {
//				dbMsg = ":reqCode="+reqCode;///////////////////////////
//				dbMsg +=  ",pdTitol="+pdTitol;///////////////////////////
//				dbMsg +=  ",Message="+pdMessage;///////////////////////////
//				dbMsg += ",pdMaxVal="+pdMaxVal;///////////////////////////
//		//		myLog(TAG, dbMsg);
//				//☆こここで初期設定が出来ないのでonCreate相当のコンストラクタ；plogTaskで行う
//			} catch (Exception e) {
//				myErrorLog(TAG,"でエラー発生；"+e.toString());
//			}
//		}
//
//		@SuppressWarnings("resource")
//		@Override
//	/**
//	 * doInBackground
//	 * ワーカースレッド上で実行されます。 このメソッドに渡されるパラメータの型はAsyncTaskの一つ目のパラメータです。
//	 * このメソッドの戻り値は AsyncTaskの三つ目のパラメータです。
//	 * メインスレッドとは別のスレッドで実行されます。
//	 * 非同期で処理したい内容を記述します。 このメソッドだけは必ず実装する必要があります。
//	 *0 ;reqCode, 1; pdMessage , 2;pdMaxVal ,3:cursor , 4;cUri , 5;where , 6;stmt , 7;cv ,  8;omitlist , 9;tList );
//	 * */
//		public AsyncTaskResult<Integer> doInBackground(Object... params) {//InParams続けて呼ばれる処理；第一引数が反映されるのはここなのでここからダイアログ更新 バックスレッドで実行する処理;getProgress=0で呼ばれている
//			final String TAG = "doInBackground";
//			String dbMsg="[plogTask.TagBrows]";
//			try {
//				pdCoundtVal = 0;
//				this.reqCode = (Integer) params[0] ;			//0.処理;reqCode
//				dbMsg="reqCode = " + reqCode;
//				CharSequence setStr=(CharSequence) params[1];	//1.次の処理に渡すメッセージ;pdMessage
//				if(setStr !=null ){
//					if(! setStr.equals(pdMessage)){
//						pdMessage = setStr;
//						this.pdMessage = setStr;
//						dbMsg +=",Message = " + pdMessage;
//					}
//				}
//				String result2 = null;
//				switch(reqCode) {
//				case read_FILE:				//ファイル読込
//					File file=(File) params[2] ;																	//2.file , null );
//					do{
//						result2 = raf2Str(file, true);			//RandomAccessFileをString変換
//					}while( result2 == null );
//					TagBrows.this.result = result2;
//					dbMsg +=", result = " + TagBrows.this.result.substring(0, 20) +"～"  + TagBrows.this.result.length() +"文字" ;
//					break;
//				default:
//					TagBrows.this.result=(String) params[2] ;																//2.result
//					dbMsg +=", result = " + TagBrows.this.result.substring(0, 20) +"～"  + TagBrows.this.result.length() +"文字" ;
//					List<String> kensaku=(List<String>) params[3] ;													//3.検索するフレーム名, kensaku
//					pdMaxVal = kensaku.size();
//					dbMsg +=", kensaku = " + pdMaxVal + "項目" ;
//					for(int i = 0; i < pdMaxVal ; i++){
//						dbMsg= reqCode + ";" + i + "/ " + pdMaxVal +")" ;
//						String freamName = kensaku.get(i);
//						dbMsg +=freamName + ";";
//						int sInt = TagBrows.this.result.length();
//						dbMsg +="残り" + sInt + "文字";
//						if(freamName.equals("USLT") || freamName.equals("USLT")){
//							pdMessage =getApplicationContext().getString(R.string.tag_prog_msg1) + " ; " + freamName;		//歌詞を探しています。
//						} else {
//							pdMessage =getApplicationContext().getString(R.string.tag_prog_msg2) + " ; " + freamName;		//その他の書き込みを検索しています。
//						}
//						TagBrows.this.result = getTargetFream( TagBrows.this.result , freamName , reqCode);			//<UNSYNCED LYRICS>	非同期 歌詞/文書のコピー	渡された文字列から指定されたフレームを切り出す
//						pdCoundtVal = i + 1 ;
//						if( TagBrows.this.result_USLT != null ||  TagBrows.this.result_SYLT != null){			//歌詞情報が取得できたところで
//							pdCoundtVal = pdMaxVal ;																//ループ中断
//							i = pdMaxVal;
//						}
//						int eInt = TagBrows.this.result.length();
//						dbMsg += ">>" + eInt + "文字(処理" + (sInt - eInt ) + "文字)";
//						myLog(TAG,dbMsg);
//						publishProgress( pdCoundtVal );		//progressDialog.progBar1.setProgress(step1);
//					}
//					break;
//				}
//				stepSyuuryou = System.currentTimeMillis();		//この処理の終了時刻の取得
//				dbMsg = this.reqCode +";経過時間"+(int)((stepSyuuryou - stepKaisi)) + "[mS]";				//各処理の所要時間
//				return AsyncTaskResult.createNormalResult( reqCode );
//			} catch (Exception e) {
//				myErrorLog(TAG,dbMsg+"；"+e.toString());
//				return AsyncTaskResult.createNormalResult(reqCode) ;				//onPostExecuteへ
//			}
//		}
//
//		@Override
//	/**
//	 * onProgressUpdate
//	 * プログレスバー更新処理： UIスレッドで実行される doInBackground内でpublishProgressメソッドが呼ばれると、
//	 * UIスレッド上でこのメソッドが呼ばれます。   このメソッドの引数の型はAsyncTaskの二つ目のパラメータです。
//	 * メインスレッドで実行されます。非同期処理の進行状況をプログレスバーで 表示したい時などに使うことができます。*/
//		public void onProgressUpdate(Integer... values) {			//
//			final String TAG = "onProgressUpdate";
//			String dbMsg="[plogTask.TagBrows]";
//			int progress = values[0];
//			try{
//				dbMsg= this.reqCode +")progress= " + progress;
//				progressDialog.setProgress(progress);
//				dbMsg +=">> " + progressDialog.getProgress();
//				dbMsg +="/" + progressDialog.getMax();///////////////////////////////////
//		//		myLog(TAG,dbMsg);
//			} catch (Exception e) {
//				myErrorLog(TAG,dbMsg+"；"+e.toString());
//			}
//		}
//
//		@Override
//	/**
//	 * onPostExecute
//	 * doInBackground が終わるとそのメソッドの戻り値をパラメータとして渡して onPostExecute が呼ばれます。
//	 * このパラメータの型は AsyncTask を extends するときの三つめのパラメータです。
//	 *  バックグラウンド処理が終了し、メインスレッドに反映させる処理をここに書きます。
//	 *  doInBackgroundメソッドの実行後にメインスレッドで実行されます。
//	 *  doInBackgroundメソッドの戻り値をこのメソッドの引数として受け取り、その結果を画面に反映させることができます。*/
//		public void onPostExecute(AsyncTaskResult<Integer> ret){	// タスク終了後処理：UIスレッドで実行される AsyncTaskResult<Object>
//			super.onPostExecute(ret);
//				final String TAG = "onPostExecute";
//				String dbMsg="[plogTask.TagBrows]";
//				try{
//					Thread.sleep(100);			//書ききる為の時間（100msでは不足）
//					reqCode = ret.getReqCode();
//					dbMsg +="終了；reqCode=" + reqCode +"(終端"+ pdCoundtVal +")";
//					dbMsg +=",callback = " + callback;	/////http://techbooster.org/android/ui/1282/
//					dbMsg +="[ " + pdCoundtVal +  "/ " + pdMaxVal +"]";	/////http://techbooster.org/android/ui/1282/
//	//				myLog(TAG, dbMsg);
//			//		if( pdCoundtVal  >= pdMaxVal){
//						callback.onSuccessplogTask(reqCode );		//1.次の処理;2.次の処理に渡すメッセージ
//						progressDialog.dismiss();
//	//					cursor.close();
//			//		}
//				} catch (Exception e) {
//					myErrorLog(TAG,dbMsg + "でエラー発生；"+e.toString());
//				}
//			}
//	}  //public class plogTask
//
//	@Override
//	public void onSuccessplogTask(int reqCode) {															//①ⅵ3；
//		final String TAG = "onSuccessplogTask";
//		String dbMsg="[plogTask.TagBrows]";
//		try{
//			dbMsg= "reqCode=" + reqCode;/////////////////////////////////////
//			switch(reqCode) {
//			case read_FILE:				//ファイル読込
//				dbMsg +=", result = " + TagBrows.this.result.substring(0, 20) +"～"  + TagBrows.this.result.length() +"文字" ;
//				file2Tag2(TagBrows.this.result);			//文字列からフィールドを抽出				break;
//			case read_USLT:				//	<UNSYNCED LYRICS>	非同期 歌詞/文書のコピー									//10cc(3)
//				back2Activty(  );			//呼び出しの戻り処理
//				break;
//			case read_AAC_PRE:				//最小限の設定読取り
//				lyricReadAac();		//@lirからの優先読込み
//				break;
//			case read_AAC_LYRIC:				//@Lyrだけを読めるか試みる
//				if( result_USLT == null ){
//					result = resultStock;
//		//			headReadAac2(result);		//上位階層から順次読み込み		final RandomAccessFile newFile
//					itemReadAac(  );		//QuickTime ItemList Tagsの読取り準備
//				}else{
//					readEndAac(  );
//				}
//				resultStock = null;
//				break;
//			case read_AAC_HEAD:				//QuickTime Tagsの読取り
//				movieReadAac(  );		//QuickTime Tags.QuickTime Movie Tagsの読取りの読取り準備
//				break;
//			case read_AAC_HEAD_Movie:		//QuickTime Tags.QuickTime Movie Tagsの読取り
//				metaReadAac(  );		//QuickTime Tags.QuickTime Movie Tagsの読取りの読取り準備
//				break;
//			case read_AAC_Movie_Meta:		//QuickTime Tags.QuickTime Meta Tagsの読取り
//				itemReadAac(  );		//QuickTime ItemList Tagsの読取り準備
//				break;
//			case read_AAC_ITEM:				//QuickTime Tagsの読取り
//				readEndAac(  );
//				break;
//			case read_WMA_ITEM:									//WMAのオブジェクト読取り
//			case read_WMA_ID32:									//WMAに埋め込まれたID3v2タグの読取り
//			case read_WMA_ID33:									//WMAに埋め込まれたID3v3タグの読取り
//			case read_WMA_AAC:									//WMAに埋め込まれたAAC Atomの読取り
//				itemReadWmaEnd();		//WMAのオブジェクト読取り終了処理
//				break;
//			default:
//				break;
//			}
//	//		myLog(TAG,dbMsg);
//		} catch (Exception e) {		//汎用
//			myErrorLog(TAG,dbMsg+"で"+e.toString());
//		}
//	}
	///////////////////////////////////////////////////////////////////////////////////
	public static void myLog(String TAG , String dbMsg) {
		Util UTIL = new Util();
		Util.myLog(TAG , "[TagBrows]" + dbMsg);
	}

	public static void myErrorLog(String TAG , String dbMsg) {
		Util UTIL = new Util();
		Util.myErrorLog(TAG , "[TagBrows]" + dbMsg);
	}
}
/**汎用資料
 *	文字コード											http://pentan.info/doc/codepage_list.html
 *	ファイル・ディレクトリー操作						http://www.ne.jp/asahi/hishidama/home/tech/java/file.html
 * */
