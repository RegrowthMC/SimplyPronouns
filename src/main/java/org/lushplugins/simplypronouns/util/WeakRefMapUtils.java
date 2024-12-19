package org.lushplugins.simplypronouns.util;

import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

public class WeakRefMapUtils {

    public static <K, V> @Nullable V get(HashMap<K, WeakReference<V>> map, K key) {
        WeakReference<V> cachedRef = map.get(key);
        if (cachedRef != null) {
            V cachedValue = cachedRef.get();
            if (cachedValue != null) {
                return cachedValue;
            } else {
                map.remove(key);
            }
        }

        return null;
    }

    public static <K, V> CompletableFuture<V> get(HashMap<K, WeakReference<V>> map, K key, Callable<CompletableFuture<V>> loader) {
        WeakReference<V> cachedRef = map.get(key);
        if (cachedRef != null) {
            V cachedValue = cachedRef.get();
            if (cachedValue != null) {
                return CompletableFuture.completedFuture(cachedValue);
            } else {
                map.remove(key);
            }
        }

        try {
            CompletableFuture<V> future = loader.call();
            future.thenAccept((loadedValue) -> map.put(key, new WeakReference<>(loadedValue)));
            return future;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
