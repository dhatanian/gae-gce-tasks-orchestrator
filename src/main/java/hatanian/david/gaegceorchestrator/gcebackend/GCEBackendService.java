package hatanian.david.gaegceorchestrator.gcebackend;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.api.client.extensions.appengine.http.UrlFetchTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.AccessConfig;
import com.google.api.services.compute.model.AttachedDisk;
import com.google.api.services.compute.model.Disk;
import com.google.api.services.compute.model.DiskList;
import com.google.api.services.compute.model.Instance;
import com.google.api.services.compute.model.InstanceList;
import com.google.api.services.compute.model.Metadata;
import com.google.api.services.compute.model.Metadata.Items;
import com.google.api.services.compute.model.NetworkInterface;
import com.google.api.services.compute.model.ServiceAccount;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import com.google.appengine.api.utils.SystemProperty;

import hatanian.david.gaegceorchestrator.domain.Execution;
import hatanian.david.gaegceorchestrator.oauth.OAuthHelper;

public class GCEBackendService {
	private static final String ZONE = "us-central1-a";
	public static final String NAME_PREFIX = "gce-orchestrator-";
	private HttpTransport transport = new UrlFetchTransport();
	private JsonFactory factory = new GsonFactory();
    private OAuthHelper oAuthHelper = new OAuthHelper();

	private static final Logger logger = Logger.getLogger(GCEBackendService.class.getName());
	private static final String MACHINE_TYPE = "n1-highmem-8";
	private static final String SOURCE_IMAGE = "https://www.googleapis.com/compute/v1/projects/debian-cloud/global/images/debian-7-wheezy-v20131120";

    //TODO set configurable scopes
    //TODO set configurable machine type and zone
    //TODO set configurable source image
    //TODO change start script
    public Compute getCompute(){
        return new Compute.Builder(transport, factory, oAuthHelper.getAppIdentityCredential(Arrays.asList("https://www.googleapis.com/auth/compute",
                "https://www.googleapis.com/auth/devstorage.full_control"))).setApplicationName(OAuthHelper.APPNAME).build();
    }

	public void startReport(Execution execution) throws IOException, InterruptedException, GCEBackendException {
		startGceInstance(execution);
		startExecutionWatchingTask(execution.getId());
	}
	
	public static void startExecutionWatchingTask(String executionId){
		QueueFactory.getQueue("checkexecutions").add(TaskOptions.Builder.withMethod(Method.GET).url("/admin/tasks/checkexecutions").param("executionId", executionId));
	}

	private void startGceInstance(Execution execution) throws IOException, InterruptedException, GCEBackendException {
		// 1. Create new disk.
		String diskAndInstanceName = execution.buildDiskAndInstanceName();
		createDisk(diskAndInstanceName);
		// 2. Poll to wait until the disk is ready or until we time out.
		if (!waitForDisk(diskAndInstanceName)) {
			throw new GCEBackendException("Unable to create disk");
		}

		// 3. Create instance
		createInstance(diskAndInstanceName, execution);
		if (!waitForInstance(diskAndInstanceName)) {
			throw new GCEBackendException("Unable to create instance");
		}
	}

	private boolean waitForInstance(String instanceName) throws InterruptedException {
		long timeout = System.currentTimeMillis() + 2 * 60000L;
		boolean diskCreated = false;

		while (!diskCreated && System.currentTimeMillis() < timeout) {
			if (checkInstance(instanceName)) {
				diskCreated = true;
				logger.info("Instance is ready.");
			} else {
				logger.info("Instance is not ready. Sleeping for ten seconds ...");
				Thread.sleep(10000);
			}
		}
		if (!diskCreated) {
			logger.warning("Timed out. Giving up.");
			return false;
		} else {
			return true;
		}
	}

	private boolean checkInstance(String instanceName) {
		try {
			Instance instance = getCompute().instances().get(oAuthHelper.getProjectId(), ZONE, instanceName).execute();
			if ("RUNNING".equalsIgnoreCase(instance.getStatus())) {
				return true;
			} else {
				logger.info("Instance status : " + instance.getStatus());
				return false;
			}
		} catch (Throwable t) {
			logger.log(Level.INFO, "Error when fetching instance info", t);
			return false;
		}
	}

