package com.yzb.lock.service.codec;

import com.yzb.lock.common.TPMSConsts;
import com.yzb.lock.dao.SignDao;
import com.yzb.lock.util.BCD8421Operater;
import com.yzb.lock.util.BitOperator;
import com.yzb.lock.util.ContextUtils;
import com.yzb.lock.vo.PackageData;
import com.yzb.lock.vo.Sign;
import com.yzb.lock.vo.req.LocationInfoUploadMsg;
import com.yzb.lock.vo.req.TerminalRegisterMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;

public class MsgDecoder {

    private SignDao signDao;

    private static final Logger log = LoggerFactory.getLogger(MsgDecoder.class);

    private BitOperator bitOperator;
    private BCD8421Operater bcd8421Operater;

    public MsgDecoder() {
        this.bitOperator = new BitOperator();
        this.bcd8421Operater = new BCD8421Operater();
    }

    public PackageData bytes2PackageData(byte[] data) {
        PackageData ret = new PackageData();

        // 0. 终端套接字地址信息
        // ret.setChannel(msg.getChannel());

        // 1. 16byte 或 12byte 消息头
        PackageData.MsgHeader msgHeader = this.parseMsgHeaderFromBytes(data);
        ret.setMsgHeader(msgHeader);

        int msgBodyByteStartIndex = 13;
        // 2. 消息体
        // 有子包信息,消息体起始字节后移四个字节:消息包总数(word(16))+包序号(word(16))
        if (msgHeader.isHasSubPackage()) {
            msgBodyByteStartIndex = 17;
        }
        byte[] tmp = null;
        if (data.length == 104) {
            tmp = new byte[msgHeader.getMsgBodyLength()];
        } else {
            tmp = new byte[msgHeader.getMsgBodyLength() + 1];
        }
        ret.setDataLength(tmp.length);

        System.out.println("MsgBody: " + tmp.length);
        System.arraycopy(data, msgBodyByteStartIndex, tmp, 0, tmp.length);
        ret.setMsgBodyBytes(tmp);

        // 3. 去掉分隔符之后，最后一位就是校验码
        int checkSumInPkg = this.bitOperator.oneByteToInteger(data[data.length - 2]);
//        int checkSumInPkg = data[data.length - 2];
        int calculatedCheckSum = this.bitOperator.getCheckSum4JT808(data, 0, data.length - 2);
        System.out.println("checkSumInPkg: " + toHexString1(data[data.length - 2]));
        ret.setCheckSum(checkSumInPkg);
        if (checkSumInPkg != calculatedCheckSum) {
            log.warn("检验码不一致,msgid:{},pkg:{},calculated:{}", msgHeader.getMsgId(), checkSumInPkg, calculatedCheckSum);
        }
        return ret;
    }

    public static void main2(String[] args) {
        String by = "7e0100003e68612352501300340001000237303935360000000000000000000000000000000000000000383638363836313233353235303133eb88e60036590102898604041918900959392e7e";
        BitOperator bitOperator = new BitOperator();
        byte[] data = new byte[]{126, 1, 0, 0, 62, 104, 97, 35, 82, 80, 19, 0, 9, 0, 1, 0, 2, 55, 48, 57, 53, 54, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 56, 54, 56, 54, 56, 54, 49, 50, 51, 53, 50, 53, 48, 49, 51, -21, -120, -26, 0, 54, 89, 1, 2, -119, -122, 4, 4, 25, 24, -112, 9, 89, 57, 19, 126};
        byte[] tmp = new byte[2];
        System.arraycopy(data, 2, tmp, 0, 2);
        System.out.println(toHexString1(tmp));
    }

    public static void main1(String[] args) {
        String by = "7e 0102 000c 686123525013 0001 363836313233353235303133 3d7e";
        System.out.println(by.length());

    }

