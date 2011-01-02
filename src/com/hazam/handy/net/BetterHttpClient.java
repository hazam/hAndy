package com.hazam.handy.net;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HttpContext;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * DefaultHttpClient that features a couple of additional features, namely
 * <ul>
 * 	<li>UA built from application name and version</li>
 *  <li>Reasonable defaults for Socket buffer Size and Timeouts</li>
 *  <li>Support for GZIPped Entities</li>
 *  <li>Workaround for accepting invalid certificates over HTTPS</li>
 * </ul> 
 * 
 * @author hazam
 */
public class BetterHttpClient extends DefaultHttpClient {
	private static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
	private static final String ENCODING_GZIP = "gzip";
	public static final int DEFAULT_TIMEOUT = 20000;
	public static final int DEFAULT_SOCK_SIZE = 8192;

	/**
	 * @param context context to be bound. Not used in this implementation unless for {@link BetterHttpClient#buildUserAgent}
	 * @param customUA meant to be used in subclasses, causes to call {@link BetterHttpClient#buildUserAgent}
	 */
	public BetterHttpClient(Context context, boolean customUA) {
		final HttpParams params = new BasicHttpParams();

		// Use generous timeouts for slow mobile networks
		HttpConnectionParams.setConnectionTimeout(params, DEFAULT_TIMEOUT);

		HttpConnectionParams.setSoTimeout(params, DEFAULT_TIMEOUT);

		HttpConnectionParams.setSocketBufferSize(params, DEFAULT_SOCK_SIZE);
		if (customUA) {
			HttpProtocolParams.setUserAgent(params, buildUserAgent(context));
		}

		setParams(params);

		addRequestInterceptor(new HttpRequestInterceptor() {
			public void process(HttpRequest request, HttpContext context) {
				// Add header to accept gzip content
				if (!request.containsHeader(HEADER_ACCEPT_ENCODING)) {
					request.addHeader(HEADER_ACCEPT_ENCODING, ENCODING_GZIP);
				}
			}
		});

		addResponseInterceptor(new HttpResponseInterceptor() {
			public void process(HttpResponse response, HttpContext context) {
				// Inflate any responses compressed with gzip
				final HttpEntity entity = response.getEntity();
				final Header encoding = entity.getContentEncoding();
				if (encoding != null) {
					for (HeaderElement element : encoding.getElements()) {
						if (element.getName().equalsIgnoreCase(ENCODING_GZIP)) {
							response.setEntity(new InflatingEntity(response.getEntity()));
							break;
						}
					}
				}
			}
		});
	}

	/**
	 * Simple {@link org.apache.http.entity.HttpEntityWrapper} that inflates the wrapped {@link HttpEntity} by passing
	 * it through {@link java.util.zip.GZIPInputStream}.
	 */
	private static class InflatingEntity extends HttpEntityWrapper {
		public InflatingEntity(HttpEntity wrapped) {
			super(wrapped);
		}

		@Override
		public InputStream getContent() throws IOException {
			return new GZIPInputStream(wrappedEntity.getContent());
		}

		@Override
		public long getContentLength() {
			return -1;
		}
	}

	/**
	 * Build and return a user-agent string that can identify this application to remote servers. Contains the package
	 * name and version code.
	 */
	protected String buildUserAgent(Context context) {
		try {
			final PackageManager manager = context.getPackageManager();
			final PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);

			// Some APIs require "(gzip)" in the user-agent string.
			return info.packageName + "/" + info.versionName + " (" + info.versionCode + ") (gzip)";
		} catch (PackageManager.NameNotFoundException e) {
			return null;
		}
	}

	/**
	 * Apache HTTP Client found in Android is an old version suffering from a limitation,
	 * it has known problems in connecting via HTTPS with self-signed or unknown CA Certificates.
	 * This implements a (dirty) workaround to make it work, accessing private variables along the way.
	 * 
	 * @param ignore whether we sould ignore Certificates and Hostname checking or not
	 * @return if the operation was successful (a lot of bad thing could happen when using this hack)
	 */
	public boolean setIgnoreInvalidCertificates(boolean ignore) {
		boolean successful = false;
		if (ignore) {
			try {
				SSLSocketFactory sf = SSLSocketFactory.getSocketFactory();
				sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
				Field privateStringField;
				privateStringField = SSLSocketFactory.class.getDeclaredField("socketfactory");
				privateStringField.setAccessible(true);
				//TLS the most recent implementation
				SSLContext context = SSLContext.getInstance("TLS");
				context.init(null, new TrustManager[] { new NullTrustManager() }, null);
				privateStringField.set(sf, context);
				privateStringField.setAccessible(false);
				getConnectionManager().getSchemeRegistry().register(new Scheme("https", sf, 443));
				successful = true;
			} catch (Throwable e) {
				e.printStackTrace();
				successful = false;
			}
		} else {
			try {
				SSLSocketFactory sf = SSLSocketFactory.getSocketFactory();
				getConnectionManager().getSchemeRegistry().register(new Scheme("https", sf, 443));
				successful = true;
			} catch (Throwable e) {
				e.printStackTrace();
				successful = false;
			}
		}
		return successful;
	}

	/**
	 * TrustManager not really caring about certificates chain
	 * 
	 * @author hazam
	 */
	private static class NullTrustManager implements X509TrustManager {

		@Override
		public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}

		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}
	};
}
