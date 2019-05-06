package com.hijiyam_koubou.marasongs;

import android.graphics.Bitmap;

public class CustomData {
	public Bitmap imageData_;
	public String imageUrl_;
	public String textData_;
	public String subData_;
	public String dataUri;
	public String no_;

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

}

