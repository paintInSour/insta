package src.service;

import static src.config.Config.instagram;

import java.io.IOException;
import java.util.ArrayList;

import org.brunocvcunha.instagram4j.requests.InstagramDirectShareRequest;
import org.brunocvcunha.instagram4j.requests.InstagramDirectShareRequest.ShareType;
import org.brunocvcunha.instagram4j.requests.InstagramGetInboxRequest;
import org.brunocvcunha.instagram4j.requests.InstagramGetInboxThreadRequest;
import org.brunocvcunha.instagram4j.requests.payload.InstagramInbox;
import org.brunocvcunha.instagram4j.requests.payload.InstagramInboxResult;
import org.brunocvcunha.instagram4j.requests.payload.InstagramInboxThread;
import org.brunocvcunha.instagram4j.requests.payload.InstagramInboxThreadItem;
import org.brunocvcunha.instagram4j.requests.payload.InstagramInboxThreadResult;
import org.brunocvcunha.instagram4j.requests.payload.InstagramUser;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AutoansweringService {

	private ArrayList<String> userIdList = new ArrayList<String>();
	private ArrayList<InstagramUser> userList = new ArrayList<InstagramUser>();

	public void connect() {
		try {
			instagram.setup();
			instagram.login();
			log.info("logged : " + instagram.getUsername());
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void answer() {
		try {
			InstagramGetInboxRequest getInboxResult = new InstagramGetInboxRequest();
			InstagramInboxResult inboxResult = instagram.sendRequest(getInboxResult);
			InstagramInbox inbox = inboxResult.getInbox();
			int unseen = inbox.getUnseen_count();
			inbox.getThreads().forEach((obj) -> {
				if (!obj.isNamed()) {
					userList.addAll(obj.getUsers());
				}
			});
			for (InstagramUser user : userList) {
				System.out.println("user : " + user.getUsername());
				userIdList.add(Long.toString(user.getPk()));
			}

			for (String item : userIdList) {
				ArrayList<String> tmp = new ArrayList<String>();
				tmp.add(item);
				if (unseen > 0) {
					instagram.sendRequest(InstagramDirectShareRequest.builder().recipients(tmp)
							.shareType(ShareType.MESSAGE).message("Hello").build());
					tmp.clear();
					unseen--;
				}
			}

			System.out.println("unseen : " + inbox.getUnseen_count());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private ArrayList<InstagramInboxThread> threads = new ArrayList<InstagramInboxThread>();

	public void answer2() {
		InstagramGetInboxRequest i = new InstagramGetInboxRequest();
		try {
			InstagramInboxResult s = instagram.sendRequest(i);
			String nextMaxId = null;
			InstagramInboxResult in = instagram.sendRequest(new InstagramGetInboxRequest(nextMaxId));

			int ind = 0;
			while (true) {
				ind = in.inbox.threads.size() - 1;
				threads.addAll(in.inbox.threads);

				if (!in.inbox.threads.get(ind).getItems().isEmpty()) {
					if (in.inbox.threads.get(0).getItems().get(0).reel_share != null) {
						System.out.println("M " + in.inbox.threads.get(0).getItems().get(0).reel_share.text);
					}
				}
				nextMaxId = in.inbox.oldest_cursor;
				if (nextMaxId == null) {
					break;
				}
				in = instagram.sendRequest(new InstagramGetInboxRequest(nextMaxId));

			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