    /**
     * 7e 0100 003e 686123525013 0039 0001000237303935360000000000000000000000000000000000000000383638363836313233353235303133eb88e6003659010289860404191890095939237e
     *
     * @param args
     */
    public static void main(String[] args) {

//        byte[] data = new byte[]{126, 1, 0, 0, 62, 104, 97, 35, 82, 80, 19, 0, 57, 0, 1, 0, 2, 55, 48, 57, 53, 54, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 56, 54, 56, 54, 56, 54, 49, 50, 51, 53, 50, 53, 48, 49, 51, -21, -120, -26, 0, 54, 89, 1, 2, -119, -122, 4, 4, 25, 24, -112, 9, 89, 57, 35, 126};
//        byte[] data = new byte[]{126, 1, 0, 0, 62, 104, 97, 35, 82, 80, 19, 0, 39, 0, 1, 0, 2, 55, 48, 57, 53, 54, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 56, 54, 56, 54, 56, 54, 49, 50, 51, 53, 50, 53, 48, 49, 51, -21, -120, -26, 0, 54, 89, 1, 2, -119, -122, 4, 4, 25, 24, -112, 9, 89, 57, 61, 126};
//          byte[] data = new byte[]{126, 1, 0, 0, 47, 99, 112, 81, -107, 88, -123, 5, 125, 2, 0, 1, 0, 2, 55, 48, 57, 53, 54, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -61, -38, -123, -67, -7, -59, 1, 2, -119, -122, 4, 3, 16, 24, -110, 1, 18, 48, -50, 126};
//        byte[] data = new byte[]{126, 0, 2, 0, 0, 104, 97, 35, 82, 80, 19, 0, 6, 63, 126};
//        byte[] data = new byte[]{126, 2, 0, 0, 89, 99, 112, 81, -107, 96, -127, 0, 125, 1, 0, 0, 0, 0, 0, 60, 0, 0, 1, -32, 95, -33, 7, 46, 2, 127, 0, 0, 0, 0, 0, 0, 25, 1, 36, 4, 6, 19, 48, 1, 12, 49, 1, 0, -31, 4, 0, 0, 0, -53, -30, 2, 0, 65, -29, 6, 0, 98, 1, -97, 1, -32, -28, 32, 1, -52, 0, 0, 82, -107, 0, 0, 95, 38, 21, 82, -107, 0, 0, 18, -53, 24, 82, -107, 0, 0, 18, -47, 14, 82, -107, 0, 0, 4, -31, 13, -26, 1, -18, -58, 126};
        byte[] data = new byte[]{126, 2, 0, 0, 89, 99, 112, 81, -107, 96, -127, 0, -60, 0, 0, 0, 0, 0, 60, 0, 0, 1, -32, 95, -33, 7, 46, 2, 127, 0, 0, 0, 0, 0, 0, 25, 1, 37, 20, 70, 35, 48, 1, 18, 49, 1, 0, -31, 4, 0, 0, 0, -53, -30, 2, 0, 65, -29, 6, 0, 100, 1, -97, 1, -34, -28, 32, 1, -52, 0, 0, 82, -107, 0, 0, 58, 64, 35, 82, -107, 0, 0, 18, -52, 29, 82, -107, 0, 0, -72, -107, 24, 82, -27, 0, 0, 2, -27, 24, -26, 1, 0, 125, 2, 126};
//        byte[] data = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 71, 126, 126, 1, 0, 0, 47, 99, 112, 81, -107, 96, -127, 1, 125, 2, 0, 1, 0, 2, 55, 48, 57, 53, 54, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -15, -30, 47, -99, 125, 1, 76, 1, 2, -119, -122, 4, 3, 16, 24, -110, 1, 18, 55, 124, 126, 126, 1, 2, 0};


        //location
//        byte[] data = new byte[]{126, 2, 0, 0, 89, 99, 112, 81, -107, 88, -123, 0, 125, 1, 0, 0, 0, 0, 0, 60, 0, 0, 1, -32, 102, -22, 7, 45, -21, 105, 0, 0, 0, 0, 0, 0, 25, 1, 35, 20, 35, 64, 48, 1, 25, 49, 1, 0, -31, 4, 0, 0, 0, -16, -30, 2, 0, 65, -29, 6, 0, 100, 1, -93, 1, -16, -28, 32, 1, -52, 0, 0, 82, -107, 0, 0, 58, 64, 47, 82, -107, 0, 0, 18, -52, 40, 82, -107, 0, 0, 18, -53, 31, 0, 0, 0, 0, 0, 0, 0, -26, 1, 0, -85, 126};
        System.out.println("length: " + data.length);
        System.out.println(toHexString1(data));
        System.out.println(toHexString1((byte) -54));
        System.out.println("---> " + data[data.length - 2]);
        byte[] tmp = new byte[2];
        System.arraycopy(data, 1, tmp, 0, 2);
        System.out.println(toHexString1(tmp));
        String res = toHexString1(tmp);
        int s1 = 0x0100;
        System.out.println(s1);
        BitOperator bitOperator = new BitOperator();
        int p = bitOperator.twoBytesToInteger(tmp);
        System.out.println(p);
        int s = Integer.parseInt(res, 16);
        System.out.println("---> " + s);
        boolean ss = TPMSConsts.msg_id_terminal_register == p;
        System.out.println("---->" + ss);

        MsgDecoder msgDecoder = new MsgDecoder();
        PackageData.MsgHeader saa = msgDecoder.parseMsgHeaderFromBytes(data);
        System.out.println(saa);
        PackageData pp = msgDecoder.bytes2PackageData(data);
        System.out.println("--->" + toHexString1(pp.getMsgBodyBytes()));
        LocationInfoUploadMsg msg = msgDecoder.toLocationInfoUploadMsg(pp);
        System.out.println(msg);
    }

