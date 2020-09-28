package io.dcvz.rnzendesk;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.lang.Exception;

import android.content.Intent;
import android.os.Build;
import androidx.annotation.RequiresApi;

import zendesk.commonui.UiConfig;
import zendesk.core.Zendesk;
import zendesk.core.Identity;
import zendesk.core.JwtIdentity;
import zendesk.core.AnonymousIdentity;
import zendesk.core.PushRegistrationProvider;
import zendesk.support.Attachment;
import zendesk.support.Support;
import zendesk.support.CreateRequest;
import zendesk.support.UploadProvider;
import zendesk.support.UploadResponse;
import zendesk.support.Request;
import zendesk.support.RequestProvider;
import zendesk.support.User;
import zendesk.support.guide.HelpCenterActivity;
import zendesk.support.request.RequestActivity;
import zendesk.support.requestlist.RequestListActivity;
import com.zendesk.service.ErrorResponse;
import com.zendesk.service.ZendeskCallback;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;

import java.util.Locale;

public class RNZendeskBridge extends ReactContextBaseJavaModule {

    public RNZendeskBridge(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "RNZendesk";
    }

    // MARK: - Initialization

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @ReactMethod
    public void initialize(ReadableMap config) {
        String appId = config.getString("appId");
        String zendeskUrl = config.getString("zendeskUrl");
        String clientId = config.getString("clientId");
        Zendesk.INSTANCE.init(getReactApplicationContext(), zendeskUrl, appId, clientId);
        Support.INSTANCE.init(Zendesk.INSTANCE);
    }

    // MARK: - Indentification

    @ReactMethod
    public void identifyJWT(String token) {
        JwtIdentity identity = new JwtIdentity(token);
        Zendesk.INSTANCE.setIdentity(identity);
    }

    @ReactMethod
    public void identifyAnonymous(String name, String email) {
        Identity identity = new AnonymousIdentity.Builder()
            .withNameIdentifier(name)
            .withEmailIdentifier(email)
            .build();

        Zendesk.INSTANCE.setIdentity(identity);
    }

    // MARK: - Notifications

    @ReactMethod
    public void registerWithDeviceIdentifier(String deviceIdentifier, Callback successCallback, Callback errorCallback) {
        final Callback _successCallback = successCallback;
        final Callback _errorCallback = errorCallback;

        Zendesk.INSTANCE.provider().pushRegistrationProvider().registerWithDeviceIdentifier(deviceIdentifier, new ZendeskCallback<String>() {
            @Override
            public void onSuccess(String result) {
                _successCallback.invoke(result);
            }

            @Override
            public void onError(ErrorResponse errorResponse) {
                _errorCallback.invoke(errorResponse.getReason());
            }
        });
    }

    @ReactMethod
    public void unregisterDevice() {
        Zendesk.INSTANCE.provider().pushRegistrationProvider().unregisterDevice(new ZendeskCallback<Void>() {
            @Override
            public void onSuccess(final Void response) {
            }

            @Override
            public void onError(ErrorResponse errorResponse) {
            }
        });
    }


    // MARK: - UI Methods

    @ReactMethod
    public void showHelpCenter(ReadableMap options) {
//        Boolean hideContact = options.getBoolean("hideContactUs") || false;
        UiConfig hcConfig = HelpCenterActivity.builder()
                .withContactUsButtonVisible(!(options.hasKey("hideContactSupport") && options.getBoolean("hideContactSupport")))
                .config();

        Intent intent = HelpCenterActivity.builder()
                .withContactUsButtonVisible(true)
                .intent(getReactApplicationContext(), hcConfig);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getReactApplicationContext().startActivity(intent);
    }

    @ReactMethod
    public void showTicket(String requestId) {
        final Intent intent = RequestActivity.builder()
                .withRequestId(requestId)
                .intent(getReactApplicationContext());

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getReactApplicationContext().startActivity(intent);
    }

    @ReactMethod
    public void refreshTicket(String requestId, Callback resultCallback) {
        boolean ticketWasVisibleAndRefreshed = Support.INSTANCE.refreshRequest(requestId, getReactApplicationContext());
        resultCallback.invoke(ticketWasVisibleAndRefreshed);
    }

