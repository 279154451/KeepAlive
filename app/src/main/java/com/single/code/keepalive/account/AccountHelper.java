package com.single.code.keepalive.account;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

public class AccountHelper {

    private static final String TAG = "AccountHelper";

    private static final String ACCOUNT_TYPE = "com.single.code.keepalive.account";//与account-authenticator.xml、sync_adapter.xml这个三个的accountType保持一致
    private static final String ACCOUNT_PROVIDER_AUTHORITY = "com.single.code.keepalive.account.provider";//与sync_adapter.xml 、SyncProvider的AUTHORITY保持一致
    private static  String accountName ="keepAlive";
    private static final String accountPwd = "123456";
    /**
     * 添加账号
     *
     * @param context
     */
    public static void addAccount(Context context) {
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // 获得此类型的账户
        // 需要增加权限  GET_ACCOUNTS
        Account[] accounts = accountManager.getAccountsByType(ACCOUNT_TYPE);

        if (accounts.length > 0) {
            Log.e(TAG, "账户已存在");
            return;
        }
        Account account = new Account(accountName, ACCOUNT_TYPE);
        // 给这个账户类型添加一个账户
        // 需要增加权限  AUTHENTICATE_ACCOUNTS
        accountManager.addAccountExplicitly(account, accountPwd, new Bundle());
    }

    /**
     * 设置账户自动同步
     */
    public static void autoSync() {
        Account account = new Account(accountName, ACCOUNT_TYPE);

        // 下面三个都需要同一个权限  WRITE_SYNC_SETTINGS

        // 设置同步
        ContentResolver.setIsSyncable(account, ACCOUNT_PROVIDER_AUTHORITY, 1);

        // 自动同步
        ContentResolver.setSyncAutomatically(account, ACCOUNT_PROVIDER_AUTHORITY, true);

        // 设置同步周期
        ContentResolver.addPeriodicSync(account, ACCOUNT_PROVIDER_AUTHORITY, new Bundle(), 1);
    }

}
