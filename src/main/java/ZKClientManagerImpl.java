
import java.util.List;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

public class ZKClientManagerImpl implements ZKManager {
    private ZooKeeper zkeeper;
    private ZKConnection zkConnection;


    public ZKClientManagerImpl() {
        this.initialize();
    }

    private void initialize() {
        try {
            zkConnection = new ZKConnection();
            zkeeper = zkConnection.connect("localhost");

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void create(String path, byte[] data) throws KeeperException,
            InterruptedException {
        zkeeper.create(path, data, ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.PERSISTENT);

    }

    public Stat getZNodeStats(String path) throws KeeperException,
            InterruptedException {
        Stat stat = zkeeper.exists(path, true);
        if (stat != null) {
            System.out.println("Node exists and the node version is "
                    + stat.getVersion());
        } else {
            System.out.println("Node does not exists");
        }
        return stat;
    }

    public Object getZNodeData(String path, boolean watchFlag) throws KeeperException,
            InterruptedException {


        try {

            Stat stat = getZNodeStats(path);
            byte[] b = null;
            if (stat != null) {
                if(watchFlag){
                    ZKWatcher watch = new ZKWatcher();
                    b = zkeeper.getData(path, watch, null);
                    watch.await();
                }else{
                    b = zkeeper.getData(path, null,null);
                }
                String data = new String(b, "UTF-8");
                System.out.println(data);

                return data;
            } else {
                System.out.println("Node does not exists");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }


    public void update(String path, byte[] data) throws KeeperException,
            InterruptedException {
        int version = zkeeper.exists(path, true).getVersion();
        zkeeper.setData(path, data, version);

    }

    public List<String> getZNodeChildren(String path) throws KeeperException,
            InterruptedException {
        Stat stat = getZNodeStats(path);
        List<String> children  = null;

        if (stat != null) {
            children = zkeeper.getChildren(path, false);
            for (int i = 0; i < children.size(); i++)
                System.out.println(children.get(i));

        } else {
            System.out.println("Node does not exists");
        }
        return children;
    }

    public void delete(String path) throws KeeperException,
            InterruptedException {
        int version = zkeeper.exists(path, true).getVersion();
        zkeeper.delete(path, version);

    }

}