    @ReactMethod
    public void showNewTicket(ReadableMap options) {
        ArrayList tags = options.getArray("tags").toArrayList();

        Intent intent = RequestActivity.builder()
                .withTags(tags)
                .intent(getReactApplicationContext());

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getReactApplicationContext().startActivity(intent);
    }

    @ReactMethod
    public void showTicketList() {
        Intent intent = RequestListActivity.builder()
                .intent(getReactApplicationContext());

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getReactApplicationContext().startActivity(intent);
    }

    // MARK: - Ticket Methods
    @ReactMethod
    public void createTicket(String subject, String desc, ReadableArray tags, ReadableArray attachments, final Promise promise) {
        final RequestProvider provider = Support.INSTANCE.provider().requestProvider();
        final CreateRequest request = new CreateRequest();

        request.setSubject(subject);
        request.setDescription(desc);

        // Need to pass upload tokens for any previously uploaded attachments
        // (see the uploadAttachment method)
        ArrayList<String> attachmentsUploaded = new ArrayList<String>();
        for (int i = 0; i < attachments.size(); i++) {
            attachmentsUploaded.add(attachments.getString(i));
        }
        request.setAttachments(attachmentsUploaded);

        ArrayList<String> tagsSelected = new ArrayList<String>();
        for (int i = 0; i < tags.size(); i++) {
            tagsSelected.add(tags.getString(i));
        }
        request.setTags(tagsSelected);

        provider.createRequest(request, new ZendeskCallback<Request>() {
            @Override
            public void onSuccess(Request request) {
                promise.resolve(request.getId());
            }
            @Override
            public void onError(ErrorResponse errorResponse) {
                String errorResponseBody = errorResponse.getResponseBody();
                promise.reject(errorResponseBody.isEmpty() ? "unknown error" : errorResponseBody);
            }
        });
    }

    @ReactMethod
    public void uploadAttachment(String path, String mimeType, String fileName, final Promise promise) {
        try {
            File fileToUpload = new File(new URI(path));

            UploadProvider uploadProvider = Support.INSTANCE.provider().uploadProvider();
            uploadProvider.uploadAttachment(fileName, fileToUpload, mimeType,  new
                ZendeskCallback<UploadResponse>() {
                    @Override
                    public void onSuccess(UploadResponse uploadResponse) {
                        // When uploading an attachment to zendesk, we are given an
                        // upload token in the response.
                        // We need to pass this token when we make the request to
                        // create a ticket
                        promise.resolve(uploadResponse.getToken());
                    }

                    @Override
                    public void onError(ErrorResponse errorResponse) {
                        String errorResponseBody = errorResponse.getResponseBody();
                        promise.reject(errorResponseBody.isEmpty() ? "unknown error" : errorResponseBody);
                    }
                });
        } catch (URISyntaxException e) {
            promise.reject("Error uploading attachment: invalid file path");
        }

    }

    @ReactMethod
    public void getRequests(final String statuses, final Promise promise) {
        final RequestProvider provider = Support.INSTANCE.provider().requestProvider();
        final WritableArray transformedRequests = new WritableNativeArray();

        provider.getRequests(statuses, new ZendeskCallback<List<Request>>() {
            @Override
            public void onSuccess(List<Request> requests) {
                for (Request r : requests) {
                    List<User> commentingAgents = r.getLastCommentingAgents();
                    WritableArray commentingAgentAvatarUrls = new WritableNativeArray();
                    for (User user : commentingAgents) {
                        if (user != null) {
                            Attachment avatar = user.getPhoto();

                            if (avatar != null) {
                                commentingAgentAvatarUrls.pushString(avatar.getContentUrl());
                            }
                        }
                    }

                    WritableMap request = new WritableNativeMap();
                    request.putString("id", r.getId());
                    request.putString("status", r.getStatus().name().toLowerCase());
                    request.putString("subject", r.getSubject());
                    request.putString("lastComment", r.getLastComment().getBody());
                    request.putString("updatedAt", Long.toString(r.getUpdatedAt().getTime()));
                    request.putArray("avatarUrls", commentingAgentAvatarUrls);

                    transformedRequests.pushMap(request);
                }
                promise.resolve(transformedRequests);
            }

            @Override
            public void onError(ErrorResponse errorResponse) {
                String errorResponseBody = errorResponse.getResponseBody();
                promise.reject(errorResponseBody.isEmpty() ? "unknown error" : errorResponseBody);
            }
        });
    }
}
