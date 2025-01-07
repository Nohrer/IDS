package IDS;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
public class BannedIpAddresse {
    private List<String> blackList;
    private String filePath;
    public BannedIpAddresse(String filePath){
        this.filePath=filePath;
        blackList=new ArrayList<>();
        loadBlackList();
    }

    private void loadBlackList(){
        try{
            blackList=Files.readAllLines(Paths.get(filePath));

        }catch (IOException e){
            System.err.println("Error Reading Black list"+e.getMessage());
        }
    }

    public List<String> getBlackList(){
        return blackList;
    }
    public boolean isIpBanned(String ipAddress) {
        return blackList.contains(ipAddress);
    }

    public void addIpToBlackList(String ipAddress) {
        if (!blackList.contains(ipAddress)) {
            blackList.add(ipAddress);
            saveBlackList();
        }
    }

    private void saveBlackList() {
        try {
            Files.write(Paths.get(filePath), blackList);
        } catch (IOException e) {
            System.err.println("Error saving the blackList file: " + e.getMessage());
        }
    }

    public void removeIpFromBlackList(String ipAddress) {
        if (blackList.contains(ipAddress)) {
            blackList.remove(ipAddress);
            saveBlackList();
        } else {
            System.out.println(ipAddress + " not found in blacklist.");
        }
    }
}
