package com.app.XGOS.wishProvider;

import java.net.URI;
import java.util.List;

public interface IWishIterativeProvider {

    void finish() throws WishProviderException;

    List<Wish> getUpToNWishes(int n) throws WishProviderException;

    boolean isMoreDataAvailable() throws WishProviderException;

    void initFromNewSource(URI uri) throws WishProviderException;
}
