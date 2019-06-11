# androidloginkitdemo
A simple demo for Snap Loginkit Android

The build.gradle for your App and Project already has the appropriate permissions in place, but you will need to amend the AndroidManifest.xml to include your own App details, see below.

<application ...>
   <meta-data android:name="com.snapchat.kit.sdk.clientId" android:value="your app client id" />
   <meta-data android:name="com.snapchat.kit.sdk.redirectUrl" android:value="the url that will handle login completion" />

You will also need to amend the sheme, host and path as per below.

           <data
               android:scheme="the scheme of your redirect url"
               android:host="the host of your redirect url"
               android:path="the path of your redirect url"
               />
       </intent-filter>

If your redirect url is myapp://snap-kit/oauth2
Then the breakdown is:
                   android:scheme="myapp"
                   android:host="snap-kit"
                   android:path="oauth2"

