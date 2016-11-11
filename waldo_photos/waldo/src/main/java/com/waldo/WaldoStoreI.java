package com.waldo;

public interface WaldoStoreI {

	EXIFData getDoc(String key);

	void storeDoc(EXIFData exifData);

}