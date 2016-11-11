package com.waldo;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

//Here we have used ehcache for storing the exif data taking java object as the 
//primary element.However for portability json should be used as the stored element
//and document store such as mongo db would be more appropriate
public class WaldoStore implements WaldoStoreI {
	private static final String CACHE = "cache";
	private static final CacheManager CACHE_MANAGER = CacheManager.getInstance();
	static {
		if (!CACHE_MANAGER.cacheExists(CACHE))
			CACHE_MANAGER.addCache(CACHE);
	}
	private Cache getWaldoCache() {
		return CACHE_MANAGER.getCache(CACHE);
	}
	
	/* (non-Javadoc)
	 * @see com.waldo.WaldoStoreI#getDoc(java.lang.String)
	 */
	@Override
	public EXIFData getDoc(String key) {
		Element element = getWaldoCache().get(key);
		if (element != null) {
			return (EXIFData) element.getObjectValue();
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see com.waldo.WaldoStoreI#storeDoc(com.waldo.EXIFData)
	 */
	@Override
	public void storeDoc(EXIFData exifData) {
		getWaldoCache().put(new Element(exifData.getKey(), exifData));
	}
}
