package com.andy.spring.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 * 网络工具
 *
 * @author 庞先海 2019-11-14
 */

public class NetworkUtil {

    /**
     * 获取本机IP地址
     *
     * 返回网络开启运行的IP地址，不是送回地址
     * 未开启网络运行的网络可能时虚拟机docker
     * 回送地址一般是127.0.0.1
     *
     * @return ip地址
     */
    public static String getLocalHostIp() {
        InetAddress inetAddress = getLocalHostLanAddress();
        if (inetAddress != null) {
            return inetAddress.getHostAddress();
        } else {
            return null;
        }

    }

    /**
     * 获取本地地址  直接读取网卡地址
     *
     * @return 本机地址
     */
    public static String getLocalHostAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    public static InetAddress getLocalHostLanAddress() {
        try {
            InetAddress candidateAddress = null;
            for (Enumeration interfaces = NetworkInterface.getNetworkInterfaces(); interfaces.hasMoreElements(); ) {
                NetworkInterface ifce = (NetworkInterface)interfaces.nextElement();
                if (ifce.isUp() && ! ifce.isLoopback()) {
                    for (Enumeration inetAddrs = ifce.getInetAddresses(); inetAddrs.hasMoreElements(); ) {
                        InetAddress inetAddr = (InetAddress)inetAddrs.nextElement();
                        if (! inetAddr.isLoopbackAddress() && inetAddr.isSiteLocalAddress()) {
                            return inetAddr;
                        } else if (! inetAddr.isLoopbackAddress()) {
                            candidateAddress = inetAddr;
                        }
                    }
                }
            }
            if (candidateAddress != null) {
                return candidateAddress;
            }
            return InetAddress.getLocalHost();
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }
}
