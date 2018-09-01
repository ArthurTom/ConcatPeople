package com.eway.concatpeople;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import java.util.HashMap;
import java.util.Map;

/**
 * 获取手机联系人的工具类
 */
public class ConstactUtil {
    /**
     * 获取手机上所有的联系人的数据
     *
     * @return 返回的map<String ,  String > 是名字作为key，电话号码做为number
     */
    public static Map<String, String> getAllCallRecords(Context context) {
        Map<String, String> temp = new HashMap<String, String>();
        Cursor c = context.getContentResolver().query(
                ContactsContract.Contacts.CONTENT_URI,
                null,
                null,
                null,
                ContactsContract.Contacts.DISPLAY_NAME
                        + " COLLATE LOCALIZED ASC");
        if (c.moveToFirst()) {   // 游标指向数据的第一位
            do {
                // 获得联系人的ID号
                String contactId = c.getString(c
                        .getColumnIndex(ContactsContract.Contacts._ID));
                // 获得联系人姓名
                String name = c
                        .getString(c
                                .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                // 查看该联系人有多少个电话号码。如果没有这返回值为0
                int phoneCount = c
                        .getInt(c
                                .getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                String number = null;
                if (phoneCount > 0) {
                    // 获得联系人的电话号码
                    Cursor phones = context.getContentResolver().query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                    + " = " + contactId, null, null);
                    if (phones.moveToFirst()) {
                        number = phones
                                .getString(phones
                                        .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    }
                    phones.close();
                }
                temp.put(name, number);
            } while (c.moveToNext());
        }
        c.close();
        return temp;
    }
}
