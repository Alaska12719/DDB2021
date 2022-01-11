package network;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
    public static void log(String newContent) {
        Date nowTime = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try (EtcdClient client = new EtcdClient(Constants.ETCD_ENDPOINTS)) {
            client.lock();
            String originContent = client.get("LOG");
            StringBuffer contentBuffer = new StringBuffer(originContent);
            contentBuffer.append(dateFormat.format(nowTime))
                         .append("   ")
                         .append(newContent);
            contentBuffer.append('\n');
            String content = contentBuffer.toString();
            client.put("LOG", content);
            client.unlock();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void show() {
        try (EtcdClient client = new EtcdClient(Constants.ETCD_ENDPOINTS)) {
            client.lock();
            String content = client.get("LOG");
            client.remove("LOG");

            long time = 0;
            long size = 0;
            String sites = client.get(Constants.SITES_KEY);
            String[] sitesArray = sites.split(",");
            for (String site : sitesArray) {
                String siteTime = client.get(site + "_TIME");
                if (siteTime.isEmpty()) {
                    siteTime = "0";
                }
                time += Long.parseLong(siteTime);
                client.remove(site + "_TIME");
                String siteSize = client.get(site + "_SIZE");
                if (siteSize.isEmpty()) {
                    siteSize = "0";
                }
                size += Long.parseLong(siteSize);
                client.remove(site + "_SIZE");
            }
            System.out.println("=================");
            System.out.print(content);
            System.out.println("=================");
            System.out.println(size + " bytes are transmitted");
            System.out.println("Transmission time: " + time + " ms");
            client.unlock();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}