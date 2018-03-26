package com.isuwang.dapeng.tools.helps;


import com.github.dapeng.core.SoaException;
import com.github.dapeng.metadata.MetadataClient;

/**
 * @author Eric on  2016/2/15.
 */
public class MetaInfoHelper {

    public static String getService(String... args) {

        String serviceName = args[0];
        String versionName = args[1];

        String metadata = "";
        try {
            System.out.println(serviceName+" getting metadata ...");
            metadata = new MetadataClient(serviceName, versionName).getServiceMetadata();
            System.out.println("------------------------------------------------------");
            System.out.println(metadata);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return metadata;
    }

}
