package hatanian.david.gaegceorchestrator.gcebackend;

import com.google.api.client.extensions.appengine.http.UrlFetchTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.*;
import com.google.api.services.compute.model.Metadata.Items;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import com.google.appengine.api.utils.SystemProperty;
import hatanian.david.gaegceorchestrator.domain.Execution;
import hatanian.david.gaegceorchestrator.oauth.OAuthHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GCEBackendService {
    public static final String NAME_PREFIX = "gce-orchestrator-";
    private HttpTransport transport = new UrlFetchTransport();
    private JsonFactory factory = new GsonFactory();
    private OAuthHelper oAuthHelper = new OAuthHelper();

    private static final Logger logger = Logger.getLogger(GCEBackendService.class.getName());

    private Compute getCompute() {
        return new Compute.Builder(transport, factory, oAuthHelper.getAppIdentityCredential(Arrays.asList("https://www.googleapis.com/auth/bigquery", "https://www.googleapis.com/auth/taskqueue", "https://www.googleapis.com/auth/sqlservice", "https://www.googleapis.com/auth/compute",
                "https://www.googleapis.com/auth/devstorage.full_control"))).setApplicationName(OAuthHelper.APPNAME).build();
    }

    public void startExecution(Execution execution) throws IOException, InterruptedException, GCEBackendException {
        startGceInstance(execution);
        startExecutionWatchingTask(execution.getId());
    }

    public static void startExecutionWatchingTask(String executionId) {
        QueueFactory.getQueue("checkexecutions").add(TaskOptions.Builder.withMethod(Method.GET).url("/admin/tasks/checkexecutions").param("executionId", executionId));
    }

    private void startGceInstance(Execution execution) throws IOException, InterruptedException, GCEBackendException {
        // 1. Create new disk.
        execution.configureDiskAndInstanceName();
        createDisk(execution.getDiskAndInstanceName(), execution.getGceConfiguration().getZone(), execution.getGceConfiguration().getImage(), execution.getProjectId());
        // 2. Poll to wait until the disk is ready or until we time out.
        if (!waitForDisk(execution.getDiskAndInstanceName(), execution.getGceConfiguration().getZone(), execution.getProjectId())) {
            throw new GCEBackendException("Unable to create disk");
        }

        String executionScript = ScriptBuilder.getScript(execution, getResultUrl());

        // 3. Create instance
        createInstance(execution.getProjectId(), execution.getDiskAndInstanceName(), execution.getGceConfiguration().getZone(), execution.getGceConfiguration().getMachineType(), executionScript);
        if (!waitForInstance(execution.getDiskAndInstanceName(), execution.getGceConfiguration().getZone(), execution.getProjectId())) {
            throw new GCEBackendException("Unable to create instance");
        }
    }

    private boolean waitForInstance(String instanceName, String zone, String projectId) throws InterruptedException {
        long timeout = System.currentTimeMillis() + 2 * 60000L;
        boolean diskCreated = false;

        while (!diskCreated && System.currentTimeMillis() < timeout) {
            if (checkInstance(instanceName, zone, projectId)) {
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

    private boolean checkInstance(String instanceName, String zone, String projectId) {
        try {
            Instance instance = getCompute().instances().get(projectId, zone, instanceName).execute();
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

    private void createInstance(String projectId, String diskAndInstanceName, String zone, String machineType, String executionScript) throws IOException {
        Instance instance = new Instance();
        instance.setMachineType("https://www.googleapis.com/compute/v1/projects/" + projectId + "/zones/" + zone + "/machineTypes/" + machineType);
        instance.setName(diskAndInstanceName);

        AttachedDisk disk = new AttachedDisk();
        disk.setSource("https://www.googleapis.com/compute/v1/projects/" + projectId + "/zones/" + zone + "/disks/" + diskAndInstanceName);
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
        startUpScript.setValue(executionScript);
        items.add(startUpScript);
        metadata.setItems(items);

        instance.setMetadata(metadata);

        getCompute().instances().insert(projectId, zone, instance).execute();
    }

    private String getResultUrl() {
        return "https://" + SystemProperty.applicationId.get() + ".appspot.com/backendresult";
    }

    private boolean waitForDisk(String diskName, String zone, String projectId) throws InterruptedException, IOException {
        long timeout = System.currentTimeMillis() + 2 * 60000L;
        boolean diskCreated = false;

        while (!diskCreated && System.currentTimeMillis() < timeout) {
            if (checkDisk(diskName, zone, projectId)) {
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

    private boolean checkDisk(String diskName, String zone, String projectId) throws IOException {
        try {
            Disk disk = getCompute().disks().get(projectId, zone, diskName).execute();
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

    private void createDisk(String diskName, String zone, String sourceImage, String projectId) throws IOException {
        Disk disk = new Disk();
        disk.setName(diskName);
        getCompute().disks().insert(projectId, zone, disk).setSourceImage(sourceImage).execute();
    }

    public void deleteInstance(String instance, String zone, String projectId) throws IOException {
        getCompute().instances().delete(projectId, zone, instance).execute();
    }

    public void deleteDisk(String disk, String zone, String projectId) throws IOException {
        getCompute().disks().delete(projectId, zone, disk).execute();
    }
}
