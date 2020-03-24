package me.saharnooby.plugins.customarmor.util;

import lombok.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author saharNooby
 * @since 19:28 16.03.2020
 */
public final class IOUtil {

	public static void transferBytes(@NonNull InputStream in, @NonNull OutputStream out) throws IOException {
		byte[] buf = new byte[8192];
		int i;
		while ((i = in.read(buf)) != -1) {
			out.write(buf, 0, i);
		}
	}

}