    public static void main4(String[] args) {
//        byte[] data = new byte[]{126, 1, 0, 0, 47, 99, 112, 81, -107, 96, -127, 5, 125, 1, 0, 1, 0, 2, 55, 48, 57, 53, 54, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -15, -30, 47, -99, 125, 1, 76, 1, 2, -119, -122, 4, 3, 16, 24, -110, 1, 18, 55, 123, 126};
        byte[] data2 = new byte[]{126, 2, 0, 0, 89, 99, 112, 81, -107, 88, -123, 0, 125, 1, 0, 0, 0, 0, 0, 60, 0, 0, 1, -32, 102, -22, 7, 45, -21, 105, 0, 0, 0, 0, 0, 0, 25, 1, 35, 20, 35, 64, 48, 1, 25, 49, 1, 0, -31, 4, 0, 0, 0, -16, -30, 2, 0, 65, -29, 6, 0, 100, 1, -93, 1, -16, -28, 32, 1, -52, 0, 0, 82, -107, 0, 0, 58, 64, 47, 82, -107, 0, 0, 18, -52, 40, 82, -107, 0, 0, 18, -53, 31, 0, 0, 0, 0, 0, 0, 0, -26, 1, 0, -85, 126};
        byte[] data1 = new byte[]{126, 2, 0, 0, 89, 99, 112, 81, -107, 96, -127, 0, -47, 0, 0, 0, 0, 0, 60, 0, 0, 1, -32, 95, -33, 7, 46, 2, 127, 0, 0, 0, 0, 0, 0, 25, 1, 37, 21, 2, 24, 48, 1, 17, 49, 1, 0, -31, 4, 0, 0, 0, -53, -30, 2, 0, 65, -29, 6, 0, 98, 1, -98, 1, -81, -28, 32, 1, -52, 0, 0, 82, -107, 0, 0, 58, 64, 32, 82, -107, 0, 0, 18, -52, 28, 82, -107, 0, 0, 18, -53, 24, 82, -107, 0, 0, -72, -107, 24, -26, 1, 125, 1, 81, 126};
//        byte[] data1 = new byte[]{126, 1, 0, 0, 47, 99, 112, 81, -107, 88, -123, 0, 34, 0, 1, 0, 2, 55, 48, 57, 53, 54, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -61, -38, -123, -67, -7, -59, 1, 2, -119, -122, 4, 3, 16, 24, -110, 1, 18, 48, -105, 126};
//        byte[] data2 = new byte[]{126, 2, 0, 0, 89, 99, 112, 81, -107, 96, -127, 0, 125, 1, 0, 0, 0, 0, 0, 60, 0, 0, 1, -32, 95, -33, 7, 46, 2, 127, 0, 0, 0, 0, 0, 0, 25, 1, 36, 4, 6, 19, 48, 1, 12, 49, 1, 0, -31, 4, 0, 0, 0, -53, -30, 2, 0, 65, -29, 6, 0, 98, 1, -97, 1, -32, -28, 32, 1, -52, 0, 0, 82, -107, 0, 0, 95, 38, 21, 82, -107, 0, 0, 18, -53, 24, 82, -107, 0, 0, 18, -47, 14, 82, -107, 0, 0, 4, -31, 13, -26, 1, -18, -58, 126};
//        byte[] data2 = new byte[]{12, 99, 112, 81, -107, 96, -127, 0, 125, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 71, 126, 126, 1, 0, 0, 47, 99, 112, 81, -107, 96, -127, 1, 125, 2, 0, 1, 0, 2, 55, 48, 57, 53, 54, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -15, -30, 47, -99, 125, 1, 76, 1, 2, -119, -122, 4, 3};
//        byte[] data2 = new byte[]{1, 2, 0, 12, 99, 112, 81, -107, 88, -123, 1, 125, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 122, 126};

        System.out.println(toHexString1(data1));
        System.out.println(toHexString1(data2));

    }

