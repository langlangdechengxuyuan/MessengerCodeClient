package com.example.messengerclient;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.List;

/**
 * 客户端只需根据服务返回的 IBinder 创建一个 Messenger，然后利用 send() 发送一条消息。
 */
public class MainActivity extends AppCompatActivity {

    /** Messenger for communicating with the service. */
    Messenger mService = null;

    /** Flag indicating whether we have called bind on the service. */
    boolean mBound;

    private Messenger mMessenger = new Messenger(new Handler()
    {
        @Override
        public void handleMessage(Message msgFromServer)
        {
            switch (msgFromServer.what)
            {
                case 1:
                    Toast.makeText(getApplicationContext(), "replay replay", Toast.LENGTH_SHORT).show();
                    break;
            }
            super.handleMessage(msgFromServer);
        }
    });

    /**
     * Class for interacting with the main interface of the service.
     */
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the object we can use to
            // interact with the service.  We are communicating with the
            // service using a Messenger, so here we get a client-side
            // representation of that from the raw IBinder object.
            mService = new Messenger(service);
            mBound = true;
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            mService = null;
            mBound = false;
        }
    };

    public void sayHello(View v) {
        if (!mBound) return;
        // Create and send a message to the service, using a supported 'what' value
        Message msg = Message.obtain(null, 1, 0, 0);
        msg.replyTo = mMessenger;
        try {
            mService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to the service
//        bindService(new Intent(this, MessengerService.class), mConnection,
//                Context.BIND_AUTO_CREATE);

        bindService(createExplicitIntent("com.example.messengerservice",
                "com.example.messengerservice.MessengerService",
                "com.example.messengerservice.SERVICE"), mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    public  Intent createExplicitIntent(String pkgName, String className, String action) {
        ComponentName component = new ComponentName(pkgName, className);
        Intent intent = new Intent(action);
        intent.setComponent(component);
        return intent;
    }

}