	private void createInstance(String diskAndInstanceName, Execution execution) throws IOException {
        String projectId = oAuthHelper.getProjectId();
		Instance instance = new Instance();
		instance.setMachineType("https://www.googleapis.com/compute/v1/projects/" + projectId + "/zones/" + ZONE + "/machineTypes/" + MACHINE_TYPE);
		instance.setName(diskAndInstanceName);

		AttachedDisk disk = new AttachedDisk();
		disk.setSource("https://www.googleapis.com/compute/v1/projects/" + projectId + "/zones/" + ZONE + "/disks/" + diskAndInstanceName);
		disk.setBoot(true);
		disk.setType("PERSISTENT");
		disk.setMode("READ_WRITE");
		instance.setDisks(Collections.singletonList(disk));

		AccessConfig accessConfig = new AccessConfig();
		accessConfig.setName("External NAT");
		accessConfig.setType("ONE_TO_ONE_NAT");

		NetworkInterface networkInterface = new NetworkInterface();
		networkInterface.setAccessConfigs(Collections.singletonList(accessConfig));
		networkInterface.setNetwork("https://www.googleapis.com/compute/v1/projects/" + projectId + "/global/networks/default");
		instance.setNetworkInterfaces(Collections.singletonList(networkInterface));

		ServiceAccount sa = new ServiceAccount();
		sa.setEmail("default");
		sa.setScopes(Arrays.asList("https://www.googleapis.com/auth/compute", "https://www.googleapis.com/auth/devstorage.full_control", "https://www.googleapis.com/auth/bigquery"));
		instance.setServiceAccounts(Collections.singletonList(sa));

		Metadata metadata = new Metadata();
		List<Items> items = new ArrayList<>();
		Items startUpScript = new Items();

		// watch jar execution and stop after 3 hours, no
		// matter what (using the timoeout command :
		// http://linux.die.net/man/1/timeout)
		// then use wget to inform gae of the result
		// then use gsutil to store the execution log
		startUpScript.setKey("startup-script");
		startUpScript.setValue("#! /bin/bash\n" + "cd /root \n " + "apt-get update\n" + "apt-get install -y openjdk-7-jre wget\n"
				+ "gsutil cp gs://blabla/blabla.jar ./script.jar\n" + "timeout 3h java -jar script.jar " + "TODO another parameter to remove"
				+ " " + execution.getId() + " " + "TODO replace this old call to isProd" + " &> script.log \n" + "wget -X POST " + getResultUrl() + " --post-data=" + buildJsonResult(execution) + "  \n"
				+ "gsutil cp script.log gs://blabla/" + execution.getId() + ".log \n" + "gcutil deleteinstance " + diskAndInstanceName + " --zone=" + ZONE + " --project="
				+ projectId + " --force --nodelete_boot_pd");
		items.add(startUpScript);
		metadata.setItems(items);

		instance.setMetadata(metadata);

		getCompute().instances().insert(projectId, ZONE, instance).execute();
	}

	private String buildJsonResult(Execution execution) {
		return "'{\"executionId\":\"" + execution.getId() + "\",\"resultCode\":'$?'}'";
	}

	private String getResultUrl() {
		return "https://" + SystemProperty.applicationId.get() + ".appspot.com/backendresult";
	}

	private boolean waitForDisk(String diskName) throws InterruptedException, IOException {
		long timeout = System.currentTimeMillis() + 2 * 60000L;
		boolean diskCreated = false;

		while (!diskCreated && System.currentTimeMillis() < timeout) {
			if (checkDisk(diskName)) {
				diskCreated = true;
				logger.info("Disk is ready.");
			} else {
				logger.info("Disk is not ready. Sleeping for ten seconds ...");
				Thread.sleep(10000);
			}
		}
		if (!diskCreated) {
			logger.warning("Timed out. Giving up.");
			return false;
		} else {
			return true;
		}
	}

	private boolean checkDisk(String diskName) throws IOException {
		try {
			Disk disk = getCompute().disks().get(oAuthHelper.getProjectId(), ZONE, diskName).execute();
			if ("READY".equalsIgnoreCase(disk.getStatus())) {
				return true;
			} else {
				logger.info("Disk status : " + disk.getStatus());
				return false;
			}
		} catch (Throwable t) {
			logger.log(Level.INFO, "Error when fetching disk info", t);
			return false;
		}
	}

	private void createDisk(String diskName) throws IOException {
		Disk disk = new Disk();
		disk.setName(diskName);
		getCompute().disks().insert(oAuthHelper.getProjectId(), ZONE, disk).setSourceImage(SOURCE_IMAGE).execute();
	}

	public void deleteInstance(String instance) throws IOException {
		getCompute().instances().delete(oAuthHelper.getProjectId(), ZONE, instance).execute();
	}

	public void deleteDisk(String disk) throws IOException {
		getCompute().disks().delete(oAuthHelper.getProjectId(), ZONE, disk).execute();
	}

	public List<String> findDisks() throws IOException {
		List<String> result = new ArrayList<>();
		String pageToken = null;
		do {
			com.google.api.services.compute.Compute.Disks.List list = getCompute().disks().list(oAuthHelper.getProjectId(), ZONE);
			if (pageToken != null) {
				list.setPageToken(pageToken);
			}
			DiskList diskList = list.execute();
			if(diskList.getItems()!=null){
				for (Disk disk : diskList.getItems()) {
					result.add(disk.getName());
				}
			}
			pageToken = diskList.getNextPageToken();
		} while (pageToken != null);
		return result;
	}
	
	public List<String> findRunningInstances() throws IOException {
		List<String> result = new ArrayList<>();
		String pageToken = null;
		do {
			com.google.api.services.compute.Compute.Instances.List list = getCompute().instances().list(oAuthHelper.getProjectId(), ZONE);
			list.setFilter("status eq RUNNING");
			if (pageToken != null) {
				list.setPageToken(pageToken);
			}
			InstanceList instanceList = list.execute();
			if(instanceList.getItems()!=null){
				for (Instance instance : instanceList.getItems()) {
					result.add(instance.getName());
				}
			}
			pageToken = instanceList.getNextPageToken();
		} while (pageToken != null);
		return result;
	}
}