    /**
     * 数组转成十六进制字符串
     *
     * @param b
     * @return
     */
    public static String toHexString1(byte[] b) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < b.length; ++i) {
            buffer.append(toHexString1(b[i]));
        }
        return buffer.toString();
    }

    public static String toHexString1(byte b) {
        String s = Integer.toHexString(b & 0xFF);
        if (s.length() == 1) {
            return "0" + s;
        } else {
            return s;
        }
    }

    /**
     * 解析消息头
     *
     * @param data
     * @return
     */
    private PackageData.MsgHeader parseMsgHeaderFromBytes(byte[] data) {
        PackageData.MsgHeader msgHeader = new PackageData.MsgHeader();

        // 1. 消息ID word(16) √
        byte[] tmp = new byte[2];
        System.arraycopy(data, 1, tmp, 0, 2);
        msgHeader.setMsgId(this.bitOperator.twoBytesToInteger(tmp));
//		msgHeader.setMsgId(this.parseIntFromBytes(data, 1, 2));

        // 2. 消息体属性 word(16)==> √
        System.arraycopy(data, 3, tmp, 0, 2);
        int msgBodyProps = this.bitOperator.twoBytesToInteger(tmp);
//		int msgBodyProps = this.parseIntFromBytes(data, 3, 2);
        msgHeader.setMsgBodyPropsField(msgBodyProps);


        // [ 0-9 ] 0000,0011,1111,1111(3FF)(消息体长度)
        msgHeader.setMsgBodyLength(msgBodyProps & 0x1ff);
        System.out.println("------> 0x1ff  " + (msgBodyProps & 0x1ff));

        // [10-12] 0001,1100,0000,0000(1C00)(加密类型)
        msgHeader.setEncryptionType((msgBodyProps & 0xe00) >> 10);
        // [ 13_ ] 0010,0000,0000,0000(2000)(是否有子包)
        msgHeader.setHasSubPackage(((msgBodyProps & 0x2000) >> 13) == 1);
        // [14-15] 1100,0000,0000,0000(C000)(保留位)
        msgHeader.setReservedBit(((msgBodyProps & 0xc000) >> 14) + "");
        // 消息体属性 word(16)<=================

        // 3. 终端手机号 bcd[6] √
        tmp = new byte[6];
//        System.arraycopy(data, 4, tmp, 0, 6);
        System.arraycopy(data, 5, tmp, 0, 6);
        msgHeader.setTerminalPhone(this.bcd8421Operater.bcd2String(tmp));
//		msgHeader.setTerminalPhone(this.parseBcdStringFromBytes(data, 4, 6));

        // 4. 消息流水号 word(16) 按发送顺序从 0 开始循环累加 √
        tmp = new byte[2];
        System.arraycopy(data, 11, tmp, 0, 2);
        System.out.println(toHexString1(tmp));
        msgHeader.setFlowId(this.bitOperator.twoBytesToInteger(tmp));
//		msgHeader.setFlowId(this.parseIntFromBytes(data, 10, 2));
        // 5. 消息包封装项
        if (msgHeader.isHasSubPackage()) {
            // 消息包封装项字段
            msgHeader.setPackageInfoField(this.parseIntFromBytes(data, 12, 4));
            // byte[0-1] 消息包总数(word(16))
            tmp = new byte[2];
            System.arraycopy(data, 12, tmp, 0, 2);
            msgHeader.setTotalSubPackage(this.bitOperator.twoBytesToInteger(tmp));
//            msgHeader.setTotalSubPackage(this.parseIntFromBytes(data, 12, 2));

            // byte[2-3] 包序号(word(16)) 从 1 开始
            tmp = new byte[2];
            System.arraycopy(data, 12, tmp, 0, 2);
            msgHeader.setSubPackageSeq(this.bitOperator.twoBytesToInteger(tmp));
//            msgHeader.setSubPackageSeq(this.parseIntFromBytes(data, 12, 2));
        }
        return msgHeader;
    }

    protected String parseStringFromBytes(byte[] data, int startIndex, int lenth) {
        return this.parseStringFromBytes(data, startIndex, lenth, null);
    }

    private String parseStringFromBytes(byte[] data, int startIndex, int lenth, String defaultVal) {
        try {
            byte[] tmp = new byte[lenth];
            System.arraycopy(data, startIndex, tmp, 0, lenth);
            return new String(tmp, TPMSConsts.string_charset);
        } catch (Exception e) {
            log.error("解析字符串出错:{}", e.getMessage());
            e.printStackTrace();
            return defaultVal;
        }
    }

    private String parseBcdStringFromBytes(byte[] data, int startIndex, int lenth) {
        return this.parseBcdStringFromBytes(data, startIndex, lenth, null);
    }

    private String parseBcdStringFromBytes(byte[] data, int startIndex, int lenth, String defaultVal) {
        try {
            byte[] tmp = new byte[lenth];
            System.arraycopy(data, startIndex, tmp, 0, lenth);
            return this.bcd8421Operater.bcd2String(tmp);
        } catch (Exception e) {
            log.error("解析BCD(8421码)出错:{}", e.getMessage());
            e.printStackTrace();
            return defaultVal;
        }
    }

    private int parseIntFromBytes(byte[] data, int startIndex, int length) {
        return this.parseIntFromBytes(data, startIndex, length, 0);
    }

    private int parseIntFromBytes(byte[] data, int startIndex, int length, int defaultVal) {
        try {
            // 字节数大于4,从起始索引开始向后处理4个字节,其余超出部分丢弃
            final int len = length > 4 ? 4 : length;
            byte[] tmp = new byte[len];
            System.arraycopy(data, startIndex, tmp, 0, len);
            return bitOperator.byteToInteger(tmp);
        } catch (Exception e) {
            log.error("解析整数出错:{}", e.getMessage());
            e.printStackTrace();
            return defaultVal;
        }
    }

    /**
     * 锁注册
     *
     * @param packageData
     * @return
     */
    public TerminalRegisterMsg toTerminalRegisterMsg(PackageData packageData) {
        TerminalRegisterMsg ret = new TerminalRegisterMsg(packageData);
        byte[] data = ret.getMsgBodyBytes();

        TerminalRegisterMsg.TerminalRegInfo body = new TerminalRegisterMsg.TerminalRegInfo();

        // 1. byte[0-1] 省域ID(WORD)
        // 设备安装车辆所在的省域，省域ID采用GB/T2260中规定的行政区划代码6位中前两位
        // 0保留，由平台取默认值
        body.setProvinceId(this.parseIntFromBytes(data, 0, 2));

        // 2. byte[2-3] 市县域ID
        // 0保留，由平台取默认值
        body.setCityId(this.parseIntFromBytes(data, 2, 2));

        // 3. byte[4-8] 制造商ID(BYTE[5]) 5 个字节，终端制造商编码
        // byte[] tmp = new byte[5];
        body.setManufacturerId(this.parseStringFromBytes(data, 4, 5));

        // 4. byte[9-28] 终端型号(BYTE[8]) 八个字节， 此终端型号 由制造商自行定义 位数不足八位的，补空格。
        body.setTerminalType(this.parseStringFromBytes(data, 9, 20));

        // 5. byte[29] 蓝牙 MAC 地址
        body.setTerminalId(this.parseStringFromBytes(data, 29, 7));

        // 6. byte[36] 锁类型
        body.setLicensePlateColor(this.parseIntFromBytes(data, 36, 1));

        // 7. byte[37-46] 锁类型
        body.setLicensePlateColor(this.parseIntFromBytes(data, 37, 10));

        // 7. byte[47-x] OTG ID
        body.setLicensePlate(this.parseStringFromBytes(data, 47, data.length - 47));

        ret.setTerminalRegInfo(body);
        return ret;
    }

    /**
     * 解析位置信息
     *
     * @param packageData
     * @return
     */
    public LocationInfoUploadMsg toLocationInfoUploadMsg(PackageData packageData) {
        LocationInfoUploadMsg ret = new LocationInfoUploadMsg(packageData);
        final byte[] data = ret.getMsgBodyBytes();

        // 1. byte[0-3] 报警标志(DWORD(32))
        ret.setWarningFlagField(this.parseIntFromBytes(data, 0, 4));
        // 2. byte[4-7] 状态(DWORD(32))
        ret.setStatusField(this.parseIntFromBytes(data, 4, 4));
        // 3. byte[8-11] 纬度(DWORD(32)) 以度为单位的纬度值乘以10^6，精确到百万分之一度
        ret.setLatitude(this.parseFloatFromBytes(data, 8, 4));
        // 4. byte[12-15] 经度(DWORD(32)) 以度为单位的经度值乘以10^6，精确到百万分之一度
        ret.setLongitude(this.parseFloatFromBytes(data, 10, 4));
        // 5. byte[16-17] 高程(WORD(16)) 海拔高度，单位为米（ m）
        ret.setElevation(this.parseIntFromBytes(data, 16, 2));
        // byte[18-19] 速度(WORD) 1/10km/h
        ret.setSpeed(this.parseIntFromBytes(data, 18, 2));
        // byte[20-21] 方向(WORD) 0-359，正北为 0，顺时针
        ret.setDirection(this.parseIntFromBytes(data, 20, 2));
        // byte[22-27] 时间(BCD[6]) YY-MM-DD-hh-mm-ss

        byte[] tmp = new byte[6];

        System.out.println("---->" + toHexString1(data));

        //判断是否格式错误
        int wei = 0;
        log.info("data length：{}", packageData.getDataLength());
        if (packageData.getDataLength() <= 89) {
            wei = 28;
            System.arraycopy(data, 26, tmp, 0, 6);
        } else {
            wei = 29;
            System.arraycopy(data, 23, tmp, 0, 6);
        }
        log.info("time:{}", toHexString1(tmp));

        Sign sign = new Sign();
        sign.setDeivceid(packageData.getMsgHeader().getTerminalPhone());

        //无线通信信号 1
        byte[] tmp1 = new byte[1];
        System.arraycopy(data, wei, tmp1, 0, 1);
        log.info("extraId1:{}", toHexString1(tmp1));


        wei += 1;
        System.arraycopy(data, wei, tmp1, 0, 1);
        int extraLength1 = this.parseIntFromBytes(data, wei, 1);
        log.info("extraId1 length:{}", extraLength1 + " ex " + toHexString1(tmp1));
        tmp1 = new byte[extraLength1];
        wei += 1;
        System.arraycopy(data, wei, tmp1, 0, extraLength1);
        log.info("wirelessSignal:{}", toHexString1(tmp1));


        //卫星数 2
        wei += extraLength1;
        tmp1 = new byte[1];
        System.arraycopy(data, wei, tmp1, 0, 1);
        log.info("extraId2:{}", toHexString1(tmp1));

        wei += 1;
        System.arraycopy(data, wei, tmp1, 0, 1);

        int extraLength2 = this.parseIntFromBytes(data, wei, 1);
        log.info("extraId2 length:{}", extraLength2 + " ex " + toHexString1(tmp1));
        tmp1 = new byte[extraLength2];
        wei += 1;
        System.arraycopy(data, wei, tmp1, 0, extraLength2);
        log.info("satelliteCount:{}", toHexString1(tmp1));

        //开锁次数 3
        wei += extraLength2;
        tmp1 = new byte[1];
        System.arraycopy(data, wei, tmp1, 0, 1);
        log.info("extraId3:{}", toHexString1(tmp1));

        wei += 1;
        System.arraycopy(data, wei, tmp1, 0, 1);
        int extraLength3 = this.parseIntFromBytes(data, wei, 1);
        log.info("extraId3 length:{}", extraLength3 + " ex " + toHexString1(tmp1));
        tmp1 = new byte[extraLength3];
        wei += 1;
        System.arraycopy(data, wei, tmp1, 0, extraLength3);
        log.info("unLockCount:{}", toHexString1(tmp1));

        //锁状态 4
        wei += extraLength3;
        tmp1 = new byte[1];
        System.arraycopy(data, wei, tmp1, 0, 1);
        log.info("extraId4:{}", toHexString1(tmp1));
        sign.setLockstatus(toHexString1(tmp1));

        wei += 1;
        System.arraycopy(data, wei, tmp1, 0, 1);
        int extraLength4 = this.parseIntFromBytes(data, wei, 1);
        log.info("extraId4 length:{}", extraLength4 + " ex " + toHexString1(tmp1));
        tmp1 = new byte[extraLength4];
        wei += 1;
        System.arraycopy(data, wei, tmp1, 0, extraLength4);
        log.info("lockStatus:{}", toHexString1(tmp1));

        //电压 5
        wei += extraLength4;

        tmp1 = new byte[1];
        System.arraycopy(data, wei, tmp1, 0, 1);
        log.info("extraId4:{}", toHexString1(tmp1));

        wei += 1;
        System.arraycopy(data, wei, tmp1, 0, 1);
        int extraLength5 = this.parseIntFromBytes(data, wei, 1);
        log.info("extraId4 length:{}", extraLength5 + " ex " + toHexString1(tmp1));
        tmp1 = new byte[extraLength5];
        wei += 1;
        System.arraycopy(data, wei, tmp1, 0, extraLength5);
        log.info("lockBattery:{},{}", Arrays.toString(tmp1), toHexString1(tmp1));

        tmp1 = new byte[2];
        System.arraycopy(data, wei, tmp1, 0, extraLength5 - 4);

        String battery = String.valueOf(Integer.parseInt(toHexString1(tmp1), 16));
        log.info("当前电量：lockBattery:{}", battery);
        sign.setLockstatus(String.valueOf(Integer.parseInt(toHexString1(tmp1))));
        sign.setBattery(battery);
        sign.setTime(new Date());
        //保存到数据库
        try {
            signDao = ContextUtils.getBeanByClass(SignDao.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        int row = signDao.insertSign(sign);
        log.info("updateSign dao:{}", row);

        //基站
//        int baseStation = this.parseIntFromBytes(data, 44, data.length - 48);
        //流水号
        int lockFlow = this.parseIntFromBytes(data, data.length - 4, 4);
        log.info("lockFlow:{}", lockFlow);

        // GMT+8 时间，本标准中之后涉及的时间均采用此时区
        // ret.setTime(this.par);
        return ret;
    }

    private float parseFloatFromBytes(byte[] data, int startIndex, int length) {
        return this.parseFloatFromBytes(data, startIndex, length, 0f);
    }

    private float parseFloatFromBytes(byte[] data, int startIndex, int length, float defaultVal) {
        try {
            // 字节数大于4,从起始索引开始向后处理4个字节,其余超出部分丢弃
            final int len = length > 4 ? 4 : length;
            byte[] tmp = new byte[len];
            System.arraycopy(data, startIndex, tmp, 0, len);
            return bitOperator.byte2Float(tmp);
        } catch (Exception e) {
            log.error("解析浮点数出错:{}", e.getMessage());
            e.printStackTrace();
            return defaultVal;
        }
    }
}
