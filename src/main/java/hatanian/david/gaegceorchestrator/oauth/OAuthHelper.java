package hatanian.david.gaegceorchestrator.oauth;

import java.util.List;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.appengine.api.appidentity.AppIdentityService;
import com.google.appengine.api.appidentity.AppIdentityServiceFactory;

public class OAuthHelper {
	public static final String APPNAME = "gae-gce-orchestrator";
    AppIdentityService appIdentityService = AppIdentityServiceFactory.getAppIdentityService();

	public Credential getAppIdentityCredential(List<String> scopes) {
		AppIdentityService.GetAccessTokenResult accessTokenResult = appIdentityService.getAccessToken(scopes);
		GoogleCredential result = new GoogleCredential.Builder().build();
		result.setAccessToken(accessTokenResult.getAccessToken());
        result.setExpirationTimeMilliseconds(accessTokenResult.getExpirationTime().getTime());
		return result;
	}
}
