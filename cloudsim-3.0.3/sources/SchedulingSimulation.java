import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

import java.util.*;

public class SchedulingSimulation {
    public static void main(String[] args) {
        try {
            CloudSim.init(1, Calendar.getInstance(), false);

            Datacenter datacenter0 = createDatacenter("Datacenter_0");
            DatacenterBroker broker = createBroker();
            int brokerId = broker.getId();

            List<Vm> vmlist = new ArrayList<Vm>();
            Vm vm = new Vm(0, brokerId, 1000, 1, 512, 1000, 10000, "Xen", new CloudletSchedulerTimeShared());
            vmlist.add(vm);
            broker.submitVmList(vmlist);

            List<Cloudlet> cloudletList = new ArrayList<Cloudlet>();
            UtilizationModel utilizationModel = new UtilizationModelFull();
            Cloudlet cloudlet = new Cloudlet(0, 40000, 1, 300, 300, utilizationModel, utilizationModel, utilizationModel);
            cloudlet.setUserId(brokerId);
            cloudletList.add(cloudlet);
            broker.submitCloudletList(cloudletList);

            CloudSim.startSimulation();
            List<Cloudlet> newList = broker.getCloudletReceivedList();
            CloudSim.stopSimulation();

            for (Cloudlet c : newList) {
                System.out.println("Cloudlet " + c.getCloudletId() + " Status: " + c.getCloudletStatusString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Datacenter createDatacenter(String name) {
        List<Host> hostList = new ArrayList<Host>();
        List<Pe> peList = new ArrayList<Pe>();
        peList.add(new Pe(0, new PeProvisionerSimple(1000)));

        hostList.add(new Host(0, new RamProvisionerSimple(2048), new BwProvisionerSimple(10000), 1000000, peList, new VmSchedulerTimeShared(peList)));

        DatacenterCharacteristics characteristics = new DatacenterCharacteristics("x86", "Linux", "Xen", hostList, 10.0, 3.0, 0.05, 0.001, 0.0);
        try {
            return new Datacenter(name, characteristics, new VmAllocationPolicySimple(hostList), new LinkedList<Storage>(), 0);
        } catch (Exception e) {
            return null;
        }
    }

    private static DatacenterBroker createBroker() {
        try {
            return new DatacenterBroker("Broker");
        } catch (Exception e) {
            return null;
        }
    }
}