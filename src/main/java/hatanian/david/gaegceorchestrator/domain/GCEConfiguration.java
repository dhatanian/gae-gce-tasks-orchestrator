package hatanian.david.gaegceorchestrator.domain;

import com.googlecode.objectify.annotation.Embed;

@Embed
public class GCEConfiguration {
    private String machineType = "n1-highmem-8";
    private String zone = "us-central1-a";
    private String image = "https://www.googleapis.com/compute/v1/projects/debian-cloud/global/images/debian-7-wheezy-v20131120";

    public String getMachineType() {
        return machineType;
    }

    public void setMachineType(String machineType) {
        this.machineType = machineType;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
