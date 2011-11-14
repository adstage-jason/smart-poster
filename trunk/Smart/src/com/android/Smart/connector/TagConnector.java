package com.android.Smart.connector;

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

	public void writeTag(Intent intent, Long id) {
		Tag t = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		Ndef tag = Ndef.get(t);
		try {
			NdefMessage message = getPosteridAsNdef(id);
			int size = message.toByteArray().length;
			if (tag != null) {
				tag.connect();
				if (!tag.isWritable()) {
					Log.i("tag", "not writable");
				}
				if (tag.getMaxSize() < size) {
					Log.i("tag", "less than size");
				}
				Log.i("tag", "writing to tag using ndef");
				tag.writeNdefMessage(message);
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
	}

	private static NdefMessage getPosteridAsNdef(Long id) {
		String msg = ((Long) id).toString();
		byte[] textBytes = msg.getBytes();
		NdefRecord textRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA,
				"application/vnd.text".getBytes(), new byte[] {}, textBytes);
		return new NdefMessage(new NdefRecord[] { textRecord });
	}

}
