package com.hijiyam_koubou.marasongs;

import android.graphics.Bitmap;

public class CustomData {
	public Bitmap imageData_;
	public String imageUrl_;
	public String textData_;
	public String subData_;
	public String dataUri;
	public String no_;
	public String album_name_;
	public String album_id_;
	public String album_key_;
//	public ItemLayout itemLayout_;

	public void setImagaData(Bitmap image) {
		imageData_ = image;
	}

	public Bitmap getImageData() {
		return imageData_;
	}

	public void setTextData(String text) {
		textData_ = text;
	}

	public String getTextData() {
		return textData_;
	}


	public String getimageUrl() {
		return imageUrl_;
	}

	public void setimageUrl(String text) {
		imageUrl_ = text;
	}

	public void setSubData(String text) {
		subData_ = text;
	}

	public String getSubData() {
		return subData_;
	}

	public void setDataUri(String text) {
		dataUri = text;
	}

	public String getDataUri() {
		return dataUri;
	}

	public void setNom(String text) {
		no_ = text;
	}

	public String getNom() {
		return no_;
	}

	public void setAlbum_name(String text) {album_name_ = text;}

	public String getAlbum_name() {
		return album_name_;
	}

	public void setAlbum_id(String text) {album_id_ = text;}

	public String getAlbum_id() {
		return album_id_;
	}

	public void setAlbum_key(String text) {album_key_ = text;}

	public String getAlbum_key() {
		return album_key_;
	}

//	public void setItemLayout(ItemLayout itemLayout) {itemLayout_ = itemLayout;}
//
//	public ItemLayout getItemLayout() {
//		return itemLayout_;
//	}

}

