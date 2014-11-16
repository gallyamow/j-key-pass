package jkeypass.sync.dropbox;

import com.dropbox.core.*;

import java.util.Locale;

public class Dropbox {
	public Dropbox(String key, String secret, String token) throws DbxException {
//		DbxAppInfo appInfo = new DbxAppInfo(key, secret);

		System.out.println(Locale.getDefault().toString());

		DbxRequestConfig config = new DbxRequestConfig("j-key-pass", Locale.getDefault().toString());
		DbxClient client = new DbxClient(config, token);

		System.out.println("Linked account: " + client.getAccountInfo().displayName);
	}

	public boolean save() {
		return true;
	}

	public Object load() {
		return null;
	}
}
