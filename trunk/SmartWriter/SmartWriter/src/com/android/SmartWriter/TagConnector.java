package com.android.SmartWriter;

import java.nio.charset.Charset;
import java.util.Locale;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Parcelable;
import android.util.Log;

public class TagConnector {

	public String readTag(Intent intent) {
		NdefMessage[] messages = getNdefMessages(intent);
		byte[] payload = messages[0].getRecords()[0].getPayload();
		String placeId = new String(payload);
		return placeId;
	}

	private NdefMessage[] getNdefMessages(Intent intent) {
		// Parse the intent
		NdefMessage[] msgs = null;
		String action = intent.getAction();
		if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
				|| NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
			Parcelable[] rawMsgs = intent
					.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
			if (rawMsgs != null) {
				msgs = new NdefMessage[rawMsgs.length];
				for (int i = 0; i < rawMsgs.length; i++) {
					msgs[i] = (NdefMessage) rawMsgs[i];
				}
			} else {
				// Unknown tag type
				byte[] empty = new byte[] {};
				NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN,
						empty, empty, empty);
				NdefMessage msg = new NdefMessage(new NdefRecord[] { record });
				msgs = new NdefMessage[] { msg };
			}
		} else {
			Log.i("TAG", "Unknown intent.");
		}
		return msgs;
	}

	public boolean writeTag(Intent intent, String id) {
		Tag t = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		Ndef tag = Ndef.get(t);
		try {
			//NdefMessage message = getPosteridAsNdef(id);
			NdefMessage message = new NdefMessage(
	                new NdefRecord[] { newTextRecord(id, Locale.ENGLISH, true)});
			int size = message.toByteArray().length;
			if (tag != null) {
				tag.connect();
				if (!tag.isWritable()) {
					Log.i("tag", "not writable");
					return false;
				}
				Log.i("tag", String.valueOf(size));
				Log.i("tag", String.valueOf(tag.getMaxSize()));
				if (tag.getMaxSize() < size) {
					Log.i("tag", "less than size");
					return false;
				}
				Log.i("tag", "writing to tag using ndef");
				tag.writeNdefMessage(message);
				return true;
			} else {
				NdefFormatable format = NdefFormatable.get(t);
				if (format != null) {
					format.connect();
					format.format(message);
					Log.i("tag", "writing to tag");
				}
			}
		} catch (Exception e) {
			// do error handling
			Log.i("tag", "error");
			e.printStackTrace();
		}
		return false;
	}

	/*private static NdefMessage getPosteridAsNdef(String id) {
		byte[] textBytes = id.getBytes();
		NdefRecord textRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA,
				"application/vnd.text".getBytes(), new byte[] {}, textBytes);
		return new NdefMessage(new NdefRecord[] { textRecord });
	}*/
	
	// http://developer.android.com/resources/samples/ApiDemos/src/com/example/android/apis/nfc/ForegroundNdefPush.html
	// Fewer bytes than above
	public static NdefRecord newTextRecord(String text, Locale locale, boolean encodeInUtf8) {
        byte[] langBytes = locale.getLanguage().getBytes(Charset.forName("US-ASCII"));

        Charset utfEncoding = encodeInUtf8 ? Charset.forName("UTF-8") : Charset.forName("UTF-16");
        byte[] textBytes = text.getBytes(utfEncoding);

        int utfBit = encodeInUtf8 ? 0 : (1 << 7);
        char status = (char) (utfBit + langBytes.length);

        byte[] data = new byte[1 + langBytes.length + textBytes.length]; 
        data[0] = (byte) status;
        System.arraycopy(langBytes, 0, data, 1, langBytes.length);
        System.arraycopy(textBytes, 0, data, 1 + langBytes.length, textBytes.length);

        return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], data);
    }

}
